# PES 权限管理系统

基于 **Spring Boot 3** + **Vue 3** 前后端分离架构的 RBAC（基于角色的访问控制）权限管理系统。支持用户管理、角色管理、菜单权限管理、操作日志审计等核心功能，适合作为企业级后台管理系统的基础框架。

---

## 项目特点

- **RBAC 权限模型**：用户 → 角色 → 菜单/按钮权限，粒度可到按钮级
- **前后端协同鉴权**：后端自定义 `@RequirePermission` 注解 + AOP 切面拦截，前端 `usePermission` 组合式函数控制 UI 显隐
- **JWT 令牌认证**：登录后签发 JWT，包含用户身份与权限信息，前端路由守卫拦截未登录请求
- **BCrypt 密码加密**：自动加盐，相同明文产生不同密文，保障数据安全
- **AOP 操作日志**：通过 `@LogOperation` 注解 + AOP 切面自动记录用户操作，支持分页查询与清理
- **登录日志记录**：记录每次登录的 IP 地址、时间、成功/失败状态
- **图形验证码**：登录时校验验证码，防暴力破解
- **统一响应封装**：所有接口返回 `Result<T>` 结构（code / message / data）
- **统一异常处理**：`@RestControllerAdvice` 全局拦截业务异常，返回标准错误码

---

## 技术栈

| 后端 | 前端 |
|------|------|
| Java 17 | Vue 3 (Composition API) |
| Spring Boot 3.x | Vite |
| Spring Security 6.x | Element Plus |
| MyBatis-Plus | Pinia |
| JWT (jjwt) | Vue Router |
| MySQL 8.0+ | Axios |
| Redis 6.0+ | SCSS |

---

## 项目结构

```
PES/
├── pes-java/                    # 后端 Spring Boot 项目
│   └── src/main/java/com/pes/
│       ├── annotation/          # 自定义注解（@RequirePermission、@LogOperation）
│       ├── aspect/              # AOP 切面（权限拦截、操作日志）
│       ├── common/              # 通用响应体（Result）
│       ├── config/              # 配置类（Security、CORS、MyBatis-Plus）
│       ├── controller/          # 控制器（Auth、User、Role、Menu、Log、Dashboard）
│       ├── dto/                 # 请求/响应 DTO
│       ├── entity/              # 数据实体（SysUser、SysRole、SysMenu 等）
│       ├── exception/           # 异常定义与全局处理器
│       ├── mapper/              # MyBatis-Plus Mapper
│       ├── security/            # Spring Security 配置（JWT 过滤器、权限处理器）
│       ├── service/             # 业务服务层
│       └── utils/               # 工具类（JWT、验证码、Redis）
│
└── pes-vue/                     # 前端 Vue 3 项目
    └── src/
        ├── api/                 # 后端 API 请求封装
        ├── assets/styles/       # 全局样式
        ├── components/          # 公共组件
        │   ├── common/          # 通用组件（Captcha 验证码）
        │   └── layout/          # 布局组件（Sidebar、Header、Breadcrumb）
        ├── composables/         # 组合式函数（usePermission）
        ├── img/                 # 图片资源
        ├── router/              # 路由配置与守卫
        ├── store/modules/       # Pinia 状态管理（user、app）
        ├── utils/               # 工具函数（storage、iconMap）
        └── views/               # 页面视图
            ├── dashboard/       # 仪表盘
            ├── login/           # 登录
            ├── log/             # 操作日志、登录日志
            ├── profile/         # 个人中心
            └── system/          # 系统管理（用户、角色、菜单）
```

---

## 功能模块

### 用户认证
| 功能 | 说明 |
|------|------|
| 用户登录 | 验证码校验 + 账号密码校验，成功后签发 JWT |
| 用户注册 | 密码 BCrypt 加密后入库 |
| 图形验证码 | 后端生成，前端展示，有效期 2 分钟 |
| 个人中心 | 修改个人信息、修改密码（需验证旧密码） |

### 用户管理
| 功能 | 权限标识 |
|------|----------|
| 分页查询用户列表 | `user:list` |
| 新增用户 | `user:add` |
| 编辑用户 | `user:edit` |
| 删除用户 | `user:delete` |

### 角色管理
| 功能 | 权限标识 |
|------|----------|
| 分页查询角色列表 | `role:list` |
| 新增 / 编辑 / 删除角色 | `role:add` / `role:edit` / `role:delete` |
| 为角色分配菜单/按钮权限（树形勾选） | `role:assign` |

### 菜单管理
| 功能 | 权限标识 |
|------|----------|
| 菜单树形展示（类型：菜单/按钮） | `menu:list` |
| 新增 / 编辑 / 删除菜单节点 | `menu:add` / `menu:edit` / `menu:delete` |
| 配置权限标识（用于后端接口拦截） | `menu:edit` |

### 日志管理
| 功能 | 权限标识 |
|------|----------|
| 操作日志查询 | `log:list` |
| 登录日志查询 | `log:list` |
| 单条删除 / 批量删除 / 清空日志 | `log:delete` |

### 仪表盘
| 功能 | 说明 |
|------|------|
| 系统概览统计 | 用户数、角色数、菜单数、今日登录数 |

---

## 权限模型

```
用户（SysUser） ──多对多──→ 角色（SysRole） ──多对多──→ 菜单（SysMenu）
                                                          │
                                            ┌─────────────┴─────────────┐
                                           type=1 菜单（侧边栏可见）    type=2 按钮（权限标识）
```

- **后端**：通过 `@RequirePermission("xxx")` 注解 + AOP 切面（`PermissionAspect`）拦截接口
- **前端侧边栏**：根据用户权限自动过滤菜单（`Sidebar.vue` 中 `filterByPermission` 递归过滤）
- **前端按钮**：通过 `v-if="checkPermission('xxx')"` 控制显隐（`usePermission` 组合式函数）

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+

### 后端启动

```bash
cd pes-java

# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS pes_db DEFAULT CHARACTER SET utf8mb4;"

# 2. 修改 application-dev.yml 中的数据库和 Redis 连接信息

# 3. 启动（首次启动自动初始化表结构和种子数据）
mvn spring-boot:run
```

默认管理员账号：`admin` / `admin123`

### 前端启动

```bash
cd pes-vue

# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev

# 生产构建
npm run build
```

---

## 内置角色

| 角色 | 编码 | 权限范围 |
|------|------|----------|
| 超级管理员 | admin | 全部菜单和按钮权限 |
| 普通用户 | user | 仅仪表盘查看 |
| 部门经理 | manager | 用户管理 + 日志管理 |
| 运维人员 | operator | 系统管理（只读）+ 日志管理 |
| 审计员 | auditor | 仅日志管理 |
