# PES Java Backend

PES 权限管理系统后端，基于 Spring Boot 3 + MyBatis-Plus + Spring Security 构建。

## 技术栈

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security 6.x** — 认证与授权
- **MyBatis-Plus** — ORM 框架
- **JWT (jjwt)** — 令牌认证
- **MySQL 8.0+** — 关系型数据库
- **Redis 6.0+** — 缓存（验证码存储）

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

# 2. 修改 application-dev.yml 中的数据库和 Redis 连接信息

# 3. 启动（首次启动自动初始化表结构和种子数据）
mvn spring-boot:run
```

首次启动时，Spring Boot 会自动执行 `schema.sql`（建表）和 `data.sql`（种子数据）。

默认管理员账号：`admin` / `admin123`

## 项目结构

```
src/main/java/com/pes/
├── annotation/              # 自定义注解
│   ├── LogOperation.java    # 操作日志注解
│   └── RequirePermission.java # 权限校验注解
├── aspect/                  # AOP 切面
│   ├── LogAspect.java       # 操作日志记录切面
│   └── PermissionAspect.java # 权限拦截切面
├── common/
│   └── Result.java          # 统一响应体（code/message/data）
├── config/                  # 配置类
│   ├── CorsConfig.java      # 跨域配置
│   ├── MybatisPlusConfig.java # MyBatis-Plus 配置（自动填充）
│   ├── SecurityConfig.java  # Spring Security 配置
│   └── WebMvcConfig.java    # Web MVC 配置
├── controller/              # 控制器
│   ├── AuthController.java  # 认证（登录、注册、验证码）
│   ├── DashboardController.java # 仪表盘统计
│   ├── LogController.java   # 日志管理
│   ├── MenuController.java  # 菜单管理
│   ├── RoleController.java  # 角色管理
│   └── UserController.java  # 用户管理
├── dto/                     # 数据传输对象
│   ├── request/             # 请求 DTO
│   └── response/            # 响应 DTO
├── entity/                  # 数据实体
│   ├── SysLoginLog.java
│   ├── SysMenu.java
│   ├── SysOperLog.java
│   ├── SysRole.java
│   ├── SysRoleMenu.java
│   ├── SysUser.java
│   └── SysUserRole.java
├── exception/               # 异常处理
│   ├── BusinessException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
├── mapper/                  # MyBatis-Plus Mapper
├── security/                # 安全组件
│   ├── filter/JwtAuthenticationFilter.java  # JWT 认证过滤器
│   ├── handler/             # 认证/授权异常处理器
│   ├── service/UserDetailsServiceImpl.java  # 用户详情加载
│   └── utils/JwtUtils.java  # JWT 工具类
├── service/                 # 业务服务层
│   └── impl/                # 服务实现
└── utils/                   # 工具类
    ├── CaptchaStore.java    # 验证码存储
    ├── CaptchaUtils.java    # 验证码生成
    └── RedisUtils.java      # Redis 工具
```

## 权限控制

### 后端接口鉴权

使用自定义 `@RequirePermission` 注解 + AOP 切面实现接口级权限控制：

```java
@GetMapping
@RequirePermission("user:list")
public Result<List<SysUser>> list() { ... }

@PostMapping
@RequirePermission("user:add")
public Result<SysUser> create(@RequestBody UserCreateReq req) { ... }
```

`PermissionAspect` 切面拦截所有标注了 `@RequirePermission` 的方法，从 Spring Security 上下文中获取当前用户的权限列表，与注解值进行匹配。

### 操作日志

使用 `@LogOperation` 注解 + AOP 切面自动记录操作日志：

```java
@DeleteMapping("/{id}")
@RequirePermission("user:delete")
@LogOperation("删除用户")
public Result<Void> delete(@PathVariable("id") Long id) { ... }
```

`LogAspect` 切面记录操作人、操作描述、类名、方法名、IP 地址、耗时和成功/失败状态。

## API 接口

### 认证（AuthController）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /captcha | 获取图形验证码 |
| POST | /login | 用户登录 |
| POST | /register | 用户注册 |
| POST | /logout | 用户登出 |

### 仪表盘（DashboardController）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /dashboard/stats | 获取系统统计信息 |

### 用户管理（UserController）
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /users | `user:list` | 分页查询用户列表 |
| GET | /users/options | — | 获取用户选项列表 |
| GET | /users/profile | — | 获取当前用户信息 |
| PUT | /users/profile | — | 修改个人信息 |
| PUT | /users/password | — | 修改密码 |
| GET | /users/{id} | `user:view` | 获取用户详情 |
| POST | /users | `user:add` | 新增用户 |
| PUT | /users/{id} | `user:edit` | 修改用户 |
| DELETE | /users/{id} | `user:delete` | 删除用户 |

### 角色管理（RoleController）
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /roles | `role:list` | 分页查询角色列表 |
| GET | /roles/{id} | `role:view` | 获取角色详情（含菜单ID） |
| POST | /roles | `role:add` | 新增角色 |
| PUT | /roles/{id} | `role:edit` | 修改角色 |
| DELETE | /roles/{id} | `role:delete` | 删除角色 |
| POST | /roles/{roleId}/assign | `role:assign` | 分配角色权限 |

### 菜单管理（MenuController）
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /menus/tree | — | 获取菜单树（侧边栏和权限分配用） |
| GET | /menus | `menu:list` | 分页查询菜单列表 |
| GET | /menus/{id} | `menu:view` | 获取菜单详情 |
| POST | /menus | `menu:add` | 新增菜单 |
| PUT | /menus/{id} | `menu:edit` | 修改菜单 |
| DELETE | /menus/{id} | `menu:delete` | 删除菜单 |

### 日志管理（LogController）
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /logs/operation | `log:list` | 查询操作日志 |
| DELETE | /logs/operation/{id} | `log:delete` | 删除操作日志 |
| DELETE | /logs/operation/batch | `log:delete` | 批量删除操作日志 |
| DELETE | /logs/operation/clear | `log:delete` | 清空操作日志 |
| GET | /logs/login | `log:list` | 查询登录日志 |
| DELETE | /logs/login/{id} | `log:delete` | 删除登录日志 |
| DELETE | /logs/login/batch | `log:delete` | 批量删除登录日志 |
| DELETE | /logs/login/clear | `log:delete` | 清空登录日志 |
