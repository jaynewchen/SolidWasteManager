<template>
  <div class="receiving-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable />
        </el-form-item>
        <el-form-item label="产废单位">
          <el-select v-model="queryParams.producerId" placeholder="请选择" clearable filterable>
            <el-option v-for="item in producerList" :key="item.id" :label="item.producerName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收日期">
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
            <el-option label="已接收" :value="1" />
            <el-option label="处置中" :value="2" />
            <el-option label="已处置" :value="3" />
            <el-option label="已完结" :value="4" />
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
      <el-button type="primary" icon="el-icon-plus" v-permission="'receiving:add'" @click="handleAdd">新增接收</el-button>
      <el-button icon="el-icon-download" v-permission="'receiving:export'" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- Table -->
    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%" size="medium">
      <el-table-column prop="batchNo" label="批次号" min-width="160">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">{{ scope.row.batchNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="producerName" label="产废单位" min-width="120" show-overflow-tooltip />
      <el-table-column prop="workshopName" label="车间" min-width="100" show-overflow-tooltip />
      <el-table-column prop="mineSourceName" label="矿源" min-width="100" show-overflow-tooltip />
      <el-table-column prop="wasteCategoryName" label="废物类别" min-width="100" show-overflow-tooltip />
      <el-table-column prop="plateNumber" label="车牌号" min-width="110" />
      <el-table-column prop="driverName" label="司机" min-width="80" />
      <el-table-column prop="grossWeight" label="毛重(吨)" min-width="100" align="right" />
      <el-table-column prop="tareWeight" label="皮重(吨)" min-width="100" align="right" />
      <el-table-column prop="netWeight" label="净重(吨)" min-width="100" align="right">
        <template slot-scope="scope">
          <span style="font-weight: bold">{{ scope.row.netWeight }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="receiveDate" label="接收日期" min-width="110" />
      <el-table-column prop="status" label="状态" min-width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusType(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="primary" v-permission="'receiving:edit'" :disabled="scope.row.status !== 1" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除该记录？" v-permission="'receiving:delete'" @confirm="handleDelete(scope.row)">
            <el-button slot="reference" size="mini" type="danger" :disabled="scope.row.status >= 2">删除</el-button>
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
      <el-form ref="receivingForm" :model="formData" :rules="formRules" label-width="110px" size="small">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产废单位" prop="producerId">
              <el-select v-model="formData.producerId" placeholder="请选择" clearable filterable style="width:100%">
                <el-option v-for="item in producerList" :key="item.id" :label="item.producerName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车间" prop="workshopId">
              <el-select v-model="formData.workshopId" placeholder="请选择" clearable filterable style="width:100%">
                <el-option v-for="item in workshopList" :key="item.id" :label="item.workshopName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="矿源" prop="mineSourceId">
              <el-select v-model="formData.mineSourceId" placeholder="请选择" clearable filterable style="width:100%">
                <el-option v-for="item in mineList" :key="item.id" :label="item.mineName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="危废类别" prop="wasteCategoryId">
              <el-select v-model="formData.wasteCategoryId" placeholder="请选择" clearable filterable style="width:100%">
                <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="plateNumber">
              <el-input v-model="formData.plateNumber" placeholder="请输入车牌号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机" prop="driverId">
              <el-select v-model="formData.driverId" placeholder="请选择司机" clearable filterable style="width:100%">
                <el-option v-for="item in driverList" :key="item.id" :label="item.realName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="毛重(吨)" prop="grossWeight">
              <el-input-number v-model="formData.grossWeight" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="皮重(吨)" prop="tareWeight">
              <el-input-number v-model="formData.tareWeight" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="净重(吨)">
              <el-input-number :value="netWeight" disabled style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接收日期" prop="receiveDate">
              <el-date-picker v-model="formData.receiveDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接收时间">
              <el-time-picker v-model="formData.receiveTime" placeholder="选择时间" value-format="HH:mm:ss" style="width:100%" />
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
    <el-dialog title="接收详情" :visible.sync="viewDialogVisible" width="700px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="批次号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产废单位">{{ viewData.producerName }}</el-descriptions-item>
        <el-descriptions-item label="车间">{{ viewData.workshopName }}</el-descriptions-item>
        <el-descriptions-item label="矿源">{{ viewData.mineSourceName }}</el-descriptions-item>
        <el-descriptions-item label="危废类别">{{ viewData.wasteCategoryName }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ viewData.plateNumber }}</el-descriptions-item>
        <el-descriptions-item label="司机">{{ viewData.driverName }}</el-descriptions-item>
        <el-descriptions-item label="接收人">{{ viewData.receiveUserName }}</el-descriptions-item>
        <el-descriptions-item label="毛重(吨)">{{ viewData.grossWeight }}</el-descriptions-item>
        <el-descriptions-item label="皮重(吨)">{{ viewData.tareWeight }}</el-descriptions-item>
        <el-descriptions-item label="净重(吨)" :span="2">
          <strong>{{ viewData.netWeight }}</strong>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(viewData.status)" size="small">{{ statusText(viewData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收日期">{{ viewData.receiveDate }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="viewDialogVisible = false">关 闭</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { getReceivingList, getReceivingDetail, createReceiving, updateReceiving, deleteReceiving } from '@/api/receiving'
import { getDictList } from '@/api/dict'
import { getUserList } from '@/api/system'

export default {
  name: 'Receiving',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      dateRange: [],
      queryParams: {
        batchNo: '',
        producerId: null,
        status: null,
        page: 1,
        size: 20
      },
      producerList: [],
      workshopList: [],
      mineList: [],
      categoryList: [],
      driverList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        producerId: null,
        workshopId: null,
        mineSourceId: null,
        wasteCategoryId: null,
        plateNumber: '',
        driverId: null,
        grossWeight: 0,
        tareWeight: 0,
        receiveDate: '',
        receiveTime: '',
        remark: ''
      },
      formRules: {
        producerId: [{ required: true, message: '请选择产废单位', trigger: 'change' }],
        workshopId: [{ required: true, message: '请选择车间', trigger: 'change' }],
        mineSourceId: [{ required: true, message: '请选择矿源', trigger: 'change' }],
        wasteCategoryId: [{ required: true, message: '请选择危废类别', trigger: 'change' }],
        plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
        grossWeight: [{ required: true, message: '请输入毛重', trigger: 'blur' }],
        tareWeight: [{ required: true, message: '请输入皮重', trigger: 'blur' }],
        receiveDate: [{ required: true, message: '请选择接收日期', trigger: 'change' }]
      },
      viewDialogVisible: false,
      viewData: {}
    }
  },
  computed: {
    netWeight() {
      const g = Number(this.formData.grossWeight) || 0
      const t = Number(this.formData.tareWeight) || 0
      return (g - t).toFixed(2)
    }
  },
  created() {
    this.fetchDictData()
    this.fetchList()
  },
  methods: {
    statusType(status) {
      const map = { 1: '', 2: 'warning', 3: 'success', 4: 'info' }
      return map[status] || 'info'
    },
    statusText(status) {
      const map = { 1: '已接收', 2: '处置中', 3: '已处置', 4: '已完结' }
      return map[status] || '未知'
    },
    fetchDictData() {
      getDictList('producers').then(res => { this.producerList = res.data || [] }).catch(() => {})
      getDictList('workshops').then(res => { this.workshopList = res.data || [] }).catch(() => {})
      getDictList('mines').then(res => { this.mineList = res.data || [] }).catch(() => {})
      getDictList('categories').then(res => { this.categoryList = res.data || [] }).catch(() => {})
      // Drivers are users with DRIVER role - fetch from user list
      getUserList({ page: 1, size: 100 }).then(res => {
        this.driverList = (res.data && res.data.records) ? res.data.records : []
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.startDate = this.dateRange[0]
        params.endDate = this.dateRange[1]
      }
      getReceivingList(params).then(res => {
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
      this.queryParams = { batchNo: '', producerId: null, status: null, page: 1, size: 20 }
      this.fetchList()
    },
    handleAdd() {
      this.dialogTitle = '新增接收'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑接收'
      this.isEdit = true
      this.editId = row.id
      getReceivingDetail(row.id).then(res => {
        const d = res.data
        this.formData = {
          producerId: d.producerId,
          workshopId: d.workshopId,
          mineSourceId: d.mineSourceId,
          wasteCategoryId: d.wasteCategoryId,
          plateNumber: d.plateNumber,
          driverId: d.driverId,
          grossWeight: d.grossWeight,
          tareWeight: d.tareWeight,
          receiveDate: d.receiveDate,
          receiveTime: d.receiveTime,
          remark: d.remark || ''
        }
        this.dialogVisible = true
      }).catch(() => {})
    },
    handleView(row) {
      getReceivingDetail(row.id).then(res => {
        this.viewData = res.data
        this.viewDialogVisible = true
      }).catch(() => {})
    },
    handleDelete(row) {
      deleteReceiving(row.id).then(() => {
        this.$message.success('删除成功')
        this.fetchList()
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.receivingForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.isEdit ? updateReceiving(this.editId, this.formData) : createReceiving(this.formData)
        api.then(() => {
          this.$message.success(this.isEdit ? '编辑成功' : '新增成功')
          this.dialogVisible = false
          this.fetchList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDialogClose() {
      this.$refs.receivingForm && this.$refs.receivingForm.resetFields()
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    resetForm() {
      this.formData = {
        producerId: null, workshopId: null, mineSourceId: null, wasteCategoryId: null,
        plateNumber: '', driverId: null, grossWeight: 0, tareWeight: 0,
        receiveDate: '', receiveTime: '', remark: ''
      }
      this.$nextTick(() => {
        this.$refs.receivingForm && this.$refs.receivingForm.clearValidate()
      })
    }
  }
}
</script>
<style scoped>
.receiving-container { padding: 0; }
</style>
