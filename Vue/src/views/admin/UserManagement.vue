<template>
  <div class="user-management">
    <h1>用户管理</h1>
    
    <!-- 搜索和操作区域 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、姓名、邮箱"
            clearable
            @keyup.enter.native="searchUsers"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchUsers"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.userType" placeholder="用户类型" clearable>
            <el-option label="管理员" value="1"></el-option>
            <el-option label="读者" value="0"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="用户状态" clearable>
            <el-option label="正常" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-col>
        <el-col :span="10">
          <el-button type="primary" @click="searchUsers">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="showAddDialog">添加用户</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table
        :data="users"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="phone" label="电话" width="120"></el-table-column>
        <el-table-column prop="userType" label="用户类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.userType === 1 ? 'danger' : 'primary'">
              {{ scope.row.userType === 1 ? '管理员' : '读者' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button 
              size="mini" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="toggleUserStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="mini" 
              type="danger" 
              @click="deleteUser(scope.row)"
              :disabled="scope.row.userType === 1"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      >
      </el-pagination>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        :model="userForm"
        :rules="userRules"
        ref="userForm"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input type="password" v-model="userForm.password" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input type="password" v-model="userForm.confirmPassword" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="userForm.userType" placeholder="选择用户类型">
            <el-option label="读者" :value="0"></el-option>
            <el-option label="管理员" :value="1"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-select v-model="userForm.status" placeholder="选择状态">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      users: [],
      searchForm: {
        keyword: '',
        userType: '',
        status: ''
      },
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      isEdit: false,
      userForm: {
        id: null,
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        email: '',
        phone: '',
        userType: 0,
        status: 1
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑用户' : '添加用户'
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        
        const response = await axios.get('/users', { params })
        const data = response.data.data
        
        this.users = data.records
        this.pagination.total = data.total
      } catch (error) {
        console.error('加载用户列表失败:', error)
        this.$message.error('加载用户列表失败')
      } finally {
        this.loading = false
      }
    },
    
    searchUsers() {
      this.pagination.current = 1
      this.loadUsers()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        userType: '',
        status: ''
      }
      this.searchUsers()
    },
    
    handleSizeChange(val) {
      this.pagination.size = val
      this.loadUsers()
    },
    
    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadUsers()
    },
    
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
    },
    
    showEditDialog(user) {
      this.isEdit = true
      this.userForm = {
        id: user.id,
        username: user.username,
        password: '',
        confirmPassword: '',
        realName: user.realName,
        email: user.email,
        phone: user.phone,
        userType: user.userType,
        status: user.status
      }
      this.dialogVisible = true
    },
    
    resetForm() {
      this.userForm = {
        id: null,
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        email: '',
        phone: '',
        userType: 0,
        status: 1
      }
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields()
      }
    },
    
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.userForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    
    submitForm() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          try {
            const formData = { ...this.userForm }
            delete formData.confirmPassword
            
            if (this.isEdit) {
              // 编辑时不传递密码字段
              delete formData.password
              await axios.put(`/users/${formData.id}`, formData)
              this.$message.success('更新用户成功')
            } else {
              await axios.post('/users/register', formData)
              this.$message.success('添加用户成功')
            }
            
            this.dialogVisible = false
            this.loadUsers()
          } catch (error) {
            console.error('保存用户失败:', error)
            this.$message.error(error.response?.data?.message || '保存用户失败')
          }
        }
      })
    },
    
    async toggleUserStatus(user) {
      const action = user.status === 1 ? '禁用' : '启用'
      this.$confirm(`确定要${action}用户《${user.username}》吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const newStatus = user.status === 1 ? 0 : 1
          await axios.put(`/users/${user.id}/status`, { status: newStatus })
          this.$message.success(`${action}用户成功`)
          this.loadUsers()
        } catch (error) {
          console.error(`${action}用户失败:`, error)
          this.$message.error(error.response?.data?.message || `${action}用户失败`)
        }
      })
    },
    
    deleteUser(user) {
      if (user.userType === 1) {
        this.$message.warning('不能删除管理员用户')
        return
      }
      
      this.$confirm(`确定要删除用户《${user.username}》吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`/users/${user.id}`)
          this.$message.success('删除用户成功')
          this.loadUsers()
        } catch (error) {
          console.error('删除用户失败:', error)
          this.$message.error(error.response?.data?.message || '删除用户失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>