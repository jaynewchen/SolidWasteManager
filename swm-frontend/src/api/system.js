import request from '@/utils/request'

export function getUserList(params) {
  return request({ url: '/system/users', method: 'get', params })
}

export function createUser(data) {
  return request({ url: '/system/users', method: 'post', data })
}

export function updateUser(id, data) {
  return request({ url: `/system/users/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/system/users/${id}`, method: 'delete' })
}

export function getRoleList() {
  return request({ url: '/system/roles/all', method: 'get' })
}

export function getRolePage(params) {
  return request({ url: '/system/roles', method: 'get', params })
}

export function createRole(data) {
  return request({ url: '/system/roles', method: 'post', data })
}

export function updateRole(id, data) {
  return request({ url: `/system/roles/${id}`, method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: `/system/roles/${id}`, method: 'delete' })
}

export function getRoleDetail(id) {
  return request({ url: `/system/roles/${id}`, method: 'get' })
}

export function getMenuTree() {
  return request({ url: '/system/roles/menus/tree', method: 'get' })
}
