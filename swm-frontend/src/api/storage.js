import request from '@/utils/request'

export function getStorageList(params) {
  return request({ url: '/storage', method: 'get', params })
}

export function getStorageDetail(id) {
  return request({ url: `/storage/${id}`, method: 'get' })
}

export function createStorage(data) {
  return request({ url: '/storage', method: 'post', data })
}

export function updateStorage(id, data) {
  return request({ url: `/storage/${id}`, method: 'put', data })
}

export function deleteStorage(id) {
  return request({ url: `/storage/${id}`, method: 'delete' })
}

export function getInventoryByArea() {
  return request({ url: '/storage/stats/inventory-by-area', method: 'get' })
}

export function getDistributionByCategory() {
  return request({ url: '/storage/stats/distribution-by-category', method: 'get' })
}

export function getAvailableBatches() {
  return request({ url: '/storage/batches/available', method: 'get' })
}
