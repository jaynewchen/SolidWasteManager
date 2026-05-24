import router from './router'
import store from './store'
import { getToken, removeToken } from '@/utils/auth'

const whiteList = ['/login', '/404', '/401']

// Suppress Vue Router "Redirected" errors that occur during login flow
// These happen because the guard re-navigates after addRoutes, which is expected behavior
const originalPush = router.push.bind(router)
router.push = function push(location) {
  return originalPush(location).catch(err => {
    if (err.name === 'NavigationDuplicated') return err
    if (err.message && /Redirected when going from/i.test(err.message)) return err
    throw err
  })
}

router.onError((err) => {
  const pattern = /Redirected when going from/i
  if (pattern.test(err.message)) {
    return
  }
  console.error(err)
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 固废管理系统' : '固废管理系统'

  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/dashboard' })
      return
    }

    const hasUserInfo = store.state.user.userId != null
    if (hasUserInfo) {
      next()
      return
    }

    // Load user info and generate dynamic routes
    try {
      const data = await store.dispatch('user/getUserInfo')
      const accessRoutes = await store.dispatch('permission/generateRoutes', data.menus || [])
      router.addRoutes(accessRoutes)
      // Re-trigger navigation so the new routes take effect
      next({ path: to.path, query: to.query, replace: true })
    } catch (error) {
      console.error('getUserInfo failed:', error)
      removeToken()
      await store.dispatch('user/resetToken')
      // Use native navigation to break out of router redirect chain
      window.location.replace('/login')
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.path)}`)
    }
  }
})
