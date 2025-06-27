<template>
  <div class="admin-dashboard">
    <h1>仪表盘</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon books">
              <i class="el-icon-reading"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.totalBooks }}</h3>
              <p>图书总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon users">
              <i class="el-icon-user"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.totalUsers }}</h3>
              <p>用户总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon borrows">
              <i class="el-icon-document"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.totalBorrows }}</h3>
              <p>借阅总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon overdue">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.overdueBooks }}</h3>
              <p>逾期图书</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>借阅趋势</span>
          </div>
          <div id="borrowTrendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>热门图书</span>
          </div>
          <div id="popularBooksChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row class="recent-activity">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>最近借阅记录</span>
          </div>
          <el-table :data="recentBorrows" style="width: 100%">
            <el-table-column prop="bookTitle" label="图书名称" width="200"></el-table-column>
            <el-table-column prop="username" label="借阅用户" width="150"></el-table-column>
            <el-table-column prop="borrowTime" label="借阅时间" width="180"></el-table-column>
            <el-table-column prop="dueTime" label="应还时间" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      stats: {
        totalBooks: 0,
        totalUsers: 0,
        totalBorrows: 0,
        overdueBooks: 0
      },
      recentBorrows: [],
      borrowTrendChart: null,
      popularBooksChart: null
    }
  },
  mounted() {
    this.loadDashboardData()
    this.initCharts()
  },
  methods: {
    async loadDashboardData() {
      try {
        // 加载统计数据
        const [booksRes, usersRes, borrowsRes, overdueRes, recentRes] = await Promise.all([
          axios.get('/books/count'),
          axios.get('/users/count'),
          axios.get('/borrow-records/count'),
          axios.get('/borrow-records/overdue/count'),
          axios.get('/borrow-records/recent')
        ])
        
        this.stats = {
          totalBooks: booksRes.data.data || 0,
          totalUsers: usersRes.data.data || 0,
          totalBorrows: borrowsRes.data.data || 0,
          overdueBooks: overdueRes.data.data || 0
        }
        
        this.recentBorrows = recentRes.data.data || []
        
        // 加载图表数据
        this.loadBorrowTrendChart()
        this.loadPopularBooksChart()
      } catch (error) {
        console.error('加载仪表盘数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },
    
    async loadBorrowTrendChart() {
      try {
        const response = await axios.get('/borrow-records/statistics/trend')
        const data = response.data.data || []
        
        const option = {
          title: {
            text: '最近7天借阅趋势'
          },
          tooltip: {
            trigger: 'axis'
          },
          xAxis: {
            type: 'category',
            data: data.map(item => item.date)
          },
          yAxis: {
            type: 'value'
          },
          series: [{
            data: data.map(item => item.count),
            type: 'line',
            smooth: true
          }]
        }
        
        this.borrowTrendChart.setOption(option)
      } catch (error) {
        console.error('加载借阅趋势图表失败:', error)
      }
    },
    
    async loadPopularBooksChart() {
      try {
        const response = await axios.get('/borrow-records/statistics/popular-books')
        const data = response.data.data || []
        
        const option = {
          title: {
            text: '热门图书排行'
          },
          tooltip: {
            trigger: 'item'
          },
          series: [{
            type: 'pie',
            radius: '50%',
            data: data.map(item => ({
              value: item.borrowCount,
              name: item.title
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }]
        }
        
        this.popularBooksChart.setOption(option)
      } catch (error) {
        console.error('加载热门图书图表失败:', error)
      }
    },
    
    initCharts() {
      this.$nextTick(() => {
        this.borrowTrendChart = this.$echarts.init(document.getElementById('borrowTrendChart'))
        this.popularBooksChart = this.$echarts.init(document.getElementById('popularBooksChart'))
      })
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
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stats-icon.books {
  background-color: #409eff;
}

.stats-icon.users {
  background-color: #67c23a;
}

.stats-icon.borrows {
  background-color: #e6a23c;
}

.stats-icon.overdue {
  background-color: #f56c6c;
}

.stats-info h3 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stats-info p {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.charts-row {
  margin-bottom: 20px;
}

.recent-activity {
  margin-top: 20px;
}
</style>