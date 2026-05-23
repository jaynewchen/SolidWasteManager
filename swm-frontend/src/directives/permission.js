import store from '@/store'

export default {
  inserted(el, binding) {
    const { value } = binding
    const permissions = store.getters.permissions || []
    if (value) {
      const permissionList = Array.isArray(value) ? value : [value]
      const hasPermission = permissionList.some(p => permissions.includes(p))
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}
