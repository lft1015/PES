# PES Vue Frontend

PES 权限管理系统前端，基于 Vue 3 + Element Plus + Pinia 构建。

## 技术栈

- **Vue 3** — Composition API
- **Vite** — 构建工具
- **Element Plus** — UI 组件库
- **Pinia** — 状态管理
- **Vue Router** — 路由管理
- **Axios** — HTTP 请求
- **SCSS** — 样式预处理

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev

# 生产构建
npm run build
```

## 项目结构

```
src/
├── api/                    # 后端 API 请求封装
│   ├── request.js          # Axios 实例（拦截器、Token 注入）
│   ├── auth.js             # 认证 API（登录、注册、验证码）
│   ├── user.js             # 用户管理 API
│   ├── role.js             # 角色管理 API
│   ├── menu.js             # 菜单管理 API
│   ├── log.js              # 日志管理 API
│   └── dashboard.js        # 仪表盘 API
├── assets/styles/          # 全局样式
│   ├── index.scss          # 基础样式
│   └── element-override.scss # Element Plus 样式覆盖
├── components/             # 公共组件
│   ├── common/Captcha.vue  # 图形验证码组件
│   └── layout/             # 布局组件
│       ├── AppLayout.vue   # 主布局（侧边栏 + 顶部 + 内容区）
│       ├── Sidebar.vue     # 侧边栏（权限过滤菜单）
│       ├── Header.vue      # 顶部导航栏
│       └── Breadcrumb.vue  # 面包屑导航
├── composables/            # 组合式函数
│   └── usePermission.js    # 权限检查（checkPermission、hasRole）
├── img/                    # 图片资源
├── router/                 # 路由
│   ├── index.js            # 路由配置（静态路由表）
│   └── guard.js            # 路由守卫（Token 校验、菜单加载）
├── store/modules/          # Pinia 状态管理
│   ├── user.js             # 用户状态（Token、权限列表）
│   └── app.js              # 应用状态（菜单列表、侧边栏折叠）
├── utils/                  # 工具函数
│   ├── storage.js          # localStorage 封装
│   └── iconMap.js          # 图标名称 → 组件映射
└── views/                  # 页面视图
    ├── login/Login.vue     # 登录页
    ├── Register.vue        # 注册页
    ├── dashboard/          # 仪表盘
    ├── system/             # 系统管理
    │   ├── user/           # 用户管理
    │   │   ├── UserList.vue
    │   │   └── UserForm.vue
    │   ├── role/           # 角色管理
    │   │   ├── RoleList.vue
    │   │   └── RolePerm.vue
    │   └── menu/           # 菜单管理
    │       └── MenuManage.vue
    ├── log/                # 日志管理
    │   ├── OperationLog.vue
    │   └── LoginLog.vue
    └── profile/Profile.vue # 个人中心
```

## 权限控制

### 侧边栏菜单过滤

侧边栏根据当前用户的权限列表自动过滤菜单树，只显示用户有权访问的菜单项。实现在 `Sidebar.vue` 的 `filterByPermission` 函数中，递归过滤 `type=1` 的菜单节点。

### 按钮级权限

页面中的操作按钮通过 `usePermission` 组合式函数控制显隐：

```javascript
import { usePermission } from '@/composables/usePermission'
const { checkPermission } = usePermission()
```

```html
<el-button v-if="checkPermission('user:add')" @click="...">新增用户</el-button>
<el-button v-if="checkPermission('user:delete')" @click="...">删除</el-button>
```

### 路由守卫

`router/guard.js` 在每次路由跳转前校验 Token 有效性，并在首次进入时从后端加载菜单树。

## 环境变量

开发环境（`.env.development`）：

```
VITE_API_BASE_URL=/api
```

Vite 代理配置自动将 `/api` 请求转发到 `http://localhost:8080`（去掉 `/api` 前缀）。
