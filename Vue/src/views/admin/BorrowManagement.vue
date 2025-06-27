<template>
  <div class="borrow-management">
    <h1>借阅管理</h1>
    
    <!-- 搜索和操作区域 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、图书名称"
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
          <el-button type="warning" @click="checkOverdueBooks">检查逾期</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 借阅记录列表 -->
    <el-card class="table-card">
      <el-table
        :data="borrows"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="bookTitle" label="图书名称" width="200"></el-table-column>
        <el-table-column prop="bookAuthor" label="作者" width="120"></el-table-column>
        <el-table-column prop="borrowTime" label="借阅时间" width="180"></el-table-column>
        <el-table-column prop="dueTime" label="应还时间" width="180"></el-table-column>
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
            <el-button 
              v-if="scope.row.status === 0 && isOverdue(scope.row.dueTime)"
              size="mini" 
              type="warning"
              @click="markOverdue(scope.row)"
            >
              标记逾期
            </el-button>
            <el-button size="mini" @click="showDetailDialog(scope.row)">详情</el-button>
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
        <div class="detail-item"><strong>用户名：</strong>{{ selectedBorrow.username }}</div>
        <div class="detail-item"><strong>用户姓名：</strong>{{ selectedBorrow.realName }}</div>
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
        <div class="detail-item" v-if="selectedBorrow.status === 0 && isOverdue(selectedBorrow.dueTime)">
          <strong>逾期天数：</strong>
          <span style="color: red;">{{ getOverdueDays(selectedBorrow.dueTime) }} 天</span>
        </div>
      </div>
    </el-dialog>

    <!-- 逾期图书列表对话框 -->
    <el-dialog
      title="逾期图书列表"
      :visible.sync="overdueDialogVisible"
      width="800px"
    >
      <el-table :data="overdueBooks" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="bookTitle" label="图书名称" width="200"></el-table-column>
        <el-table-column prop="dueTime" label="应还时间" width="180"></el-table-column>
        <el-table-column label="逾期天数" width="100">
          <template slot-scope="scope">
            <span style="color: red;">{{ getOverdueDays(scope.row.dueTime) }} 天</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="warning" @click="markOverdue(scope.row)">标记逾期</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'BorrowManagement',
  data() {
    return {
      loading: false,
      borrows: [],
      overdueBooks: [],
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
      overdueDialogVisible: false,
      selectedBorrow: null
    }
  },
  mounted() {
    this.loadBorrows()
  },
  methods: {
    async loadBorrows() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current,
          size: this.pagination.size,
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
      this.$confirm(`确定要归还图书《${borrow.bookTitle}》吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.put(`/borrow-records/${borrow.id}/return`)
          this.$message.success('归还图书成功')
          this.loadBorrows()
        } catch (error) {
          console.error('归还图书失败:', error)
          this.$message.error(error.response?.data?.message || '归还图书失败')
        }
      })
    },
    
    async markOverdue(borrow) {
      try {
        await axios.put(`/borrow-records/${borrow.id}/overdue`)
        this.$message.success('标记逾期成功')
        this.loadBorrows()
        if (this.overdueDialogVisible) {
          this.loadOverdueBooks()
        }
      } catch (error) {
        console.error('标记逾期失败:', error)
        this.$message.error(error.response?.data?.message || '标记逾期失败')
      }
    },
    
    async checkOverdueBooks() {
      try {
        const response = await axios.get('/borrow-records/overdue')
        this.overdueBooks = response.data.data || []
        this.overdueDialogVisible = true
        
        if (this.overdueBooks.length === 0) {
          this.$message.info('暂无逾期图书')
        }
      } catch (error) {
        console.error('获取逾期图书失败:', error)
        this.$message.error('获取逾期图书失败')
      }
    },
    
    async loadOverdueBooks() {
      try {
        const response = await axios.get('/borrow-records/overdue')
        this.overdueBooks = response.data.data || []
      } catch (error) {
        console.error('获取逾期图书失败:', error)
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
    }
  }
}
</script>

<style scoped>
.borrow-management {
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

.detail-item {
  margin-bottom: 10px;
  line-height: 1.5;
}
</style>