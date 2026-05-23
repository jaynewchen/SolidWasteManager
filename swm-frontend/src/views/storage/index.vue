<template>
  <div class="storage-container">
    <!-- Statistics Cards -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-number" style="color: #67C23A">{{ statsData.inboundTotal }}</div>
            <div class="stat-label">总入库量(吨)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-number" style="color: #E6A23C">{{ statsData.outboundTotal }}</div>
            <div class="stat-label">总出库量(吨)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-number" style="color: #409EFF">{{ statsData.totalInventory }}</div>
            <div class="stat-label">当前库存(吨)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-number" style="color: #909399">{{ statsData.activeAreas }}</div>
            <div class="stat-label">活跃库区数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Inventory by Area -->
    <el-card class="section-card">
      <div slot="header"><i class="el-icon-data-analysis"></i> 库区库存概览</div>
      <el-table :data="areaInventory" border size="small" empty-text="暂无库存数据">
        <el-table-column prop="storageArea" label="库区" min-width="120" />
        <el-table-column prop="inboundTotal" label="入库量(吨)" min-width="110" align="right" />
        <el-table-column prop="outboundTotal" label="出库量(吨)" min-width="110" align="right" />
        <el-table-column prop="totalInventory" label="当前库存(吨)" min-width="120" align="right">
          <template slot-scope="scope">
            <strong :style="{ color: scope.row.totalInventory > 0 ? '#409EFF' : '#909399' }">
              {{ scope.row.totalInventory }}
            </strong>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable />
        </el-form-item>
        <el-form-item label="库区">
          <el-input v-model="queryParams.storageArea" placeholder="库区" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.storageType" placeholder="请选择" clearable>
            <el-option label="入库" :value="1" />
            <el-option label="出库" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Action Bar -->
    <div class="table-operations">
      <el-button type="primary" icon="el-icon-plus" v-permission="'storage:add'" @click="handleAdd(1)">入库</el-button>
      <el-button type="warning" icon="el-icon-minus" v-permission="'storage:add'" @click="handleAdd(2)">出库</el-button>
      <el-button icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- Table -->
    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%" size="medium">
      <el-table-column prop="batchNo" label="批次号" min-width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">{{ scope.row.batchNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="storageArea" label="存放库区" min-width="110" show-overflow-tooltip />
      <el-table-column prop="storageTypeDesc" label="类型" min-width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.storageType === 1 ? 'success' : 'warning'" size="small">
            {{ scope.row.storageTypeDesc }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="changeWeight" label="重量(吨)" min-width="100" align="right">
        <template slot-scope="scope">
          <span style="font-weight: bold">{{ scope.row.changeWeight }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="operationDate" label="操作日期" min-width="110" />
      <el-table-column prop="destination" label="出库去向" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.destination || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="wasteCategoryName" label="废物类别" min-width="100" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.wasteCategoryName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="producerName" label="产废单位" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.producerName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" min-width="80" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.remark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="primary" v-permission="'storage:edit'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除该记录？" v-permission="'storage:delete'" @confirm="handleDelete(scope.row)">
            <el-button slot="reference" size="mini" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      style="margin-top: 16px; text-align: right"
      :current-page="queryParams.page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="hs => { queryParams.size = hs; fetchList() }"
      @current-change="hc => { queryParams.page = hc; fetchList() }"
    />

    <!-- Add/Edit Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px" :close-on-click-modal="false" @close="handleDialogClose">
      <el-form ref="storageForm" :model="formData" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="批次号" prop="batchNo">
          <el-select v-model="formData.batchNo" placeholder="请选择批次号" filterable clearable style="width:100%" @change="onBatchChange">
            <el-option
              v-for="item in batchList"
              :key="item.batchNo"
              :label="item.batchNo + ' - ' + item.producerName + ' ' + item.wasteCategoryName + ' (' + item.netWeight + '吨)'"
              :value="item.batchNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="formData.storageType" disabled>
            <el-radio :label="1">入库</el-radio>
            <el-radio :label="2">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="存放库区" prop="storageArea">
          <el-input v-model="formData.storageArea" placeholder="请输入存放库区名称" />
        </el-form-item>
        <el-form-item label="重量(吨)" prop="changeWeight">
          <el-input-number v-model="formData.changeWeight" :min="0.01" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="操作日期">
          <el-date-picker v-model="formData.operationDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="formData.storageType === 2" label="出库去向" prop="destination">
          <el-input v-model="formData.destination" placeholder="请输入出库去向" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- View Dialog -->
    <el-dialog title="贮存详情" :visible.sync="viewDialogVisible" width="700px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="批次号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产废单位">{{ viewData.producerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="存放库区">{{ viewData.storageArea }}</el-descriptions-item>
        <el-descriptions-item label="废物类别">{{ viewData.wasteCategoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="viewData.storageType === 1 ? 'success' : 'warning'" size="small">{{ viewData.storageTypeDesc }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收净重(吨)">{{ viewData.netWeight || '-' }}</el-descriptions-item>
        <el-descriptions-item label="重量(吨)">
          <strong>{{ viewData.changeWeight }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="操作日期">{{ viewData.operationDate }}</el-descriptions-item>
        <el-descriptions-item label="出库去向">{{ viewData.destination || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ viewData.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="viewDialogVisible = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getStorageList, getStorageDetail, createStorage, updateStorage, deleteStorage,
  getInventoryByArea, getDistributionByCategory, getAvailableBatches
} from '@/api/storage'

export default {
  name: 'Storage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      dateRange: [],
      queryParams: {
        batchNo: '',
        storageArea: '',
        storageType: null,
        page: 1,
        size: 20
      },
      statsData: {
        inboundTotal: '0.00',
        outboundTotal: '0.00',
        totalInventory: '0.00',
        activeAreas: 0
      },
      areaInventory: [],
      batchList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        batchNo: '',
        storageArea: '',
        storageType: 1,
        changeWeight: 0,
        operationDate: '',
        destination: '',
        receiveRecordId: null,
        remark: ''
      },
      formRules: {
        batchNo: [{ required: true, message: '请选择批次号', trigger: 'change' }],
        storageArea: [{ required: true, message: '请输入存放库区', trigger: 'blur' }],
        changeWeight: [{ required: true, message: '请输入重量', trigger: 'blur' }],
        destination: [{ required: true, message: '请输入出库去向', trigger: 'blur' }]
      },
      viewDialogVisible: false,
      viewData: {}
    }
  },
  created() {
    this.fetchStats()
    this.fetchList()
  },
  methods: {
    fetchStats() {
      getInventoryByArea().then(res => {
        const data = res.data || []
        this.areaInventory = data
        let inbound = 0; let outbound = 0; let inv = 0
        data.forEach(d => {
          inbound += Number(d.inboundTotal) || 0
          outbound += Number(d.outboundTotal) || 0
          inv += Number(d.totalInventory) || 0
        })
        this.statsData.inboundTotal = inbound.toFixed(2)
        this.statsData.outboundTotal = outbound.toFixed(2)
        this.statsData.totalInventory = inv.toFixed(2)
        this.statsData.activeAreas = data.filter(d => Number(d.totalInventory) > 0).length
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }
      getStorageList(params).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
      this.fetchStats()
    },
    handleReset() {
      this.dateRange = []
      this.queryParams = { batchNo: '', storageArea: '', storageType: null, page: 1, size: 20 }
      this.fetchList()
    },
    handleAdd(storageType) {
      this.dialogTitle = storageType === 1 ? '入库' : '出库'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.formData.storageType = storageType
      this.fetchBatchList()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑贮存'
      this.isEdit = true
      this.editId = row.id
      this.fetchBatchList()
      getStorageDetail(row.id).then(res => {
        const d = res.data
        this.formData = {
          batchNo: d.batchNo,
          storageArea: d.storageArea,
          storageType: d.storageType,
          changeWeight: d.changeWeight,
          operationDate: d.operationDate,
          destination: d.destination || '',
          receiveRecordId: d.receiveRecordId,
          remark: d.remark || ''
        }
        this.dialogVisible = true
      }).catch(() => {})
    },
    handleView(row) {
      getStorageDetail(row.id).then(res => {
        this.viewData = res.data
        this.viewDialogVisible = true
      }).catch(() => {})
    },
    handleDelete(row) {
      deleteStorage(row.id).then(() => {
        this.$message.success('删除成功')
        this.fetchList()
        this.fetchStats()
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.storageForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.isEdit
          ? updateStorage(this.editId, this.formData)
          : createStorage(this.formData)
        api.then(() => {
          this.$message.success(this.isEdit ? '编辑成功' : '操作成功')
          this.dialogVisible = false
          this.fetchList()
          this.fetchStats()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDialogClose() {
      this.$refs.storageForm && this.$refs.storageForm.resetFields()
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    fetchBatchList() {
      getAvailableBatches().then(res => {
        this.batchList = res.data || []
      }).catch(() => {})
    },
    onBatchChange(val) {
      if (val) {
        const selected = this.batchList.find(b => b.batchNo === val)
        this.formData.receiveRecordId = selected ? selected.receiveRecordId : null
      } else {
        this.formData.receiveRecordId = null
      }
    },
    resetForm() {
      this.formData = {
        batchNo: '',
        storageArea: '',
        storageType: 1,
        changeWeight: 0,
        operationDate: '',
        destination: '',
        receiveRecordId: null,
        remark: ''
      }
      this.$nextTick(() => {
        this.$refs.storageForm && this.$refs.storageForm.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
.storage-container { padding: 0; }
.stats-row { margin-bottom: 16px; }
.stat-card { text-align: center; padding: 8px 0; }
.stat-number { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 13px; color: #909399; margin-top: 8px; }
.section-card { margin-bottom: 16px; }
</style>
