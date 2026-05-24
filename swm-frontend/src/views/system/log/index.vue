<template>
  <div class="app-container">
    <!-- 日志保留配置（仅管理员可见） -->
    <el-card v-if="isAdmin" style="margin-bottom: 16px">
      <div slot="header">
        <span>日志保留配置</span>
      </div>
      <el-form :inline="true">
        <el-form-item label="当前保留上限">
          <el-tag type="warning">{{ retentionLimit }} 条</el-tag>
        </el-form-item>
        <el-form-item label="修改上限">
          <el-input-number v-model="newLimit" :min="100" :max="100000" :step="100" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="savingConfig" @click="saveConfig">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作日志列表 -->
    <el-card>
      <div slot="header">
        <span>操作日志</span>
      </div>

      <!-- 筛选栏 -->
      <el-form :inline="true" :model="filters" style="margin-bottom: 16px">
        <el-form-item label="操作人">
          <el-input v-model="filters.username" placeholder="输入用户名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="模块">
          <el-select v-model="filters.module" placeholder="全部模块" clearable style="width: 140px">
            <el-option v-for="m in modules" :key="m" :label="m" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="filters.level" placeholder="全部级别" clearable style="width: 120px">
            <el-option label="重要" value="IMPORTANT" />
            <el-option label="一般" value="NORMAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="module" label="模块" width="110" />
        <el-table-column prop="operation" label="操作" width="120" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="requestUrl" label="请求URL" min-width="180" show-overflow-tooltip />
        <el-table-column label="级别" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.level === 'IMPORTANT' ? 'danger' : 'primary'" size="small">
              {{ scope.row.level === 'IMPORTANT' ? '重要' : '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="80" align="center" />
        <el-table-column prop="ipAddress" label="IP" width="130" />
        <el-table-column prop="createTime" label="操作时间" width="160">
          <template slot-scope="scope">
            {{ scope.row.createTime }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无操作日志" />

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        style="margin-top: 16px; text-align: right"
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script>
import { getLogPage, getLogConfig, setLogConfig } from '@/api/log'
import { mapGetters } from 'vuex'

export default {
  name: 'OperationLog',
  computed: {
    ...mapGetters(['roles']),
    isAdmin() {
      return this.roles && this.roles.includes('ADMIN')
    }
  },
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 20,
      filters: {
        username: '',
        module: '',
        level: ''
      },
      dateRange: null,
      modules: [
        '用户管理', '角色管理', '收货管理', '入库管理',
        '处置管理', '转运管理', '检测管理', '数据字典'
      ],
      retentionLimit: 5000,
      newLimit: 5000,
      savingConfig: false
    }
  },
  mounted() {
    this.fetchData()
    this.fetchConfig()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          username: this.filters.username || undefined,
          module: this.filters.module || undefined,
          level: this.filters.level || undefined
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await getLogPage(params)
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        this.$message.error('获取日志列表失败')
      } finally {
        this.loading = false
      }
    },
    async fetchConfig() {
      try {
        const res = await getLogConfig()
        this.retentionLimit = res.data.retentionLimit
        this.newLimit = res.data.retentionLimit
      } catch (e) {
        // ignore
      }
    },
    async saveConfig() {
      this.savingConfig = true
      try {
        await setLogConfig({ retentionLimit: this.newLimit })
        this.retentionLimit = this.newLimit
        this.$message.success('保存成功')
      } catch (e) {
        this.$message.error('保存失败')
      } finally {
        this.savingConfig = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchData()
    },
    handleReset() {
      this.filters = { username: '', module: '', level: '' }
      this.dateRange = null
      this.currentPage = 1
      this.fetchData()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchData()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchData()
    }
  }
}
</script>
