
# PES Vue Frontend

Vue 3 frontend for PES (Permission & Enterprise System).

## Requirements

- Node.js 18+
- npm 9+

## Quick Start

1. Install dependencies:
```bash
npm install
```

2. Start development server:
```bash
npm run dev
```

3. Build for production:
```bash
npm run build
```

## Project Structure

```
src/
├── api/           # 网络请求模块
├── assets/        # 静态资源
├── components/    # 公共组件
├── composables/   # 组合式函数
├── directives/    # 自定义指令
├── router/        # 路由配置
├── store/         # Pinia 状态管理
├── utils/         # 工具函数
└── views/         # 页面视图
```

## Features

- 🔐 JWT Authentication
- 📋 User Management
- 🔒 Role Management
- 🗂️ Menu Management
- 🎨 Element Plus UI
- 📱 Responsive Layout

## Environment Variables

Create `.env.development` and `.env.production` files:

```
VITE_API_BASE_URL=http://localhost:8080/api
```