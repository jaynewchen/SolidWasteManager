<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2>欢迎回来，{{ realName || username }}！</h2>
      <p>今天是 {{ currentDate }}</p>
    </div>
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayReceivingCount }}</div>
              <div class="stat-label">今日接收批次数</div>
            </div>
            <div class="stat-icon" style="background: #409EFF">
              <i class="el-icon-document"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthTotalWeight }}</div>
              <div class="stat-label">本月接收总量(吨)</div>
            </div>
            <div class="stat-icon" style="background: #67C23A">
              <i class="el-icon-data-line"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingDisposalCount }}</div>
              <div class="stat-label">待处置批次数</div>
            </div>
            <div class="stat-icon" style="background: #E6A23C">
              <i class="el-icon-warning"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCheckCount }}</div>
              <div class="stat-label">待复核检测数</div>
            </div>
            <div class="stat-icon" style="background: #F56C6C">
              <i class="el-icon-circle-check"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        todayReceivingCount: 0,
        monthTotalWeight: 0,
        pendingDisposalCount: 0,
        pendingCheckCount: 0
      }
    }
  },
  computed: {
    ...mapState({
      realName: state => state.user.realName,
      username: state => state.user.username
    }),
    currentDate() {
      const now = new Date()
      return now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日'
    }
  },
  created() {
    this.fetchStats()
  },
  methods: {
    fetchStats() {
      // Placeholder: fetch dashboard stats from backend
      // In production, call an API endpoint to get real data
      // For now, display with zeros
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.welcome-section {
  margin-bottom: 30px;
}
.welcome-section h2 {
  font-size: 22px;
  color: #303133;
  margin-bottom: 8px;
}
.welcome-section p {
  color: #909399;
  font-size: 14px;
}
.stat-card {
  cursor: pointer;
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}
</style>
