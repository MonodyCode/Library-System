<template>
  <div class="admin-layout">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2>图书管理系统 - 管理员</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ user.username }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px" class="sidebar">
          <el-menu
            :default-active="$route.path"
            class="el-menu-vertical"
            router
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="/admin/dashboard">
              <i class="el-icon-s-home"></i>
              <span slot="title">仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/admin/books">
              <i class="el-icon-reading"></i>
              <span slot="title">图书管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <i class="el-icon-user"></i>
              <span slot="title">用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/borrows">
              <i class="el-icon-document"></i>
              <span slot="title">借阅管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/statistics">
              <i class="el-icon-s-data"></i>
              <span slot="title">统计分析</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'AdminLayout',
  computed: {
    ...mapGetters(['user'])
  },
  methods: {
    ...mapActions(['logout']),
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.logout()
          this.$router.push('/login')
          this.$message.success('退出登录成功')
        })
      } else if (command === 'profile') {
        // 跳转到个人信息页面
        this.$message.info('个人信息功能待开发')
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.header {
  background-color: #409eff;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
}

.header-right {
  cursor: pointer;
}

.el-dropdown-link {
  color: white;
  font-size: 14px;
}

.sidebar {
  background-color: #545c64;
}

.main-content {
  background-color: #f5f5f5;
  padding: 20px;
}

.el-menu {
  border-right: none;
}
</style>