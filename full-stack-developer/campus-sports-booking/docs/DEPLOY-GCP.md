# Google Cloud 部署指南

> 使用 Google Cloud 部署校园运动场地预约系统
>
> - 后端：Cloud Run（Spring Boot + Docker）
> - 数据库：Cloud SQL（MySQL 8.0）
> - 前端：Firebase Hosting（Vue 3 静态站点）

---

## 目录

1. [创建 Cloud SQL MySQL](#1-创建-cloud-sql-mysql)
2. [初始化数据库](#2-初始化数据库)
3. [部署后端到 Cloud Run](#3-部署后端到-cloud-run)
4. [获取 Cloud Run 服务地址](#4-获取-cloud-run-服务地址)
5. [部署前端到 Firebase Hosting](#5-部署前端到-firebase-hosting)

---

## 1. 创建 Cloud SQL MySQL

### 1.1 在 Google Cloud Console 创建实例

访问 [Cloud SQL](https://console.cloud.google.com/sql/) 或执行：

```bash
# 设置项目（替换 YOUR_PROJECT_ID）
gcloud config set project YOUR_PROJECT_ID

# 创建 MySQL 实例（约3-5分钟）
gcloud sql instances create campus-sports-mysql \
    --database-version=MYSQL_8_0 \
    --region=asia-east1 \
    --tier=db-f1-micro \
    --storage-type=SSD \
    --storage-size=10GB \
    --availability-type=ZONAL
```

### 1.2 创建数据库和用户

```bash
# 创建数据库
gcloud sql databases create campus_sports --instance=campus-sports-mysql

# 创建用户（记住用户名和密码，后续需要）
gcloud sql users create campus_sports_user \
    --instance=campus-sports-mysql \
    --password=YOUR_STRONG_PASSWORD
```

### 1.3 配置专用网络（允许 Cloud Run 访问）

```bash
# 获取项目网络
gcloud compute networks list

# 创建专用网络（仅内部通信）
gcloud compute networks create campus-sports-network \
    --subnet-mode=custom

# 创建子网
gcloud compute networks subnets create campus-sports-subnet \
    --network=campus-sports-network \
    --region=asia-east1 \
    --range=10.0.0.0/24

# 开启私有IP
gcloud sql instances patch campus-sports-mysql \
    --network=PRIVATE_NETWORK \
    --no-assign-ip
```

### 1.4 获取连接信息

```bash
gcloud sql instances describe campus-sports-mysql --format="value(connectionName)"
# 输出格式: YOUR_PROJECT:asia-east1:campus-sports-mysql
```

---

## 2. 初始化数据库

### 方式一：本地 MySQL 客户端连接

```bash
# 通过 Cloud SQL Auth Proxy 连接
./cloud-sql-proxy --port 3306 YOUR_PROJECT:asia-east1:campus-sports-mysql &

# 连接数据库
mysql -h 127.0.0.1 -P 3306 -u campus_sports_user -p campus_sports < scripts/campus-sports-init.sql
```

### 方式二：Google Cloud Console

1. 进入 Cloud SQL 实例页面
2. 点击「导入」按钮
3. 上传 `scripts/campus-sports-init.sql`
4. 选择目标数据库 `campus_sports`
5. 点击导入

---

## 3. 部署后端到 Cloud Run

### 3.1 构建并推送 Docker 镜像

```bash
cd backend

# 配置 Docker（使用 gcloud 作为 Docker 凭证帮助程序）
gcloud auth configure-docker asia-east1-docker.pkg.dev

# 创建 Artifact Registry 仓库
gcloud artifacts repositories create campus-sports-repo \
    --repository-format=docker \
    --location=asia-east1

# 构建并推送镜像
docker build -t asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest .
docker push asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest
```

### 3.2 部署到 Cloud Run

```bash
# 获取 Cloud SQL 连接名称
CONNECTION_NAME=$(gcloud sql instances describe campus-sports-mysql --format="value(connectionName)")

# 部署（同时授权 Cloud Run 访问 Cloud SQL）
gcloud run deploy campus-sports-backend \
    --image=asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest \
    --region=asia-east1 \
    --platform=managed \
    --allow-unauthenticated \
    --add-cloudsql-instances=$CONNECTION_NAME \
    --set-env-vars="DB_HOST=/cloudsql/$CONNECTION_NAME,DB_PORT=3306,DB_NAME=campus_sports,DB_USERNAME=campus_sports_user,DB_PASSWORD=YOUR_PASSWORD,JWT_SECRET=your-production-jwt-secret-at-least-32-chars" \
    --memory=512Mi \
    --cpu=1 \
    --port=8080 \
    --min-instances=0 \
    --max-instances=10
```

### 3.3 验证部署

```bash
# 检查服务状态
gcloud run services describe campus-sports-backend --region=asia-east1

# 测试 API（获取服务 URL）
SERVICE_URL=$(gcloud run services describe campus-sports-backend --region=asia-east1 --format="value(status.url)")
curl -s $SERVICE_URL/api/auth/me
```

---

## 4. 获取 Cloud Run 服务地址

```bash
gcloud run services describe campus-sports-backend --region=asia-east1 --format="value(status.url)"
```

输出示例：
```
https://campus-sports-backend-xxxxx-as.a.run.app
```

**注意**：API 基础地址为：`https://campus-sports-backend-xxxxx-as.a.run.app/api`

---

## 5. 部署前端到 Firebase Hosting

### 5.1 安装 Firebase CLI

```bash
npm install -g firebase-tools
firebase login
```

### 5.2 配置前端环境变量

编辑 `frontend/.env.production`，填入 Cloud Run 服务地址：

```env
VITE_API_BASE_URL=https://campus-sports-backend-xxxxx-as.a.run.app/api
```

### 5.3 构建前端

```bash
cd frontend
npm install
npm run build
```

### 5.4 部署到 Firebase Hosting

```bash
# 初始化 Firebase（仅首次需要）
firebase init hosting
# 选择项目
# 设置 public 目录为 dist
# 选择单页应用配置 (Yes)

# 部署
firebase deploy --only hosting
```

### 5.5 验证部署

访问 Firebase 提供的 Hosting URL：
```
https://YOUR_PROJECT.web.app
```

---

## 环境变量汇总

### 后端（Cloud Run）

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `DB_HOST` | Cloud SQL 连接路径 | `/cloudsql/YOUR_PROJECT:asia-east1:campus-sports-mysql` |
| `DB_PORT` | MySQL 端口 | `3306` |
| `DB_NAME` | 数据库名 | `campus_sports` |
| `DB_USERNAME` | 数据库用户 | `campus_sports_user` |
| `DB_PASSWORD` | 数据库密码 | `YourStrongPassword123` |
| `JWT_SECRET` | JWT 密钥（至少32字符） | `your-production-jwt-secret-change-me` |

### 前端（Firebase）

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `VITE_API_BASE_URL` | 后端 API 地址 | `https://campus-sports-backend-xxxxx-as.a.run.app/api` |

---

## 故障排查

### 后端无法连接数据库
- 确认 Cloud Run 和 Cloud SQL 在同一区域
- 确认私有 IP 已启用
- 检查 Cloud SQL 用户权限

### 前端 API 请求失败
- 确认 VITE_API_BASE_URL 已正确配置
- 检查浏览器控制台 Network 面板
- 确认后端 CORS 配置允许 Firebase Hosting 域名

### 需要重新部署

```bash
# 后端
docker build -t asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest . && \
docker push asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest && \
gcloud run deploy campus-sports-backend --image=asia-east1-docker.pkg.dev/YOUR_PROJECT/campus-sports-repo/backend:latest --region=asia-east1 --platform=managed --allow-unauthenticated

# 前端
cd frontend && npm run build && cd .. && firebase deploy --only hosting
```

---

## 费用估算

| 服务 | 配置 | 预估费用 |
|------|------|----------|
| Cloud SQL | db-f1-micro, 10GB SSD | ~$7/月 |
| Cloud Run | 512Mi, 1 CPU, 0-10实例 | $0 ~ $15/月 |
| Firebase Hosting | 免费套餐 | $0/月 |
| Artifact Registry | 0.5GB存储 | ~$0.03/月 |

**总计约：$7-22/月**（取决于流量）
