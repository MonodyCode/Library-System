<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>图书管理系统</h1>
        <p>Library Management System</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form 
            ref="loginForm" 
            :model="loginForm" 
            :rules="loginRules" 
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                @keyup.enter.native="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn"
                :loading="$store.state.loading"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="注册" name="register">
          <el-form 
            ref="registerForm" 
            :model="registerForm" 
            :rules="registerRules" 
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="el-icon-lock"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="realName">
              <el-input
                v-model="registerForm.realName"
                placeholder="请输入真实姓名"
                prefix-icon="el-icon-user-solid"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                prefix-icon="el-icon-message"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="el-icon-phone"
                size="large"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn"
                :loading="$store.state.loading"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="demo-accounts">
        <h4>演示账号</h4>
        <p><strong>管理员：</strong>admin / admin</p>
        <p><strong>读者：</strong>reader1 / 123456</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      activeTab: 'login',
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        email: '',
        phone: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      registerRules: {
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
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
        const result = await this.$store.dispatch('login', this.loginForm)
        
        if (result.success) {
          this.$message.success('登录成功')
          // 根据用户类型跳转到不同页面
          if (result.user.userType === 2) {
            this.$router.push('/admin')
          } else {
            this.$router.push('/reader')
          }
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        console.error('登录失败:', error)
      }
    },
    
    async handleRegister() {
      try {
        await this.$refs.registerForm.validate()
        const result = await this.$store.dispatch('register', {
          ...this.registerForm,
          userType: 1 // 默认注册为读者
        })
        
        if (result.success) {
          this.$message.success('注册成功，请登录')
          this.activeTab = 'login'
          this.resetForms()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        console.error('注册失败:', error)
      }
    },
    
    resetForms() {
      this.loginForm = { username: '', password: '' }
      this.registerForm = {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        email: '',
        phone: ''
      }
      this.$refs.loginForm && this.$refs.loginForm.resetFields()
      this.$refs.registerForm && this.$refs.registerForm.resetFields()
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.demo-accounts {
  margin-top: 30px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
}

.demo-accounts h4 {
  margin-bottom: 10px;
  color: #333;
}

.demo-accounts p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}
</style>