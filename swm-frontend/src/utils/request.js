import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { Message, MessageBox } from 'element-ui'
import router from '@/router'
import store from '@/store'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api/v1',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '操作失败')
      if (res.code === 40100 || res.code === 40300) {
        const msg = res.code === 40100 ? '登录已过期，请重新登录' : '权限已更新，请重新登录'
        MessageBox.confirm(msg, '提示', {
          confirmButtonText: '重新登录',
          showCancelButton: false,
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    Message.error('网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default service
