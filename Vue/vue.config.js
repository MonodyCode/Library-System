const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  // 基础路径
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  
  // 输出目录
  outputDir: 'dist',
  
  // 静态资源目录
  assetsDir: 'static',
  
  // 生产环境是否生成 sourceMap 文件
  productionSourceMap: false,
  
  // 开发服务器配置
  devServer: {
    port: 8080,
    host: 'localhost',
    open: true, // 自动打开浏览器
    hot: true,  // 热重载
    
    // 代理配置，用于解决跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:8888', // Spring Boot 后端地址
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api': '/api' // 保持 /api 前缀
        }
      }
    },
    
    // 客户端日志等级
    client: {
      logging: 'info',
      overlay: {
        errors: true,
        warnings: false
      }
    }
  },
  
  // CSS 相关配置
  css: {
    // 是否使用css分离插件 ExtractTextPlugin
    extract: process.env.NODE_ENV === 'production',
    
    // 开启 CSS source maps
    sourceMap: false,
    
    // css预设器配置项
    loaderOptions: {
      scss: {
        // 全局引入变量和混入
        additionalData: `
          // 这里可以引入全局的 SCSS 变量和混入
        `
      }
    }
  },
  
  // webpack 配置
  configureWebpack: {
    // 性能提示
    performance: {
      hints: false
    },
    
    // 优化配置
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            name: 'chunk-vendors',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial'
          },
          elementUI: {
            name: 'chunk-elementUI',
            priority: 20,
            test: /[\\/]node_modules[\\/]_?element-ui(.*)/
          },
          echarts: {
            name: 'chunk-echarts',
            priority: 20,
            test: /[\\/]node_modules[\\/]_?echarts(.*)/
          }
        }
      }
    }
  },
  
  // webpack-chain 配置
  chainWebpack: config => {
    // 设置别名
    config.resolve.alias
      .set('@', require('path').resolve(__dirname, 'src'))
      .set('components', require('path').resolve(__dirname, 'src/components'))
      .set('views', require('path').resolve(__dirname, 'src/views'))
      .set('assets', require('path').resolve(__dirname, 'src/assets'))
      .set('utils', require('path').resolve(__dirname, 'src/utils'))
    
    // 移除 prefetch 插件，避免加载不必要的资源
    config.plugins.delete('prefetch')
    
    // 压缩图片
    config.module
      .rule('images')
      .test(/\.(gif|png|jpe?g|svg)(\?.*)?$/)
      .use('image-webpack-loader')
      .loader('image-webpack-loader')
      .options({
        disable: process.env.NODE_ENV === 'development'
      })
      .end()
    
    // 生产环境配置
    if (process.env.NODE_ENV === 'production') {
      // 移除 console.log
      config.optimization.minimizer('terser').tap(args => {
        args[0].terserOptions.compress.drop_console = true
        args[0].terserOptions.compress.drop_debugger = true
        return args
      })
    }
  },
  
  // 第三方插件配置
  pluginOptions: {
    // 这里可以配置各种插件选项
  },
  
  // 是否为 Babel 或 TypeScript 使用 thread-loader
  parallel: require('os').cpus().length > 1,
  
  // PWA 插件相关配置
  pwa: {
    name: '图书管理系统',
    themeColor: '#409eff',
    msTileColor: '#000000',
    appleMobileWebAppCapable: 'yes',
    appleMobileWebAppStatusBarStyle: 'black',
    
    // 配置 workbox 插件
    workboxPluginMode: 'InjectManifest',
    workboxOptions: {
      swSrc: 'src/sw.js'
    }
  }
})