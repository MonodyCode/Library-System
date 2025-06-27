<template>
  <div class="book-management">
    <h1>图书管理</h1>
    
    <!-- 搜索和操作区域 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
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
        <el-col :span="6">
          <el-button type="primary" @click="searchBooks">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="showAddDialog">添加图书</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图书列表 -->
    <el-card class="table-card">
      <el-table
        :data="books"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="书名" width="200"></el-table-column>
        <el-table-column prop="author" label="作者" width="120"></el-table-column>
        <el-table-column prop="isbn" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="category" label="分类" width="100"></el-table-column>
        <el-table-column prop="publisher" label="出版社" width="120"></el-table-column>
        <el-table-column prop="totalCount" label="总数量" width="80"></el-table-column>
        <el-table-column prop="availableCount" label="可借数量" width="80"></el-table-column>
        <el-table-column prop="price" label="价格" width="80">
          <template slot-scope="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button size="mini" type="info" @click="showDetailDialog(scope.row)">详情</el-button>
            <el-button size="mini" type="danger" @click="deleteBook(scope.row)">删除</el-button>
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

    <!-- 添加/编辑图书对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        :model="bookForm"
        :rules="bookRules"
        ref="bookForm"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="bookForm.title"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="bookForm.author"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="bookForm.isbn"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="bookForm.category" placeholder="选择分类">
                <el-option
                  v-for="category in categories"
                  :key="category"
                  :label="category"
                  :value="category"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出版社" prop="publisher">
              <el-input v-model="bookForm.publisher"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期" prop="publishDate">
              <el-date-picker
                v-model="bookForm.publishDate"
                type="date"
                placeholder="选择日期"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="bookForm.price" :min="0" :precision="2"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总数量" prop="totalCount">
              <el-input-number v-model="bookForm.totalCount" :min="1"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="描述" prop="description">
          <el-input
            type="textarea"
            :rows="3"
            v-model="bookForm.description"
            placeholder="请输入图书描述"
          >
          </el-input>
        </el-form-item>
        
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            :headers="uploadHeaders"
          >
            <img v-if="bookForm.coverUrl" :src="bookForm.coverUrl" class="cover">
            <i v-else class="el-icon-plus cover-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 图书详情对话框 -->
    <el-dialog
      title="图书详情"
      :visible.sync="detailDialogVisible"
      width="500px"
    >
      <div v-if="selectedBook">
        <div class="detail-item">
          <img v-if="selectedBook.coverUrl" :src="selectedBook.coverUrl" class="detail-cover">
        </div>
        <div class="detail-item"><strong>书名：</strong>{{ selectedBook.title }}</div>
        <div class="detail-item"><strong>作者：</strong>{{ selectedBook.author }}</div>
        <div class="detail-item"><strong>ISBN：</strong>{{ selectedBook.isbn }}</div>
        <div class="detail-item"><strong>分类：</strong>{{ selectedBook.category }}</div>
        <div class="detail-item"><strong>出版社：</strong>{{ selectedBook.publisher }}</div>
        <div class="detail-item"><strong>出版日期：</strong>{{ selectedBook.publishDate }}</div>
        <div class="detail-item"><strong>价格：</strong>¥{{ selectedBook.price }}</div>
        <div class="detail-item"><strong>总数量：</strong>{{ selectedBook.totalCount }}</div>
        <div class="detail-item"><strong>可借数量：</strong>{{ selectedBook.availableCount }}</div>
        <div class="detail-item"><strong>描述：</strong>{{ selectedBook.description }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'BookManagement',
  data() {
    return {
      loading: false,
      books: [],
      categories: [],
      searchForm: {
        keyword: '',
        category: '',
        author: '',
        publisher: ''
      },
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      detailDialogVisible: false,
      isEdit: false,
      selectedBook: null,
      bookForm: {
        id: null,
        title: '',
        author: '',
        isbn: '',
        category: '',
        publisher: '',
        publishDate: '',
        price: 0,
        totalCount: 1,
        description: '',
        coverUrl: ''
      },
      bookRules: {
        title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
        author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
        isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        totalCount: [{ required: true, message: '请输入总数量', trigger: 'blur' }]
      },
      uploadUrl: process.env.VUE_APP_API_BASE_URL + '/books/upload-cover',
      uploadHeaders: {
        'Authorization': 'Bearer ' + this.$store.getters.token
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑图书' : '添加图书'
    }
  },
  mounted() {
    this.loadBooks()
    this.loadCategories()
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
    
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
    },
    
    showEditDialog(book) {
      this.isEdit = true
      this.bookForm = { ...book }
      this.dialogVisible = true
    },
    
    showDetailDialog(book) {
      this.selectedBook = book
      this.detailDialogVisible = true
    },
    
    resetForm() {
      this.bookForm = {
        id: null,
        title: '',
        author: '',
        isbn: '',
        category: '',
        publisher: '',
        publishDate: '',
        price: 0,
        totalCount: 1,
        description: '',
        coverUrl: ''
      }
      if (this.$refs.bookForm) {
        this.$refs.bookForm.resetFields()
      }
    },
    
    submitForm() {
      this.$refs.bookForm.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await axios.put(`/books/${this.bookForm.id}`, this.bookForm)
              this.$message.success('更新图书成功')
            } else {
              await axios.post('/books', this.bookForm)
              this.$message.success('添加图书成功')
            }
            
            this.dialogVisible = false
            this.loadBooks()
          } catch (error) {
            console.error('保存图书失败:', error)
            this.$message.error(error.response?.data?.message || '保存图书失败')
          }
        }
      })
    },
    
    deleteBook(book) {
      this.$confirm(`确定要删除图书《${book.title}》吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`/books/${book.id}`)
          this.$message.success('删除图书成功')
          this.loadBooks()
        } catch (error) {
          console.error('删除图书失败:', error)
          this.$message.error(error.response?.data?.message || '删除图书失败')
        }
      })
    },
    
    handleCoverSuccess(res) {
      if (res.code === 200) {
        this.bookForm.coverUrl = res.data
        this.$message.success('封面上传成功')
      } else {
        this.$message.error(res.message || '封面上传失败')
      }
    },
    
    beforeCoverUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isJPG) {
        this.$message.error('封面图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('封面图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>

<style scoped>
.book-management {
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

.cover-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.cover-uploader .el-upload:hover {
  border-color: #409EFF;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.cover {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.detail-cover {
  width: 150px;
  height: 200px;
  object-fit: cover;
  margin-bottom: 15px;
}

.detail-item {
  margin-bottom: 10px;
  line-height: 1.5;
}
</style>