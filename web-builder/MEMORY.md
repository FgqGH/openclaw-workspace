# MEMORY.md - 织网长期记忆

## 身份
- 名字：织网 🕸️
- 角色：网站生成助手 + 前端开发专家 + UI/UX设计师
- 服务对象：强哥（软件开发者）

## 核心原则（强哥设定）
- 绝不擅自行动
- 重启、配置文件修改、依赖安装、关键决策需审批
- 使用"【请求审批】"标题

## 技术栈更新 (2026-04-16)
- 新增 Flutter/Dart 框架学习 ✅
- 新增 Node.js 后端开发 ✅
- 新增 Supabase 数据库 ✅
- 职责范围：前后端全栈开发助手

## 已安装技能（clawhub）
| 技能 | 用途 |
|------|------|
| flutter | Flutter 基础 |
| flutter-dev | Flutter 开发进阶 |
| flutter-architecture | Flutter 架构模式 |
| riverpod | 状态管理 |
| go-router | 路由管理 |
| compound-eng-nodejs-backend | Node.js 后端开发 |
| rest-api-design | REST API 设计 |
| supabase | Supabase 基础 |
| supabase-api | Supabase API 使用 |
| **fullstack-master** | ⭐ 整合技能（前后端全栈） |

## 项目架构（强哥设定）
| 层级 | 技术 |
|------|------|
| 前端 | Flutter (Dart) |
| 后端 | Node.js |
| 数据库 | Supabase |

- Flutter 专注 UI/界面开发，通过 HTTP/WebSocket 与后端通信
- Node.js 处理服务端逻辑
- Supabase 提供 PostgreSQL 数据库 + Auth + Storage

## 已安装技能
| 技能 | 来源 | 用途 |
|------|------|------|
| exa-web-search-free | clawhub | 免费网页搜索 |
| superdesign | clawhub | UI设计规范 |
| frontend-design-pro | clawhub | 前端代码质量 |
| **website-generator** | clawhub | 网站生成+部署技能 |
| **image-prompt-engineer-tm** | clawhub | AI图片提示词工程 |
| **ui-ux-design** | clawhub | UI/UX设计规范 |
| shadcn-ui | clawhub | 组件库参考 |
| responsive-design | clawhub | 响应式设计参考 |
| html-designer | clawhub | HTML设计规范 |
| **image-studio** | clawhub | 社媒图片提示词生成 |
| **prompt-engineering-expert** | clawhub | Prompt工程专家 |
| **visual-concept** | clawhub | 视觉概念指南 |

## 图片生成技能学习

### image-studio — 社媒图片提示词
**核心结构：**
```
[SUBJECT] + [ACTION/POSE] + [SETTING] + [STYLE] + [LIGHTING] + [MOOD] + [TECH SPECS]
```

**平台尺寸：**
- Instagram: 1080×1080 (1:1) / 1080×1920 (9:16故事)
- LinkedIn: 1200×628 (1.91:1)
- YouTube: 1280×720 (16:9)

### visual-concept — 视觉概念指南
**核心：** 将技术概念转化为视觉隐喻
**输出：** Core Visual Concept / Visual Themes / Symbolic Elements / Emotional Color Arc / Motion & Rhythm / Key Contrasts

### image-prompt-engineer-tm — 摄影提示词
**5层结构：** Subject → Environment → Lighting → Technical → Style

## 已创建技能
| 技能 | 路径 | 用途 |
|------|------|------|
| website-generator | ~/.openclaw/skills/website-generator/ | 一键生成网站 |

## 网站项目
| 项目 | 网址 | 说明 |
|------|------|------|
| lumi-tech | https://lumi-tech.vercel.app | 科技公司官网 |

## Vidu API
- Token：vda_942568474998226944_GgpZMJqXSMEtkf16NGGHm1CeXF7qfMLG

## Vercel部署
- Token：vcp_REDACTED
- 方案：纯HTML + Vercel deploy
- 当前状态：待命

## GitHub推送
- 用户名：FgqGH
- Token：ghp_REDACTED
- 规范文件：projects/CONVENTIONS.md
- 后续项目生成后自动推送至GitHub，无需审批

## 项目规范（已制定）
| 类型 | 格式 | 示例 |
|------|------|------|
| 公司官网 | `{company}-website` | `lumi-tech-website` |
| 落地页 | `{brand}-landing` | `startup-landing` |
| 作品集 | `portfolio-{name}` | `portfolio-designer` |
| 活动页 | `{event}-campaign` | `summer-sale-campaign` |
| 工具页面 | `{tool}-tool` | `qrcode-tool` |

## Lumi科技网站 最新状态 (2026-04-17)

### Hero视频模块待完成
- 位置：index.html Hero区域（第1161行）
- 现状：海报封面已有，弹窗视频内容为空占位符
- 方案：使用Vidu API生成视频

### 视频提示词（已生成，待提交）
```
5秒未来科技HUD界面，ARRI摄影机质感，UnrealEngine5渲染，工业光魔级VFX，
深邃数字科技美学+蓝色霓虹氛围。

0-2秒：固定镜头对准中央主面板，数据折线图缓缓动态生成，蓝色发光线条沿路径延展，薄雾粒子漂浮于面板前营造纵深感。

2-4秒：缓慢推轨推进，镜头平滑靠近左侧副面板，柱状图逐级升起发出冷蓝色辉光，金属表面反射微弱高光。

4-5秒：环绕运镜，镜头围绕核心进度环旋转270度，进度环冰蓝色自发光与边缘亮白色衍射交织，背景神经网络光点持续闪烁。

光影：深蓝冷光主光源+LED面板自发光（光源层），薄雾散射柔化轮廓+发光线条产生微蓝紫色衍射（光行为层），深蓝底调+霓虹青蓝高光冷暖对撞（色调层）。

音效：电子脉冲音、数据流涓涓声、仪表板轻响。
禁止：任何文字、字幕、LOGO或水印
```
- 状态：**待强哥批准后提交Vidu生成**

### 已安装技能
| 技能 | 路径 | 用途 |
|------|------|------|
| seedance-shot-design | /root/.openclaw/workspace/skills/seedance-shot-design | 电影级视频提示词工程 |
| video-prompt-generator | /root/.openclaw/workspace/skills/video-prompt-generator | Sora 2视频提示词生成 |
| seedance-guide | /root/.openclaw/workspace/skills/seedance-guide | Seedance使用指南 |

## Lumi科技网站 最新状态 (2026-04-15)
### 页面结构
- 案例页面 (cases.html)：Hero + 数据指标条 + 2x2卡片网格 + CTA
- 关于页面 (about.html)：已移除 timeline 和 culture 部分，精简版

### 案例页面设计要点
- Hero区域：Badge + 标题 + 副标题 + 指标条（蓝色渐变背景）
- 指标条：4个指标，间距紧密
- 卡片：2x2布局，图片比例 16:7，间距较大，内容紧凑
- 卡片hover：translateY(-4px) + 阴影增强

### 成功案例页面CSS变量参考
```css
.cases-container { max-width: 100%; padding: 0 clamp(1rem, 2.5vw, 2rem); }
.cases-hero { margin-bottom: clamp(0.6rem, 1.2vw, 0.9rem); }
.cases-badge { padding: 0.25rem 0.6rem; font-size: 0.7rem; }
.cases-hero-title { font-size: clamp(1.1rem, 2.5vw, 1.5rem); }
.cases-metrics { gap: clamp(0.75rem, 1.5vw, 1.25rem); padding: clamp(0.4rem, 0.8vw, 0.6rem) clamp(1rem, 1.5vw, 1.5rem); }
.metric-num { font-size: clamp(0.85rem, 1.8vw, 1.15rem); }
.cases-grid { gap: clamp(1rem, 2vw, 1.5rem); }
.case-card-visual { aspect-ratio: 16 / 7; }
.case-card-content { padding: clamp(0.5rem, 1vw, 0.7rem); }
.case-title { font-size: clamp(0.78rem, 1.1vw, 0.9rem); }
.page-header { padding: clamp(1rem, 2.5vw, 1.5rem) clamp(0.6rem, 1.5vw, 1rem); }
.page-header h1 { font-size: clamp(1.2rem, 3.2vw, 1.6rem); }
.results { padding: clamp(0.85rem, 2vw, 1.5rem) clamp(0.75rem, 2vw, 1.5rem); }
.result-num { font-size: clamp(1rem, 2.5vw, 1.6rem); }
```

## 设计规范（已学习）
### 颜色
- 用 oklch() 色彩空间
- 不用通用蓝色 #007bff
- 中性色带色调（warm/cool gray）

### 字体
- 推荐：Geist、DM Sans、Instrument Serif
- 禁止：Inter（太通用）、Arial、system-ui

### 间距
- 4px / 8px 基础系统
- 65ch 内容宽度

### 动效
- cubic-bezier(0.16, 1, 0.3, 1)
- 禁止：bounce / elastic
- 时长：100-200ms（微交互），300-500ms（页面）

### 组件
- Loading 用 skeleton
- Focus 状态要清晰
- 按钮文字：动词开头

## 设计反模式（避免）
- ❌ Inter 字体 + 紫色渐变
- ❌ 卡片套卡片
- ❌ 彩色背景 + 灰色文字
- ❌ bounce 动画
- ❌ 纯黑/纯灰背景
- ❌ 随意 padding

## 重要教训
- JSON5 配置文件末尾不能有逗号
- Vercel --token 不能和 login 一起用
- 频繁微调CSS时，用 `git checkout <commit> -- <file>` 来恢复文件比手动编辑更可靠（因为文件内容变化时edit工具的oldText匹配容易失败）
