import { login, getUserInfo, logout } from '@/api/auth'

const state = {
  token: '',
  userId: null,
  username: '',
  realName: '',
  avatar: '',
  roles: [],
  permissions: [],
  menus: []
}

const mutations = {
  SET_TOKEN: (state, token) => { state.token = token },
  SET_USER: (state, data) => {
    state.userId = data.userId
    state.username = data.username
    state.realName = data.realName
    state.avatar = data.avatar
    state.roles = data.roles || []
    state.permissions = data.permissions || []
    state.menus = data.menus || []
  },
  RESET: (state) => {
    state.token = ''
    state.userId = null
    state.username = ''
    state.realName = ''
    state.avatar = ''
    state.roles = []
    state.permissions = []
    state.menus = []
  }
}

const actions = {
  login({ commit }, { username, password }) {
    return new Promise((resolve, reject) => {
      login(username, password).then(res => {
        commit('SET_TOKEN', res.data.accessToken)
        resolve(res)
      }).catch(error => reject(error))
    })
  },

  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo().then(res => {
        commit('SET_USER', res.data)
        resolve(res.data)
      }).catch(error => reject(error))
    })
  },

  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('RESET')
      resolve()
    })
  }
}

export default { namespaced: true, state, mutations, actions }
