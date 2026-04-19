# 校园运动场地预约系统

> Vue3 + Spring Boot + MyBatis-Plus + MySQL

## 项目简介

面向校园的运动场地在线预约系统，支持场地分类浏览、时段预约、用户管理、角色权限控制。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + TypeScript + Pinia + Axios |
| 后端 | Spring Boot 3 + MyBatis-Plus + Spring Security |
| 数据库 | MySQL 8.0 |
| 鉴权 | JWT + Spring Security |
| API 文档 | Knife4j |

## 项目结构

```
campus-sports-booking/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
└── scripts/          # 数据库脚本
```

## 快速启动

### 1. 数据库初始化

```bash
mysql -u root -p < scripts/campus-sports-init.sql
```

### 2. 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| student001 | 123456 | 普通用户 |

## API 文档

启动后端后访问：http://localhost:8080/doc.html
