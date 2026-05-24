import request from '@/utils/request'

export function getLogPage(params) {
  return request({ url: '/system/logs', method: 'get', params })
}

export function getLogConfig() {
  return request({ url: '/system/logs/config', method: 'get' })
}

export function setLogConfig(data) {
  return request({ url: '/system/logs/config', method: 'put', data })
}
