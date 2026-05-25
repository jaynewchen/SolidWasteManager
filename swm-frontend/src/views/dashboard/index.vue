<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2>欢迎回来，<span>{{ realName || username }}</span>！</h2>
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #4F46E5, #6366F1)">
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #10B981, #34D399)">
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #F59E0B, #FBBF24)">
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #EF4444, #F87171)">
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
import { getStats } from '@/api/dashboard'

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
      getStats().then(res => {
        this.stats = res.data
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}
.welcome-section {
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, rgba(79,70,229,0.04) 0%, rgba(124,58,237,0.06) 100%);
  border-radius: 16px;
  border: 1px solid rgba(79,70,229,0.06);
}
.welcome-section h2 {
  font-size: 24px;
  font-weight: 700;
  color: #0F172A;
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}
.welcome-section h2 span {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.welcome-section p {
  color: #64748B;
  font-size: 14px;
  margin: 0;
}
.stat-card {
  cursor: pointer;
  border: 1px solid #F1F5F9;
  border-radius: 16px;
}
.stat-card:hover {
  transform: translateY(-3px);
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
  font-size: 32px;
  font-weight: 800;
  color: #0F172A;
  margin-bottom: 4px;
  letter-spacing: -0.02em;
  line-height: 1;
}
.stat-label {
  font-size: 13px;
  color: #64748B;
  font-weight: 500;
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}
</style>
