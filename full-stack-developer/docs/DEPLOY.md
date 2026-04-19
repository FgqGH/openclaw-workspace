# 部署工作指南

> 通用部署流程参考手册

---

## 📋 部署前检查清单

### 1. 环境确认
- [ ] GCP 项目 ID 确认
- [ ] GCP IAM 权限确认（Service Account Key 已配置）
- [ ] 命令行工具可用：`gcloud`, `docker`, `npm`

### 2. 代码检查
- [ ] 代码已提交到 Git
- [ ] 环境变量已正确配置（`.env.production`）
- [ ] 无敏感信息泄露（密码、密钥在环境变量或 Secret Manager）

### 3. 后端检查
- [ ] `pom.xml` / `build.gradle` 依赖完整
- [ ] Dockerfile 存在且正确
- [ ] 数据库连接配置正确
- [ ] CORS 配置包含前端域名

### 4. 前端检查
- [ ] `vite.config.ts` / `vue.config.js` 配置正确
- [ ] API 基础路径 `baseURL` 正确
- [ ] 路由 history 模式已配置 nginx rewrite
- [ ] 生产环境构建成功

---

## 🔧 通用部署命令模板

### Cloud Run 部署（推荐）

```bash
#!/bin/bash
set -e

# === 配置 ===
PROJECT_ID="your-project-id"
REGION="asia-east1"
SERVICE_NAME="your-service"
IMAGE="asia-east1-docker.pkg.dev/${PROJECT_ID}/your-repo/${SERVICE_NAME}:latest"

# === 1. 构建 Docker 镜像 ===
gcloud builds submit ./${SERVICE_NAME} \
    --config=./${SERVICE_NAME}/cloudbuild.yaml \
    --project=${PROJECT_ID}

# === 2. 部署到 Cloud Run ===
gcloud run deploy ${SERVICE_NAME} \
    --image=${IMAGE} \
    --platform=managed \
    --region=${REGION} \
    --allow-unauthenticated \
    --project=${PROJECT_ID} \
    --min-instances=1 \
    --max-instances=10
```

### 分步部署

```bash
# 步骤1: 构建并推送镜像
gcloud builds submit ./backend \
    --config=./backend/cloudbuild.yaml \
    --project=$PROJECT_ID

# 步骤2: 部署后端
gcloud run deploy backend \
    --image=asia-east1-docker.pkg.dev/$PROJECT_ID/campus-sports-repo/backend:latest \
    --platform=managed \
    --region=asia-east1 \
    --allow-unauthenticated \
    --project=$PROJECT_ID \
    --set-env-vars "SPRING_PROFILES_ACTIVE=production" \
    --set-env-vars "DB_HOST=YOUR_DB_IP" \
    --min-instances=1

# 步骤3: 构建前端
cd frontend && npm run build && cd ..

# 步骤4: 部署前端
rm -rf frontend-deploy/dist && cp -r frontend/dist frontend-deploy/dist
gcloud builds submit ./frontend-deploy \
    --config=./frontend-deploy/cloudbuild.yaml \
    --project=$PROJECT_ID

gcloud run deploy frontend \
    --image=asia-east1-docker.pkg.dev/$PROJECT_ID/campus-sports-repo/frontend:latest \
    --platform=managed \
    --region=asia-east1 \
    --allow-unauthenticated \
    --project=$PROJECT_ID \
    --port=8080 \
    --min-instances=1
```

---

## 🐳 Docker 部署

### 手动构建推送

```bash
# 1. 构建镜像
docker build -t gcr.io/$PROJECT_ID/service-name:latest ./service-dir

# 2. 推送镜像
docker push gcr.io/$PROJECT_ID/service-name:latest

# 3. 部署
gcloud run deploy service-name \
    --image=gcr.io/$PROJECT_ID/service-name:latest \
    --platform=managed \
    --region=asia-east1 \
    --allow-unauthenticated
```

### Docker Compose 本地开发

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=localhost
    depends_on:
      - db
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: app
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
volumes:
  mysql_data:
```

```bash
# 启动
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down
```

---

## ☸️ Kubernetes 部署

### 基本部署配置

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: gcr.io/$PROJECT_ID/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        resources:
          limits:
            cpu: "1"
            memory: "512Mi"
```

```bash
# 部署
kubectl apply -f deployment.yaml

# 查看状态
kubectl get pods
kubectl get services

# 扩缩容
kubectl scale deployment backend --replicas=3

# 回滚
kubectl rollout undo deployment/backend
```

---

## 🗄️ 数据库部署

### Cloud SQL 部署

```bash
# 1. 创建实例
gcloud sql instances create my-instance \
    --database-version=MYSQL_8_0 \
    --region=asia-east1 \
    --root-password=YOUR_PASSWORD \
    --project=$PROJECT_ID

# 2. 创建数据库
gcloud sql databases create mydb --instance=my-instance

# 3. 创建用户
gcloud sql users create appuser \
    --instance=my-instance \
    --password=APP_PASSWORD

# 4. 连接测试
gcloud sql connect my-instance --user=root
```

### 初始化数据

```bash
# 方式1: SQL 文件导入
mysql -h $DB_IP -u root -p $DB_NAME < init.sql

# 方式2: 逐条执行
mysql -h $DB_IP -u root -p $DB_NAME << 'EOF'
CREATE TABLE users (...);
INSERT INTO users (...) VALUES (...);
EOF
```

### 常见问题

| 问题 | 解决方案 |
|------|----------|
| Access denied | 重置密码：`gcloud sql users set-password root --instance=xxx --password=xxx` |
| 连接超时 | 检查 Cloud SQL 公共 IP 配置和安全组 |
| 无法连接 | 确认 Cloud Run 服务已关联 Cloud SQL 实例 |

---

## 🌐 前端部署

### Vercel 部署

```bash
# 1. 安装 Vercel CLI
npm i -g vercel

# 2. 登录
vercel login

# 3. 部署
cd frontend
vercel --prod

# 或预览部署
vercel
```

### Firebase Hosting 部署

```bash
# 1. 安装 Firebase CLI
npm install -g firebase-tools

# 2. 登录
firebase login

# 3. 初始化
firebase init hosting

# 4. 部署
npm run build
firebase deploy --only hosting
```

### Nginx 配置 (SPA)

```nginx
server {
    listen 8080;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Vue Router History Mode
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
}
```

---

## 🔄 CI/CD 流水线

### GitHub Actions 示例

```yaml
# .github/workflows/deploy.yml
name: Deploy to Cloud Run

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - id: 'auth'
        uses: 'google-github-actions/auth@v2'
        with:
          credentials_json: ${{ secrets.GCP_SA_KEY }}
      
      - name: 'Set up Cloud SDK'
        uses: 'google-github-actions/setup-gcloud@v2'
        
      - name: 'Build and push backend'
        run: |
          gcloud builds submit ./backend \
            --config=./backend/cloudbuild.yaml \
            --project=${{ vars.GCP_PROJECT_ID }}
      
      - name: 'Deploy backend'
        run: |
          gcloud run deploy backend \
            --image=asia-east1-docker.pkg.dev/${{ vars.GCP_PROJECT_ID }}/repo/backend:latest \
            --region=asia-east1 \
            --allow-unauthenticated \
            --project=${{ vars.GCP_PROJECT_ID }}

      - name: 'Build frontend'
        run: |
          cd frontend && npm ci && npm run build
      
      - name: 'Deploy frontend'
        run: |
          gcloud run deploy frontend \
            --image=asia-east1-docker.pkg.dev/${{ vars.GCP_PROJECT_ID }}/repo/frontend:latest \
            --region=asia-east1 \
            --allow-unauthenticated \
            --project=${{ vars.GCP_PROJECT_ID }}
```

---

## 🐛 故障排查

### 常见问题速查

| 问题 | 检查命令 |
|------|----------|
| 服务无法访问 | `curl -v https://service-url/health` |
| 查看日志 | `gcloud run services describe svc --region=xxx` |
| 查看构建日志 | `gcloud builds list --limit=5` |
| 数据库连接 | `mysql -h $DB_IP -u root -p -e "SELECT 1"` |
| 镜像是否存在 | `gcloud artifacts docker images list` |

### 日志查看

```bash
# Cloud Run 日志
gcloud logging read "resource.type=cloud_run_revision AND resource.service_name=backend" \
    --limit=50 --project=$PROJECT_ID

# 实时日志
gcloud run services logs read backend --region=asia-east1 --project=$PROJECT_ID --follow

# 构建日志
gcloud builds log $BUILD_ID --project=$PROJECT_ID
```

### 重启服务

```bash
gcloud run services restart backend --region=asia-east1 --project=$PROJECT_ID
```

---

## 📝 部署文档模板

每个项目部署后应记录：

```markdown
# [项目名] 部署文档

## 基本信息
- **项目ID**: xxx
- **仓库**: asia-east1-docker.pkg.dev/$PROJECT/repo
- **部署日期**: 2024-xx-xx

## 服务地址
| 服务 | URL |
|------|-----|
| 前端 | https://xxx.run.app |
| 后端 | https://xxx.run.app |
| API文档 | https://xxx.run.app/doc.html |

## 环境变量
| 变量 | 值 |
|------|---|
| SPRING_PROFILES_ACTIVE | production |
| DB_HOST | 10.x.x.x |

## 数据库
- **类型**: Cloud SQL MySQL 8.0
- **地址**: 10.x.x.x:3306
- **数据库名**: xxx

## 测试账号
| 账号 | 密码 | 角色 |
|------|------|------|
| admin | xxx | 管理员 |

## 部署命令
```bash
# 后端
gcloud run deploy backend ...

# 前端
gcloud run deploy frontend ...
```
```

---

## ✅ 部署完成检查

- [ ] 服务可访问
- [ ] 登录功能正常
- [ ] 核心业务流程测试通过
- [ ] 数据库连接正常
- [ ] CORS 配置正确
- [ ] 环境变量已配置
- [ ] 部署文档已更新
