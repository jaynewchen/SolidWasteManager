<template>
  <div class="treatment-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable />
        </el-form-item>
        <el-form-item label="处置工艺">
          <el-select v-model="queryParams.processId" placeholder="请选择" clearable>
            <el-option v-for="p in processList" :key="p.id" :label="p.processName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置日期">
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
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Action Bar -->
    <div class="table-operations">
      <el-button type="primary" icon="el-icon-plus" v-permission="'treatment:add'" @click="handleAdd">新增处置</el-button>
      <el-button icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- Table -->
    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%" size="medium">
      <el-table-column prop="batchNo" label="批次号" min-width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">{{ scope.row.batchNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="producerName" label="产废单位" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.producerName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="wasteCategoryName" label="废物类别" min-width="100" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.wasteCategoryName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="netWeight" label="接收净重(吨)" min-width="110" align="right">
        <template slot-scope="scope">
          <span>{{ scope.row.netWeight || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="processName" label="处置工艺" min-width="110" show-overflow-tooltip />
      <el-table-column prop="treatmentDate" label="处置日期" min-width="110" />
      <el-table-column prop="inputWeight" label="入炉重量(吨)" min-width="120" align="right">
        <template slot-scope="scope">
          <span style="font-weight: bold">{{ scope.row.inputWeight }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="outputWeight" label="产出重量(吨)" min-width="120" align="right">
        <template slot-scope="scope">
          <span style="font-weight: bold">{{ scope.row.outputWeight }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="treatmentLoss" label="处置损耗(吨)" min-width="120" align="right">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.treatmentLoss >= 0 ? '#E6A23C' : '#67C23A' }">{{ scope.row.treatmentLoss }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="temperature" label="温度(℃)" min-width="90" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.temperature != null ? scope.row.temperature : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="equipmentName" label="设备" min-width="110" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.equipmentName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" min-width="80" />
      <el-table-column prop="statusDesc" label="状态" min-width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'warning' : 'success'" size="small">
            {{ scope.row.statusDesc }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.remark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="primary" v-permission="'treatment:edit'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除该记录？" v-permission="'treatment:delete'" @confirm="handleDelete(scope.row)">
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
      <el-form ref="treatmentForm" :model="formData" :rules="formRules" label-width="110px" size="small">
        <el-form-item label="批次号" prop="batchNo">
          <el-select v-model="formData.batchNo" placeholder="请选择批次号" filterable clearable style="width:100%">
            <el-option
              v-for="item in batchList"
              :key="item.batchNo"
              :label="item.batchNo + ' - ' + item.producerName + ' ' + item.wasteCategoryName + ' (' + item.netWeight + '吨)'"
              :value="item.batchNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处置工艺" prop="processId">
          <el-select v-model="formData.processId" placeholder="请选择处置工艺" clearable style="width:100%">
            <el-option
              v-for="item in processList"
              :key="item.id"
              :label="item.processName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处置日期">
          <el-date-picker v-model="formData.treatmentDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入炉重量(吨)" prop="inputWeight">
              <el-input-number v-model="formData.inputWeight" :min="0.01" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产出重量(吨)" prop="outputWeight">
              <el-input-number v-model="formData.outputWeight" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="温度(℃)">
              <el-input-number v-model="formData.temperature" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="formData.equipmentName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
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
    <el-dialog title="处置详情" :visible.sync="viewDialogVisible" width="700px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="批次号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产废单位">{{ viewData.producerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="废物类别">{{ viewData.wasteCategoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接收净重(吨)">{{ viewData.netWeight || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处置工艺">{{ viewData.processName }}</el-descriptions-item>
        <el-descriptions-item label="处置日期">{{ viewData.treatmentDate }}</el-descriptions-item>
        <el-descriptions-item label="入炉重量(吨)">
          <strong>{{ viewData.inputWeight }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="产出重量(吨)">
          <strong>{{ viewData.outputWeight }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="处置损耗(吨)">
          <span :style="{ color: viewData.treatmentLoss >= 0 ? '#E6A23C' : '#67C23A', fontWeight: 'bold' }">{{ viewData.treatmentLoss }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="温度(℃)">{{ viewData.temperature != null ? viewData.temperature : '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ viewData.equipmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ viewData.startTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ viewData.endTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'warning' : 'success'" size="small">{{ viewData.statusDesc }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ viewData.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="viewDialogVisible = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getTreatmentList, getTreatmentDetail, createTreatment,
  updateTreatment, deleteTreatment, getAvailableBatches
} from '@/api/treatment'
import { getDictList } from '@/api/dict'

export default {
  name: 'Treatment',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      dateRange: [],
      queryParams: {
        batchNo: '',
        processId: null,
        status: null,
        page: 1,
        size: 20
      },
      processList: [],
      batchList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        batchNo: '',
        processId: null,
        treatmentDate: '',
        inputWeight: 0,
        outputWeight: 0,
        temperature: null,
        equipmentName: '',
        startTime: '',
        endTime: '',
        remark: ''
      },
      formRules: {
        batchNo: [{ required: true, message: '请选择批次号', trigger: 'change' }],
        processId: [{ required: true, message: '请选择处置工艺', trigger: 'change' }],
        inputWeight: [{ required: true, message: '请输入入炉重量', trigger: 'blur' }],
        outputWeight: [{ required: true, message: '请输入产出重量', trigger: 'blur' }]
      },
      viewDialogVisible: false,
      viewData: {}
    }
  },
  created() {
    this.fetchProcessList()
    this.fetchList()
  },
  methods: {
    fetchProcessList() {
      getDictList('processes').then(res => {
        this.processList = res.data || []
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }
      getTreatmentList(params).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.dateRange = []
      this.queryParams = { batchNo: '', processId: null, status: null, page: 1, size: 20 }
      this.fetchList()
    },
    handleAdd() {
      this.dialogTitle = '新增处置'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.fetchBatchList()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑处置'
      this.isEdit = true
      this.editId = row.id
      this.fetchBatchList()
      getTreatmentDetail(row.id).then(res => {
        const d = res.data
        this.formData = {
          batchNo: d.batchNo,
          processId: d.processId,
          treatmentDate: d.treatmentDate,
          inputWeight: d.inputWeight,
          outputWeight: d.outputWeight,
          temperature: d.temperature,
          equipmentName: d.equipmentName || '',
          startTime: d.startTime || '',
          endTime: d.endTime || '',
          remark: d.remark || ''
        }
        this.dialogVisible = true
      }).catch(() => {})
    },
    handleView(row) {
      getTreatmentDetail(row.id).then(res => {
        this.viewData = res.data
        this.viewDialogVisible = true
      }).catch(() => {})
    },
    handleDelete(row) {
      deleteTreatment(row.id).then(() => {
        this.$message.success('删除成功')
        this.fetchList()
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.treatmentForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.isEdit
          ? updateTreatment(this.editId, this.formData)
          : createTreatment(this.formData)
        api.then(() => {
          this.$message.success(this.isEdit ? '编辑成功' : '新增成功')
          this.dialogVisible = false
          this.fetchList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDialogClose() {
      this.$refs.treatmentForm && this.$refs.treatmentForm.resetFields()
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    fetchBatchList() {
      getAvailableBatches().then(res => {
        this.batchList = res.data || []
      }).catch(() => {})
    },
    resetForm() {
      this.formData = {
        batchNo: '',
        processId: null,
        treatmentDate: '',
        inputWeight: 0,
        outputWeight: 0,
        temperature: null,
        equipmentName: '',
        startTime: '',
        endTime: '',
        remark: ''
      }
      this.$nextTick(() => {
        this.$refs.treatmentForm && this.$refs.treatmentForm.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
.treatment-container { padding: 0; }
</style>
