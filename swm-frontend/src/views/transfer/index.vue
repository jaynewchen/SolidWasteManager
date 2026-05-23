<template>
  <div class="transfer-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="转运单号">
          <el-input v-model="queryParams.transferNo" placeholder="转运单号" clearable />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待装车" :value="1" />
            <el-option label="运输中" :value="2" />
            <el-option label="已到达" :value="3" />
            <el-option label="已卸车" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="转运时间">
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
      <el-button type="primary" icon="el-icon-plus" v-permission="'transfer:add'" @click="handleAdd">新增转运</el-button>
      <el-button icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- Table -->
    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%" size="medium">
      <el-table-column prop="transferNo" label="转运单号" min-width="150">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">{{ scope.row.transferNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="batchNo" label="批次号" min-width="160" />
      <el-table-column prop="plateNumber" label="车牌号" min-width="100" />
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
      <el-table-column prop="fromLocation" label="起运地" min-width="100" show-overflow-tooltip />
      <el-table-column prop="toLocation" label="目的地" min-width="100" show-overflow-tooltip />
      <el-table-column prop="transferWeight" label="转运重量(吨)" min-width="120" align="right">
        <template slot-scope="scope">
          <span style="font-weight: bold">{{ scope.row.transferWeight }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="driverName" label="驾驶员" min-width="80" />
      <el-table-column prop="transferTime" label="转运时间" min-width="140" />
      <el-table-column prop="statusDesc" label="状态" min-width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">
            {{ scope.row.statusDesc }}
          </el-tag>
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
          <el-button size="mini" type="primary" v-permission="'transfer:edit'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除该记录？" v-permission="'transfer:delete'" @confirm="handleDelete(scope.row)">
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" :close-on-click-modal="false" @close="handleDialogClose">
      <el-form ref="transferForm" :model="formData" :rules="formRules" label-width="110px" size="small">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="关联批次号" prop="batchNo">
          <el-select v-model="formData.batchNo" placeholder="请选择批次号" filterable clearable style="width:100%">
            <el-option
              v-for="item in batchList"
              :key="item.batchNo"
              :label="item.batchNo + ' - ' + item.producerName + ' ' + item.wasteCategoryName + ' (' + item.netWeight + '吨)'"
              :value="item.batchNo"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="车牌号" prop="plateNumber">
              <el-input v-model="formData.plateNumber" placeholder="请输入车牌号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="车辆备案号">
              <el-input v-model="formData.vehicleRegNo" placeholder="请输入备案号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="驾驶员" prop="driverId">
              <el-select v-model="formData.driverId" placeholder="请选择驾驶员" clearable style="width:100%">
                <el-option v-for="u in userList" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="计划路线">
          <el-input v-model="formData.plannedRoute" placeholder="请输入计划路线" />
        </el-form-item>
        <el-divider content-position="left">运输信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起运地" prop="fromLocation">
              <el-input v-model="formData.fromLocation" placeholder="请输入起运地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的地" prop="toLocation">
              <el-input v-model="formData.toLocation" placeholder="请输入目的地" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="转运时间" prop="transferTime">
              <el-date-picker v-model="formData.transferTime" type="datetime" placeholder="选择转运时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转运重量(吨)" prop="transferWeight">
              <el-input-number v-model="formData.transferWeight" :min="0.01" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">装车信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="装车时间">
              <el-date-picker v-model="formData.loadingTime" type="datetime" placeholder="选择装车时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发运确认人">
              <el-select v-model="formData.shippingConfirmerId" placeholder="请选择" clearable style="width:100%">
                <el-option v-for="u in userList" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">到达信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="到达时间">
              <el-date-picker v-model="formData.arrivalTime" type="datetime" placeholder="选择到达时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="到达毛重(吨)">
              <el-input-number v-model="formData.arrivalGrossWeight" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="到达确认人">
              <el-select v-model="formData.arrivalConfirmerId" placeholder="请选择" clearable style="width:100%">
                <el-option v-for="u in userList" :key="u.id" :label="u.realName" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">完成信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="卸车时间">
              <el-date-picker v-model="formData.unloadingTime" type="datetime" placeholder="选择卸车时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="formData.status" :disabled="!isEdit" style="width:100%">
                <el-option label="待装车" :value="1" />
                <el-option label="运输中" :value="2" />
                <el-option label="已到达" :value="3" />
                <el-option label="已卸车" :value="4" />
              </el-select>
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
    <el-dialog title="转运详情" :visible.sync="viewDialogVisible" width="750px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="转运单号" :span="2"><strong>{{ viewData.transferNo }}</strong></el-descriptions-item>
        <el-descriptions-item label="关联批次号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产废单位">{{ viewData.producerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="废物类别">{{ viewData.wasteCategoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接收净重(吨)">{{ viewData.netWeight || '-' }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ viewData.plateNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="车辆备案号">{{ viewData.vehicleRegNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="驾驶员">{{ viewData.driverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划路线">{{ viewData.plannedRoute || '-' }}</el-descriptions-item>
        <el-descriptions-item label="起运地">{{ viewData.fromLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目的地">{{ viewData.toLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="转运时间">{{ viewData.transferTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="转运重量(吨)"><strong>{{ viewData.transferWeight }}</strong></el-descriptions-item>
        <el-descriptions-item label="装车时间">{{ viewData.loadingTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发运确认人">{{ viewData.shippingConfirmerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="到达时间">{{ viewData.arrivalTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="到达毛重(吨)">{{ viewData.arrivalGrossWeight != null ? viewData.arrivalGrossWeight : '-' }}</el-descriptions-item>
        <el-descriptions-item label="到达确认人">{{ viewData.arrivalConfirmerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="卸车时间">{{ viewData.unloadingTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(viewData.status)" size="small">{{ viewData.statusDesc }}</el-tag>
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
  getTransferList, getTransferDetail, createTransfer,
  updateTransfer, deleteTransfer, getAvailableBatches, getActiveUsers
} from '@/api/transfer'

export default {
  name: 'Transfer',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      dateRange: [],
      queryParams: {
        transferNo: '',
        batchNo: '',
        status: null,
        page: 1,
        size: 20
      },
      batchList: [],
      userList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        batchNo: '',
        plateNumber: '',
        vehicleRegNo: '',
        driverId: null,
        plannedRoute: '',
        fromLocation: '',
        toLocation: '',
        transferTime: '',
        transferWeight: 0,
        loadingTime: '',
        shippingConfirmerId: null,
        arrivalTime: '',
        arrivalGrossWeight: null,
        arrivalConfirmerId: null,
        unloadingTime: '',
        remark: '',
        status: 1
      },
      formRules: {
        batchNo: [{ required: true, message: '请选择批次号', trigger: 'change' }],
        plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
        driverId: [{ required: true, message: '请选择驾驶员', trigger: 'change' }],
        fromLocation: [{ required: true, message: '请输入起运地', trigger: 'blur' }],
        toLocation: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
        transferWeight: [{ required: true, message: '请输入转运重量', trigger: 'blur' }]
      },
      viewDialogVisible: false,
      viewData: {}
    }
  },
  created() {
    this.fetchUserList()
    this.fetchList()
  },
  methods: {
    statusTagType(status) {
      const map = { 1: 'info', 2: 'warning', 3: '', 4: 'success' }
      return map[status] || 'info'
    },
    fetchUserList() {
      getActiveUsers().then(res => {
        this.userList = res.data || []
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }
      getTransferList(params).then(res => {
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
      this.queryParams = { transferNo: '', batchNo: '', status: null, page: 1, size: 20 }
      this.fetchList()
    },
    handleAdd() {
      this.dialogTitle = '新增转运'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.fetchBatchList()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑转运'
      this.isEdit = true
      this.editId = row.id
      this.fetchBatchList()
      getTransferDetail(row.id).then(res => {
        const d = res.data
        this.formData = {
          batchNo: d.batchNo,
          plateNumber: d.plateNumber || '',
          vehicleRegNo: d.vehicleRegNo || '',
          driverId: d.driverId,
          plannedRoute: d.plannedRoute || '',
          fromLocation: d.fromLocation || '',
          toLocation: d.toLocation || '',
          transferTime: d.transferTime || '',
          transferWeight: d.transferWeight,
          loadingTime: d.loadingTime || '',
          shippingConfirmerId: d.shippingConfirmerId,
          arrivalTime: d.arrivalTime || '',
          arrivalGrossWeight: d.arrivalGrossWeight,
          arrivalConfirmerId: d.arrivalConfirmerId,
          unloadingTime: d.unloadingTime || '',
          remark: d.remark || '',
          status: d.status
        }
        this.dialogVisible = true
      }).catch(() => {})
    },
    handleView(row) {
      getTransferDetail(row.id).then(res => {
        this.viewData = res.data
        this.viewDialogVisible = true
      }).catch(() => {})
    },
    handleDelete(row) {
      deleteTransfer(row.id).then(() => {
        this.$message.success('删除成功')
        this.fetchList()
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.transferForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.isEdit
          ? updateTransfer(this.editId, this.formData)
          : createTransfer(this.formData)
        api.then(() => {
          this.$message.success(this.isEdit ? '编辑成功' : '新增成功')
          this.dialogVisible = false
          this.fetchList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDialogClose() {
      this.$refs.transferForm && this.$refs.transferForm.resetFields()
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
        plateNumber: '',
        vehicleRegNo: '',
        driverId: null,
        plannedRoute: '',
        fromLocation: '',
        toLocation: '',
        transferTime: '',
        transferWeight: 0,
        loadingTime: '',
        shippingConfirmerId: null,
        arrivalTime: '',
        arrivalGrossWeight: null,
        arrivalConfirmerId: null,
        unloadingTime: '',
        remark: '',
        status: 1
      }
      this.$nextTick(() => {
        this.$refs.transferForm && this.$refs.transferForm.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
.transfer-container { padding: 0; }
</style>
