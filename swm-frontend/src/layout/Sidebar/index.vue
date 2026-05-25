<template>
  <el-aside :width="isCollapse ? '64px' : '210px'" class="sidebar-container">
    <div class="sidebar-logo" @click="$router.push('/')">
      <span v-if="!isCollapse" class="logo-text">固废管理系统</span>
      <span v-else class="logo-text-mini">固废</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      :unique-opened="true"
      background-color="transparent"
      text-color="#A5B4FC"
      active-text-color="#E0E7FF"
      mode="vertical"
      router
    >
      <template v-for="menu in menus">
        <el-submenu v-if="menu.children && menu.children.length > 0" :key="menu.path" :index="menu.path">
          <template slot="title">
            <i :class="menu.icon || 'el-icon-menu'"></i>
            <span>{{ menu.menuName }}</span>
          </template>
          <el-menu-item
            v-for="child in menu.children"
            :key="child.path"
            :index="child.path"
          >
            <i :class="child.icon || 'el-icon-document'"></i>
            <span>{{ child.menuName }}</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item v-else :key="menu.path" :index="menu.path">
          <i :class="menu.icon || 'el-icon-menu'"></i>
          <span>{{ menu.menuName }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </el-aside>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Sidebar',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapState({
      menus: state => state.user.menus
    }),
    activeMenu() {
      const route = this.$route
      return route.path
    }
  },
  mounted() {
    this.$root.$on('toggle-sidebar', () => {
      this.isCollapse = !this.isCollapse
    })
  },
  beforeDestroy() {
    this.$root.$off('toggle-sidebar')
  }
}
</script>

<style scoped>
.sidebar-container {
  transition: width 0.3s ease-out;
  overflow: hidden;
}
.sidebar-logo {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.15);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  letter-spacing: 0.02em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo-text {
  padding: 0 10px;
  background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.logo-text-mini {
  font-size: 14px;
  font-weight: 700;
  background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.el-menu {
  border-right: none;
}
</style>
