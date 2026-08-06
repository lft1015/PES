-- ================================================
-- 先清空所有数据（按外键依赖顺序，先删子表再删父表）
-- ================================================
DELETE FROM sys_login_log;
DELETE FROM sys_oper_log;
DELETE FROM sys_role_menu;
DELETE FROM sys_user_role;
DELETE FROM sys_menu;
DELETE FROM sys_user;
DELETE FROM sys_role;

INSERT INTO sys_role (id, name, code, description, status) VALUES
(1, '超级管理员', 'admin', '系统超级管理员，拥有所有权限', 1),
(2, '普通用户', 'user', '普通用户，拥有基本权限', 1),
(3, '部门经理', 'manager', '部门经理，拥有部门管理权限', 1),
(4, '运维人员', 'operator', '运维人员，拥有系统运维权限', 1),
(5, '审计员', 'auditor', '审计员，拥有日志审计权限', 1);

INSERT INTO sys_user (id, username, password, nickname, email, phone, status) VALUES
(1, 'admin', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '超级管理员', 'admin@pes.com', '13800000001', 1),
(2, 'zhangsan', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '张三', 'zhangsan@pes.com', '13800000002', 1),
(3, 'lisi', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '李四', 'lisi@pes.com', '13800000003', 1),
(4, 'wangwu', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '王五', 'wangwu@pes.com', '13800000004', 1),
(5, 'zhaoliu', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '赵六', 'zhaoliu@pes.com', '13800000005', 1),
(6, 'sunqi', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '孙七', 'sunqi@pes.com', '13800000006', 0),
(7, 'zhouba', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '周八', 'zhouba@pes.com', '13800000007', 1),
(8, 'wujiu', '$2a$10$s9TNg6NhFV31m9NOUzze3.iwlldKIoUS4MkgUXXJyYTsjPAUCaFrW', '吴九', 'wujiu@pes.com', '13800000008', 1);

INSERT INTO sys_menu (id, parent_id, name, path, component, icon, permission, sort, type, status) VALUES
(1, 0, '系统管理', '/system', 'Layout', 'Setting', 'system:manage', 2, 1, 1),
(2, 1, '用户管理', '/system/user', 'system/user/index', 'User', 'user:list', 1, 1, 1),
(3, 2, '用户查询', '', '', '', 'user:view', 1, 2, 1),
(4, 2, '用户新增', '', '', '', 'user:add', 2, 2, 1),
(5, 2, '用户修改', '', '', '', 'user:edit', 3, 2, 1),
(6, 2, '用户删除', '', '', '', 'user:delete', 4, 2, 1),
(7, 1, '角色管理', '/system/role', 'system/role/index', 'UserFilled', 'role:list', 2, 1, 1),
(8, 7, '角色查询', '', '', '', 'role:view', 1, 2, 1),
(9, 7, '角色新增', '', '', '', 'role:add', 2, 2, 1),
(10, 7, '角色修改', '', '', '', 'role:edit', 3, 2, 1),
(11, 7, '角色删除', '', '', '', 'role:delete', 4, 2, 1),
(12, 1, '菜单管理', '/system/menu', 'system/menu/index', 'Menu', 'menu:list', 3, 1, 1),
(13, 12, '菜单查询', '', '', '', 'menu:view', 1, 2, 1),
(14, 12, '菜单新增', '', '', '', 'menu:add', 2, 2, 1),
(15, 12, '菜单修改', '', '', '', 'menu:edit', 3, 2, 1),
(16, 12, '菜单删除', '', '', '', 'menu:delete', 4, 2, 1),
(17, 0, '日志管理', '/log', 'Layout', 'Document', 'log:manage', 3, 1, 1),
(18, 17, '操作日志', '/log/operation', 'log/operation/index', 'Tickets', 'log:list', 1, 1, 1),
(19, 17, '登录日志', '/log/login', 'log/login/index', 'Stamp', 'log:list', 2, 1, 1),
(20, 19, '日志删除', '', '', '', 'log:delete', 3, 2, 1),
(21, 0, '仪表盘', '/dashboard', 'dashboard/index', 'DataBoard', 'dashboard:view', 1, 1, 1);

INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3),
(4, 4, 2),
(5, 5, 3),
(6, 6, 2),
(7, 7, 4),
(8, 8, 5);

INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 4),
(5, 1, 5),
(6, 1, 6),
(7, 1, 7),
(8, 1, 8),
(9, 1, 9),
(10, 1, 10),
(11, 1, 11),
(12, 1, 12),
(13, 1, 13),
(14, 1, 14),
(15, 1, 15),
(16, 1, 16),
(17, 1, 17),
(18, 1, 18),
(19, 1, 19),
(20, 1, 20),
(21, 1, 21),
(22, 3, 1),
(23, 3, 2),
(24, 3, 3),
(25, 3, 4),
(26, 3, 5),
(27, 3, 17),
(28, 3, 18),
(29, 3, 19),
(30, 2, 21),
(31, 4, 1),
(32, 4, 17),
(33, 4, 18),
(34, 4, 19),
(35, 5, 17),
(36, 5, 18),
(37, 5, 19);

INSERT INTO sys_oper_log (id, username, operation, class_name, method_name, time, status, create_time) VALUES
(1, 'admin', '用户登录', 'AuthController', 'login', 120, 1, '2025-01-15 08:30:00'),
(2, 'admin', '新增用户', 'UserController', 'create', 85, 1, '2025-01-15 09:15:00'),
(3, 'admin', '修改用户', 'UserController', 'update', 65, 1, '2025-01-15 10:00:00'),
(4, 'zhangsan', '用户登录', 'AuthController', 'login', 98, 1, '2025-01-15 10:30:00'),
(5, 'admin', '删除用户', 'UserController', 'delete', 45, 1, '2025-01-15 11:00:00'),
(6, 'admin', '查询角色列表', 'RoleController', 'list', 30, 1, '2025-01-15 14:00:00'),
(7, 'admin', '修改角色', 'RoleController', 'update', 55, 1, '2025-01-15 14:30:00'),
(8, 'lisi', '用户登录', 'AuthController', 'login', 110, 1, '2025-01-15 15:00:00'),
(9, 'admin', '查询菜单列表', 'MenuController', 'list', 25, 1, '2025-01-15 16:00:00'),
(10, 'admin', '修改菜单', 'MenuController', 'update', 40, 1, '2025-01-15 16:30:00'),
(11, 'admin', '新增菜单', 'MenuController', 'create', 50, 1, '2025-01-16 08:00:00'),
(12, 'wangwu', '用户登录', 'AuthController', 'login', 135, 1, '2025-01-16 08:45:00'),
(13, 'admin', '删除菜单', 'MenuController', 'delete', 35, 1, '2025-01-16 09:30:00'),
(14, 'admin', '新增角色', 'RoleController', 'create', 60, 1, '2025-01-16 10:15:00'),
(15, 'admin', '删除角色', 'RoleController', 'delete', 40, 0, '2025-01-16 11:00:00');

INSERT INTO sys_login_log (id, username, ip, status, login_time) VALUES
(1, 'admin', '192.168.1.100', 1, '2025-01-15 08:30:00'),
(2, 'zhangsan', '192.168.1.101', 1, '2025-01-15 10:30:00'),
(3, 'lisi', '192.168.1.102', 1, '2025-01-15 15:00:00'),
(4, 'admin', '192.168.1.100', 1, '2025-01-16 08:00:00'),
(5, 'wangwu', '192.168.1.103', 1, '2025-01-16 08:45:00'),
(6, 'admin', '192.168.1.100', 1, '2025-01-16 13:00:00'),
(7, 'unknown', '10.0.0.55', 0, '2025-01-16 14:30:00'),
(8, 'zhangsan', '192.168.1.101', 0, '2025-01-16 15:00:00'),
(9, 'admin', '192.168.1.100', 1, '2025-01-17 08:00:00'),
(10, 'lisi', '192.168.1.102', 1, '2025-01-17 09:00:00');