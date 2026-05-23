<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">页面不存在</p>
      <p class="error-desc">您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error404'
}
</script>

<style scoped>
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
}
.error-content {
  text-align: center;
}
.error-code {
  font-size: 120px;
  color: #409EFF;
  margin: 0;
  line-height: 1;
}
.error-message {
  font-size: 24px;
  color: #606266;
  margin: 20px 0 10px;
}
.error-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 30px;
}
</style>
