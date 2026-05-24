-- ============================================================
-- 固废管理系统 - 还原清洁数据脚本
-- 适用数据库: H2 (MySQL Compatibility Mode)
-- 说明: 删除所有演示预制数据，恢复到初始种子数据状态
-- ============================================================

-- ==================== 删除业务数据 ====================

DELETE FROM swm_testing WHERE id >= 1;
DELETE FROM swm_transfer WHERE id >= 1;
DELETE FROM swm_storage WHERE id >= 1;
DELETE FROM swm_treatment WHERE id >= 1;
DELETE FROM swm_receiving WHERE id >= 1;

-- ==================== 删除演示用户及角色关联 ====================

DELETE FROM sys_user_role WHERE user_id IN (2, 3, 4, 5, 6, 7, 8);
DELETE FROM sys_user WHERE id IN (2, 3, 4, 5, 6, 7, 8);

-- ==================== 删除新增字典数据 ====================

DELETE FROM swm_waste_producer WHERE producer_code IN ('PROD003', 'PROD004', 'PROD005');
DELETE FROM swm_workshop WHERE workshop_code IN ('WS003', 'WS004');
DELETE FROM swm_mine_source WHERE mine_code IN ('MINE003');
DELETE FROM swm_waste_category WHERE category_code IN ('HW03', 'HW04');

-- ==================== 重置自增序列 ====================

ALTER TABLE swm_testing ALTER COLUMN id RESTART WITH 1;
ALTER TABLE swm_transfer ALTER COLUMN id RESTART WITH 1;
ALTER TABLE swm_storage ALTER COLUMN id RESTART WITH 1;
ALTER TABLE swm_treatment ALTER COLUMN id RESTART WITH 1;
ALTER TABLE swm_receiving ALTER COLUMN id RESTART WITH 1;

ALTER TABLE sys_user_role ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM sys_user_role);
ALTER TABLE sys_user ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM sys_user);

ALTER TABLE swm_waste_producer ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM swm_waste_producer);
ALTER TABLE swm_workshop ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM swm_workshop);
ALTER TABLE swm_mine_source ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM swm_mine_source);
ALTER TABLE swm_waste_category ALTER COLUMN id RESTART WITH (SELECT COUNT(*) + 1 FROM swm_waste_category);

-- ============================================================
-- 还原完成
-- ============================================================
