import request from '@/utils/request'

export function getDictList(type) {
  return request({ url: `/dict/${type}/list`, method: 'get' })
}

export function getDictPage(type, params) {
  return request({ url: `/dict/${type}`, method: 'get', params })
}
