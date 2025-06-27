<template>
  <div class="statistics">
    <h1>统计分析</h1>
    
    <!-- 统计概览 -->
    <el-row :gutter="20" class="stats-overview">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon books">
              <i class="el-icon-reading"></i>
            </div>
            <div class="stats-info">
              <h3>{{ overview.totalBooks }}</h3>
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
              <h3>{{ overview.totalUsers }}</h3>
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
              <h3>{{ overview.totalBorrows }}</h3>
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
              <h3>{{ overview.overdueBooks }}</h3>
              <p>逾期图书</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <!-- 借阅趋势图 -->
      <el-col :span="12">
        <el-card>
          <div slot="header" class="chart-header">
            <span>借阅趋势</span>
            <el-select v-model="borrowTrendDays" @change="loadBorrowTrend" size="small">
              <el-option label="最近7天" value="7"></el-option>
              <el-option label="最近30天" value="30"></el-option>
              <el-option label="最近90天" value="90"></el-option>
            </el-select>
          </div>
          <div id="borrowTrendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <!-- 图书分类统计 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>图书分类统计</span>
          </div>
          <div id="categoryChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-section">
      <!-- 热门图书排行 -->
      <el-col :span="12">
        <el-card>
          <div slot="header" class="chart-header">
            <span>热门图书排行</span>
            <el-select v-model="popularBooksLimit" @change="loadPopularBooks" size="small">
              <el-option label="前10名" value="10"></el-option>
              <el-option label="前20名" value="20"></el-option>
              <el-option label="前50名" value="50"></el-option>
            </el-select>
          </div>
          <div id="popularBooksChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <!-- 用户借阅活跃度 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>用户借阅活跃度</span>
          </div>
          <div id="userActivityChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-row class="data-tables">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>详细统计数据</span>
          </div>
          
          <el-tabs v-model="activeTab">
            <!-- 图书统计 -->
            <el-tab-pane label="图书统计" name="books">
              <el-table :data="bookStats" style="width: 100%">
                <el-table-column prop="category" label="分类" width="150"></el-table-column>
                <el-table-column prop="totalBooks" label="图书总数" width="120"></el-table-column>
                <el-table-column prop="availableBooks" label="可借数量" width="120"></el-table-column>
                <el-table-column prop="borrowedBooks" label="已借数量" width="120"></el-table-column>
                <el-table-column prop="borrowRate" label="借阅率" width="120">
                  <template slot-scope="scope">
                    {{ (scope.row.borrowRate * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <!-- 用户统计 -->
            <el-tab-pane label="用户统计" name="users">
              <el-table :data="userStats" style="width: 100%">
                <el-table-column prop="userType" label="用户类型" width="150">
                  <template slot-scope="scope">
                    {{ scope.row.userType === 1 ? '管理员' : '读者' }}
                  </template>
                </el-table-column>
                <el-table-column prop="totalUsers" label="用户总数" width="120"></el-table-column>
                <el-table-column prop="activeUsers" label="活跃用户" width="120"></el-table-column>
                <el-table-column prop="inactiveUsers" label="非活跃用户" width="120"></el-table-column>
                <el-table-column prop="activityRate" label="活跃率" width="120">
                  <template slot-scope="scope">
                    {{ (scope.row.activityRate * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <!-- 借阅统计 -->
            <el-tab-pane label="借阅统计" name="borrows">
              <el-table :data="borrowStats" style="width: 100%">
                <el-table-column prop="date" label="日期" width="150"></el-table-column>
                <el-table-column prop="totalBorrows" label="借阅总数" width="120"></el-table-column>
                <el-table-column prop="returns" label="归还数量" width="120"></el-table-column>
                <el-table-column prop="overdue" label="逾期数量" width="120"></el-table-column>
                <el-table-column prop="returnRate" label="归还率" width="120">
                  <template slot-scope="scope">
                    {{ (scope.row.returnRate * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Statistics',
  data() {
    return {
      overview: {
        totalBooks: 0,
        totalUsers: 0,
        totalBorrows: 0,
        overdueBooks: 0
      },
      borrowTrendDays: '30',
      popularBooksLimit: '10',
      activeTab: 'books',
      bookStats: [],
      userStats: [],
      borrowStats: [],
      borrowTrendChart: null,
      categoryChart: null,
      popularBooksChart: null,
      userActivityChart: null
    }
  },
  mounted() {
    this.initCharts()
    this.loadOverviewData()
    this.loadAllCharts()
    this.loadTableData()
  },
  methods: {
    initCharts() {
      this.$nextTick(() => {
        this.borrowTrendChart = this.$echarts.init(document.getElementById('borrowTrendChart'))
        this.categoryChart = this.$echarts.init(document.getElementById('categoryChart'))
        this.popularBooksChart = this.$echarts.init(document.getElementById('popularBooksChart'))
        this.userActivityChart = this.$echarts.init(document.getElementById('userActivityChart'))
      })
    },
    
    async loadOverviewData() {
      try {
        const [booksRes, usersRes, borrowsRes, overdueRes] = await Promise.all([
          axios.get('/books/count'),
          axios.get('/users/count'),
          axios.get('/borrow-records/count'),
          axios.get('/borrow-records/overdue/count')
        ])
        
        this.overview = {
          totalBooks: booksRes.data.data || 0,
          totalUsers: usersRes.data.data || 0,
          totalBorrows: borrowsRes.data.data || 0,
          overdueBooks: overdueRes.data.data || 0
        }
      } catch (error) {
        console.error('加载概览数据失败:', error)
      }
    },
    
    loadAllCharts() {
      this.loadBorrowTrend()
      this.loadCategoryChart()
      this.loadPopularBooks()
      this.loadUserActivity()
    },
    
    async loadBorrowTrend() {
      try {
        const response = await axios.get('/borrow-records/statistics/trend', {
          params: { days: this.borrowTrendDays }
        })
        const data = response.data.data || []
        
        const option = {
          title: {
            text: `最近${this.borrowTrendDays}天借阅趋势`
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['借阅数量', '归还数量']
          },
          xAxis: {
            type: 'category',
            data: data.map(item => item.date)
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '借阅数量',
              data: data.map(item => item.borrowCount),
              type: 'line',
              smooth: true,
              itemStyle: { color: '#409EFF' }
            },
            {
              name: '归还数量',
              data: data.map(item => item.returnCount),
              type: 'line',
              smooth: true,
              itemStyle: { color: '#67C23A' }
            }
          ]
        }
        
        this.borrowTrendChart.setOption(option)
      } catch (error) {
        console.error('加载借阅趋势失败:', error)
      }
    },
    
    async loadCategoryChart() {
      try {
        const response = await axios.get('/books/statistics/category')
        const data = response.data.data || []
        
        const option = {
          title: {
            text: '图书分类分布'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          series: [{
            name: '图书分类',
            type: 'pie',
            radius: ['40%', '70%'],
            data: data.map(item => ({
              value: item.count,
              name: item.category
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
        
        this.categoryChart.setOption(option)
      } catch (error) {
        console.error('加载分类统计失败:', error)
      }
    },
    
    async loadPopularBooks() {
      try {
        const response = await axios.get('/borrow-records/statistics/popular-books', {
          params: { limit: this.popularBooksLimit }
        })
        const data = response.data.data || []
        
        const option = {
          title: {
            text: `热门图书排行榜（前${this.popularBooksLimit}名）`
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value'
          },
          yAxis: {
            type: 'category',
            data: data.map(item => item.title)
          },
          series: [{
            name: '借阅次数',
            type: 'bar',
            data: data.map(item => item.borrowCount),
            itemStyle: {
              color: '#E6A23C'
            }
          }]
        }
        
        this.popularBooksChart.setOption(option)
      } catch (error) {
        console.error('加载热门图书失败:', error)
      }
    },
    
    async loadUserActivity() {
      try {
        const response = await axios.get('/users/statistics/activity')
        const data = response.data.data || []
        
        const option = {
          title: {
            text: '用户借阅活跃度'
          },
          tooltip: {
            trigger: 'axis'
          },
          xAxis: {
            type: 'category',
            data: data.map(item => item.range)
          },
          yAxis: {
            type: 'value'
          },
          series: [{
            name: '用户数量',
            type: 'bar',
            data: data.map(item => item.userCount),
            itemStyle: {
              color: '#F56C6C'
            }
          }]
        }
        
        this.userActivityChart.setOption(option)
      } catch (error) {
        console.error('加载用户活跃度失败:', error)
      }
    },
    
    async loadTableData() {
      try {
        const [bookStatsRes, userStatsRes, borrowStatsRes] = await Promise.all([
          axios.get('/books/statistics/detailed'),
          axios.get('/users/statistics/detailed'),
          axios.get('/borrow-records/statistics/detailed')
        ])
        
        this.bookStats = bookStatsRes.data.data || []
        this.userStats = userStatsRes.data.data || []
        this.borrowStats = borrowStatsRes.data.data || []
      } catch (error) {
        console.error('加载表格数据失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.stats-overview {
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

.charts-section {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.data-tables {
  margin-top: 20px;
}
</style>