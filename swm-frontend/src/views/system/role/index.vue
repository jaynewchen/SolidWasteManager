<template>
  <div class="role-container">
    <!-- Filter Bar -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item label="角色编码">
          <el-input v-model="queryParams.roleCode" placeholder="角色编码" clearable />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="角色名称" clearable />
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
      <el-button type="primary" icon="el-icon-plus" v-permission="'system:role:add'" @click="handleAdd">
        新增角色
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
      <el-table-column prop="roleCode" label="角色编码" min-width="120" />
      <el-table-column prop="roleName" label="角色名称" min-width="120" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="userCount" label="用户数" min-width="80" align="center" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" min-width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" v-permission="'system:role:edit'" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" icon="el-icon-delete" v-permission="'system:role:delete'" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
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
      width="650px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form ref="roleForm" :model="formData" :rules="formRules" label-width="100px" size="small">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" :disabled="isEdit" maxlength="50" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入角色描述" :rows="2" maxlength="200" />
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
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTree"
            :data="menuTreeData"
            node-key="id"
            show-checkbox
            default-expand-all
            :props="treeProps"
          >
            <span slot-scope="{ node, data }" style="font-size: 14px">
              <span>{{ data.menuName }}</span>
              <el-tag
                v-if="data.menuType === 'M'"
                size="mini"
                type="info"
                style="margin-left: 8px"
              >目录</el-tag>
              <el-tag
                v-else-if="data.menuType === 'C'"
                size="mini"
                type="success"
                style="margin-left: 8px"
              >菜单</el-tag>
              <el-tag
                v-else-if="data.menuType === 'B'"
                size="mini"
                type="warning"
                style="margin-left: 8px"
              >按钮</el-tag>
            </span>
          </el-tree>
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
import { getRolePage, createRole, updateRole, deleteRole, getRoleDetail, getMenuTree } from '@/api/system'

export default {
  name: 'RoleManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      queryParams: {
        roleCode: '',
        roleName: '',
        status: '',
        page: 1,
        pageSize: 20
      },
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      editId: null,
      formData: {
        roleCode: '',
        roleName: '',
        description: '',
        status: 1,
        menuIds: []
      },
      formRules: {
        roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
        roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
      },
      menuTreeData: [],
      treeProps: {
        children: 'children',
        label: 'menuName'
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    fetchList() {
      this.loading = true
      getRolePage(this.queryParams).then(res => {
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
        roleCode: '',
        roleName: '',
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
      this.dialogTitle = '新增角色'
      this.isEdit = false
      this.editId = null
      this.resetForm()
      this.dialogVisible = true
      this.loadMenuTree()
    },
    handleEdit(row) {
      this.dialogTitle = '编辑角色'
      this.isEdit = true
      this.editId = row.id
      getRoleDetail(row.id).then(res => {
        const detail = res.data
        this.formData = {
          roleCode: detail.roleCode || '',
          roleName: detail.roleName || '',
          description: detail.description || '',
          status: detail.status !== undefined ? detail.status : 1,
          menuIds: detail.menuIds || []
        }
        this.dialogVisible = true
        this.loadMenuTree()
      })
    },
    loadMenuTree() {
      getMenuTree().then(res => {
        this.menuTreeData = res.data || []
        if (this.isEdit) {
          this.$nextTick(() => {
            if (this.$refs.menuTree) {
              this.$refs.menuTree.setCheckedKeys(this.formData.menuIds)
            }
          })
        }
      })
    },
    handleDelete(row) {
      let message = '确认删除该角色？'
      if (row.userCount > 0) {
        message = `该角色下有 ${row.userCount} 个用户，确认删除？`
      }
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteRole(row.id).then(() => {
          this.$message.success('删除成功')
          this.fetchList()
        }).catch(() => {})
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.roleForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true

        const checkedKeys = this.$refs.menuTree.getCheckedKeys()
        const halfCheckedKeys = this.$refs.menuTree.getHalfCheckedKeys()
        const data = {
          ...this.formData,
          menuIds: [...checkedKeys, ...halfCheckedKeys]
        }

        if (this.isEdit) {
          updateRole(this.editId, data).then(() => {
            this.$message.success('编辑成功')
            this.dialogVisible = false
            this.fetchList()
            this.submitLoading = false
          }).catch(() => {
            this.submitLoading = false
          })
        } else {
          createRole(data).then(() => {
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
      this.$refs.roleForm && this.$refs.roleForm.resetFields()
    },
    resetForm() {
      this.formData = {
        roleCode: '',
        roleName: '',
        description: '',
        status: 1,
        menuIds: []
      }
    }
  }
}
</script>

<style scoped>
.role-container {
  padding: 0;
}
</style>
