import Layout from '@/layout/index'
import { constantRoutes } from '@/router'

const state = { routes: [] }

function filterAsyncRoutes(menus) {
  const routes = []
  menus.forEach(menu => {
    if (menu.menuType === 'M' || menu.menuType === 'C') {
      const route = {
        path: menu.path,
        component: menu.menuType === 'M' ? Layout : resolveComponent(menu.component),
        redirect: menu.menuType === 'M' && menu.children && menu.children.length > 0 ? menu.children[0].path : undefined,
        meta: { title: menu.menuName, icon: menu.icon },
        children: []
      }
      if (menu.children && menu.children.length > 0) {
        route.children = filterAsyncRoutes(menu.children)
      }
      routes.push(route)
    }
  })
  return routes
}

function resolveComponent(componentPath) {
  if (!componentPath) return Layout
  return () => import(`@/views/${componentPath}`)
}

const mutations = {
  SET_ROUTES: (state, routes) => { state.routes = constantRoutes.concat(routes) }
}

const actions = {
  generateRoutes({ commit }, menus) {
    return new Promise(resolve => {
      const accessedRoutes = filterAsyncRoutes(menus || [])
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default { namespaced: true, state, mutations, actions }
