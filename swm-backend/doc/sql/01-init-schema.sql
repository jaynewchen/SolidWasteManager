-- ============================================================
-- Solid Waste Management System - Database Schema
-- 固废管理系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS swm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE swm;

-- ==================== System Tables ====================

-- 系统用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 系统角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 系统菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type CHAR(1) NOT NULL DEFAULT 'C' COMMENT '菜单类型: M=目录, C=菜单, B=按钮',
    permission VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    component VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见: 1=可见, 0=隐藏',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ==================== Dictionary Tables ====================

-- 产废单位表
DROP TABLE IF EXISTS swm_waste_producer;
CREATE TABLE swm_waste_producer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    producer_code VARCHAR(50) NOT NULL COMMENT '单位编码',
    producer_name VARCHAR(100) NOT NULL COMMENT '单位名称',
    license_no VARCHAR(50) DEFAULT NULL COMMENT '许可证编号',
    license_suffix VARCHAR(20) DEFAULT NULL COMMENT '许可证后缀(用于生成批次号)',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_producer_code (producer_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产废单位表';

-- 车间表
DROP TABLE IF EXISTS swm_workshop;
CREATE TABLE swm_workshop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    workshop_code VARCHAR(50) NOT NULL COMMENT '车间编码',
    workshop_abbr VARCHAR(20) DEFAULT NULL COMMENT '车间缩写(用于生成批次号)',
    workshop_name VARCHAR(100) NOT NULL COMMENT '车间名称',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_workshop_code (workshop_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车间表';

-- 矿源表
DROP TABLE IF EXISTS swm_mine_source;
CREATE TABLE swm_mine_source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    mine_code VARCHAR(50) NOT NULL COMMENT '矿源码',
    mine_name VARCHAR(100) NOT NULL COMMENT '矿源名称',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_mine_code (mine_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='矿源表';

-- 危废类别表
DROP TABLE IF EXISTS swm_waste_category;
CREATE TABLE swm_waste_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    category_code VARCHAR(50) NOT NULL COMMENT '类别编码',
    category_name VARCHAR(100) NOT NULL COMMENT '类别名称',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='危废类别表';

-- 处置工艺表
DROP TABLE IF EXISTS swm_treatment_process;
CREATE TABLE swm_treatment_process (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    process_code VARCHAR(50) NOT NULL COMMENT '工艺编码',
    process_name VARCHAR(100) NOT NULL COMMENT '工艺名称',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_process_code (process_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处置工艺表';

-- ==================== Business Tables ====================

-- 接收台账表
DROP TABLE IF EXISTS swm_receiving;
CREATE TABLE swm_receiving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    batch_no VARCHAR(100) DEFAULT NULL COMMENT '批次号',
    producer_id BIGINT DEFAULT NULL COMMENT '产废单位ID',
    workshop_id BIGINT DEFAULT NULL COMMENT '车间ID',
    mine_source_id BIGINT DEFAULT NULL COMMENT '矿源ID',
    waste_category_id BIGINT DEFAULT NULL COMMENT '危废类别ID',
    plate_number VARCHAR(20) DEFAULT NULL COMMENT '车牌号',
    driver_id BIGINT DEFAULT NULL COMMENT '驾驶员ID(关联sys_user)',
    gross_weight DECIMAL(12,2) DEFAULT NULL COMMENT '毛重(kg)',
    tare_weight DECIMAL(12,2) DEFAULT NULL COMMENT '皮重(kg)',
    net_weight DECIMAL(12,2) DEFAULT NULL COMMENT '净重(kg)',
    receive_date DATE DEFAULT NULL COMMENT '接收日期',
    receive_time TIME DEFAULT NULL COMMENT '接收时间',
    receive_user_id BIGINT DEFAULT NULL COMMENT '接收人ID(关联sys_user)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=待处理, 2=处理中, 3=已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_batch_no (batch_no),
    INDEX idx_producer_id (producer_id),
    INDEX idx_receive_date (receive_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接收台账表';

-- 处置台账表
DROP TABLE IF EXISTS swm_treatment;
CREATE TABLE swm_treatment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    batch_no VARCHAR(100) DEFAULT NULL COMMENT '批次号',
    process_id BIGINT DEFAULT NULL COMMENT '处置工艺ID',
    treatment_date DATE DEFAULT NULL COMMENT '处置日期',
    input_weight DECIMAL(12,2) DEFAULT NULL COMMENT '入炉重量(吨)',
    output_weight DECIMAL(12,2) DEFAULT NULL COMMENT '产出重量(吨)',
    treatment_loss DECIMAL(12,2) DEFAULT NULL COMMENT '处置损耗(吨)',
    temperature DECIMAL(5,1) DEFAULT NULL COMMENT '处置温度(℃)',
    equipment_name VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    operator_id BIGINT DEFAULT NULL COMMENT '操作员ID(关联sys_user)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=进行中, 2=已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_batch_no (batch_no),
    INDEX idx_treatment_date (treatment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处置台账表';

-- 贮存台账表
DROP TABLE IF EXISTS swm_storage;
CREATE TABLE swm_storage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    receiving_id BIGINT DEFAULT NULL COMMENT '关联接收记录ID',
    batch_no VARCHAR(100) DEFAULT NULL COMMENT '批次号',
    storage_area VARCHAR(100) DEFAULT NULL COMMENT '贮存区域',
    storage_location VARCHAR(100) DEFAULT NULL COMMENT '贮存位置',
    in_date DATE DEFAULT NULL COMMENT '入库日期',
    out_date DATE DEFAULT NULL COMMENT '出库日期',
    weight DECIMAL(12,2) DEFAULT NULL COMMENT '重量(kg)',
    keeper_id BIGINT DEFAULT NULL COMMENT '保管员ID(关联sys_user)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=在库, 2=已出库',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_receiving_id (receiving_id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贮存台账表';

-- 转运台账表
DROP TABLE IF EXISTS swm_transfer;
CREATE TABLE swm_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    transfer_no VARCHAR(50) NOT NULL COMMENT '转运单号',
    batch_no VARCHAR(100) DEFAULT NULL COMMENT '关联批次号',
    plate_number VARCHAR(20) DEFAULT NULL COMMENT '车牌号',
    vehicle_reg_no VARCHAR(50) DEFAULT NULL COMMENT '车辆备案号',
    driver_id BIGINT DEFAULT NULL COMMENT '驾驶员ID',
    planned_route VARCHAR(255) DEFAULT NULL COMMENT '计划路线',
    from_location VARCHAR(100) DEFAULT NULL COMMENT '起运地',
    to_location VARCHAR(100) DEFAULT NULL COMMENT '目的地',
    transfer_time DATETIME DEFAULT NULL COMMENT '转运时间',
    transfer_weight DECIMAL(12,2) DEFAULT NULL COMMENT '转运重量(吨)',
    loading_time DATETIME DEFAULT NULL COMMENT '装车时间',
    shipping_confirmer_id BIGINT DEFAULT NULL COMMENT '发运确认人ID',
    arrival_time DATETIME DEFAULT NULL COMMENT '到达时间',
    arrival_gross_weight DECIMAL(12,2) DEFAULT NULL COMMENT '到达毛重(吨)',
    arrival_confirmer_id BIGINT DEFAULT NULL COMMENT '到达确认人ID',
    unloading_time DATETIME DEFAULT NULL COMMENT '卸车时间',
    operator_id BIGINT DEFAULT NULL COMMENT '操作员ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=待装车, 2=运输中, 3=已到达, 4=已卸车',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_transfer_no (transfer_no),
    INDEX idx_batch_no (batch_no),
    INDEX idx_transfer_time (transfer_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转运台账表';

-- 检测台账表
DROP TABLE IF EXISTS swm_testing;
CREATE TABLE swm_testing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    testing_no VARCHAR(50) DEFAULT NULL COMMENT '检测单号',
    testing_type TINYINT NOT NULL DEFAULT 2 COMMENT '检测类型: 1=检测计划, 2=进厂物料检测, 3=处理过程检测, 4=产物质量检测',
    batch_no VARCHAR(100) DEFAULT NULL COMMENT '关联批次号',
    receiving_id BIGINT DEFAULT NULL COMMENT '关联接收记录ID',
    treatment_id BIGINT DEFAULT NULL COMMENT '关联处置记录ID',
    plan_name VARCHAR(200) DEFAULT NULL COMMENT '计划名称',
    testing_date DATE DEFAULT NULL COMMENT '检测日期',
    planned_date DATE DEFAULT NULL COMMENT '计划检测日期',
    sample_name VARCHAR(200) DEFAULT NULL COMMENT '样品名称',
    testing_item VARCHAR(200) DEFAULT NULL COMMENT '检测项目',
    testing_method VARCHAR(200) DEFAULT NULL COMMENT '检测方法',
    testing_standard VARCHAR(200) DEFAULT NULL COMMENT '检测标准/依据',
    standard_value VARCHAR(100) DEFAULT NULL COMMENT '标准值',
    testing_value VARCHAR(100) DEFAULT NULL COMMENT '检测值',
    testing_result VARCHAR(500) DEFAULT NULL COMMENT '检测结果',
    is_qualified TINYINT DEFAULT NULL COMMENT '是否合格: 0=不合格, 1=合格',
    tester_id BIGINT DEFAULT NULL COMMENT '检测员ID',
    plan_executor_id BIGINT DEFAULT NULL COMMENT '计划执行人ID',
    reviewer_id BIGINT DEFAULT NULL COMMENT '复核人ID',
    review_time DATETIME DEFAULT NULL COMMENT '复核时间',
    review_opinion VARCHAR(500) DEFAULT NULL COMMENT '复核意见',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_testing_no (testing_no),
    INDEX idx_batch_no (batch_no),
    INDEX idx_testing_type (testing_type),
    INDEX idx_receiving_id (receiving_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测台账表';

-- 操作日志表
DROP TABLE IF EXISTS swm_operation_log;
CREATE TABLE swm_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '操作人用户名',
    module VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    operation VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    method VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params TEXT DEFAULT NULL COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 1=成功, 0=失败',
    error_msg VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    execution_time BIGINT DEFAULT NULL COMMENT '执行时间(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
