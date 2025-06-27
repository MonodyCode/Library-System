<template>
  <div class="reader-profile">
    <h1>个人信息</h1>
    
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="16">
        <el-card class="profile-card">
          <div slot="header">
            <span>基本信息</span>
            <el-button 
              style="float: right; padding: 3px 0" 
              type="text" 
              @click="editMode = !editMode"
            >
              {{ editMode ? '取消编辑' : '编辑信息' }}
            </el-button>
          </div>
          
          <el-form 
            ref="profileForm" 
            :model="profileForm" 
            :rules="profileRules" 
            label-width="100px"
            :disabled="!editMode"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="profileForm.realName"></el-input>
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email"></el-input>
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone"></el-input>
            </el-form-item>
            
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="profileForm.birthDate"
                type="date"
                placeholder="选择日期"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              >
              </el-date-picker>
            </el-form-item>
            
            <el-form-item label="地址" prop="address">
              <el-input 
                v-model="profileForm.address" 
                type="textarea" 
                :rows="3"
                placeholder="请输入详细地址"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="注册时间">
              <el-input v-model="profileForm.createTime" disabled></el-input>
            </el-form-item>
            
            <el-form-item v-if="editMode">
              <el-button type="primary" @click="updateProfile" :loading="updating">保存修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 统计信息卡片 -->
      <el-col :span="8">
        <el-card class="stats-card">
          <div slot="header">
            <span>借阅统计</span>
          </div>
          
          <div class="stat-item">
            <div class="stat-icon current">
              <i class="el-icon-reading"></i>
            </div>
            <div class="stat-info">
              <h3>{{ borrowStats.currentBorrows }}</h3>
              <p>当前借阅</p>
            </div>
          </div>
          
          <div class="stat-item">
            <div class="stat-icon total">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <h3>{{ borrowStats.totalBorrows }}</h3>
              <p>借阅总数</p>
            </div>
          </div>
          
          <div class="stat-item">
            <div class="stat-icon returned">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-info">
              <h3>{{ borrowStats.returnedBorrows }}</h3>
              <p>已归还</p>
            </div>
          </div>
          
          <div class="stat-item">
            <div class="stat-icon overdue">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <h3>{{ borrowStats.overdueBorrows }}</h3>
              <p>逾期图书</p>
            </div>
          </div>
        </el-card>
        
        <!-- 密码修改卡片 -->
        <el-card class="password-card" style="margin-top: 20px;">
          <div slot="header">
            <span>修改密码</span>
          </div>
          
          <el-form 
            ref="passwordForm" 
            :model="passwordForm" 
            :rules="passwordRules" 
            label-width="100px"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                show-password
                placeholder="请输入当前密码"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                show-password
                placeholder="请输入新密码"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                show-password
                placeholder="请再次输入新密码"
              ></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="changingPassword">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近借阅记录 -->
    <el-card class="recent-borrows-card" style="margin-top: 20px;">
      <div slot="header">
        <span>最近借阅记录</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text" 
          @click="$router.push('/reader/borrows')"
        >
          查看全部
        </el-button>
      </div>
      
      <el-table :data="recentBorrows" style="width: 100%" v-loading="loadingBorrows">
        <el-table-column label="图书封面" width="100">
          <template slot-scope="scope">
            <img :src="scope.row.bookCoverUrl || '/default-book-cover.jpg'" class="book-cover" />
          </template>
        </el-table-column>
        <el-table-column prop="bookTitle" label="图书名称" width="200"></el-table-column>
        <el-table-column prop="bookAuthor" label="作者" width="120"></el-table-column>
        <el-table-column prop="borrowTime" label="借阅时间" width="180"></el-table-column>
        <el-table-column prop="dueTime" label="应还时间" width="180">
          <template slot-scope="scope">
            <span :class="{ 'overdue-text': isOverdue(scope.row.dueTime) && scope.row.status === 0 }">
              {{ scope.row.dueTime }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="剩余天数" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 0">
              <span v-if="isOverdue(scope.row.dueTime)" class="overdue-text">
                逾期 {{ getOverdueDays(scope.row.dueTime) }} 天
              </span>
              <span v-else class="remaining-text">
                剩余 {{ getRemainingDays(scope.row.dueTime) }} 天
              </span>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'ReaderProfile',
  data() {
    return {
      editMode: false,
      updating: false,
      changingPassword: false,
      loadingBorrows: false,
      profileForm: {
        id: '',
        username: '',
        realName: '',
        email: '',
        phone: '',
        gender: 0,
        birthDate: '',
        address: '',
        createTime: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      borrowStats: {
        currentBorrows: 0,
        totalBorrows: 0,
        returnedBorrows: 0,
        overdueBorrows: 0
      },
      recentBorrows: [],
      profileRules: {
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['user'])
  },
  mounted() {
    this.loadProfile()
    this.loadBorrowStats()
    this.loadRecentBorrows()
  },
  methods: {
    async loadProfile() {
      try {
        const response = await axios.get(`/users/${this.user.id}`)
        const userData = response.data.data
        
        this.profileForm = {
          id: userData.id,
          username: userData.username,
          realName: userData.realName || '',
          email: userData.email || '',
          phone: userData.phone || '',
          gender: userData.gender || 0,
          birthDate: userData.birthDate || '',
          address: userData.address || '',
          createTime: userData.createTime
        }
      } catch (error) {
        console.error('加载个人信息失败:', error)
        this.$message.error('加载个人信息失败')
      }
    },
    
    async loadBorrowStats() {
      try {
        const response = await axios.get(`/borrow-records/user/${this.user.id}/stats`)
        this.borrowStats = response.data.data || {
          currentBorrows: 0,
          totalBorrows: 0,
          returnedBorrows: 0,
          overdueBorrows: 0
        }
      } catch (error) {
        console.error('加载借阅统计失败:', error)
      }
    },
    
    async loadRecentBorrows() {
      this.loadingBorrows = true
      try {
        const response = await axios.get('/borrow-records', {
          params: {
            userId: this.user.id,
            page: 1,
            size: 5
          }
        })
        
        this.recentBorrows = response.data.data.records || []
      } catch (error) {
        console.error('加载最近借阅记录失败:', error)
      } finally {
        this.loadingBorrows = false
      }
    },
    
    async updateProfile() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          this.updating = true
          try {
            await axios.put(`/users/${this.user.id}`, this.profileForm)
            
            // 更新 Vuex 中的用户信息
            this.$store.dispatch('updateUser', this.profileForm)
            
            this.$message.success('个人信息更新成功')
            this.editMode = false
          } catch (error) {
            console.error('更新个人信息失败:', error)
            this.$message.error(error.response?.data?.message || '更新个人信息失败')
          } finally {
            this.updating = false
          }
        }
      })
    },
    
    async changePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.changingPassword = true
          try {
            await axios.put(`/users/${this.user.id}/password`, {
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            })
            
            this.$message.success('密码修改成功')
            this.resetPasswordForm()
          } catch (error) {
            console.error('修改密码失败:', error)
            this.$message.error(error.response?.data?.message || '修改密码失败')
          } finally {
            this.changingPassword = false
          }
        }
      })
    },
    
    resetForm() {
      this.loadProfile()
      this.editMode = false
    },
    
    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$refs.passwordForm.clearValidate()
    },
    
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    
    getStatusType(status) {
      const statusMap = {
        0: 'info',    // 借阅中
        1: 'success', // 已归还
        2: 'danger'   // 逾期
      }
      return statusMap[status] || 'info'
    },
    
    getStatusText(status) {
      const statusMap = {
        0: '借阅中',
        1: '已归还',
        2: '逾期'
      }
      return statusMap[status] || '未知'
    },
    
    isOverdue(dueTime) {
      if (!dueTime) return false
      const due = new Date(dueTime)
      const now = new Date()
      return now > due
    },
    
    getOverdueDays(dueTime) {
      if (!dueTime) return 0
      const due = new Date(dueTime)
      const now = new Date()
      const diffTime = now - due
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      return diffDays > 0 ? diffDays : 0
    },
    
    getRemainingDays(dueTime) {
      if (!dueTime) return 0
      const due = new Date(dueTime)
      const now = new Date()
      const diffTime = due - now
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      return diffDays > 0 ? diffDays : 0
    }
  }
}
</script>

<style scoped>
.reader-profile {
  padding: 20px;
}

.profile-card {
  min-height: 500px;
}

.stats-card {
  min-height: 400px;
}

.password-card {
  min-height: 300px;
}

.recent-borrows-card {
  margin-top: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  color: white;
}

.stat-icon.current {
  background-color: #409eff;
}

.stat-icon.total {
  background-color: #67c23a;
}

.stat-icon.returned {
  background-color: #e6a23c;
}

.stat-icon.overdue {
  background-color: #f56c6c;
}

.stat-info h3 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-info p {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.book-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}

.remaining-text {
  color: #67c23a;
}
</style>