# Library Management System

## 项目简介
本项目是一个简单的图书管理系统，旨在提高学生的系统编程能力，加深对数据库原理及应用的理解。项目使用Java编程语言和MySQL数据库，提供管理员端的功能，包括图书和读者信息的管理、借还书管理以及统计功能。

## 功能需求
### 登录
- 管理员登录（身份验证）：管理员需输入管理员编号及密码进行登录。
- 首次登录使用初始密码，登录后可修改密码。

### 图书管理
- 图书信息的增、删、改、查。
- 支持模糊查找。

### 读者管理
- 读者信息的增、删、改、查。
- 支持模糊查找。

### 借书管理
- 借书：读者借书不能超过15本。
- 查询某位读者的借书信息（包括读者编号、书号、书名、出版社及借书时间）。

### 还书管理
- 还书：查询某位读者的还书信息（包括读者编号、书号、书名、出版社及还书时间）。

### 统计功能
- 统计某位读者的借书数量。
- 统计某个类别图书的总藏书量。
- 统计某本书的借阅量。

### 数据校验
- 插入或更新数据时，进行数据校验：
  - `price`、`book_total`、`inventory`的值都大于0。
  - `gender`的取值只能是“男”或“女”。
  - `telephone`手机号要符合格式要求。

## 数据表设计
### 管理员表 (manager)
- `manager_id`：管理员编号
- `name`：管理员姓名
- `telephone`：管理员手机号
- `password`：登录密码

### 图书表 (book)
- `book_number`：书号
- `category`：类别
- `book_name`：书名
- `publisher`：出版社
- `author`：作者
- `price`：价格
- `book_total`：总藏书量
- `inventory`：库存

### 读者表 (reader)
- `reader_number`：读者编号
- `name`：姓名
- `department`：单位
- `gender`：性别
- `telephone`：手机号

### 借书表 (borrow_book)
- `reader_number`：读者编号
- `book_number`：书号
- `borrow_time`：借书时间

### 还书表 (return_book)
- `reader_number`：读者编号
- `book_number`：书号
- `return_time`：还书时间

## 使用技术
- 数据库：MySQL
- 编程语言：Java
- 数据库连接：JDBC
- 开发环境：IDEA
- 图形界面：Java Swing


