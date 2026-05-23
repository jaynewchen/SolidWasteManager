<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>检测台账</span>
      </div>
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="检测计划管理" name="plan" />
        <el-tab-pane label="进厂物料检测管理" name="material" />
        <el-tab-pane label="处理过程检测管理" name="process" />
        <el-tab-pane label="产物质量检测管理" name="product" />
        <el-tab-pane label="台账查询与报表" name="report" />
      </el-tabs>

      <template v-if="activeTab !== 'report'">
        <el-form :inline="true" :model="queryParams" class="filter-bar">
          <el-form-item label="批次号">
            <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable style="width:160px" />
          </el-form-item>
          <el-form-item label="检测单号">
            <el-input v-model="queryParams.testingNo" placeholder="检测单号" clearable style="width:180px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
              <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width:260px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="toolbar">
          <el-button v-permission="'testing:add'" type="primary" icon="el-icon-plus" @click="handleAdd">新增{{ tabLabel }}</el-button>
        </div>

        <el-table v-loading="loading" :data="tableData" border stripe style="width:100%">
          <el-table-column v-for="col in currentColumns" :key="col.prop" v-bind="col" />
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="{row}">
              <el-button type="text" icon="el-icon-view" @click="handleView(row)">查看</el-button>
              <el-button v-permission="'testing:edit'" type="text" icon="el-icon-edit" @click="handleEdit(row)">编辑</el-button>
              <template v-if="isTestingTab && row.status === 3">
                <el-button v-permission="'testing:edit'" type="text" icon="el-icon-check" style="color:#67c23a" @click="handleReview(row)">复核</el-button>
              </template>
              <el-popconfirm title="确认删除该记录？" @confirm="handleDelete(row.id)">
                <el-button slot="reference" v-permission="'testing:delete'" type="text" icon="el-icon-delete" style="color:#f56c6c">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <span class="total-info">共 {{ total }} 条</span>
          <el-pagination
            background
            layout="sizes, prev, pager, next, jumper"
            :current-page="queryParams.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="queryParams.size"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </template>

      <template v-if="activeTab === 'report'">
        <el-form :inline="true" class="filter-bar">
          <el-form-item label="检测类型">
            <el-select v-model="statsFilter.testingType" placeholder="全部" clearable style="width:150px" @change="fetchStatistics">
              <el-option label="检测计划" :value="1" />
              <el-option label="进厂物料检测" :value="2" />
              <el-option label="处理过程检测" :value="3" />
              <el-option label="产物质量检测" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="statsDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width:260px"
              @change="fetchStatistics"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-refresh" @click="fetchStatistics">刷新</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="20" style="margin-bottom:20px">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">总检测数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color:#67c23a">{{ stats.qualifiedCount }}</div>
              <div class="stat-label">合格数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color:#f56c6c">{{ stats.unqualifiedCount }}</div>
              <div class="stat-label">不合格数</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color:#409eff">{{ stats.qualifyRate ? stats.qualifyRate.toFixed(1) + '%' : '-' }}</div>
              <div class="stat-label">合格率</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card header="按类型统计" style="margin-bottom:20px">
          <el-table :data="typeStatsData" border stripe>
            <el-table-column label="检测类型" prop="name" />
            <el-table-column label="数量" prop="count" />
          </el-table>
        </el-card>

        <el-card header="按状态统计">
          <el-table :data="statusStatsData" border stripe>
            <el-table-column label="状态" prop="name" />
            <el-table-column label="数量" prop="count" />
          </el-table>
        </el-card>
      </template>

      <el-dialog
        :title="dialogTitle"
        :visible.sync="dialogVisible"
        width="700px"
        :close-on-click-modal="false"
        @close="handleDialogClose"
      >
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
          <template v-if="activeTab === 'plan'">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="formData.planName" placeholder="请输入计划名称" />
            </el-form-item>
            <el-form-item label="计划日期" prop="plannedDate">
              <el-date-picker v-model="formData.plannedDate" type="date" placeholder="选择计划日期" value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
            <el-form-item label="执行人" prop="planExecutorId">
              <el-select v-model="formData.planExecutorId" placeholder="请选择执行人" style="width:100%">
                <el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="检测项目" prop="testingItem">
              <el-input v-model="formData.testingItem" placeholder="请输入检测项目" />
            </el-form-item>
            <el-form-item label="检测方法">
              <el-input v-model="formData.testingMethod" placeholder="请输入检测方法" />
            </el-form-item>
            <el-form-item label="检测标准">
              <el-input v-model="formData.testingStandard" placeholder="请输入检测标准" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="备注" />
            </el-form-item>
            <el-form-item v-if="isEdit" label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width:100%">
                <el-option label="待执行" :value="1" />
                <el-option label="执行中" :value="2" />
                <el-option label="已完成" :value="3" />
              </el-select>
            </el-form-item>
          </template>

          <template v-else>
            <el-form-item label="批次号" prop="batchNo">
              <el-select v-model="formData.batchNo" placeholder="请选择批次号" filterable style="width:100%" @change="onBatchChange">
                <el-option v-for="b in batches" :key="b.batchNo" :label="b.batchNo + ' - ' + b.producerName + ' ' + b.wasteCategoryName" :value="b.batchNo" />
              </el-select>
            </el-form-item>
            <el-form-item label="样品名称">
              <el-input v-model="formData.sampleName" placeholder="请输入样品名称" />
            </el-form-item>
            <el-form-item label="检测项目" prop="testingItem">
              <el-input v-model="formData.testingItem" placeholder="请输入检测项目" />
            </el-form-item>
            <el-form-item label="检测方法">
              <el-input v-model="formData.testingMethod" placeholder="请输入检测方法" />
            </el-form-item>
            <el-form-item label="标准值">
              <el-input v-model="formData.standardValue" placeholder="请输入标准值" />
            </el-form-item>
            <el-form-item label="检测值">
              <el-input v-model="formData.testingValue" placeholder="请输入检测值" />
            </el-form-item>
            <el-form-item label="检测结果">
              <el-input v-model="formData.testingResult" type="textarea" :rows="2" placeholder="请输入检测结果" />
            </el-form-item>
            <el-form-item label="检测员" prop="testerId">
              <el-select v-model="formData.testerId" placeholder="请选择检测员" style="width:100%">
                <el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="检测日期">
              <el-date-picker v-model="formData.testingDate" type="date" placeholder="选择检测日期" value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="备注" />
            </el-form-item>
            <el-form-item v-if="isEdit" label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width:100%">
                <el-option label="待检测" :value="1" />
                <el-option label="检测中" :value="2" />
                <el-option label="待复核" :value="3" />
                <el-option label="已完成" :value="4" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
        </div>
      </el-dialog>

      <el-dialog title="检测详情" :visible.sync="viewDialogVisible" width="700px">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="检测单号">{{ viewData.testingNo }}</el-descriptions-item>
          <el-descriptions-item label="检测类型">{{ viewData.testingTypeDesc }}</el-descriptions-item>
          <el-descriptions-item v-if="viewData.planName" label="计划名称">{{ viewData.planName }}</el-descriptions-item>
          <el-descriptions-item v-if="viewData.plannedDate" label="计划日期">{{ viewData.plannedDate }}</el-descriptions-item>
          <el-descriptions-item v-if="viewData.planExecutorName" label="执行人">{{ viewData.planExecutorName }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ viewData.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="产废单位">{{ viewData.producerName }}</el-descriptions-item>
          <el-descriptions-item label="废物类别">{{ viewData.wasteCategoryName }}</el-descriptions-item>
          <el-descriptions-item label="样品名称">{{ viewData.sampleName }}</el-descriptions-item>
          <el-descriptions-item label="检测项目">{{ viewData.testingItem }}</el-descriptions-item>
          <el-descriptions-item label="检测方法">{{ viewData.testingMethod }}</el-descriptions-item>
          <el-descriptions-item label="检测标准">{{ viewData.testingStandard }}</el-descriptions-item>
          <el-descriptions-item label="标准值">{{ viewData.standardValue }}</el-descriptions-item>
          <el-descriptions-item label="检测值">{{ viewData.testingValue }}</el-descriptions-item>
          <el-descriptions-item label="检测结果">{{ viewData.testingResult }}</el-descriptions-item>
          <el-descriptions-item label="检测员">{{ viewData.testerName }}</el-descriptions-item>
          <el-descriptions-item label="检测日期">{{ viewData.testingDate }}</el-descriptions-item>
          <el-descriptions-item label="是否合格">
            <el-tag v-if="viewData.isQualified === 1" type="success">合格</el-tag>
            <el-tag v-else-if="viewData.isQualified === 0" type="danger">不合格</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="复核人">{{ viewData.reviewerName }}</el-descriptions-item>
          <el-descriptions-item label="复核时间">{{ viewData.reviewTime }}</el-descriptions-item>
          <el-descriptions-item label="复核意见" :span="2">{{ viewData.reviewOpinion }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(viewData.status, viewData.testingType)">{{ viewData.statusDesc }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作人">{{ viewData.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        </el-descriptions>
      </el-dialog>

      <el-dialog title="复核检测" :visible.sync="reviewDialogVisible" width="500px" :close-on-click-modal="false">
        <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="100px">
          <el-form-item label="是否合格" prop="qualified">
            <el-radio-group v-model="reviewForm.qualified">
              <el-radio :label="true">合格</el-radio>
              <el-radio :label="false">不合格</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="复核意见" prop="opinion">
            <el-input v-model="reviewForm.opinion" type="textarea" :rows="3" placeholder="请输入复核意见" />
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="reviewDialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="reviewLoading" @click="handleReviewSubmit">确 定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import {
  getTestingList, getTestingDetail, createTesting, updateTesting, deleteTesting,
  reviewTesting, getAvailableBatches, getActiveUsers, getStatistics
} from '@/api/testing'

export default {
  name: 'Testing',
  data() {
    return {
      activeTab: 'plan',
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      dateRange: null,
      queryParams: { page: 1, size: 20, batchNo: '', testingNo: '', status: null },
      batches: [],
      users: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        testingType: 1, planName: '', plannedDate: '', planExecutorId: null,
        batchNo: '', sampleName: '', testingItem: '', testingMethod: '',
        testingStandard: '', standardValue: '', testingValue: '', testingResult: '',
        testerId: null, testingDate: '', remark: '', status: null
      },
      formRules: {},
      viewDialogVisible: false,
      viewData: {},
      reviewDialogVisible: false,
      reviewLoading: false,
      reviewForm: { qualified: true, opinion: '' },
      reviewRules: { opinion: [{ required: true, message: '请输入复核意见', trigger: 'blur' }] },
      reviewRow: null,
      stats: { totalCount: 0, qualifiedCount: 0, unqualifiedCount: 0, qualifyRate: 0, byType: {}, byStatus: {} },
      statsFilter: { testingType: null },
      statsDateRange: null
    }
  },
  computed: {
    tabLabel() {
      const map = { plan: '检测计划', material: '进厂物料检测', process: '处理过程检测', product: '产物质量检测' }
      return map[this.activeTab] || ''
    },
    isTestingTab() {
      return this.activeTab !== 'plan' && this.activeTab !== 'report'
    },
    testingType() {
      const map = { plan: 1, material: 2, process: 3, product: 4 }
      return map[this.activeTab] || 2
    },
    statusOptions() {
      if (this.activeTab === 'plan') {
        return [
          { label: '待执行', value: 1 }, { label: '执行中', value: 2 }, { label: '已完成', value: 3 }
        ]
      }
      return [
        { label: '待检测', value: 1 }, { label: '检测中', value: 2 },
        { label: '待复核', value: 3 }, { label: '已完成', value: 4 }
      ]
    },
    planColumns() {
      return [
        { prop: 'testingNo', label: '检测单号', width: 180 },
        { prop: 'planName', label: '计划名称', width: 160 },
        { prop: 'planExecutorName', label: '执行人', width: 100 },
        { prop: 'plannedDate', label: '计划日期', width: 120 },
        { prop: 'testingItem', label: '检测项目', width: 140, 'show-overflow-tooltip': true },
        { prop: 'testingMethod', label: '检测方法', width: 120, 'show-overflow-tooltip': true },
        { prop: 'statusDesc', label: '状态', width: 90 },
        { prop: 'operatorName', label: '操作人', width: 100 },
        { prop: 'createTime', label: '创建时间', width: 160 }
      ]
    },
    testingColumns() {
      return [
        { prop: 'testingNo', label: '检测单号', width: 180 },
        { prop: 'batchNo', label: '批次号', width: 160 },
        { prop: 'producerName', label: '产废单位', width: 120, 'show-overflow-tooltip': true },
        { prop: 'wasteCategoryName', label: '废物类别', width: 100 },
        { prop: 'sampleName', label: '样品名称', width: 120, 'show-overflow-tooltip': true },
        { prop: 'testingItem', label: '检测项目', width: 140, 'show-overflow-tooltip': true },
        { prop: 'testerName', label: '检测员', width: 90 },
        { prop: 'testingDate', label: '检测日期', width: 120 },
        { prop: 'isQualified', label: '是否合格', width: 90 },
        { prop: 'statusDesc', label: '状态', width: 90 },
        { prop: 'operatorName', label: '操作人', width: 100 },
        { prop: 'createTime', label: '创建时间', width: 160 }
      ]
    },
    currentColumns() {
      return this.activeTab === 'plan' ? this.planColumns : this.testingColumns
    },
    planFormRules() {
      return {
        planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
        plannedDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
        planExecutorId: [{ required: true, message: '请选择执行人', trigger: 'change' }]
      }
    },
    testingFormRules() {
      return {
        batchNo: [{ required: true, message: '请选择批次号', trigger: 'change' }],
        testingItem: [{ required: true, message: '请输入检测项目', trigger: 'blur' }],
        testerId: [{ required: true, message: '请选择检测员', trigger: 'change' }]
      }
    },
    typeStatsData() {
      const names = { 1: '检测计划', 2: '进厂物料检测', 3: '处理过程检测', 4: '产物质量检测' }
      return Object.entries(this.stats.byType || {}).map(([k, v]) => ({ name: names[k] || k, count: v }))
    },
    statusStatsData() {
      const names = { 1: '待检测/待执行', 2: '检测中/执行中', 3: '待复核/已完成', 4: '已完成' }
      return Object.entries(this.stats.byStatus || {}).map(([k, v]) => ({ name: names[k] || k, count: v }))
    }
  },
  watch: {
    activeTab: {
      handler() { this.formRules = this.activeTab === 'plan' ? this.planFormRules : this.testingFormRules },
      immediate: true
    }
  },
  created() {
    this.fetchList()
    this.fetchBatchesAndUsers()
  },
  methods: {
    statusTagType(status, type) {
      if (type === 1) return ['', 'info', 'warning', 'success'][status] || 'info'
      return ['', 'info', 'warning', '', 'success'][status] || 'info'
    },
    fetchList() {
      this.loading = true
      const params = { ...this.queryParams, testingType: this.testingType }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      } else {
        params.startDate = null; params.endDate = null
      }
      getTestingList(params).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      }).finally(() => { this.loading = false })
    },
    fetchBatchesAndUsers() {
      getAvailableBatches().then(res => { this.batches = res.data || [] })
      getActiveUsers().then(res => { this.users = res.data || [] })
    },
    fetchStatistics() {
      const params = { ...this.statsFilter }
      if (this.statsDateRange && this.statsDateRange.length === 2) {
        params.startDate = this.statsDateRange[0]
        params.endDate = this.statsDateRange[1]
      }
      getStatistics(params).then(res => { this.stats = res.data || {} })
    },
    handleTabChange() {
      if (this.activeTab === 'report') {
        this.fetchStatistics()
      } else {
        this.queryParams.page = 1
        this.dateRange = null
        this.queryParams.status = null
        this.queryParams.batchNo = ''
        this.queryParams.testingNo = ''
        this.fetchList()
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, size: 20, batchNo: '', testingNo: '', status: null }
      this.dateRange = null
      this.fetchList()
    },
    handleSizeChange(val) { this.queryParams.size = val; this.fetchList() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchList() },

    handleAdd() {
      this.dialogTitle = '新增' + this.tabLabel
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.fetchBatchesAndUsers()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑' + this.tabLabel
      this.isEdit = true
      this.editId = row.id
      this.fetchBatchesAndUsers()
      getTestingDetail(row.id).then(res => {
        const d = res.data
        this.formData = {
          testingType: d.testingType,
          planName: d.planName || '',
          plannedDate: d.plannedDate || '',
          planExecutorId: d.planExecutorId || null,
          batchNo: d.batchNo || '',
          sampleName: d.sampleName || '',
          testingItem: d.testingItem || '',
          testingMethod: d.testingMethod || '',
          testingStandard: d.testingStandard || '',
          standardValue: d.standardValue || '',
          testingValue: d.testingValue || '',
          testingResult: d.testingResult || '',
          testerId: d.testerId || null,
          testingDate: d.testingDate || '',
          remark: d.remark || '',
          status: d.status
        }
        this.dialogVisible = true
      })
    },
    handleView(row) {
      getTestingDetail(row.id).then(res => {
        this.viewData = res.data
        this.viewDialogVisible = true
      })
    },
    handleDelete(id) {
      deleteTesting(id).then(() => {
        this.$message.success('删除成功')
        this.fetchList()
      })
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = {
          ...this.formData,
          testingType: this.testingType
        }
        const action = this.isEdit
          ? updateTesting(this.editId, data)
          : createTesting(data)
        action.then(() => {
          this.$message.success(this.isEdit ? '编辑成功' : '新增成功')
          this.dialogVisible = false
          this.fetchList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDialogClose() {
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    resetForm() {
      this.formData = {
        testingType: this.testingType, planName: '', plannedDate: '', planExecutorId: null,
        batchNo: '', sampleName: '', testingItem: '', testingMethod: '',
        testingStandard: '', standardValue: '', testingValue: '', testingResult: '',
        testerId: null, testingDate: '', remark: '', status: null
      }
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleReview(row) {
      this.reviewRow = row
      this.reviewForm = { qualified: true, opinion: '' }
      this.reviewDialogVisible = true
    },
    handleReviewSubmit() {
      this.$refs.reviewFormRef.validate(valid => {
        if (!valid) return
        this.reviewLoading = true
        reviewTesting(this.reviewRow.id, this.reviewForm).then(() => {
          this.$message.success('复核完成')
          this.reviewDialogVisible = false
          this.fetchList()
        }).finally(() => { this.reviewLoading = false })
      })
    },
    onBatchChange(val) {
      const batch = this.batches.find(b => b.batchNo === val)
      if (batch) {
        this.formData.receivingId = batch.receiveRecordId
      }
    }
  }
}
</script>

<style scoped>
.filter-bar { margin-bottom: 10px; }
.toolbar { margin-bottom: 10px; }
.pagination-wrap { display: flex; align-items: center; justify-content: flex-end; margin-top: 15px; }
.total-info { margin-right: 15px; font-size: 13px; color: #606266; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 5px; }
</style>
