<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">固废管理系统</h2>
      <p class="subtitle">Solid Waste Management System</p>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            show-password
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return
        this.loading = true
        this.$store.dispatch('user/login', this.loginForm).then(() => {
          const redirect = this.$route.query.redirect || '/dashboard'
          this.$router.push(redirect).catch(() => {})
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  position: relative;
  overflow: hidden;
}
.login-container::before {
  content: '';
  position: absolute;
  top: -30%;
  right: -15%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(255,255,255,0.08) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(80px);
}
.login-container::after {
  content: '';
  position: absolute;
  bottom: -20%;
  left: -10%;
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(99,102,241,0.2) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(80px);
}
.login-card {
  position: relative;
  z-index: 1;
  width: 424px;
  padding: 48px 44px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
}
.title {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 6px;
  letter-spacing: -0.01em;
}
.subtitle {
  text-align: center;
  font-size: 13px;
  color: #94A3B8;
  margin-bottom: 36px;
  font-weight: 500;
}
</style>
