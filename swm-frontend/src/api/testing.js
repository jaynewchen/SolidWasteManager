import request from '@/utils/request'

export function getTestingList(params) {
  return request({ url: '/testing', method: 'get', params })
}

export function getTestingDetail(id) {
  return request({ url: `/testing/${id}`, method: 'get' })
}

export function createTesting(data) {
  return request({ url: '/testing', method: 'post', data })
}

export function updateTesting(id, data) {
  return request({ url: `/testing/${id}`, method: 'put', data })
}

export function deleteTesting(id) {
  return request({ url: `/testing/${id}`, method: 'delete' })
}

export function reviewTesting(id, data) {
  return request({ url: `/testing/${id}/review`, method: 'put', data })
}

export function getAvailableBatches() {
  return request({ url: '/testing/batches/available', method: 'get' })
}

export function getActiveUsers() {
  return request({ url: '/testing/users/active', method: 'get' })
}

export function getStatistics(params) {
  return request({ url: '/testing/statistics', method: 'get', params })
}
