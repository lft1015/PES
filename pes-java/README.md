
# PES Java Backend

Spring Boot backend for PES (Permission & Enterprise System).

## Requirements

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

## Quick Start

1. Create database `pes_db` in MySQL

2. Run the schema script:
```sql
source src/main/resources/db/schema.sql
```

3. Update database configuration in `application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pes_db
    username: your_username
    password: your_password
```

4. Run the application:
```bash
mvn spring-boot:run
```

## API Endpoints

### Auth
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### User Management
- `GET /api/users` - Get user list
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Role Management
- `GET /api/roles` - Get role list
- `POST /api/roles` - Create role
- `POST /api/roles/{roleId}/assign` - Assign menus to role

### Menu Management
- `GET /api/menus/tree` - Get menu tree
- `POST /api/menus` - Create menu