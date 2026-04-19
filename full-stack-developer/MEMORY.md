# MEMORY.md - 长期记忆

## 用户信息
- **用户 ID**: ou_73449e62f5049f6ab85e3c15733c2154
- **开发者**: 全栈开发工程师，熟悉 Vue + Spring Boot + MySQL 技术栈
- **工作方式**: 严谨，先讨论思路再动手，严格遵循审批流程

## 项目：校园运动场地预约系统

### 仓库信息
- **GitHub**: https://github.com/FgqGH/campus-sports-booking
- **Owner**: FgqGH
- **Token**: ghp_REDACTED

### 技术栈
- 前端: Vue 3 + Vite + TypeScript + Pinia + Element Plus
- 后端: Spring Boot 3 + MyBatis-Plus + Spring Security
- 数据库: MySQL 8.0
- 鉴权: JWT
- API文档: Knife4j

### 数据库设计（9张表）
- system_user, system_role, system_permission
- system_user_role, system_role_permission
- system_venue_category, system_venue
- system_time_slot, system_reservation

### 测试账号
| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |
| student001 | ? | 需重置 |

### GCP 部署状态（已完成）
- **GCP Project**: my-project-openclaw-492614
- **后端**: https://backend-385311213796.asia-east1.run.app
- **前端**: https://frontend-385311213796.asia-east1.run.app
- **API 文档**: https://backend-385311213796.asia-east1.run.app/doc.html
- **数据库**: Cloud SQL MySQL 8.0, IP: 35.236.174.16
- **Artifact Registry**: asia-east1-docker.pkg.dev/my-project-openclaw-492614/campus-sports-repo

### 部署教训
1. Cloud Run 连接 Cloud SQL 必须通过 Cloud SQL Auth Proxy (Unix socket) 或公共 IP
2. Docker 镜像中 Maven + Alpine 有兼容性问题，改用 `apt-get install maven`
3. 前端 TypeScript 必须配置 `paths` alias (baseUrl + paths)
4. Lombok 注解处理器需要正确配置 annotationProcessorPaths
5. Entity 类使用 FieldFill 枚举必须导入: `import com.baomidou.mybatisplus.annotation.FieldFill;`

## 项目：LUMI科技小程序
- **GitHub**: https://github.com/FgqGH/lumi-tech-mini-native
- **类型**: 微信小程序（企业AI转型服务展示官网）
- **技术栈**: 原生微信小程序（WXML + WXSS + JS）
- **今日完成**: UI图片优化，13张emoji占位图替换为AI生成专业配图

## GCP 部署配置
- **通用部署手册**: `docs/DEPLOY.md` (通用部署流程)
- **部署专家 Skill**: `skills/deploy-master/SKILL.md` (一键部署专家)
- **后端**: Cloud Run + Docker
- **数据库**: Cloud SQL (MySQL 8.0)
- **前端**: Cloud Run + nginx (因 Firebase CLI 认证问题改用 Cloud Run)

## 协作约定
- 数据库变更必须：影响分析 → 方案报告 → 【请求审批】 → 等待批准
- 代码重构必须：现状分析 → 方案对比 → 【请求审批】 → 等待批准
- 系统命令执行必须：风险评估 → 详细说明 → 【请求审批】 → 等待批准

## 设计规范（重要）

### 字体
- ✅ 使用：Geist, DM Sans, Sora, Outfit, Plus Jakarta Sans
- ❌ 禁止：Inter（过于通用）、Arial、system-ui

### 色彩
- ✅ 使用 OKLCH 色彩空间（感知均匀）
- ✅ 中性色带色调（warm gray / cool gray）
- ✅ 暗色模式背景用 #0f0f0f（不要纯黑）
- ❌ 禁止：纯黑/纯灰、彩色背景上用灰色文字

### 动效
- ✅ easing: cubic-bezier(0.16, 1, 0.3, 1)
- ✅ 微交互: 100-200ms；页面过渡: 300-500ms
- ❌ 禁止：bounce/elastic（显廉价）、超过 600ms 动画

### 空间
- ✅ 4px 或 8px 基础间距系统
- ✅ 正文 65ch，宽容器 1280px
- ❌ 禁止：随意的 padding（13px, 22px）

### 交互
- ✅ Focus 状态清晰可见
- ✅ Loading 用 skeleton（不用 spinner）
- ✅ 错误信息具体可操作

### 设计命令
- /audit - 检查无障碍、性能问题
- /polish - 发布前最终打磨
- /critique - UX 设计评审
- /colorize - 引入战略性色彩
- /animate - 添加有意义的动效

## 2026-04-17 Lumi Tech 网站更新记录

### 关于我们页面视频功能
- 视频：`vidu-video-3253670965714097.mp4`（存储在 gs://lumi-tech-videos/videos/）
- 封面图：`vidu-image-3253712926738881.png`（上传到仓库 assets/images/）
- 功能：视频自动播放，播放结束后自动显示封面图
- CSS：.about-image 设为 position:relative，视频/图片用 position:absolute 填充

### 最终部署版本
- revision: lumi-tech-website-00025-79p
- URL: https://lumi-tech-website-385311213796.asia-east1.run.app/about.html

### 网站响应式问题
- 用户询问适配性，提供了响应式优化建议（但用户选择恢复）
- 最终保持原始固定高度 320px + 视频比例填充
