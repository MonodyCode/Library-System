-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型：1-读者，2-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN号',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    category VARCHAR(50) COMMENT '分类',
    price DECIMAL(10,2) COMMENT '价格',
    total_count INT NOT NULL DEFAULT 1 COMMENT '总数量',
    available_count INT NOT NULL DEFAULT 1 COMMENT '可借数量',
    description TEXT COMMENT '描述',
    cover_image VARCHAR(500) COMMENT '封面图片路径',
    publish_date DATE COMMENT '出版日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category),
    FULLTEXT idx_search (title, author, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-借阅中，2-已归还，3-逾期',
    borrow_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_time DATETIME COMMENT '归还时间',
    due_time DATETIME NOT NULL COMMENT '应还时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_time (borrow_time),
    INDEX idx_due_time (due_time),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 插入管理员用户数据
INSERT INTO users (username, password, real_name, email, phone, user_type) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', '系统管理员', 'admin@library.com', '13800138000', 2),
('manager', '1d0258c2440a8d19e716292b231e3190', '图书管理员', 'manager@library.com', '13800138001', 2);

-- 插入读者用户数据
INSERT INTO users (username, password, real_name, email, phone, user_type) VALUES
('reader1', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@example.com', '13800138002', 1),
('reader2', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@example.com', '13800138003', 1),
('reader3', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@example.com', '13800138004', 1);

-- 插入图书数据
INSERT INTO books (isbn, title, author, publisher, category, price, total_count, available_count, description, publish_date) VALUES
('9787111544937', 'Java核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', '计算机', 89.00, 5, 5, 'Java编程经典教材，全面介绍Java语言的核心概念和技术。', '2018-01-01'),
('9787115428028', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '计算机', 69.00, 3, 3, 'Python编程入门经典，适合初学者学习Python编程。', '2016-07-01'),
('9787121313462', 'Spring Boot实战', 'Craig Walls', '电子工业出版社', '计算机', 79.00, 4, 4, 'Spring Boot开发实战指南，涵盖微服务开发的方方面面。', '2017-03-01'),
('9787111421900', '算法导论', 'Thomas H. Cormen', '机械工业出版社', '计算机', 128.00, 2, 2, '计算机算法经典教材，深入讲解各种算法的设计与分析。', '2013-01-01'),
('9787115404831', 'MySQL必知必会', 'Ben Forta', '人民邮电出版社', '数据库', 49.00, 3, 3, 'MySQL数据库学习经典，快速掌握MySQL核心技术。', '2015-05-01'),
('9787111558422', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '计算机', 139.00, 2, 2, '计算机系统经典教材，深入理解计算机系统的工作原理。', '2016-11-01'),
('9787115429827', 'Vue.js实战', '梁灏', '人民邮电出版社', '前端开发', 79.00, 3, 3, 'Vue.js前端框架实战指南，从基础到高级应用。', '2017-09-01'),
('9787121284000', 'Redis设计与实现', '黄健宏', '电子工业出版社', '数据库', 79.00, 2, 2, 'Redis内部机制深度解析，理解Redis的设计思想。', '2014-06-01');

-- 插入借阅记录数据
INSERT INTO borrow_records (user_id, book_id, status, borrow_time, due_time) VALUES
(3, 1, 1, '2024-01-15 10:00:00', '2024-02-14 10:00:00'),
(4, 2, 2, '2024-01-10 14:30:00', '2024-02-09 14:30:00'),
(5, 3, 1, '2024-01-20 09:15:00', '2024-02-19 09:15:00'),
(3, 5, 2, '2024-01-05 16:45:00', '2024-02-04 16:45:00'),
(4, 7, 1, '2024-01-25 11:20:00', '2024-02-24 11:20:00');

-- 更新已归还记录的归还时间
UPDATE borrow_records SET return_time = '2024-01-30 15:20:00' WHERE id = 2;
UPDATE borrow_records SET return_time = '2024-01-28 10:30:00' WHERE id = 4;