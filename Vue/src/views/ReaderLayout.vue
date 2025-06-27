<template>
  <div class="reader-layout">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2>图书管理系统</h2>
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
            background-color="#2c3e50"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="/reader/books">
              <i class="el-icon-reading"></i>
              <span slot="title">图书浏览</span>
            </el-menu-item>
            <el-menu-item index="/reader/borrows">
              <i class="el-icon-document"></i>
              <span slot="title">我的借阅</span>
            </el-menu-item>
            <el-menu-item index="/reader/profile">
              <i class="el-icon-user"></i>
              <span slot="title">个人信息</span>
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
  name: 'ReaderLayout',
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
        this.$router.push('/reader/profile')
      }
    }
  }
}
</script>

<style scoped>
.reader-layout {
  height: 100vh;
}

.header {
  background-color: #2c3e50;
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
  background-color: #2c3e50;
}

.main-content {
  background-color: #f5f5f5;
  padding: 20px;
}

.el-menu {
  border-right: none;
}
</style>