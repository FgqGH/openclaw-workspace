# Design Patterns Reference

## Common Layouts

### Hero Section
```
┌─────────────────────────────────────┐
│           HEADER / NAV              │
├─────────────────────────────────────┤
│                                     │
│    HEADLINE + SUBTEXT + CTA         │
│           (centered)                │
│                                     │
│         [Button] [Button]           │
│                                     │
│       (Hero Image/Graphic)          │
│                                     │
└─────────────────────────────────────┘
```

### Features Grid
```
┌─────────┐ ┌─────────┐ ┌─────────┐
│  Icon   │ │  Icon   │ │  Icon   │
│  Title  │ │  Title  │ │  Title  │
│  Desc   │ │  Desc   │ │  Desc   │
│  Link   │ │  Link   │ │  Link   │
└─────────┘ └─────────┘ └─────────┘
```

### Contact Form
```
┌─────────────────────────────────────┐
│           Section Title             │
├─────────────────────────────────────┤
│  Info        │    Form              │
│  Address     │    Input             │
│  Email       │    Input             │
│  Phone       │    Textarea          │
│              │    [Submit]          │
└─────────────────────────────────────┘
```

## Component CSS Patterns

### Card Hover
```css
.card-hover {
  transition: transform 200ms cubic-bezier(0.16, 1, 0.3, 1), 
              box-shadow 200ms cubic-bezier(0.16, 1, 0.3, 1);
}
.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.4);
}
```

### Button Primary
```css
.btn-primary {
  background: linear-gradient(135deg, oklch(0.60 0.18 280), oklch(0.50 0.22 260));
  transition: all 150ms cubic-bezier(0.16, 1, 0.3, 1);
}
.btn-primary:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 24px -4px rgba(120, 80, 255, 0.4);
}
```

### Gradient Text
```css
.gradient-text {
  background: linear-gradient(135deg, oklch(0.70 0.15 280), oklch(0.55 0.2 260));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
```

## Tailwind Config Pattern
```js
tailwind.config = {
  theme: {
    extend: {
      colors: {
        primary: 'oklch(0.55 0.2 260)',
        secondary: 'oklch(0.65 0.18 200)',
        accent: 'oklch(0.70 0.15 280)',
        dark: 'oklch(0.15 0.02 260)',
        surface: 'oklch(0.18 0.02 260)',
      },
      fontFamily: {
        sans: 'DM Sans, system-ui, sans-serif',
      },
    }
  }
}
```

## Dark Theme Colors
- Background: oklch(0.15 0.02 260)
- Surface: oklch(0.18 0.02 260)
- Primary: oklch(0.55 0.2 260)
- Accent: oklch(0.70 0.15 280)
- Text: white / oklch(0.65 0.02 260) for muted
- Border: oklch(0.22 0.02 260) or rgba(255,255,255,0.1)

## Light Theme Colors
- Background: oklch(0.98 0 0)
- Surface: oklch(0.95 0.01 260)
- Primary: oklch(0.55 0.2 260)
- Text: oklch(0.15 0 0)
- Muted: oklch(0.55 0 0)
- Border: oklch(0.89 0 0)
