import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import permission from './modules/permission'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: { app, user, permission },
  getters: {
    roles: state => state.user.roles,
    permissions: state => state.user.permissions,
    menus: state => state.user.menus
  }
})
