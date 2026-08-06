# PES Java Backend

Spring Boot backend for PES (Permission & Enterprise System).

## Requirements

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

## Quick Start

1. Make sure MySQL and Redis are running

2. Create database `pes_db` in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS pes_db DEFAULT CHARACTER SET utf8mb4;
```

3. Update database and Redis configuration in `application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pes_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

4. The application will auto-initialize database schema and seed data on first run (configured via `spring.sql.init.mode: always`).

5. Run the application:
```bash
mvn spring-boot:run
```

Default admin account: `admin` / `admin123`

## API Endpoints

### Auth
| Method | Path | Description |
|--------|------|-------------|
| GET | `/captcha` | Get captcha image |
| POST | `/login` | Login |
| POST | `/register` | Register |
| POST | `/logout` | Logout |

### Dashboard
| Method | Path | Description |
|--------|------|-------------|
| GET | `/dashboard/stats` | Get dashboard statistics |

### User Management
| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/users` | `user:list` | Get user list |
| GET | `/users/options` | — | Get user options (lightweight) |
| GET | `/users/profile` | — | Get current user profile |
| PUT | `/users/profile` | — | Update profile |
| PUT | `/users/password` | — | Change password |
| GET | `/users/{id}` | `user:view` | Get user by ID |
| POST | `/users` | `user:add` | Create user |
| PUT | `/users/{id}` | `user:edit` | Update user |
| DELETE | `/users/{id}` | `user:delete` | Delete user |

### Role Management
| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/roles` | `role:list` | Get role list |
| GET | `/roles/{id}` | `role:view` | Get role by ID |
| POST | `/roles` | `role:add` | Create role |
| PUT | `/roles/{id}` | `role:edit` | Update role |
| DELETE | `/roles/{id}` | `role:delete` | Delete role |
| POST | `/roles/{roleId}/assign` | `role:assignPerm` | Assign menus to role |

### Menu Management
| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/menus/tree` | — | Get menu tree |
| GET | `/menus` | `menu:list` | Get menu list |
| GET | `/menus/{id}` | `menu:view` | Get menu by ID |
| POST | `/menus` | `menu:add` | Create menu |
| PUT | `/menus/{id}` | `menu:edit` | Update menu |
| DELETE | `/menus/{id}` | `menu:delete` | Delete menu |

### Log Management
| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/logs/operation` | `log:list` | List operation logs |
| GET | `/logs/login` | `log:list` | List login logs |
| DELETE | `/logs/operation/{id}` | `log:delete` | Delete operation log |
| DELETE | `/logs/login/{id}` | `log:delete` | Delete login log |
| DELETE | `/logs/operation/batch` | `log:delete` | Batch delete operation logs |
| DELETE | `/logs/login/batch` | `log:delete` | Batch delete login logs |
| DELETE | `/logs/operation/clear` | `log:delete` | Clear all operation logs |
| DELETE | `/logs/login/clear` | `log:delete` | Clear all login logs |