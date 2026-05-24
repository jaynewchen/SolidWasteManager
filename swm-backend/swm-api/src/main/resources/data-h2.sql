-- ============================================================
-- Solid Waste Management System - Initial Data (H2)
-- Uses MERGE to safely re-run on every startup
-- ============================================================

-- ==================== System Users ====================

-- 管理员账号: admin / admin123
MERGE INTO sys_user (id, username, password, real_name, phone, email, status) KEY(id) VALUES
(1, 'admin', '$2a$10$Df8FrCswJPJRhqOilkluje5a9NsJoWa4yLv/3wwsns5gX1Kd/QJLi',
 '系统管理员', '13800000000', 'admin@swm.com', 1);

-- ==================== Roles ====================

MERGE INTO sys_role (id, role_code, role_name, description, status) KEY(id) VALUES
(1, 'ADMIN',   '系统管理员', '拥有所有权限', 1);

MERGE INTO sys_role (id, role_code, role_name, description, status) KEY(id) VALUES
(2, 'KEEPER',  '库管员',     '管理接收、贮存、转运台账', 1);

MERGE INTO sys_role (id, role_code, role_name, description, status) KEY(id) VALUES
(3, 'DRIVER',  '司机',       '查看接收列表权限', 1);

MERGE INTO sys_role (id, role_code, role_name, description, status) KEY(id) VALUES
(4, 'OPERATOR','操作员',     '管理处置台账', 1);

MERGE INTO sys_role (id, role_code, role_name, description, status) KEY(id) VALUES
(5, 'TESTER',  '检测员',     '管理检测台账', 1);

-- ==================== Menus ====================

-- 1. Dashboard
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(1, 0, '仪表盘', 'C', 'dashboard', '/dashboard', 'dashboard/index', 'dashboard', 1, 1, 1);

-- 2. 接收台账
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(2, 0, '接收台账', 'M', NULL, '/receiving', NULL, 'form', 2, 1, 1),
(3, 2, '接收列表', 'C', 'receiving:list', '/receiving/list', 'receiving/index', 'list', 1, 1, 1),
(5, 3, '新增接收', 'B', 'receiving:add', NULL, NULL, NULL, 1, 1, 1),
(6, 3, '编辑接收', 'B', 'receiving:edit', NULL, NULL, NULL, 2, 1, 1),
(7, 3, '删除接收', 'B', 'receiving:delete', NULL, NULL, NULL, 3, 1, 1),
(8, 3, '导出接收', 'B', 'receiving:export', NULL, NULL, NULL, 4, 1, 1);

-- 3. 处置台账
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(9, 0, '处置台账', 'M', NULL, '/treatment', NULL, 'operation', 3, 1, 1),
(10, 9, '处置列表', 'C', 'treatment:list', '/treatment/list', 'treatment/index', 'list', 1, 1, 1),
(11, 10, '新增处置', 'B', 'treatment:add', NULL, NULL, NULL, 1, 1, 1),
(12, 10, '编辑处置', 'B', 'treatment:edit', NULL, NULL, NULL, 2, 1, 1),
(13, 10, '删除处置', 'B', 'treatment:delete', NULL, NULL, NULL, 3, 1, 1);

-- 4. 贮存台账
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(14, 0, '贮存台账', 'M', NULL, '/storage', NULL, 'database', 4, 1, 1),
(15, 14, '贮存列表', 'C', 'storage:list', '/storage/list', 'storage/index', 'list', 1, 1, 1),
(16, 15, '入库', 'B', 'storage:add', NULL, NULL, NULL, 1, 1, 1),
(17, 15, '编辑贮存', 'B', 'storage:edit', NULL, NULL, NULL, 2, 1, 1),
(18, 15, '删除贮存', 'B', 'storage:delete', NULL, NULL, NULL, 3, 1, 1);

-- 5. 转运台账
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(19, 0, '转运台账', 'M', NULL, '/transfer', NULL, 'guide', 5, 1, 1),
(20, 19, '转运列表', 'C', 'transfer:list', '/transfer/list', 'transfer/index', 'list', 1, 1, 1),
(21, 20, '新增转运', 'B', 'transfer:add', NULL, NULL, NULL, 1, 1, 1),
(22, 20, '编辑转运', 'B', 'transfer:edit', NULL, NULL, NULL, 2, 1, 1),
(23, 20, '删除转运', 'B', 'transfer:delete', NULL, NULL, NULL, 3, 1, 1);

-- 6. 检测台账
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(24, 0, '检测台账', 'M', NULL, '/testing', NULL, 'monitor', 6, 1, 1),
(25, 24, '检测列表', 'C', 'testing:list', '/testing/list', 'testing/index', 'list', 1, 1, 1),
(26, 25, '新增检测', 'B', 'testing:add', NULL, NULL, NULL, 1, 1, 1),
(27, 25, '编辑检测', 'B', 'testing:edit', NULL, NULL, NULL, 2, 1, 1),
(28, 25, '删除检测', 'B', 'testing:delete', NULL, NULL, NULL, 3, 1, 1);

-- 7. 系统管理
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(29, 0, '系统管理', 'M', NULL, '/system', NULL, 'setting', 7, 1, 1),
(30, 29, '用户管理', 'C', 'system:user:list', '/system/user', 'system/user/index', 'user', 1, 1, 1),
(31, 30, '新增用户', 'B', 'system:user:add', NULL, NULL, NULL, 1, 1, 1),
(32, 30, '编辑用户', 'B', 'system:user:edit', NULL, NULL, NULL, 2, 1, 1),
(33, 30, '删除用户', 'B', 'system:user:delete', NULL, NULL, NULL, 3, 1, 1),
(34, 29, '角色管理', 'C', 'system:role:list', '/system/role', 'system/role/index', 'peoples', 2, 1, 1),
(35, 34, '新增角色', 'B', 'system:role:add', NULL, NULL, NULL, 1, 1, 1),
(36, 34, '编辑角色', 'B', 'system:role:edit', NULL, NULL, NULL, 2, 1, 1),
(37, 34, '删除角色', 'B', 'system:role:delete', NULL, NULL, NULL, 3, 1, 1),
(38, 29, '菜单管理', 'C', 'system:menu:list', '/system/menu', 'system/menu/index', 'tree-table', 3, 1, 1),
(39, 38, '新增菜单', 'B', 'system:menu:add', NULL, NULL, NULL, 1, 1, 1),
(40, 38, '编辑菜单', 'B', 'system:menu:edit', NULL, NULL, NULL, 2, 1, 1),
(41, 38, '删除菜单', 'B', 'system:menu:delete', NULL, NULL, NULL, 3, 1, 1),
(42, 29, '操作日志', 'C', 'system:log:list', '/system/log', 'system/log/index', 'log', 4, 1, 1);

-- 8. 字典管理
MERGE INTO sys_menu (id, parent_id, menu_name, menu_type, permission, path, component, icon, sort_order, visible, status) KEY(id) VALUES
(43, 0, '字典管理', 'M', NULL, '/dict', NULL, 'dict', 8, 1, 1),
(44, 43, '产废单位', 'C', 'dict:producer:list', '/dict/producer', 'dict/producer/index', 'education', 1, 1, 1),
(45, 44, '新增产废单位', 'B', 'dict:producer:add', NULL, NULL, NULL, 1, 1, 1),
(46, 44, '编辑产废单位', 'B', 'dict:producer:edit', NULL, NULL, NULL, 2, 1, 1),
(47, 44, '删除产废单位', 'B', 'dict:producer:delete', NULL, NULL, NULL, 3, 1, 1),
(48, 43, '车间', 'C', 'dict:workshop:list', '/dict/workshop', 'dict/workshop/index', 'component', 2, 1, 1),
(49, 48, '新增车间', 'B', 'dict:workshop:add', NULL, NULL, NULL, 1, 1, 1),
(50, 48, '编辑车间', 'B', 'dict:workshop:edit', NULL, NULL, NULL, 2, 1, 1),
(51, 48, '删除车间', 'B', 'dict:workshop:delete', NULL, NULL, NULL, 3, 1, 1),
(52, 43, '矿源', 'C', 'dict:mine:list', '/dict/mine', 'dict/mine/index', 'nested', 3, 1, 1),
(53, 52, '新增矿源', 'B', 'dict:mine:add', NULL, NULL, NULL, 1, 1, 1),
(54, 52, '编辑矿源', 'B', 'dict:mine:edit', NULL, NULL, NULL, 2, 1, 1),
(55, 52, '删除矿源', 'B', 'dict:mine:delete', NULL, NULL, NULL, 3, 1, 1),
(56, 43, '危废类别', 'C', 'dict:category:list', '/dict/category', 'dict/category/index', 'tab', 4, 1, 1),
(57, 56, '新增类别', 'B', 'dict:category:add', NULL, NULL, NULL, 1, 1, 1),
(58, 56, '编辑类别', 'B', 'dict:category:edit', NULL, NULL, NULL, 2, 1, 1),
(59, 56, '删除类别', 'B', 'dict:category:delete', NULL, NULL, NULL, 3, 1, 1),
(60, 43, '处置工艺', 'C', 'dict:process:list', '/dict/process', 'dict/process/index', 'example', 5, 1, 1),
(61, 60, '新增工艺', 'B', 'dict:process:add', NULL, NULL, NULL, 1, 1, 1),
(62, 60, '编辑工艺', 'B', 'dict:process:edit', NULL, NULL, NULL, 2, 1, 1),
(63, 60, '删除工艺', 'B', 'dict:process:delete', NULL, NULL, NULL, 3, 1, 1);

-- ==================== Role-Menu Mappings ====================

-- ADMIN role (1) gets all menus (1-63)
MERGE INTO sys_role_menu (role_id, menu_id) KEY(role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 1 AND 63;

-- KEEPER role (2): Dashboard, 接收台账, 贮存台账, 转运台账
MERGE INTO sys_role_menu (role_id, menu_id) KEY(role_id, menu_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 5), (2, 6), (2, 7), (2, 8),
(2, 14), (2, 15), (2, 16), (2, 17), (2, 18),
(2, 19), (2, 20), (2, 21), (2, 22), (2, 23);

-- DRIVER role (3): Dashboard, 接收列表 (view only)
MERGE INTO sys_role_menu (role_id, menu_id) KEY(role_id, menu_id) VALUES
(3, 1), (3, 2), (3, 3);

-- OPERATOR role (4): Dashboard, 处置台账
MERGE INTO sys_role_menu (role_id, menu_id) KEY(role_id, menu_id) VALUES
(4, 1), (4, 9), (4, 10), (4, 11), (4, 12), (4, 13);

-- TESTER role (5): Dashboard, 检测台账
MERGE INTO sys_role_menu (role_id, menu_id) KEY(role_id, menu_id) VALUES
(5, 1), (5, 24), (5, 25), (5, 26), (5, 27), (5, 28);

-- ==================== User-Role Mapping ====================

-- Admin user gets ADMIN role
MERGE INTO sys_user_role (user_id, role_id) KEY(user_id, role_id) VALUES (1, 1);

-- ==================== Dictionary Data ====================

-- 产废单位
MERGE INTO swm_waste_producer (id, producer_code, producer_name, license_no, license_suffix, contact_person, contact_phone, address, status) KEY(id) VALUES
(1, 'PROD001', '中石化炼油厂', 'SWM-LC-2024-001', 'SH', '张三', '13900001111', '北京市朝阳区化工路88号', 1),
(2, 'PROD002', '华能电力公司', 'SWM-LC-2024-002', 'HN', '李四', '13900002222', '河北省唐山市工业园5号', 1);

-- 车间
MERGE INTO swm_workshop (id, workshop_code, workshop_abbr, workshop_name, status) KEY(id) VALUES
(1, 'WS001', 'F1', '第一车间', 1),
(2, 'WS002', 'F2', '第二车间', 1);

-- 矿源
MERGE INTO swm_mine_source (id, mine_code, mine_name, status) KEY(id) VALUES
(1, 'MINE001', '蒙古国TT矿', 1),
(2, 'MINE002', '山西大同煤矿', 1);

-- 危废类别
MERGE INTO swm_waste_category (id, category_code, category_name, status) KEY(id) VALUES
(1, 'HW01', '医疗废物', 1),
(2, 'HW02', '医药废物', 1);

-- 处置工艺
MERGE INTO swm_treatment_process (id, process_code, process_name, status) KEY(id) VALUES
(1, 'P001', '高温焚烧', 1),
(2, 'P002', '物理化学处理', 1);

-- 系统配置
MERGE INTO sys_config (config_key, config_value, description) KEY(config_key) VALUES
('LOG_RETENTION_LIMIT', '5000', '操作日志保留上限条数');
