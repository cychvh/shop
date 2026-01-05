# 电商系统（Shop）

## 项目简介

这是一个 **基于 Spring Boot + MyBatis-Plus + MySQL + Redis + JWT + Vue** 技术栈实现的电商系统，采用 **前后端分离架构**。  
系统实现了用户认证、商品展示、购物车管理、订单创建等核心功能，并运用缓存与权限机制提升性能与安全性，作为 Java 后端实习项目展示。

---

## 技术栈

- **后端**：Spring Boot, MyBatis-Plus, MySQL, Redis, JWT  
- **前端**：Vue, Vue Router, Axios, Element-UI  
- **构建工具**：Maven（后端）、Node/npm（前端）  
- **开发工具**：IntelliJ IDEA, VS Code

---

## 功能模块

### 1. 用户模块
- 用户注册、登录  
- 基于 **JWT + Redis** 实现无状态身份认证与权限校验
- 用户的信息管理
- 个人信息的管理

### 2. 商品模块
- 商品浏览展示  
- 商品搜索与分页查询  

### 3. 购物车模块
- 添加商品到购物车  
- 修改商品数量、删除购物车项  

### 4. 订单模块
- 创建订单、事务控制保证数据一致性  
- 订单状态管理

### 5.公告模块
- 基于用户和商家的类型不同 可以分别发送公告
- 公告的信息管理

### 6.数据分析
- 基于售卖的数据，商家进行数据的分析


### 7. 系统模块
- 按 **RESTful 规范设计接口**  
- 支持前后端分离，提高开发和维护效率  

---

## 项目结构

shop/

├─ backend/ # 后端根目录

│ └─ xiangwei/ # 后端实际代码所在

│   ├─ src/main/java/... # Java 代码

│   ├─ src/main/resources/ # 配置文件（application.yml）

│   └─ pom.xml # Maven 配置

├─ frontend/ # 前端根目录

│   └─ xiangwei-vue/

│     └─ xiangwei_vue/ # 前端实际代码

│       ├─ src/ # Vue 组件、页面、路由

│     └─ package.json # 前端依赖管理

├─ README.md

└─ .gitignore

---
## 运行步骤
### 1.克隆项目
git clone https://github.com/cychvh/shop.git
### 2.启动后端
- 配置数据库连接和Redis配置
### 3.启动前端
- 进入前端的目录
- 安装依赖
npm install
- 启动前端
npm run serve

## 技术亮点
- JWT认证：通过 JWT + Redis 实现权限控制，减轻服务端会话压力

- Redis 缓存：缓存用户信息和高频访问数据，提高系统性能

- 事务控制与参数校验：保证订单及核心业务操作的数据一致性

- RESTful API 设计：接口规范清晰，支持前后端分离架构

- 前后端分离：前端可独立开发和部署，提高可维护性

---

## Api示例

POST /api/user/login

参数
{

  "username": "test",
  
  "password": "123456"
  
}
返回：
{

  {
  
    "code": "200",
    
    "msg": "success",
    
    "data": {
    
        "user": {
        
            "id": 2,
            
            "username": "jack",
            
            "password": null,
            
            "phone": "12345678911",
            
            "type": 1,
            
            "address": "广州"
            
        },
        
        "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqYWNrIiwiaWF0IjoxNzY3NTg0MzU3LCJleHAiOjE3Njc2NzA3NTd9.Nk2KhQUeIxKnOc8K3HESlxASPwXqwWfo3YZXO3BaQy2YWE_KWnTIDjY4CPPnMJJ5"
        
    }
 }
}

---

## 开源与贡献

- 项目开源，欢迎学习和参考

- GitHub 仓库地址：https://github.com/cychvh/shop

- 如有建议或改进，欢迎提交 Issue 或 Pull Request
 


