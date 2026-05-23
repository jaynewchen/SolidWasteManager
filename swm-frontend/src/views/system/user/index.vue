<template>
  <div class="user-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryParams.realName" placeholder="真实姓名" clearable />
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
      <el-button type="primary" icon="el-icon-plus" v-permission="'system:user:add'" @click="handleAdd">
        新增用户
      </el-button>
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
      <el-table-column prop="username" label="用户名" min-width="100" />
      <el-table-column prop="realName" label="真实姓名" min-width="100" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roles" label="角色" min-width="120">
        <template slot-scope="scope">
          <el-tag
            v-for="role in scope.row.roles"
            :key="role"
            size="small"
            style="margin-right: 4px"
          >
            {{ role }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" min-width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" v-permission="'system:user:edit'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" icon="el-icon-lock" @click="handleResetPwd(scope.row)">重置密码</el-button>
          <el-button type="text" icon="el-icon-delete" v-permission="'system:user:delete'" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
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
      width="550px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form ref="userForm" :model="formData" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="formData.roleIds">
            <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
              {{ role.name }}
            </el-checkbox>
          </el-checkbox-group>
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
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- Reset Password Dialog -->
    <el-dialog
      title="重置密码"
      :visible.sync="pwdDialogVisible"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="80px" size="small">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="pwdSubmitLoading" @click="handlePwdSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, createUser, updateUser, deleteUser } from '@/api/system'
import { getRoleList } from '@/api/system'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      queryParams: {
        username: '',
        realName: '',
        status: '',
        page: 1,
        pageSize: 20
      },
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        roleIds: [],
        status: 1
      },
      formRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur', min: 6 }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
      },
      roleList: [],
      pwdDialogVisible: false,
      pwdSubmitLoading: false,
      pwdForm: {
        userId: null,
        password: ''
      },
      pwdRules: {
        password: [{ required: true, message: '请输入新密码', trigger: 'blur', min: 6 }]
      }
    }
  },
  created() {
    this.fetchRoleList()
    this.fetchList()
  },
  methods: {
    fetchRoleList() {
      getRoleList().then(res => {
        this.roleList = res.data || []
      }).catch(() => {})
    },
    fetchList() {
      this.loading = true
      getUserList(this.queryParams).then(res => {
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
        username: '',
        realName: '',
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
      this.dialogTitle = '新增用户'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑用户'
      this.isEdit = true
      this.editId = row.id
      this.formData = {
        username: row.username || '',
        password: '',
        realName: row.realName || '',
        phone: row.phone || '',
        email: row.email || '',
        roleIds: (row.roleIds || []).slice(),
        status: row.status !== undefined ? row.status : 1
      }
      // Remove password requirement on edit
      this.formRules.password = []
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该用户？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchList()
        })
      }).catch(() => {})
    },
    handleResetPwd(row) {
      this.pwdForm = {
        userId: row.id,
        password: ''
      }
      this.pwdDialogVisible = true
    },
    handlePwdSubmit() {
      this.$refs.pwdForm.validate(valid => {
        if (!valid) return
        this.pwdSubmitLoading = true
        updateUser(this.pwdForm.userId, { password: this.pwdForm.password }).then(() => {
          this.$message.success('密码重置成功')
          this.pwdDialogVisible = false
          this.pwdSubmitLoading = false
        }).catch(() => {
          this.pwdSubmitLoading = false
        })
      })
    },
    handleSubmit() {
      this.$refs.userForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        if (this.isEdit) {
          const data = { ...this.formData }
          delete data.username
          delete data.password
          updateUser(this.editId, data).then(() => {
            this.$message.success('编辑成功')
            this.dialogVisible = false
            this.fetchList()
            this.submitLoading = false
          }).catch(() => {
            this.submitLoading = false
          })
        } else {
          createUser(this.formData).then(() => {
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
      this.formRules.password = [{ required: true, message: '请输入密码', trigger: 'blur', min: 6 }]
      this.$refs.userForm && this.$refs.userForm.resetFields()
    },
    resetForm() {
      this.formData = {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        roleIds: [],
        status: 1
      }
    }
  }
}
</script>

<style scoped>
.user-container {
  padding: 0;
}
</style>
