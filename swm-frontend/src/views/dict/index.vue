<template>
  <div class="dict-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="名称">
          <el-input v-model="queryParams.name" placeholder="名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增{{ dictLabel }}</el-button>
    </div>

    <!-- Table -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      size="medium"
    >
      <el-table-column prop="code" label="编码" min-width="120" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" min-width="80" align="center" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" min-width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" icon="el-icon-delete" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      style="margin-top: 16px; text-align: right"
      :current-page="queryParams.page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- Add/Edit Dialog -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form ref="dictForm" :model="formData" :rules="formRules" label-width="80px" size="small">
        <el-form-item label="编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
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
  </div>
</template>

<script>
import { getDictPage } from '@/api/dict'
import request from '@/utils/request'

export default {
  name: 'DataDict',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      queryParams: {
        name: '',
        status: '',
        page: 1,
        pageSize: 20
      },
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        code: '',
        name: '',
        sortOrder: 0,
        status: 1,
        remark: ''
      },
      formRules: {
        code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dictType() {
      return this.$route.meta.dictType || this.$route.query.type || 'producer'
    },
    dictLabel() {
      const labels = {
        producer: '产废单位',
        workshop: '车间',
        mine: '矿源',
        category: '废物类别',
        driver: '司机',
        process: '处置工艺'
      }
      return labels[this.dictType] || '字典'
    },
    pageTitle() {
      return this.dictLabel + '管理'
    }
  },
  watch: {
    '$route'() {
      this.resetForm()
      this.fetchList()
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    fetchList() {
      this.loading = true
      getDictPage(this.dictType, this.queryParams).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        name: '',
        status: '',
        page: 1,
        pageSize: 20
      }
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.fetchList()
    },
    handleCurrentChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleAdd() {
      this.dialogTitle = '新增' + this.dictLabel
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑' + this.dictLabel
      this.isEdit = true
      this.editId = row.id
      this.formData = {
        code: row.code || '',
        name: row.name || '',
        sortOrder: row.sortOrder || 0,
        status: row.status !== undefined ? row.status : 1,
        remark: row.remark || ''
      }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该项？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: `/dict/${this.dictType}/${row.id}`,
          method: 'delete'
        }).then(() => {
          this.$message.success('删除成功')
          this.fetchList()
        })
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.dictForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = { ...this.formData }
        if (this.isEdit) {
          request({
            url: `/dict/${this.dictType}/${this.editId}`,
            method: 'put',
            data
          }).then(() => {
            this.$message.success('编辑成功')
            this.dialogVisible = false
            this.fetchList()
            this.submitLoading = false
          }).catch(() => {
            this.submitLoading = false
          })
        } else {
          request({
            url: `/dict/${this.dictType}`,
            method: 'post',
            data
          }).then(() => {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.fetchList()
            this.submitLoading = false
          }).catch(() => {
            this.submitLoading = false
          })
        }
      })
    },
    handleDialogClose() {
      this.resetForm()
      this.$refs.dictForm && this.$refs.dictForm.resetFields()
    },
    resetForm() {
      this.formData = {
        code: '',
        name: '',
        sortOrder: 0,
        status: 1,
        remark: ''
      }
    }
  }
}
</script>

<style scoped>
.dict-container {
  padding: 0;
}
</style>
