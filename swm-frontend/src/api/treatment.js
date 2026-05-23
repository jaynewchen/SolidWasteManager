import request from '@/utils/request'

export function getTreatmentList(params) {
  return request({ url: '/treatment', method: 'get', params })
}

export function getTreatmentDetail(id) {
  return request({ url: `/treatment/${id}`, method: 'get' })
}

export function createTreatment(data) {
  return request({ url: '/treatment', method: 'post', data })
}

export function updateTreatment(id, data) {
  return request({ url: `/treatment/${id}`, method: 'put', data })
}

export function deleteTreatment(id) {
  return request({ url: `/treatment/${id}`, method: 'delete' })
}

export function getAvailableBatches() {
  return request({ url: '/treatment/batches/available', method: 'get' })
}
