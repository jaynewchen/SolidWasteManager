import request from '@/utils/request'

export function getReceivingList(params) {
  return request({ url: '/receiving', method: 'get', params })
}

export function getReceivingDetail(id) {
  return request({ url: `/receiving/${id}`, method: 'get' })
}

export function createReceiving(data) {
  return request({ url: '/receiving', method: 'post', data })
}

export function updateReceiving(id, data) {
  return request({ url: `/receiving/${id}`, method: 'put', data })
}

export function deleteReceiving(id) {
  return request({ url: `/receiving/${id}`, method: 'delete' })
}
