<template>
  <div class="reader-books">
    <h1>图书浏览</h1>
    
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索图书名称、作者、ISBN"
            clearable
            @keyup.enter.native="searchBooks"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchBooks"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.category" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchForm.author" placeholder="作者" clearable></el-input>
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchForm.publisher" placeholder="出版社" clearable></el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="searchBooks">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 热门图书推荐 -->
    <el-card class="popular-books" v-if="popularBooks.length > 0">
      <div slot="header">
        <span>热门图书推荐</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="4" v-for="book in popularBooks" :key="book.id">
          <div class="popular-book-item" @click="showBookDetail(book)">
            <img :src="book.coverUrl || '/default-book-cover.jpg'" class="book-cover" />
            <div class="book-info">
              <h4>{{ book.title }}</h4>
              <p>{{ book.author }}</p>
              <el-tag size="mini" type="success">热门</el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图书列表 -->
    <el-card class="books-card">
      <div slot="header" class="books-header">
        <span>图书列表</span>
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="grid">网格视图</el-radio-button>
            <el-radio-button label="list">列表视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      
      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="books-grid" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="6" v-for="book in books" :key="book.id" class="book-grid-item">
            <el-card class="book-card" shadow="hover" @click.native="showBookDetail(book)">
              <img :src="book.coverUrl || '/default-book-cover.jpg'" class="book-cover" />
              <div class="book-info">
                <h4 class="book-title">{{ book.title }}</h4>
                <p class="book-author">{{ book.author }}</p>
                <p class="book-category">{{ book.category }}</p>
                <div class="book-status">
                  <el-tag :type="book.availableCount > 0 ? 'success' : 'danger'" size="mini">
                    {{ book.availableCount > 0 ? '可借阅' : '已借完' }}
                  </el-tag>
                  <span class="available-count">剩余: {{ book.availableCount }}</span>
                </div>
                <div class="book-actions">
                  <el-button 
                    type="primary" 
                    size="mini" 
                    :disabled="book.availableCount === 0 || isBookBorrowed(book.id)"
                    @click.stop="borrowBook(book)"
                  >
                    {{ isBookBorrowed(book.id) ? '已借阅' : '借阅' }}
                  </el-button>
                  <el-button size="mini" @click.stop="showBookDetail(book)">详情</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 列表视图 -->
      <div v-else class="books-list" v-loading="loading">
        <el-table :data="books" style="width: 100%">
          <el-table-column label="封面" width="80">
            <template slot-scope="scope">
              <img :src="scope.row.coverUrl || '/default-book-cover.jpg'" class="table-book-cover" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="书名" width="200"></el-table-column>
          <el-table-column prop="author" label="作者" width="120"></el-table-column>
          <el-table-column prop="category" label="分类" width="100"></el-table-column>
          <el-table-column prop="publisher" label="出版社" width="120"></el-table-column>
          <el-table-column prop="publishDate" label="出版日期" width="120"></el-table-column>
          <el-table-column label="状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.availableCount > 0 ? 'success' : 'danger'" size="mini">
                {{ scope.row.availableCount > 0 ? '可借阅' : '已借完' }}
              </el-tag>
              <br>
              <span class="available-count">剩余: {{ scope.row.availableCount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button 
                type="primary" 
                size="mini" 
                :disabled="scope.row.availableCount === 0 || isBookBorrowed(scope.row.id)"
                @click="borrowBook(scope.row)"
              >
                {{ isBookBorrowed(scope.row.id) ? '已借阅' : '借阅' }}
              </el-button>
              <el-button size="mini" @click="showBookDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[12, 24, 48, 96]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      >
      </el-pagination>
    </el-card>

    <!-- 图书详情对话框 -->
    <el-dialog
      title="图书详情"
      :visible.sync="detailDialogVisible"
      width="600px"
    >
      <div v-if="selectedBook" class="book-detail">
        <el-row :gutter="20">
          <el-col :span="8">
            <img :src="selectedBook.coverUrl || '/default-book-cover.jpg'" class="detail-cover" />
          </el-col>
          <el-col :span="16">
            <div class="detail-info">
              <h2>{{ selectedBook.title }}</h2>
              <div class="detail-item"><strong>作者：</strong>{{ selectedBook.author }}</div>
              <div class="detail-item"><strong>ISBN：</strong>{{ selectedBook.isbn }}</div>
              <div class="detail-item"><strong>分类：</strong>{{ selectedBook.category }}</div>
              <div class="detail-item"><strong>出版社：</strong>{{ selectedBook.publisher }}</div>
              <div class="detail-item"><strong>出版日期：</strong>{{ selectedBook.publishDate }}</div>
              <div class="detail-item"><strong>价格：</strong>¥{{ selectedBook.price }}</div>
              <div class="detail-item">
                <strong>状态：</strong>
                <el-tag :type="selectedBook.availableCount > 0 ? 'success' : 'danger'" size="small">
                  {{ selectedBook.availableCount > 0 ? '可借阅' : '已借完' }}
                </el-tag>
              </div>
              <div class="detail-item"><strong>可借数量：</strong>{{ selectedBook.availableCount }}</div>
              <div class="detail-actions">
                <el-button 
                  type="primary" 
                  :disabled="selectedBook.availableCount === 0 || isBookBorrowed(selectedBook.id)"
                  @click="borrowBook(selectedBook)"
                >
                  {{ isBookBorrowed(selectedBook.id) ? '已借阅' : '借阅图书' }}
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="book-description">
          <h3>图书简介</h3>
          <p>{{ selectedBook.description || '暂无简介' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'ReaderBooks',
  data() {
    return {
      loading: false,
      books: [],
      popularBooks: [],
      categories: [],
      borrowedBooks: [], // 用户已借阅的图书ID列表
      viewMode: 'grid',
      searchForm: {
        keyword: '',
        category: '',
        author: '',
        publisher: ''
      },
      pagination: {
        current: 1,
        size: 12,
        total: 0
      },
      detailDialogVisible: false,
      selectedBook: null
    }
  },
  computed: {
    ...mapGetters(['user'])
  },
  mounted() {
    this.loadBooks()
    this.loadCategories()
    this.loadPopularBooks()
    this.loadBorrowedBooks()
  },
  methods: {
    async loadBooks() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        
        const response = await axios.get('/books', { params })
        const data = response.data.data
        
        this.books = data.records
        this.pagination.total = data.total
      } catch (error) {
        console.error('加载图书列表失败:', error)
        this.$message.error('加载图书列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadCategories() {
      try {
        const response = await axios.get('/books/categories')
        this.categories = response.data.data || []
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
    async loadPopularBooks() {
      try {
        const response = await axios.get('/books/popular', { params: { limit: 6 } })
        this.popularBooks = response.data.data || []
      } catch (error) {
        console.error('加载热门图书失败:', error)
      }
    },
    
    async loadBorrowedBooks() {
      try {
        const response = await axios.get(`/borrow-records/user/${this.user.id}/current`)
        this.borrowedBooks = (response.data.data || []).map(record => record.bookId)
      } catch (error) {
        console.error('加载已借阅图书失败:', error)
      }
    },
    
    searchBooks() {
      this.pagination.current = 1
      this.loadBooks()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        category: '',
        author: '',
        publisher: ''
      }
      this.searchBooks()
    },
    
    handleSizeChange(val) {
      this.pagination.size = val
      this.loadBooks()
    },
    
    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadBooks()
    },
    
    showBookDetail(book) {
      this.selectedBook = book
      this.detailDialogVisible = true
    },
    
    isBookBorrowed(bookId) {
      return this.borrowedBooks.includes(bookId)
    },
    
    async borrowBook(book) {
      if (book.availableCount === 0) {
        this.$message.warning('该图书已借完')
        return
      }
      
      if (this.isBookBorrowed(book.id)) {
        this.$message.warning('您已借阅过该图书')
        return
      }
      
      this.$confirm(`确定要借阅图书《${book.title}》吗？`, '确认借阅', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(async () => {
        try {
          await axios.post('/borrow-records/borrow', {
            bookId: book.id
          })
          
          this.$message.success('借阅成功')
          this.loadBooks()
          this.loadBorrowedBooks()
          
          if (this.detailDialogVisible) {
            this.detailDialogVisible = false
          }
        } catch (error) {
          console.error('借阅失败:', error)
          this.$message.error(error.response?.data?.message || '借阅失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.reader-books {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.popular-books {
  margin-bottom: 20px;
}

.popular-book-item {
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.popular-book-item:hover {
  transform: translateY(-5px);
}

.books-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.books-grid {
  min-height: 400px;
}

.book-grid-item {
  margin-bottom: 20px;
}

.book-card {
  height: 400px;
  cursor: pointer;
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.table-book-cover {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
}

.book-info {
  padding: 10px;
  text-align: center;
}

.book-title {
  margin: 10px 0 5px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
}

.book-category {
  margin: 5px 0;
  color: #909399;
  font-size: 12px;
}

.book-status {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.available-count {
  font-size: 12px;
  color: #909399;
}

.book-actions {
  margin-top: 10px;
}

.book-actions .el-button {
  margin: 0 5px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.book-detail {
  padding: 20px;
}

.detail-cover {
  width: 100%;
  max-width: 200px;
  border-radius: 8px;
}

.detail-info {
  padding-left: 20px;
}

.detail-info h2 {
  margin: 0 0 20px 0;
  color: #303133;
}

.detail-item {
  margin-bottom: 10px;
  line-height: 1.5;
}

.detail-actions {
  margin-top: 20px;
}

.book-description {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.book-description h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.book-description p {
  line-height: 1.6;
  color: #606266;
}
</style>