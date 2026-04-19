# Node.js 后端 + Supabase 学习笔记

> 学习时间：2026-04-16
> 状态：持续更新中

---

## Node.js 后端核心

### 基础架构

```
请求 → 路由 → 中间件 → 控制器 → 服务 → 数据库
                ↓
            错误处理
```

### 常用框架

| 框架 | 特点 | 学习曲线 |
|------|------|----------|
| Express | 简洁、灵活 | 低 |
| Fastify | 性能高、Schema 验证 | 低 |
| NestJS | 面向对象、装饰器 | 中 |
| Koa | 洋葱模型 | 低 |

### Express 基础

```javascript
const express = require('express');
const app = express();

// 中间件
app.use(express.json());           // 解析 JSON
app.use(cors());                   // 跨域
app.use(authMiddleware);           // 自定义中间件

// 路由
app.get('/api/users', getUsers);
app.post('/api/users', createUser);
app.put('/api/users/:id', updateUser);
app.delete('/api/users/:id', deleteUser);

// 启动
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Running on ${PORT}`));
```

### 路由与控制器

```javascript
// routes/users.js
const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

router.get('/', userController.getUsers);
router.post('/', userController.createUser);

module.exports = router;

// controllers/userController.js
exports.getUsers = async (req, res) => {
  try {
    const users = await UserService.findAll();
    res.json({ success: true, data: users });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};
```

### 错误处理

```javascript
// 统一错误处理中间件
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(err.status || 500).json({
    success: false,
    error: err.message || 'Internal Server Error'
  });
});

// 手动抛出错误
throw new BadRequestError('Invalid input');
```

---

## RESTful API 设计

### URL 规范

| 方法 | URL | 描述 |
|------|-----|------|
| GET | /api/users | 获取用户列表 |
| GET | /api/users/:id | 获取单个用户 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/:id | 更新用户 |
| DELETE | /api/users/:id | 删除用户 |

### 状态码

| 状态码 | 含义 |
|--------|------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Error |

---

## Node.js 进阶

### 认证 (JWT)

```javascript
const jwt = require('jsonwebtoken');

// 生成 Token
const token = jwt.sign(
  { userId: user.id, role: user.role },
  process.env.JWT_SECRET,
  { expiresIn: '7d' }
);

// 验证中间件
const auth = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'No token' });
  
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    res.status(401).json({ error: 'Invalid token' });
  }
};
```

### 文件上传 (Multer)

```javascript
const multer = require('multer');
const upload = multer({ dest: 'uploads/' });

app.post('/api/upload', upload.single('file'), (req, res) => {
  res.json({ filename: req.file.filename });
});
```

### 队列 (Bull)

```javascript
const Queue = require('bull');
const emailQueue = new Queue('email');

// 生产者
emailQueue.add({ to: 'user@example.com', subject: 'Hello' });

// 消费者
emailQueue.process(async (job) => {
  await sendEmail(job.data);
});
```

### 日志 (Winston)

```javascript
const winston = require('winston');

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.json(),
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' }),
  ],
});
```

---

## Supabase

### 概述

Supabase 是 **Firebase 开源替代品**，提供：
- PostgreSQL 数据库
- 即时 API（PostgREST）
- Auth 身份认证
- Storage 文件存储
- Realtime 实时订阅
- Edge Functions（Serverless）

### 快速开始

```bash
# 安装
npm install @supabase/supabase-js

# 连接
import { createClient } from '@supabase/supabase-js'

const supabase = createClient(
  'https://your-project.supabase.co',
  'your-anon-key'
)
```

### 数据库操作

```javascript
// 插入
const { data, error } = await supabase
  .from('users')
  .insert({ name: 'John', email: 'john@example.com' })
  .select()

// 查询
const { data, error } = await supabase
  .from('users')
  .select('id, name, profiles(*)')
  .eq('status', 'active')
  .order('created_at', { ascending: false })
  .limit(10)

// 更新
const { data, error } = await supabase
  .from('users')
  .update({ status: 'inactive' })
  .eq('id', userId)

// 删除
const { error } = await supabase
  .from('users')
  .delete()
  .eq('id', userId)
```

### 关系查询

```javascript
// 一对一
const { data } = await supabase
  .from('profiles')
  .select('*, users(name, email)')

// 一对多
const { data } = await supabase
  .from('posts')
  .select('*, comments(*)')

// 多对多
const { data } = await supabase
  .from('users')
  .select('*, teams(*), skills(*)')
```

### Auth 认证

```javascript
// 邮箱注册
const { data, error } = await supabase.auth.signUp({
  email: 'user@example.com',
  password: 'password123'
})

// 登录
const { data, error } = await supabase.auth.signInWithPassword({
  email: 'user@example.com',
  password: 'password123'
})

// 获取当前用户
const { data: { user } } = await supabase.auth.getUser()

// 登出
await supabase.auth.signOut()

// 监听状态变化
supabase.auth.onAuthStateChange((event, session) => {
  console.log(event, session)
})
```

### Storage 文件存储

```javascript
const bucket = 'avatars'

// 上传
const { data, error } = await supabase.storage
  .from(bucket)
  .upload('public/avatar.jpg', file)

// 获取公开 URL
const { data } = supabase.storage
  .from(bucket)
  .getPublicUrl('public/avatar.jpg')

// 删除
await supabase.storage.from(bucket).remove(['public/avatar.jpg'])
```

### Realtime 实时订阅

```javascript
// 监听 INSERT
const channel = supabase
  .channel('messages')
  .on('postgres_changes', 
    { event: 'INSERT', schema: 'public', table: 'messages' },
    (payload) => console.log('New message:', payload.new)
  )
  .subscribe()

// 离开时取消订阅
channel.unsubscribe()
```

### Row Level Security (RLS)

```sql
-- 只允许用户查看自己的数据
CREATE POLICY "Users can view own data" ON users
  FOR SELECT USING (auth.uid() = user_id);

-- 只允许用户更新自己的数据
CREATE POLICY "Users can update own data" ON users
  FOR UPDATE USING (auth.uid() = user_id);

-- 公开读取（示例）
CREATE POLICY "Public profiles are viewable by everyone" ON profiles
  FOR SELECT USING (visibility = 'public');
```

---

## 常用命令

```bash
# Node.js 项目初始化
npm init -y
npm install express cors dotenv
npm install -D nodemon

# Supabase CLI
npm install -g supabase
supabase init
supabase start
supabase status

# 数据库迁移
supabase db push
supabase db reset
```

---

## 项目结构示例

```
my-project/
├── src/
│   ├── index.js          # 入口
│   ├── routes/           # 路由
│   ├── controllers/     # 控制器
│   ├── services/         # 业务逻辑
│   ├── middleware/       # 中间件
│   ├── models/          # 数据模型
│   └── utils/           # 工具函数
├── supabase/
│   └── migrations/      # 数据库迁移
├── storage/             # 本地文件
└── .env
```

---

## 资源链接

- Node.js：https://nodejs.org
- Express：https://expressjs.com
- Fastify：https://fastify.dev
- Supabase：https://supabase.com
- Supabase Dart SDK：https://supabase.com/docs/dart

---

## 待深入

- [ ] NestJS 框架
- [ ] Prisma ORM
- [ ] 数据库事务
- [ ] WebSocket 实时通信
- [ ] Redis 缓存
- [ ] Docker 部署
