<template>
  <div class="reader-borrows">
    <h1>我的借阅</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon current">
              <i class="el-icon-reading"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.currentBorrows }}</h3>
              <p>当前借阅</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon total">
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
            <div class="stats-icon returned">
              <i class="el-icon-check"></i>
            </div>
            <div class="stats-info">
              <h3>{{ stats.returnedBorrows }}</h3>
              <p>已归还</p>
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
              <h3>{{ stats.overdueBorrows }}</h3>
              <p>逾期图书</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索图书名称、作者"
            clearable
            @keyup.enter.native="searchBorrows"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchBorrows"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="借阅状态" clearable>
            <el-option label="借阅中" value="0"></el-option>
            <el-option label="已归还" value="1"></el-option>
            <el-option label="逾期" value="2"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          >
          </el-date-picker>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="searchBorrows">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 借阅记录列表 -->
    <el-card class="table-card">
      <div slot="header">
        <span>借阅记录</span>
      </div>
      
      <el-table
        :data="borrows"
        style="width: 100%"
        v-loading="loading"
        :row-class-name="getRowClassName"
      >
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
        <el-table-column prop="returnTime" label="归还时间" width="180">
          <template slot-scope="scope">
            {{ scope.row.returnTime || '-' }}
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
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 0"
              size="mini" 
              type="success"
              @click="returnBook(scope.row)"
            >
              归还
            </el-button>
            <el-button size="mini" @click="showDetailDialog(scope.row)">详情</el-button>
            <el-button size="mini" type="info" @click="borrowAgain(scope.row)">再次借阅</el-button>
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

    <!-- 借阅详情对话框 -->
    <el-dialog
      title="借阅详情"
      :visible.sync="detailDialogVisible"
      width="500px"
    >
      <div v-if="selectedBorrow">
        <div class="detail-item"><strong>借阅ID：</strong>{{ selectedBorrow.id }}</div>
        <div class="detail-item"><strong>图书名称：</strong>{{ selectedBorrow.bookTitle }}</div>
        <div class="detail-item"><strong>作者：</strong>{{ selectedBorrow.bookAuthor }}</div>
        <div class="detail-item"><strong>ISBN：</strong>{{ selectedBorrow.bookIsbn }}</div>
        <div class="detail-item"><strong>借阅时间：</strong>{{ selectedBorrow.borrowTime }}</div>
        <div class="detail-item"><strong>应还时间：</strong>{{ selectedBorrow.dueTime }}</div>
        <div class="detail-item"><strong>归还时间：</strong>{{ selectedBorrow.returnTime || '未归还' }}</div>
        <div class="detail-item">
          <strong>状态：</strong>
          <el-tag :type="getStatusType(selectedBorrow.status)">{{ getStatusText(selectedBorrow.status) }}</el-tag>
        </div>
        <div class="detail-item" v-if="selectedBorrow.status === 0">
          <strong>剩余时间：</strong>
          <span v-if="isOverdue(selectedBorrow.dueTime)" class="overdue-text">
            逾期 {{ getOverdueDays(selectedBorrow.dueTime) }} 天
          </span>
          <span v-else class="remaining-text">
            剩余 {{ getRemainingDays(selectedBorrow.dueTime) }} 天
          </span>
        </div>
        <div class="detail-item" v-if="selectedBorrow.status === 0 && isOverdue(selectedBorrow.dueTime)">
          <el-alert
            title="图书已逾期，请尽快归还！"
            type="warning"
            :closable="false"
            show-icon
          >
          </el-alert>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer" v-if="selectedBorrow && selectedBorrow.status === 0">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="returnBook(selectedBorrow)">归还图书</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'ReaderBorrows',
  data() {
    return {
      loading: false,
      borrows: [],
      stats: {
        currentBorrows: 0,
        totalBorrows: 0,
        returnedBorrows: 0,
        overdueBorrows: 0
      },
      searchForm: {
        keyword: '',
        status: '',
        dateRange: []
      },
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      detailDialogVisible: false,
      selectedBorrow: null
    }
  },
  computed: {
    ...mapGetters(['user'])
  },
  mounted() {
    this.loadBorrows()
    this.loadStats()
  },
  methods: {
    async loadBorrows() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current,
          size: this.pagination.size,
          userId: this.user.id,
          keyword: this.searchForm.keyword,
          status: this.searchForm.status
        }
        
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          params.startDate = this.searchForm.dateRange[0]
          params.endDate = this.searchForm.dateRange[1]
        }
        
        const response = await axios.get('/borrow-records', { params })
        const data = response.data.data
        
        this.borrows = data.records
        this.pagination.total = data.total
      } catch (error) {
        console.error('加载借阅记录失败:', error)
        this.$message.error('加载借阅记录失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadStats() {
      try {
        const response = await axios.get(`/borrow-records/user/${this.user.id}/stats`)
        this.stats = response.data.data || {
          currentBorrows: 0,
          totalBorrows: 0,
          returnedBorrows: 0,
          overdueBorrows: 0
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    
    searchBorrows() {
      this.pagination.current = 1
      this.loadBorrows()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        status: '',
        dateRange: []
      }
      this.searchBorrows()
    },
    
    handleSizeChange(val) {
      this.pagination.size = val
      this.loadBorrows()
    },
    
    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadBorrows()
    },
    
    async returnBook(borrow) {
      this.$confirm(`确定要归还图书《${borrow.bookTitle}》吗？`, '确认归还', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.put(`/borrow-records/${borrow.id}/return`)
          this.$message.success('归还图书成功')
          this.loadBorrows()
          this.loadStats()
          
          if (this.detailDialogVisible) {
            this.detailDialogVisible = false
          }
        } catch (error) {
          console.error('归还图书失败:', error)
          this.$message.error(error.response?.data?.message || '归还图书失败')
        }
      })
    },
    
    async borrowAgain(borrow) {
      try {
        // 先检查图书是否可借
        const bookResponse = await axios.get(`/books/${borrow.bookId}`)
        const book = bookResponse.data.data
        
        if (book.availableCount === 0) {
          this.$message.warning('该图书暂无库存，无法借阅')
          return
        }
        
        // 检查是否已经借阅过该图书
        const currentBorrowsResponse = await axios.get(`/borrow-records/user/${this.user.id}/current`)
        const currentBorrows = currentBorrowsResponse.data.data || []
        const alreadyBorrowed = currentBorrows.some(record => record.bookId === borrow.bookId)
        
        if (alreadyBorrowed) {
          this.$message.warning('您已借阅过该图书，请先归还后再借阅')
          return
        }
        
        this.$confirm(`确定要再次借阅图书《${borrow.bookTitle}》吗？`, '确认借阅', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(async () => {
          await axios.post('/borrow-records/borrow', {
            bookId: borrow.bookId
          })
          
          this.$message.success('借阅成功')
          this.loadBorrows()
          this.loadStats()
        })
      } catch (error) {
        console.error('借阅失败:', error)
        this.$message.error(error.response?.data?.message || '借阅失败')
      }
    },
    
    showDetailDialog(borrow) {
      this.selectedBorrow = borrow
      this.detailDialogVisible = true
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
    
    getRowClassName({ row }) {
      if (row.status === 0 && this.isOverdue(row.dueTime)) {
        return 'overdue-row'
      }
      return ''
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
.reader-borrows {
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

.stats-icon.current {
  background-color: #409eff;
}

.stats-icon.total {
  background-color: #67c23a;
}

.stats-icon.returned {
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

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.book-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.detail-item {
  margin-bottom: 10px;
  line-height: 1.5;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}

.remaining-text {
  color: #67c23a;
}

.overdue-row {
  background-color: #fef0f0;
}

.overdue-row:hover {
  background-color: #fde2e2 !important;
}
</style>