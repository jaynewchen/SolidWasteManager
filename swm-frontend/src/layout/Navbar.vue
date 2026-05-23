<template>
  <div class="navbar">
    <div class="navbar-left">
      <i
        class="el-icon-s-fold navbar-hamburger"
        :class="{ 'is-active': isCollapse }"
        @click="toggleSidebar"
      ></i>
      <el-breadcrumb separator="/" class="navbar-breadcrumb">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
          {{ item.meta.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="navbar-user">
          <i class="el-icon-user-solid"></i>
          <span>{{ realName || username }}</span>
          <i class="el-icon-arrow-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile">
            <i class="el-icon-user"></i> 个人中心
          </el-dropdown-item>
          <el-dropdown-item command="logout" divided>
            <i class="el-icon-switch-button"></i> 退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { removeToken } from '@/utils/auth'

export default {
  name: 'Navbar',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapState({
      realName: state => state.user.realName,
      username: state => state.user.username
    }),
    breadcrumbs() {
      const matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      return matched
    }
  },
  methods: {
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
      this.$root.$emit('toggle-sidebar')
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.$confirm('确认退出登录？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('user/resetToken').then(() => {
            removeToken()
            this.$router.push('/login')
          })
        })
      }
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.navbar-left {
  display: flex;
  align-items: center;
}
.navbar-hamburger {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
  color: #666;
}
.navbar-hamburger:hover {
  color: #409EFF;
}
.navbar-breadcrumb {
  line-height: 50px;
}
.navbar-right {
  display: flex;
  align-items: center;
  margin-right: 10px;
}
.navbar-user {
  cursor: pointer;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.navbar-user:hover {
  color: #409EFF;
}
</style>
