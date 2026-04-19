# MEMORY.md - 长期记忆

## 关于强哥
- GitHub 用户名：FgqGH
- GitHub Token：ghp_REDACTED
- GCP Project ID：my-project-openclaw-492614
- 强哥做校园体育相关的创业

## 我的工作模式
- 强哥只负责：提需求 + 接收链接
- 我负责：需求分析 → 调度开发 Agent → 调度部署 Agent → 返回链接
- 代码通过 GitHub 在 Agent 之间共享
- 出错处理：强哥反馈问题 → 我修复 → 重新部署

## 项目：校园运动场地预约系统
- 仓库：https://github.com/FgqGH/campus-booking
- 后端已部署：https://campus-booking-385311213796.asia-east1.run.app
- 待完成：需要 Supabase URL + Service Key 才能让后端真正工作

## 待强哥提供的配置
- SUPABASE_URL（形如 https://xxx.supabase.co）
- SUPABASE_SERVICE_KEY（形如 eyJhbGc...）

## 技术关键点
- `runtime=subagent` 适合一次性任务，用完即弃
- `runtime=acp` 的 agentId 对应外部 CLI（Codex、Cursor 等），不是 OpenClaw 内部 Agent
- Flutter SDK 未装，当前环境只有 Node.js 可用
- 技能放在 /root/.openclaw/skills/ 下，子 Agent 可读取

## 已掌握的技能
- fullstack-master：Flutter + Node.js + Supabase 全栈开发
- deploy-master：GCP Cloud Run 部署

## 2026-04-17 Firebase Hosting 部署关键发现

### Firebase CLI 自动化认证方式
- 使用 `GOOGLE_APPLICATION_CREDENTIALS` 环境变量指向服务账号 JSON
- Firebase CLI 会自动使用 Application Default Credentials
- 需要服务账号有 `roles/firebasehosting.admin` 权限

### Firebase Hosting API 关键细节
- 启用 Firebase Management API: `gcloud services enable firebase.googleapis.com`
- 启用 Firebase Hosting API: `gcloud services enable firebasehosting.googleapis.com`
- 站点创建: `firebase hosting:sites:create <siteId>`
- 创建站点用 siteId 作为 query 参数，不是 body
- `firebase.json` 必须包含 `"site": "<siteId>"` 字段
- 部署: `firebase deploy --only hosting --project <projectId>`

### Firebase 项目 vs GCP 项目
- GCP 项目 `my-project-openclaw-492614` 已关联 Firebase（409 ALREADY_EXISTS 说明）
- 但需要单独创建 Firebase Hosting 站点

### 部署成功
- 前端已部署: https://campus-booking-492614.web.app
- 自动化脚本: campus-sports-booking/scripts/deploy_firebase_hosting.sh

## 部署经验教训（2026-04-17）

### Firebase Hosting 部署必做清单
1. `gcloud services enable firebase.googleapis.com firebasehosting.googleapis.com` — 先启用 API
2. 服务账号加 `roles/firebasehosting.admin` IAM 角色
3. `firebase hosting:sites:create <siteId>` — 提前创建站点
4. `firebase.json` 必须有 `"site": "<siteId>"` 字段
5. `npm run build` 后要 `cp -rf frontend/dist/* dist/` — firebase.json 指向根目录 dist/

### Cloud Build 注意事项
- 默认从 `/workspace` 加载代码，子目录项目要先 tar 上传到 GCS
- `docker build` 用 `-f backend/Dockerfile` 指定子目录路径
- 镜像 tag 不要用 `--push`，分开写两个 `-t`

### Cloud Run + Cloud SQL
- Unix socket `/cloudsql/...` 在 Cloud Run 可能有环境差异
- 稳妥方案：TCP 直连公网 IP（需 Cloud SQL 开启公共 IP + 0.0.0.0/0 访问）
- Cloud SQL 要确认 `sslMode=ALLOW_UNENCRYPTED_AND_ENCRYPTED`

### CORS 配置
- 后端 SecurityConfig 要同时包含所有前端域名（Firebase、Cloud Run 前端等）
- Firebase CDN 缓存：文件 hash 变化才算新文件，旧文件不会自动更新

### 前端 API 路径约定
- baseURL 包含完整路径（如 `https://xxx/api`）
- 各 API 文件里路径不带 `/api` 前缀（如 `auth.ts` 里写 `/auth/login`）
- 禁止 baseURL 带 `/api` 同时 API 文件里也带 `/api`，会导致重复

### JWT 密钥
- 生产环境密钥长度 ≥256 bits（建议 64 字符以上）


## 全自动化项目框架（✅ 已确认，✅ 已实现，✅ 已固化，✅ 已完善）

### 已修复的问题
- 项目隔离：Cloud Run 服务名 = \${PROJECT_NAME}-backend，Firebase site 由 FIREBASE_SITE_ID 控制
- 回滚机制：backend.yml 部署前保存旧 revision，失败自动回滚
- CI/CD 测试：backend.yml 拆为 test-backend + deploy-backend 两阶段，测试通过才部署
- 后端测试规范：必须包含 JUnit 5 + Mockito 单元测试，Repository 层用 @MockBean
- Flyway 数据库迁移：Spring Boot 内置 Flyway，迁移 SQL 放在 `backend/src/main/resources/db/migration/`，应用启动自动执行，无需手动脚本
- 监控告警：`scripts/health-check.py` 每 15 分钟检查后端 + 前端可用性，异常时通过 OpenClaw cron 直接通知强哥
- subagent 质量控制：必须用 `subagent-prompt-template.md` 生成任务 prompt，必须参考 `flutter-norms.md` 规范，构建失败由我接手修复而非重新 spawn
- subagent 断联恢复：subagent 定期写入检查点 `done frontend_dev "<内容>"` 到 `.task-state.json`，任务中断后恢复时从最后一个检查点继续
- 状态脚本：从 Node.js 改为 Python（WSL2 兼容性更好）
- 新增 GitHub Variables：PROJECT_NAME、FIREBASE_SITE_ID

### 断点续接机制
- 项目根目录 `.task-state.json` 记录任务进度
- 状态：idle / in_progress / completed
- 步骤：pending → in_progress → completed / failed
- 每次启动 session 先读状态文件，有断点则继续
- 管理脚本：`skills/vue-spring-fullstack/scripts/task-state.js`
- 步骤完成后自动检查并触发下一步骤

### 固定技术栈
- 前端：Flutter Web → Firebase Hosting
- 后端：Spring Boot 3 + MyBatis Plus + MySQL → GCP Cloud Run
- 数据库：Cloud SQL MySQL
- 认证：JWT
- CI/CD：GitHub Actions
- Flutter SDK：/opt/flutter (3.24.5)
- ⚠️ WSL2 dart2js daemon 不稳定，Flutter Web 放在 GitHub Actions 构建

### 固定项目目录结构
所有项目统一：
```
project/
├── backend/          # Spring Boot
├── frontend/        # Vue 3
├── scripts/         # 部署脚本
├── .github/workflows/  # CI/CD
├── firebase.json
└── dist/           # Firebase Hosting 部署目录
```

### 固定流程（7步）
1. 强哥给需求 + GitHub 仓库地址
2. 我分析需求，拆解 API + 页面列表
3. 开发后端代码
4. push → GitHub Actions 自动部署后端到 Cloud Run
5. 开发前端代码
6. push → GitHub Actions 自动部署前端到 Firebase
7. 返回链接

### 强哥只需要告诉我的（只有一件事）
- **需求是什么**（GitHub 仓库由我通过 GitHub API 自动创建）
- GitHub Token：`ghp_REDACTED`
- GitHub 用户名：`FgqGH`

### 开发分工（已确认）
- 后端（Spring Boot）：我直接写
- 前端（Vue 3）：spawn subagent 并行开发
- 整合 + review：我来做

### Skill 已创建
- `vue-spring-fullstack` skill 已写入 `/root/.openclaw/skills/vue-spring-fullstack/`
- 包含：SKILL.md + GitHub Actions 模板（backend.yml, frontend.yml）+ cloudbuild.yaml + Dockerfile


## 框架完整性检查（2026-04-17 晚间）

### 已修复的不完美问题
1. ✅ 健康检查端点：health-check.py 从环境变量读取 URL，支持 BACKEND_HEALTH_PATH
2. ✅ 种子数据重复插入：Flyway 规范要求用 INSERT IGNORE 或 ON DUPLICATE KEY UPDATE
3. ✅ 前端 CI/CD 无测试：frontend.yml 拆为 test-frontend + deploy-frontend 两阶段
4. ✅ subagent 真正并行：新增 subagent-parallelism.md 规范（模块隔离 + 独立目录）
5. ✅ API 错误率监控：error-rate-monitor.py + 每小时 cron 报告（阈值 5%）

### 当前 Cron Jobs
- 健康检查：每 15 分钟一次（后端 /api/health + 前端）
- 错误率报告：每小时一次（错误率 > 5% 则告警）

### Skill 文件清单
- SKILL.md：框架总览 + 所有规范
- scripts/health-check.py：健康检查（环境变量驱动）
- scripts/error-rate-monitor.py：错误率统计
- scripts/task-state.py：任务状态管理
- references/backend.yml：后端 CI/CD（test + deploy + 回滚）
- references/frontend.yml：前端 CI/CD（test + analyze + deploy）
- references/flutter-norms.md：Flutter 开发规范
- references/subagent-prompt-template.md：前端 subagent prompt 模板
- references/subagent-parallelism.md：subagent 并行规范
- references/project-init-checklist.md：项目初始化清单
- references/cloudbuild.yaml：GCP 构建配置
- references/Dockerfile：后端容器配置

## 框架版本快照（2026-04-18）

### 技术栈（固定不变）
- 前端：Flutter Web + Firebase Hosting
- 后端：Spring Boot 3 + MyBatis Plus + MySQL
- 部署：GCP Cloud Run + Cloud SQL（TCP直连公网IP）
- CI/CD：GitHub Actions
- 认证：JWT

### 框架路径
- `/root/.openclaw/skills/vue-spring-fullstack/`（完整框架模板）

### 框架文件清单
```
SKILL.md                              # 框架总览（~500行）
scripts/
  task-state.py                       # 任务状态管理（断点续接）
  health-check.py                     # 健康检查（环境变量驱动）
  error-rate-monitor.py              # API错误率监控（阈值5%）
references/
  backend.yml                         # 后端CI/CD（test → build → deploy → rollback）
  frontend.yml                        # 前端CI/CD（test → analyze → deploy）
  cloudbuild.yaml                     # GCP镜像构建
  Dockerfile                          # 后端容器（JDK 21）
  flutter-norms.md                  # Flutter开发规范
  subagent-prompt-template.md       # subagent prompt模板
  subagent-parallelism.md           # subagent并行规范
  project-init-checklist.md         # 项目初始化清单
  task-state.json                    # 状态文件模板
```

### 关键约定
- 健康端点：GET /api/health（无状态，permitAll）
- Cloud Run服务名：${PROJECT_NAME}-backend
- Firebase Site ID：${FIREBASE_SITE_ID}
- GCS Bucket：gs://${GCP_PROJECT_ID}-csb/
- 前端目录结构：功能模块隔离（auth/venue/reservation 各subagent独占）
- backend.yml验证：用GET /api/health，非POST

### 明日测试项
- [ ] 健康检查cron job是否正常运行
- [ ] 错误率统计cron job是否正常运行
- [ ] 框架完整性（再次审查）
