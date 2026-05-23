<template>
  <div class="profile-container">
    <el-card>
      <div slot="header">
        <span>个人中心</span>
      </div>
      <el-form label-width="100px" style="max-width: 500px">
        <el-form-item label="用户名">
          <span>{{ username }}</span>
        </el-form-item>
        <el-form-item label="姓名">
          <span>{{ realName }}</span>
        </el-form-item>
        <el-form-item label="手机号">
          <span>{{ phone || '-' }}</span>
        </el-form-item>
        <el-form-item label="角色">
          <span>{{ roles.join('、') }}</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Profile',
  computed: {
    ...mapState({
      username: state => state.user.username,
      realName: state => state.user.realName,
      phone: state => state.user.phone,
      roles: state => state.user.roles
    })
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}
</style>
