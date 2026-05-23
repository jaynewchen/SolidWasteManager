import request from '@/utils/request'
import { setToken, removeToken } from '@/utils/auth'

export function login(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  }).then(res => {
    if (res.data && res.data.accessToken) {
      setToken(res.data.accessToken)
    }
    return res
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  }).finally(() => {
    removeToken()
  })
}
