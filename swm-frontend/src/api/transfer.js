import request from '@/utils/request'

export function getTransferList(params) {
  return request({ url: '/transfer', method: 'get', params })
}

export function getTransferDetail(id) {
  return request({ url: `/transfer/${id}`, method: 'get' })
}

export function createTransfer(data) {
  return request({ url: '/transfer', method: 'post', data })
}

export function updateTransfer(id, data) {
  return request({ url: `/transfer/${id}`, method: 'put', data })
}

export function deleteTransfer(id) {
  return request({ url: `/transfer/${id}`, method: 'delete' })
}

export function getAvailableBatches() {
  return request({ url: '/transfer/batches/available', method: 'get' })
}

export function getActiveUsers() {
  return request({ url: '/transfer/users/active', method: 'get' })
}
