-- ============================================================
-- 固废管理系统 - 演示数据预制脚本
-- 适用数据库: H2 (MySQL Compatibility Mode)
-- 使用方式: 在 H2 Console 或通过 Spring Boot 启动后执行本脚本
-- 说明: 预制 30 批次固废接收数据，含处置/贮存/转运/检测记录
-- ============================================================

-- ==================== 新增用户（密码均为 123456） ====================

INSERT INTO sys_user (id, username, password, real_name, phone, email, status)
VALUES
(2, 'admin2',    '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '管理员2',   '13800000001', 'admin2@swm.com', 1),
(3, 'driver1',   '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '司机张三', '13800000002', 'driver1@swm.com', 1),
(4, 'driver2',   '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '司机李四', '13800000003', 'driver2@swm.com', 1),
(5, 'keeper1',   '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '库管王五', '13800000004', 'keeper1@swm.com', 1),
(6, 'tester1',   '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '检测员赵六', '13800000005', 'tester1@swm.com', 1),
(7, 'operator1', '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '操作员钱七', '13800000006', 'operator1@swm.com', 1),
(8, 'operator2', '$2b$10$uoGO8kYmdc7DpGSXFeTWj.9eq76xjfMcF/JMH9F2g0jNhSj6xeCPK', '操作员孙八', '13800000007', 'operator2@swm.com', 1);

-- ==================== 用户-角色关联 ====================

INSERT INTO sys_user_role (user_id, role_id) VALUES
(2, 1),  -- admin2 → ADMIN
(3, 3),  -- driver1 → DRIVER
(4, 3),  -- driver2 → DRIVER
(5, 2),  -- keeper1 → KEEPER
(6, 5),  -- tester1 → TESTER
(7, 4),  -- operator1 → OPERATOR
(8, 4);  -- operator2 → OPERATOR

-- ==================== 新增字典数据 ====================

-- 产废单位（3 个新）
INSERT INTO swm_waste_producer (id, producer_code, producer_name, license_no, license_suffix, contact_person, contact_phone, address, status) VALUES
(3, 'PROD003', '宝钢股份',   'SWM-LC-2024-003', 'BS', '王刚', '13900003333', '上海市宝山区富锦路885号', 1),
(4, 'PROD004', '中石油化工', 'SWM-LC-2024-004', 'ZS', '刘洋', '13900004444', '江苏省南京市化工园区1号', 1),
(5, 'PROD005', '华润水泥',   'SWM-LC-2024-005', 'HR', '陈明', '13900005555', '广东省深圳市南山区科技园', 1);

-- 车间（2 个新）
INSERT INTO swm_workshop (id, workshop_code, workshop_abbr, workshop_name, status) VALUES
(3, 'WS003', 'F3', '第三车间', 1),
(4, 'WS004', 'F4', '第四车间', 1);

-- 矿源（1 个新）
INSERT INTO swm_mine_source (id, mine_code, mine_name, status) VALUES
(3, 'MINE003', '内蒙古鄂尔多斯矿', 1);

-- 危废类别（2 个新）
INSERT INTO swm_waste_category (id, category_code, category_name, status) VALUES
(3, 'HW03', '废矿物油', 1),
(4, 'HW04', '废有机溶剂', 1);

-- ============================================================
-- 30 批次接收台账
-- distribute across today → 6 days ago, with status mix
-- ============================================================

-- Day 0 (today): 5 records  [status: 2,1,1,3,2]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10001', 3, 5200.00, 1800.00, 3400.00, CURRENT_DATE, '08:30:00', 1, 2, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 1 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10002', 3, 4800.00, 1600.00, 3200.00, CURRENT_DATE, '09:15:00', 1, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 2 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10003', 4, 6100.00, 2000.00, 4100.00, CURRENT_DATE, '10:00:00', 5, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 3 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10004', 3, 4500.00, 1500.00, 3000.00, CURRENT_DATE, '11:20:00', 5, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 4 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10005', 4, 5600.00, 1900.00, 3700.00, CURRENT_DATE, '14:00:00', 1, 2, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 1 AND m.id = 2;

-- Day 1 (yesterday): 5 records  [status: 1,3,1,3,1]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10006', 3, 5000.00, 1700.00, 3300.00, DATEADD('DAY', -1, CURRENT_DATE), '08:45:00', 1, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 2 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10007', 4, 4700.00, 1600.00, 3100.00, DATEADD('DAY', -1, CURRENT_DATE), '09:30:00', 5, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 3 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10008', 3, 6200.00, 2100.00, 4100.00, DATEADD('DAY', -1, CURRENT_DATE), '10:15:00', 5, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 4 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10009', 4, 5300.00, 1800.00, 3500.00, DATEADD('DAY', -1, CURRENT_DATE), '13:30:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 1 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10010', 3, 4400.00, 1400.00, 3000.00, DATEADD('DAY', -1, CURRENT_DATE), '14:50:00', 1, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 2 AND m.id = 1;

-- Day 2 (2 days ago): 5 records  [status: 3,1,3,4,1]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10011', 4, 5800.00, 1900.00, 3900.00, DATEADD('DAY', -2, CURRENT_DATE), '08:20:00', 5, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 3 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10012', 3, 4900.00, 1600.00, 3300.00, DATEADD('DAY', -2, CURRENT_DATE), '09:10:00', 1, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 4 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10013', 4, 5100.00, 1700.00, 3400.00, DATEADD('DAY', -2, CURRENT_DATE), '10:40:00', 5, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 1 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10014', 3, 4600.00, 1500.00, 3100.00, DATEADD('DAY', -2, CURRENT_DATE), '13:00:00', 1, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 2 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10015', 4, 5400.00, 1800.00, 3600.00, DATEADD('DAY', -2, CURRENT_DATE), '14:30:00', 5, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 3 AND m.id = 3;

-- Day 3 (3 days ago): 5 records  [status: 3,4,3,1,3]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10016', 3, 5000.00, 1600.00, 3400.00, DATEADD('DAY', -3, CURRENT_DATE), '08:00:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 4 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10017', 4, 4200.00, 1400.00, 2800.00, DATEADD('DAY', -3, CURRENT_DATE), '09:25:00', 5, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 1 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10018', 3, 5500.00, 1800.00, 3700.00, DATEADD('DAY', -3, CURRENT_DATE), '10:50:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 2 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10019', 4, 4700.00, 1500.00, 3200.00, DATEADD('DAY', -3, CURRENT_DATE), '13:15:00', 5, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 3 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10020', 3, 6000.00, 2000.00, 4000.00, DATEADD('DAY', -3, CURRENT_DATE), '14:40:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 4 AND m.id = 2;

-- Day 4 (4 days ago): 4 records  [status: 3,1,4,3]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10021', 4, 5200.00, 1700.00, 3500.00, DATEADD('DAY', -4, CURRENT_DATE), '08:35:00', 5, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 1 AND m.id = 3;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10022', 3, 4800.00, 1600.00, 3200.00, DATEADD('DAY', -4, CURRENT_DATE), '09:45:00', 1, 1, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 2 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10023', 4, 5600.00, 1900.00, 3700.00, DATEADD('DAY', -4, CURRENT_DATE), '10:30:00', 5, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 3 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10024', 3, 4300.00, 1400.00, 2900.00, DATEADD('DAY', -4, CURRENT_DATE), '13:20:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 4 AND m.id = 3;

-- Day 5 (5 days ago): 3 records  [status: 4,3,4]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10025', 4, 5100.00, 1700.00, 3400.00, DATEADD('DAY', -5, CURRENT_DATE), '08:50:00', 5, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 1 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10026', 3, 5900.00, 2000.00, 3900.00, DATEADD('DAY', -5, CURRENT_DATE), '09:30:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 1 AND w.id = 2 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 1, '京A10027', 4, 4500.00, 1500.00, 3000.00, DATEADD('DAY', -5, CURRENT_DATE), '13:10:00', 5, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 2 AND w.id = 3 AND m.id = 3;

-- Day 6 (6 days ago): 3 records  [status: 3,4,3]

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 2, '京A10028', 3, 5300.00, 1800.00, 3500.00, DATEADD('DAY', -6, CURRENT_DATE), '08:15:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 3 AND w.id = 4 AND m.id = 1;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 3, '京A10029', 4, 4700.00, 1600.00, 3100.00, DATEADD('DAY', -6, CURRENT_DATE), '10:00:00', 5, 4, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 4 AND w.id = 1 AND m.id = 2;

INSERT INTO swm_receiving (batch_no, producer_id, workshop_id, mine_source_id, waste_category_id, plate_number, driver_id, gross_weight, tare_weight, net_weight, receive_date, receive_time, receive_user_id, status, create_by)
SELECT CONCAT(p.license_suffix, '-', w.workshop_abbr, '-', m.mine_code, '-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', '')),
       p.id, w.id, m.id, 4, '京A10030', 3, 5000.00, 1700.00, 3300.00, DATEADD('DAY', -6, CURRENT_DATE), '13:45:00', 1, 3, 1
FROM swm_waste_producer p, swm_workshop w, swm_mine_source m
WHERE p.id = 5 AND w.id = 2 AND m.id = 3;

-- ============================================================
-- 处置台账（18 条，对应 status=3 和 status=4 的接收记录）
-- ============================================================

-- Treatment for 京A10004 (status=3, day0)
INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.88, 2), ROUND(r.net_weight/1000*0.12, 2), 7, 2, 860.0, '回转窑1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10004';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.90, 2), ROUND(r.net_weight/1000*0.10, 2), 8, 2, 72.0, '物化反应釜A', 1
FROM swm_receiving r WHERE r.plate_number = '京A10007';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.87, 2), ROUND(r.net_weight/1000*0.13, 2), 7, 2, 880.0, '高温焚烧炉2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10009';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.89, 2), ROUND(r.net_weight/1000*0.11, 2), 8, 2, 850.0, '回转窑1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10011';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.91, 2), ROUND(r.net_weight/1000*0.09, 2), 7, 2, 68.0, '物化反应釜B', 1
FROM swm_receiving r WHERE r.plate_number = '京A10013';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.86, 2), ROUND(r.net_weight/1000*0.14, 2), 7, 2, 870.0, '高温焚烧炉2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10014';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.92, 2), ROUND(r.net_weight/1000*0.08, 2), 8, 2, 70.0, '物化反应釜A', 1
FROM swm_receiving r WHERE r.plate_number = '京A10016';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.85, 2), ROUND(r.net_weight/1000*0.15, 2), 7, 2, 890.0, '回转窑2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10017';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.88, 2), ROUND(r.net_weight/1000*0.12, 2), 8, 2, 855.0, '高温焚烧炉1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10018';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.90, 2), ROUND(r.net_weight/1000*0.10, 2), 7, 2, 73.0, '物化反应釜C', 1
FROM swm_receiving r WHERE r.plate_number = '京A10020';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.87, 2), ROUND(r.net_weight/1000*0.13, 2), 7, 2, 865.0, '回转窑1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10021';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.91, 2), ROUND(r.net_weight/1000*0.09, 2), 8, 2, 69.0, '物化反应釜A', 1
FROM swm_receiving r WHERE r.plate_number = '京A10023';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.86, 2), ROUND(r.net_weight/1000*0.14, 2), 7, 2, 875.0, '高温焚烧炉2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10024';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.89, 2), ROUND(r.net_weight/1000*0.11, 2), 8, 2, 71.0, '物化反应釜B', 1
FROM swm_receiving r WHERE r.plate_number = '京A10025';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.88, 2), ROUND(r.net_weight/1000*0.12, 2), 7, 2, 860.0, '回转窑2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10026';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.84, 2), ROUND(r.net_weight/1000*0.16, 2), 8, 2, 880.0, '高温焚烧炉1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10027';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 2, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.90, 2), ROUND(r.net_weight/1000*0.10, 2), 7, 2, 70.0, '物化反应釜C', 1
FROM swm_receiving r WHERE r.plate_number = '京A10028';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 1, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.87, 2), ROUND(r.net_weight/1000*0.13, 2), 8, 2, 850.0, '回转窑1号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10029';

INSERT INTO swm_treatment (batch_no, process_id, treatment_date, input_weight, output_weight, treatment_loss, operator_id, status, temperature, equipment_name, create_by)
SELECT r.batch_no, 1, DATEADD('DAY', 2, r.receive_date), ROUND(r.net_weight/1000, 2), ROUND(r.net_weight/1000*0.86, 2), ROUND(r.net_weight/1000*0.14, 2), 7, 2, 870.0, '回转窑2号', 1
FROM swm_receiving r WHERE r.plate_number = '京A10030';

-- ============================================================
-- 贮存台账（80% = 24 条入库记录，其中已处置的也有出库记录）
-- ============================================================

-- Inbound records for 24 batches (storage_type=1)
INSERT INTO swm_storage (batch_no, storage_area, storage_type, change_weight, operation_date, receive_record_id, operator_id, create_by)
SELECT r.batch_no, 'A区-危废暂存库', 1, r.net_weight, r.receive_date, r.id, 5, 1
FROM swm_receiving r WHERE r.plate_number IN ('京A10001','京A10002','京A10003','京A10004','京A10005','京A10006','京A10007','京A10008','京A10009','京A10010','京A10011','京A10012','京A10013','京A10014','京A10015','京A10016','京A10017','京A10018','京A10019','京A10020','京A10021','京A10022','京A10024','京A10028');

-- Outbound records for disposed/completed batches (storage_type=2)
INSERT INTO swm_storage (batch_no, storage_area, storage_type, change_weight, operation_date, destination, receive_record_id, operator_id, create_by)
SELECT r.batch_no, 'A区-危废暂存库', 2, r.net_weight, DATEADD('DAY', 2, r.receive_date), '处置车间', r.id, 5, 1
FROM swm_receiving r WHERE r.plate_number IN ('京A10004','京A10007','京A10009','京A10011','京A10013','京A10014','京A10016','京A10017','京A10018','京A10020','京A10021','京A10023','京A10024','京A10025','京A10026','京A10027','京A10028','京A10029','京A10030');

-- ============================================================
-- 转运台账（18 条，60% 覆盖率，各种状态均有）
-- ============================================================

-- Status 1 (待装车) - 4 records
INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-001'), r.batch_no, r.plate_number, 3, '厂区道路A线', '接收区', '处置车间', CURRENT_DATE, ROUND(r.net_weight/1000, 2), 1, 5
FROM swm_receiving r WHERE r.plate_number = '京A10002';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-002'), r.batch_no, r.plate_number, 4, '厂区道路B线', '接收区', '暂存库A区', CURRENT_DATE, ROUND(r.net_weight/1000, 2), 1, 5
FROM swm_receiving r WHERE r.plate_number = '京A10003';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-003'), r.batch_no, r.plate_number, 3, '厂区道路A线', '接收区', '处置车间', DATEADD('DAY', -1, CURRENT_DATE), ROUND(r.net_weight/1000, 2), 1, 5
FROM swm_receiving r WHERE r.plate_number = '京A10008';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-004'), r.batch_no, r.plate_number, 4, '厂区道路C线', '接收区', '暂存库B区', DATEADD('DAY', -1, CURRENT_DATE), ROUND(r.net_weight/1000, 2), 1, 5
FROM swm_receiving r WHERE r.plate_number = '京A10012';

-- Status 2 (运输中) - 5 records
INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', ''), '-005'), r.batch_no, r.plate_number, '京A10001-备', 3, '厂区道路A线', '接收区', '处置车间', DATEADD('DAY', -2, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -2, CURRENT_DATE), 5, 2, 5
FROM swm_receiving r WHERE r.plate_number = '京A10001';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', ''), '-006'), r.batch_no, r.plate_number, '京B运001', 4, '厂区道路B线', '暂存库A区', '处置车间', DATEADD('DAY', -2, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -2, CURRENT_DATE), 5, 2, 5
FROM swm_receiving r WHERE r.plate_number = '京A10006';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', ''), '-007'), r.batch_no, r.plate_number, '京B运002', 3, '厂区道路C线', '暂存库B区', '处置车间', DATEADD('DAY', -3, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -3, CURRENT_DATE), 5, 2, 5
FROM swm_receiving r WHERE r.plate_number = '京A10010';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', ''), '-008'), r.batch_no, r.plate_number, '京B运003', 4, '厂区道路A线', '接收区', '暂存库A区', DATEADD('DAY', -3, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -3, CURRENT_DATE), 5, 2, 5
FROM swm_receiving r WHERE r.plate_number = '京A10015';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', ''), '-009'), r.batch_no, r.plate_number, '京B运004', 3, '厂区道路B线', '暂存库A区', '处置车间', DATEADD('DAY', -4, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -4, CURRENT_DATE), 5, 2, 5
FROM swm_receiving r WHERE r.plate_number = '京A10019';

-- Status 3 (已到达) - 5 records
INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', ''), '-010'), r.batch_no, r.plate_number, '京A运101', 4, '厂区道路A线', '暂存库A区', '处置车间', DATEADD('DAY', -4, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -4, CURRENT_DATE), 5, DATEADD('DAY', -3, CURRENT_DATE), r.gross_weight, 7, 3, 5
FROM swm_receiving r WHERE r.plate_number = '京A10005';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-011'), r.batch_no, r.plate_number, '京A运102', 3, '厂区道路C线', '接收区', '暂存库B区', DATEADD('DAY', -5, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -5, CURRENT_DATE), 5, DATEADD('DAY', -4, CURRENT_DATE), r.gross_weight, 8, 3, 5
FROM swm_receiving r WHERE r.plate_number = '京A10021';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-012'), r.batch_no, r.plate_number, '京A运103', 4, '厂区道路B线', '暂存库A区', '处置车间', DATEADD('DAY', -5, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -5, CURRENT_DATE), 5, DATEADD('DAY', -4, CURRENT_DATE), r.gross_weight, 7, 3, 5
FROM swm_receiving r WHERE r.plate_number = '京A10022';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-013'), r.batch_no, r.plate_number, '京A运104', 3, '厂区道路A线', '接收区', '处置车间', DATEADD('DAY', -5, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -5, CURRENT_DATE), 5, DATEADD('DAY', -5, CURRENT_DATE), r.gross_weight, 8, 3, 5
FROM swm_receiving r WHERE r.plate_number = '京A10024';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', ''), '-014'), r.batch_no, r.plate_number, '京A运105', 4, '厂区道路C线', '暂存库B区', '处置车间', DATEADD('DAY', -6, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -6, CURRENT_DATE), 5, DATEADD('DAY', -5, CURRENT_DATE), r.gross_weight, 7, 3, 5
FROM swm_receiving r WHERE r.plate_number = '京A10025';

-- Status 4 (已卸车) - 4 records
INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, unloading_time, operator_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', ''), '-015'), r.batch_no, r.plate_number, '京A运201', 3, '厂区道路A线', '暂存库A区', '处置车间', DATEADD('DAY', -6, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -6, CURRENT_DATE), 5, DATEADD('DAY', -5, CURRENT_DATE), r.gross_weight, 7, DATEADD('DAY', -4, CURRENT_DATE), 8, 4, 5
FROM swm_receiving r WHERE r.plate_number = '京A10027';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, unloading_time, operator_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', ''), '-016'), r.batch_no, r.plate_number, '京A运202', 4, '厂区道路B线', '接收区', '暂存库B区', DATEADD('DAY', -6, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -6, CURRENT_DATE), 5, DATEADD('DAY', -6, CURRENT_DATE), r.gross_weight, 8, DATEADD('DAY', -5, CURRENT_DATE), 7, 4, 5
FROM swm_receiving r WHERE r.plate_number = '京A10028';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, unloading_time, operator_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -6, CURRENT_DATE) AS VARCHAR), '-', ''), '-017'), r.batch_no, r.plate_number, '京A运203', 3, '厂区道路C线', '暂存库A区', '处置车间', DATEADD('DAY', -6, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -6, CURRENT_DATE), 5, DATEADD('DAY', -5, CURRENT_DATE), r.gross_weight, 7, DATEADD('DAY', -4, CURRENT_DATE), 8, 4, 5
FROM swm_receiving r WHERE r.plate_number = '京A10029';

INSERT INTO swm_transfer (transfer_no, batch_no, plate_number, vehicle_reg_no, driver_id, planned_route, from_location, to_location, transfer_time, transfer_weight, loading_time, shipping_confirmer_id, arrival_time, arrival_gross_weight, arrival_confirmer_id, unloading_time, operator_id, status, create_by)
SELECT CONCAT('TR-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-018'), r.batch_no, r.plate_number, '京A运204', 4, '厂区道路A线', '暂存库B区', '处置车间', DATEADD('DAY', -5, CURRENT_DATE), ROUND(r.net_weight/1000, 2), DATEADD('DAY', -5, CURRENT_DATE), 5, DATEADD('DAY', -4, CURRENT_DATE), r.gross_weight, 8, DATEADD('DAY', -4, CURRENT_DATE), 7, 4, 5
FROM swm_receiving r WHERE r.plate_number = '京A10030';

-- ============================================================
-- 检测台账（10 条，其中 5 条 status=3 待复核，确保仪表盘待复核数不为 0）
-- ============================================================

-- Status 3 (待复核) - 5 records
INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-001'), 2, r.batch_no, r.id, '固废样品-01', '重金属含量', '原子吸收光谱法', 'GB 5085.3-2007', '≤5.0 mg/L', '3.2 mg/L', '符合标准', 1, 6, 3, 6
FROM swm_receiving r WHERE r.plate_number = '京A10001';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-002'), 2, r.batch_no, r.id, '固废样品-02', '有机物含量', '气相色谱法', 'GB 5085.6-2007', '≤10.0 mg/kg', '7.5 mg/kg', '符合标准', 1, 6, 3, 6
FROM swm_receiving r WHERE r.plate_number = '京A10002';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(CURRENT_DATE AS VARCHAR), '-', ''), '-003'), 3, r.batch_no, r.id, '过程样品-01', 'pH值', '玻璃电极法', 'GB/T 15555.12-1995', '6.0-9.0', '7.8', '符合标准', 1, 6, 3, 6
FROM swm_receiving r WHERE r.plate_number = '京A10005';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', ''), '-004'), 2, r.batch_no, r.id, '固废样品-03', '氰化物含量', '离子色谱法', 'GB 5085.3-2007', '≤1.0 mg/L', '0.6 mg/L', '符合标准', 1, 6, 3, 6
FROM swm_receiving r WHERE r.plate_number = '京A10006';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '-', ''), '-005'), 4, r.batch_no, r.id, '产物样品-01', '重金属浸出', '电感耦合等离子体法', 'GB 5085.3-2007', '≤5.0 mg/L', '4.1 mg/L', '符合标准', 1, 6, 3, 6
FROM swm_receiving r WHERE r.plate_number = '京A10010';

-- Status 4 (已完成) - 5 records
INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, reviewer_id, review_time, review_opinion, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '-', ''), '-006'), 2, r.batch_no, r.id, '固废样品-04', '挥发性有机物', '气相色谱-质谱联用法', 'GB 5085.6-2007', '≤5.0 mg/kg', '2.8 mg/kg', '符合标准', 1, 6, 1, DATEADD('DAY', -1, CURRENT_DATE), '检测数据完整，同意', 4, 6
FROM swm_receiving r WHERE r.plate_number = '京A10011';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, reviewer_id, review_time, review_opinion, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '-', ''), '-007'), 3, r.batch_no, r.id, '过程样品-02', '腐蚀性', '玻璃电极法', 'GB/T 15555.12-1995', 'pH 2-12.5', '8.5', '符合标准', 1, 6, 2, DATEADD('DAY', -2, CURRENT_DATE), '数据合格，通过复核', 4, 6
FROM swm_receiving r WHERE r.plate_number = '京A10016';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, reviewer_id, review_time, review_opinion, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -4, CURRENT_DATE) AS VARCHAR), '-', ''), '-008'), 2, r.batch_no, r.id, '固废样品-05', '多环芳烃', '高效液相色谱法', 'GB 5085.6-2007', '≤0.1 mg/kg', '0.05 mg/kg', '符合标准', 1, 6, 1, DATEADD('DAY', -3, CURRENT_DATE), '结果合格', 4, 6
FROM swm_receiving r WHERE r.plate_number = '京A10021';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, reviewer_id, review_time, review_opinion, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-009'), 4, r.batch_no, r.id, '产物样品-02', '二噁英', '高分辨气相色谱-质谱法', 'GB 18484-2020', '≤0.5 ng TEQ/m³', '0.12 ng TEQ/m³', '符合标准', 1, 6, 2, DATEADD('DAY', -4, CURRENT_DATE), '检测操作规范，数据可靠', 4, 6
FROM swm_receiving r WHERE r.plate_number = '京A10025';

INSERT INTO swm_testing (testing_no, testing_type, batch_no, receiving_id, sample_name, testing_item, testing_method, testing_standard, standard_value, testing_value, testing_result, is_qualified, tester_id, reviewer_id, review_time, review_opinion, status, create_by)
SELECT CONCAT('TJ-', REPLACE(CAST(DATEADD('DAY', -5, CURRENT_DATE) AS VARCHAR), '-', ''), '-010'), 2, r.batch_no, r.id, '固废样品-06', '总石油烃', '红外分光光度法', 'GB 5085.6-2007', '≤500 mg/kg', '320 mg/kg', '符合标准', 1, 6, 1, DATEADD('DAY', -3, CURRENT_DATE), '数据无异常，通过', 4, 6
FROM swm_receiving r WHERE r.plate_number = '京A10026';

-- ============================================================
-- 预制完成
-- ============================================================
