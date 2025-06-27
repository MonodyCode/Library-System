# 图书管理系统 - 前端

基于 Vue.js 2.x 开发的图书管理系统前端应用，提供管理员和读者两种角色的完整功能界面。

## 技术栈

- **Vue.js 2.6+** - 渐进式 JavaScript 框架
- **Vue Router** - 官方路由管理器
- **Vuex** - 状态管理模式
- **Element UI** - 基于 Vue 2.0 的桌面端组件库
- **Axios** - HTTP 客户端
- **ECharts** - 数据可视化图表库
- **Vue CLI** - Vue.js 开发工具

## 功能特性

### 管理员功能
- 📊 **仪表盘** - 系统概览和数据统计
- 📚 **图书管理** - 图书的增删改查、分类管理
- 👥 **用户管理** - 用户信息管理、权限控制
- 📖 **借阅管理** - 借阅记录管理、归还处理
- 📈 **统计分析** - 借阅趋势、热门图书等数据分析

### 读者功能
- 🔍 **图书浏览** - 图书搜索、分类浏览
- 📋 **借阅记录** - 个人借阅历史查看
- 👤 **个人中心** - 个人信息管理、密码修改
- 🔔 **消息提醒** - 到期提醒、逾期通知

## 项目结构

```
src/
├── assets/          # 静态资源
├── components/      # 公共组件
├── router/          # 路由配置
├── store/           # Vuex 状态管理
├── utils/           # 工具函数
├── views/           # 页面组件
│   ├── admin/       # 管理员页面
│   └── reader/      # 读者页面
├── App.vue          # 根组件
└── main.js          # 入口文件
```

## 开发环境要求

- Node.js >= 14.0.0
- npm >= 6.0.0 或 yarn >= 1.0.0

## 安装和运行

### 1. 安装依赖

```bash
# 使用 npm
npm install

# 或使用 yarn
yarn install
```

### 2. 开发环境运行

```bash
# 启动开发服务器
npm run serve

# 或
yarn serve
```

访问 http://localhost:8080

### 3. 生产环境构建

```bash
# 构建生产版本
npm run build

# 或
yarn build
```

### 4. 代码检查

```bash
# 运行 ESLint
npm run lint

# 或
yarn lint
```

## 配置说明

### 环境变量

在项目根目录创建环境变量文件：

```bash
# .env.development (开发环境)
VUE_APP_API_BASE_URL=http://localhost:8888/api
VUE_APP_TITLE=图书管理系统

# .env.production (生产环境)
VUE_APP_API_BASE_URL=https://your-api-domain.com/api
VUE_APP_TITLE=图书管理系统
```

### 代理配置

开发环境下，API 请求会通过 `vue.config.js` 中的代理配置转发到后端服务：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8888',
    changeOrigin: true,
    pathRewrite: {
      '^/api': '/api'
    }
  }
}
```

## 默认账号

### 管理员账号
- 用户名：`admin`
- 密码：`123456`

### 读者账号
- 用户名：`reader`
- 密码：`123456`

## API 接口

前端通过 Axios 与后端 Spring Boot 应用进行通信，主要接口包括：

- `/api/auth/login` - 用户登录
- `/api/auth/register` - 用户注册
- `/api/books` - 图书管理
- `/api/users` - 用户管理
- `/api/borrow-records` - 借阅记录管理
- `/api/statistics` - 统计数据

## 浏览器支持

- Chrome >= 60
- Firefox >= 60
- Safari >= 12
- Edge >= 79

## 开发规范

### 代码风格
- 使用 ESLint + Prettier 进行代码格式化
- 遵循 Vue.js 官方风格指南
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

### Git 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 部署说明

### 1. 构建项目
```bash
npm run build
```

### 2. 部署到 Web 服务器
将 `dist` 目录下的文件部署到 Nginx、Apache 等 Web 服务器。

### 3. Nginx 配置示例
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend-server:8888;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 常见问题

### 1. 跨域问题
开发环境通过 `vue.config.js` 中的代理解决，生产环境需要在服务器端配置 CORS。

### 2. 路由刷新 404
需要配置服务器支持 History 模式，将所有路由都指向 `index.html`。

### 3. 图片资源加载失败
检查图片路径是否正确，确保静态资源服务器配置正确。

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱：your-email@example.com
- 项目地址：https://github.com/your-username/library-management-frontend

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础的图书管理功能
- 支持管理员和读者两种角色
- 集成数据可视化图表
- 响应式设计支持移动端访问