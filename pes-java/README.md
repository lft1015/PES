# PES Java Backend

PES（Permission System）权限管理系统后端，基于 Spring Boot 3.2 + MyBatis-Plus 3.5 + Spring Security 6 构建，提供完整的 RBAC（基于角色的访问控制）能力。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 基础运行环境 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Security | 6.x | 认证与授权 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| jjwt | 0.12.3 | JWT 令牌签发与校验 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存（验证码存储） |
| Lombok | — | 简化 Java 代码 |
| Spring AOP | — | 切面编程（权限校验、操作日志） |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 启动步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS pes_db DEFAULT CHARACTER SET utf8mb4;"

# 2. 修改 src/main/resources/application-dev.yml 中的数据库和 Redis 连接信息

# 3. 启动项目
mvn spring-boot:run
```

应用默认运行在 `http://localhost:8080`。

### 数据库初始化

首次启动前，需要手动执行 `src/main/resources/db/` 目录下的 SQL 脚本：

```bash
# 1. 建表
mysql -u root -p pes_db < src/main/resources/db/schema.sql

# 2. 导入种子数据
mysql -u root -p pes_db < src/main/resources/db/data.sql
```

> 注意：`application-dev.yml` 中 `spring.sql.init.mode` 默认设置为 `never`，不会在应用启动时自动执行 SQL 初始化脚本，以保护运行中的业务数据。如需自动初始化，可临时改为 `always`。

### 默认账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 超级管理员 | 拥有所有权限 |
| zhangsan | admin123 | 普通用户 | 仅仪表盘 |
| lisi | admin123 | 部门经理 | 用户管理 + 日志管理 |
| wangwu | admin123 | 普通用户 | 仅仪表盘 |
| zhaoliu | admin123 | 部门经理 | 用户管理 + 日志管理 |
| sunqi | admin123 | 普通用户 | 已禁用 |
| zhouba | admin123 | 运维人员 | 只读权限 + 日志管理 |
| wujiu | admin123 | 审计员 | 日志管理 |

## 项目结构

```
src/main/java/com/pes/
├── annotation/                  # 自定义注解
│   ├── LogOperation.java        # 操作日志注解（标注需要记录操作日志的方法）
│   └── RequirePermission.java   # 权限校验注解（标注需要权限控制的方法）
├── aspect/                      # AOP 切面
│   ├── LogAspect.java           # 操作日志切面（环绕通知，记录操作人、耗时、状态）
│   └── PermissionAspect.java    # 权限校验切面（环绕通知，校验用户权限标识）
├── common/
│   └── Result.java              # 统一响应体 { code, msg, data }
├── config/                      # 配置类
│   ├── CorsConfig.java          # 跨域配置
│   ├── MybatisPlusConfig.java   # MyBatis-Plus 配置（自动填充创建/更新时间）
│   ├── SecurityConfig.java      # Spring Security 配置（JWT 无状态认证）
│   └── WebMvcConfig.java        # Web MVC 配置
├── controller/                  # 控制器
│   ├── AuthController.java      # 认证接口（验证码、登录、注册、登出、获取当前用户信息）
│   ├── DashboardController.java # 仪表盘统计接口
│   ├── LogController.java       # 日志管理接口（操作日志 + 登录日志）
│   ├── MenuController.java      # 菜单管理接口
│   ├── RoleController.java      # 角色管理接口
│   └── UserController.java      # 用户管理接口
├── dto/                         # 数据传输对象
│   ├── request/                 # 请求 DTO
│   │   ├── LoginReq.java        # 登录请求
│   │   ├── RegisterReq.java     # 注册请求
│   │   ├── RoleAssignReq.java   # 角色分配权限请求
│   │   └── UserCreateReq.java   # 用户创建/修改请求
│   └── response/                # 响应 DTO
│       ├── CaptchaResp.java     # 验证码响应（key + Base64 图片）
│       ├── LoginResp.java       # 登录响应（token + 用户信息 + 角色 + 权限列表）
│       └── MenuTreeResp.java    # 菜单树响应（含 children 嵌套子节点）
├── entity/                      # 数据实体
│   ├── SysLoginLog.java         # 登录日志实体
│   ├── SysMenu.java             # 菜单/权限实体（支持树形结构）
│   ├── SysOperLog.java          # 操作日志实体
│   ├── SysRole.java             # 角色实体
│   ├── SysRoleMenu.java         # 角色-菜单关联实体
│   ├── SysUser.java             # 用户实体（含乐观锁）
│   └── SysUserRole.java         # 用户-角色关联实体
├── exception/                   # 异常处理
│   ├── BusinessException.java   # 业务异常
│   ├── ErrorCode.java           # 统一错误码枚举
│   └── GlobalExceptionHandler.java # 全局异常处理器
├── mapper/                      # MyBatis-Plus Mapper 接口
│   ├── SysLoginLogMapper.java
│   ├── SysMenuMapper.java
│   ├── SysOperLogMapper.java
│   ├── SysRoleMapper.java
│   ├── SysRoleMenuMapper.java
│   ├── SysUserMapper.java
│   └── SysUserRoleMapper.java
├── security/                    # 安全组件
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java  # JWT 认证过滤器
│   ├── handler/
│   │   ├── AccessDeniedHandlerImpl.java       # 权限不足处理器
│   │   └── AuthenticationEntryPointImpl.java  # 认证失败处理器
│   ├── service/
│   │   └── UserDetailsServiceImpl.java        # 用户详情加载服务
│   └── utils/
│       └── JwtUtils.java       # JWT 工具类（生成、解析、校验 Token）
├── service/                     # 业务服务接口
│   ├── AuthService.java
│   ├── SysLoginLogService.java
│   ├── SysMenuService.java
│   ├── SysOperLogService.java
│   ├── SysRoleService.java
│   └── SysUserService.java
│   └── impl/                    # 服务实现
│       ├── AuthServiceImpl.java
│       ├── SysLoginLogServiceImpl.java
│       ├── SysMenuServiceImpl.java
│       ├── SysOperLogServiceImpl.java
│       ├── SysRoleServiceImpl.java
│       └── SysUserServiceImpl.java
└── utils/                       # 工具类
    ├── BeanCopyUtils.java       # Bean 属性拷贝工具
    ├── CaptchaStore.java        # 验证码存储（基于 Redis）
    ├── CaptchaUtils.java        # 验证码生成工具
    └── RedisUtils.java          # Redis 操作工具
```

## 核心设计

### 认证流程

```
客户端请求 → JwtAuthenticationFilter 提取 Token
    → 公开接口（/captcha、/login、/register、/logout）直接放行
    → 其他接口：解析 Token → 加载用户信息 → 设置 SecurityContext
        → 认证失败 → AuthenticationEntryPoint 返回 401
        → 认证成功 → 进入控制器
```

### 权限控制

采用 **自定义注解 + AOP 切面** 实现接口级权限控制：

```java
// 在 Controller 方法上标注所需权限
@GetMapping
@RequirePermission("user:list")
public Result<List<SysUser>> list(...) { ... }

@PostMapping
@RequirePermission("user:add")
@LogOperation("新增用户")
public Result<SysUser> create(...) { ... }
```

`PermissionAspect` 切面拦截所有 `@RequirePermission` 注解的方法，从 Spring Security 上下文中获取当前用户权限集合，与注解值进行精确匹配。权限完全由用户→角色→菜单的关联关系决定，不针对任何角色做硬编码放行。

### 操作日志

使用 `@LogOperation` 注解 + AOP 切面自动记录操作日志：

```java
@DeleteMapping("/{id}")
@RequirePermission("user:delete")
@LogOperation("删除用户")
public Result<Void> delete(@PathVariable("id") Long id) { ... }
```

`LogAspect` 切面在方法执行前后采集：操作人、操作描述、类名、方法名、IP 地址、耗时（毫秒）、成功/失败状态，并持久化到 `sys_oper_log` 表。

### 统一响应格式

所有接口返回统一的 `Result<T>` 结构：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

### 错误码体系

| 编码 | 说明 | HTTP 状态码 |
|------|------|------------|
| 200 | 成功 | 200 |
| 400 | 请求参数错误 | 400 |
| 401 | 未登录或 Token 过期 | 401 |
| 403 | 权限不足 | 403 |
| 404 | 资源不存在 | 404 |
| 500 | 服务器内部错误 | 500 |
| 1001 | 用户不存在 | 404 |
| 1002 | 用户名或密码错误 | 401 |
| 1003 | 用户已禁用 | 403 |
| 1004 | 验证码错误 | 400 |
| 1005 | 用户名已存在 | 409 |
| 2001 | 角色不存在 | 404 |
| 3001 | 菜单不存在 | 404 |

## 数据库设计

### 表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表，含乐观锁 version 字段 |
| sys_role | 角色表，含乐观锁 version 字段 |
| sys_menu | 菜单/权限表，支持树形结构（parent_id），含乐观锁 |
| sys_user_role | 用户-角色关联表（一个用户一个角色） |
| sys_role_menu | 角色-菜单关联表 |
| sys_oper_log | 操作日志表 |
| sys_login_log | 登录日志表 |

### 菜单类型

| type | 含义 | 说明 |
|------|------|------|
| 0 | 目录 | 分组节点，不可点击，用于侧边栏菜单分组 |
| 1 | 菜单 | 可访问的页面 |
| 2 | 按钮 | 操作权限（增删改查等） |

### 种子数据概览

- **5 个角色**：超级管理员（admin）、普通用户（user）、部门经理（manager）、运维人员（operator）、审计员（auditor）
- **8 个用户**：admin、zhangsan、lisi、wangwu、zhaoliu、sunqi（禁用）、zhouba、wujiu
- **29 个菜单项**，分为三大模块：
  - **仪表盘**（独立菜单）：`dashboard:view`
  - **系统管理**（目录）：用户管理、角色管理、菜单管理，含各自的增删改查按钮权限
  - **日志管理**（目录）：操作日志、登录日志，含各自的查询/删除/批量删除/清空按钮权限
- **15 条操作日志示例** + **10 条登录日志示例**

### 角色权限矩阵

| 角色 | 仪表盘 | 用户管理 | 角色管理 | 菜单管理 | 操作日志 | 登录日志 |
|------|--------|----------|----------|----------|----------|----------|
| 超级管理员 | ✓ | 全部 | 全部 | 全部 | 全部 | 全部 |
| 普通用户 | ✓ | — | — | — | — | — |
| 部门经理 | ✓ | 全部 | — | — | 全部 | 全部 |
| 运维人员 | ✓ | 只读 | 只读 | 只读 | 全部 | 全部 |
| 审计员 | ✓ | — | — | — | 全部 | 全部 |

## API 接口

### 认证（AuthController）

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | /captcha | 否 | 获取图形验证码，返回 key 和 Base64 图片 |
| POST | /login | 否 | 用户登录，返回 JWT Token 和用户权限信息 |
| POST | /register | 否 | 用户注册 |
| POST | /logout | 否 | 用户登出 |
| GET | /auth/me | 是 | 获取当前登录用户的最新权限信息 |

### 仪表盘（DashboardController）

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | /dashboard/stats | 是 | 获取系统统计（用户数、角色数、菜单数） |

### 用户管理（UserController）

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /users | `user:list` | 查询用户列表（支持 keyword 过滤） |
| GET | /users/options | — | 获取用户下拉选项（供日志等页面使用） |
| GET | /users/profile | — | 获取当前登录用户个人信息 |
| PUT | /users/profile | — | 修改当前登录用户个人信息 |
| PUT | /users/password | — | 修改当前登录用户密码 |
| GET | /users/{id} | `user:view` | 获取用户详情（含角色 ID） |
| POST | /users | `user:add` | 新增用户 |
| PUT | /users/{id} | `user:edit` | 修改用户 |
| DELETE | /users/{id} | `user:delete` | 删除用户 |

### 角色管理（RoleController）

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /roles | `role:list` | 查询角色列表（支持 keyword 过滤） |
| GET | /roles/options | — | 获取角色下拉选项（供用户管理使用） |
| GET | /roles/{id} | `role:assign` | 获取角色详情（含已分配菜单 ID，用于权限分配回显） |
| POST | /roles | `role:add` | 新增角色 |
| PUT | /roles/{id} | `role:edit` | 修改角色 |
| DELETE | /roles/{id} | `role:delete` | 删除角色（级联删除关联数据） |
| POST | /roles/{roleId}/assign | `role:assign` | 为角色分配菜单权限 |

### 菜单管理（MenuController）

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /menus/tree | — | 获取菜单树（侧边栏和权限分配使用，支持 keyword 过滤） |
| GET | /menus | `menu:list` | 查询菜单列表（平铺，支持 keyword 过滤） |
| GET | /menus/{id} | `menu:view` | 获取菜单详情 |
| POST | /menus | `menu:add` | 新增菜单 |
| PUT | /menus/{id} | `menu:edit` | 修改菜单 |
| DELETE | /menus/{id} | `menu:delete` | 删除菜单（级联删除子菜单及角色关联） |

### 日志管理（LogController）

**操作日志**：

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /logs/operation | `operlog:list` | 分页查询操作日志（支持 username、operation 过滤） |
| DELETE | /logs/operation/{id} | `operlog:delete` | 删除单条操作日志 |
| DELETE | /logs/operation/batch | `operlog:batch` | 批量删除操作日志 |
| DELETE | /logs/operation/clear | `operlog:clear` | 清空所有操作日志 |

**登录日志**：

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /logs/login | `loginlog:list` | 分页查询登录日志（支持 username、status 过滤） |
| DELETE | /logs/login/{id} | `loginlog:delete` | 删除单条登录日志 |
| DELETE | /logs/login/batch | `loginlog:batch` | 批量删除登录日志 |
| DELETE | /logs/login/clear | `loginlog:clear` | 清空所有登录日志 |

## 配置说明

### 应用配置（application.yml）

```yaml
server:
  port: 8080                  # 服务端口

spring:
  profiles:
    active: dev               # 激活的环境配置

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true   # 驼峰映射
  global-config:
    db-config:
      id-type: auto           # 主键自增

jwt:
  secret: pes-jwt-secret-key-2024   # JWT 签名密钥
  expire: 86400                     # Token 过期时间（秒），默认 24 小时
```

### 开发环境（application-dev.yml）

- 数据库：`jdbc:mysql://localhost:3306/pes_db`
- Redis：`localhost:6379`
- SQL 初始化模式：`never`（不自动重置数据库）
- 日志级别：`com.pes: DEBUG`，`org.springframework.security: DEBUG`

### 生产环境（application-prod.yml）

- 独立的数据库和 Redis 连接配置
- 日志级别：`com.pes: INFO`

## 安全特性

- **无状态 JWT 认证**：不依赖 Session，服务端无需存储会话状态
- **BCrypt 密码加密**：所有用户密码使用 BCrypt 算法加密存储
- **CSRF 防护**：前后端分离架构，基于 Token 认证，已禁用 CSRF
- **认证异常统一处理**：登录失败、Token 过期、权限不足等异常统一返回标准格式
- **内部异常信息隐藏**：用户不存在等内部异常统一映射为密码错误，避免信息泄露
- **乐观锁**：用户、角色、菜单表均使用 `@Version` 实现乐观锁并发控制
- **参数校验**：使用 Jakarta Validation（`@Valid`、`@NotBlank` 等）进行请求参数校验

## 登录请求示例

```bash
# 1. 获取验证码
curl -X GET http://localhost:8080/captcha
# 响应: { "code": 200, "data": { "key": "uuid-string", "image": "data:image/png;base64,..." } }

# 2. 登录
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","captcha":"xxxx","captchaKey":"uuid-string"}'
# 响应: { "code": 200, "data": { "token": "eyJhbG...", "tokenType": "Bearer", "username": "admin", ... } }

# 3. 携带 Token 访问受保护接口
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer eyJhbG..."
```