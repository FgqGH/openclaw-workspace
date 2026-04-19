# 项目命名与GitHub推送规范

## GitHub 仓库命名规范

| 类型 | 格式 | 示例 |
|------|------|------|
| 公司官网 | `{company}-website` | `lumi-tech-website` |
| 落地页 | `{brand}-landing` | `startup-landing` |
| 作品集 | `portfolio-{name}` | `portfolio-designer` |
| 活动页 | `{event}-campaign` | `summer-sale-campaign` |
| 工具页面 | `{tool}-tool` | `qrcode-tool` |
| 移动端页面 | `{project}-mobile` | `shop-mobile` |
| 管理后台 | `{project}-admin` | `crm-admin` |
| 博客/资讯 | `{topic}-blog` | `tech-blog` |
| 品牌展示 | `{brand}-showcase` | `art-gallery-showcase` |

## 项目文件夹结构

```
{project-name}/
├── index.html          # 首页/入口
├── {page}.html         # 其他页面（about.html, contact.html 等）
├── assets/             # 静态资源（按需创建）
│   ├── css/
│   ├── js/
│   └── images/
└── README.md           # 项目说明（自动生成）
```

## Git 提交规范

```
feat:     新功能
fix:      修复bug
update:   更新/优化
refactor: 重构
docs:     文档
style:    样式调整
perf:     性能优化
test:     测试相关
chore:    构建/工具变更
```

## GitHub 推送流程

1. 项目文件夹初始化为 Git 仓库
2. 创建 GitHub 仓库（通过 GitHub API）
3. 添加 remote 并 push
4. 设置 Vercel 部署（可选）

## 自动化

后续项目生成完成后，织网将自动执行以上流程推送至 GitHub，无需额外确认。
