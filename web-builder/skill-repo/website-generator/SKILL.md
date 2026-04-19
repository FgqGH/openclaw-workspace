---
name: website-generator
description: >
  Generate and deploy website from requirements to live URL. Use when user asks to create a website, generate a webpage, or build a site.
  
  Workflow: (1) accept website requirements → (2) generate HTML/CSS/JS code → (3) deploy to Vercel → (4) return live URL.
  
  Triggers: "帮我生成网站", "创建一个网站", "生成网页", "build a website", "create a landing page", etc.
---

# Website Generator Skill

Generate static websites and deploy to Vercel with a single workflow.

## Workflow

```
User Requirements → Generate HTML → Deploy Vercel → Return URL
```

### Step 1: Clarify Requirements

Ask user for:
- **Type**: Landing page, portfolio, blog, admin dashboard, tool page?
- **Topic/Industry**: Tech, education, e-commerce, personal brand?
- **Core Features**: Product showcase, contact form, blog, login?
- **Style**: Dark/light theme, color preferences?
- **Pages**: Single page or multi-page?
- **Language**: Chinese or English content?

If user provides reference URL, try to fetch it for design inspiration.

### Step 2: Generate Website

Use `superdesign` and `frontend-design-pro` skill principles:
- Font: Geist, DM Sans, or Outfit (no Inter/Arial)
- Colors: OKLCH color space, avoid generic blue (#007bff)
- Spacing: 4px/8px base system
- Animation: cubic-bezier(0.16, 1, 0.3, 1), 150-400ms
- No bounce/elastic easing
- Mobile-first responsive design

**Output**: Single `index.html` with:
- Tailwind CSS via CDN
- Inline `<style>` for custom CSS
- Inline `<script>` for interactions
- Real Unsplash images (not placeholders)

**Save location**: `/root/.openclaw/workspace/web-builder/projects/<project-name>/index.html`

### Step 3: Deploy to Vercel

```bash
cd /root/.openclaw/workspace/web-builder/projects/<project-name>
vercel --token "YOUR_VERCEL_TOKEN" --yes
```

Wait for deployment to complete and capture the production URL.

### Step 4: Return Result

**IMPORTANT: API Response Format (for ACP API calls)**

When responding to API calls via ACP, return ONLY the URL in this exact format:
```json
{"choices":[{"message":{"content":"https://your-vercel-url.vercel.app"}}]}
```

- Do NOT add any additional text
- Do NOT include explanations or markdown
- Return ONLY the JSON above

For normal (non-API) requests, provide user with:
- Live URL (primary)
- Vercel project management link

## Design Patterns

See `references/design-patterns.md` for common layout templates and component patterns.

## Quick Reference

| Element | Standard |
|---------|----------|
| Tailwind CDN | `https://cdn.tailwindcss.com` |
| Font | DM Sans (Google Fonts) |
| Image source | `https://images.unsplash.com/photo-xxx?w=800&h=600` |
| Icon library | Emoji (lightweight) |
| Animation | 150-200ms micro, 300-500ms page |
| Easing | `cubic-bezier(0.16, 1, 0.3, 1)` |
