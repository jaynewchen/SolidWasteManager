-- ============================================================
-- H2 Database Init Script (MySQL Compatibility Mode)
-- ============================================================

-- System Tables
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    last_login_time TIMESTAMP DEFAULT NULL,
    last_login_ip VARCHAR(50) DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_role_code (role_code)
);

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type CHAR(1) NOT NULL DEFAULT 'C',
    permission VARCHAR(100) DEFAULT NULL,
    path VARCHAR(200) DEFAULT NULL,
    component VARCHAR(200) DEFAULT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    sort_order INT DEFAULT 0,
    visible TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
);

-- Dictionary Tables
CREATE TABLE IF NOT EXISTS swm_waste_producer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producer_code VARCHAR(50) NOT NULL,
    producer_name VARCHAR(100) NOT NULL,
    license_no VARCHAR(50) DEFAULT NULL,
    license_suffix VARCHAR(20) DEFAULT NULL,
    contact_person VARCHAR(50) DEFAULT NULL,
    contact_phone VARCHAR(20) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_producer_code (producer_code)
);

CREATE TABLE IF NOT EXISTS swm_workshop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workshop_code VARCHAR(50) NOT NULL,
    workshop_abbr VARCHAR(20) DEFAULT NULL,
    workshop_name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_workshop_code (workshop_code)
);

CREATE TABLE IF NOT EXISTS swm_mine_source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mine_code VARCHAR(50) NOT NULL,
    mine_name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_mine_code (mine_code)
);

CREATE TABLE IF NOT EXISTS swm_waste_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_category_code (category_code)
);

CREATE TABLE IF NOT EXISTS swm_treatment_process (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_code VARCHAR(50) NOT NULL,
    process_name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_process_code (process_code)
);

-- Business Tables
CREATE TABLE IF NOT EXISTS swm_receiving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(100) DEFAULT NULL,
    producer_id BIGINT DEFAULT NULL,
    workshop_id BIGINT DEFAULT NULL,
    mine_source_id BIGINT DEFAULT NULL,
    waste_category_id BIGINT DEFAULT NULL,
    plate_number VARCHAR(20) DEFAULT NULL,
    driver_id BIGINT DEFAULT NULL,
    gross_weight DECIMAL(12,2) DEFAULT NULL,
    tare_weight DECIMAL(12,2) DEFAULT NULL,
    net_weight DECIMAL(12,2) DEFAULT NULL,
    receive_date DATE DEFAULT NULL,
    receive_time TIME DEFAULT NULL,
    receive_user_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_receiving_batch_no ON swm_receiving(batch_no);
CREATE INDEX IF NOT EXISTS idx_receiving_producer ON swm_receiving(producer_id);
CREATE INDEX IF NOT EXISTS idx_receiving_date ON swm_receiving(receive_date);
CREATE INDEX IF NOT EXISTS idx_receiving_status ON swm_receiving(status);

CREATE TABLE IF NOT EXISTS swm_treatment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(100) DEFAULT NULL,
    process_id BIGINT DEFAULT NULL,
    treatment_date DATE DEFAULT NULL,
    input_weight DECIMAL(12,2) DEFAULT NULL,
    output_weight DECIMAL(12,2) DEFAULT NULL,
    treatment_loss DECIMAL(12,2) DEFAULT NULL,
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    temperature DECIMAL(5,1) DEFAULT NULL,
    equipment_name VARCHAR(100) DEFAULT NULL,
    start_time TIMESTAMP DEFAULT NULL,
    end_time TIMESTAMP DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);
ALTER TABLE swm_treatment ADD COLUMN IF NOT EXISTS temperature DECIMAL(5,1) DEFAULT NULL;
ALTER TABLE swm_treatment ADD COLUMN IF NOT EXISTS equipment_name VARCHAR(100) DEFAULT NULL;
ALTER TABLE swm_treatment ADD COLUMN IF NOT EXISTS start_time TIMESTAMP DEFAULT NULL;
ALTER TABLE swm_treatment ADD COLUMN IF NOT EXISTS end_time TIMESTAMP DEFAULT NULL;
CREATE INDEX IF NOT EXISTS idx_treatment_batch_no ON swm_treatment(batch_no);
CREATE INDEX IF NOT EXISTS idx_treatment_date ON swm_treatment(treatment_date);

CREATE TABLE IF NOT EXISTS swm_storage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(100) NOT NULL,
    storage_area VARCHAR(100) NOT NULL,
    storage_type TINYINT NOT NULL DEFAULT 1,
    change_weight DECIMAL(12,2) NOT NULL,
    operation_date DATE DEFAULT NULL,
    destination VARCHAR(200) DEFAULT NULL,
    receive_record_id BIGINT DEFAULT NULL,
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);
ALTER TABLE swm_storage ADD COLUMN IF NOT EXISTS storage_area VARCHAR(100) DEFAULT NULL;
ALTER TABLE swm_storage ADD COLUMN IF NOT EXISTS destination VARCHAR(200) DEFAULT NULL;
ALTER TABLE swm_storage ADD COLUMN IF NOT EXISTS receive_record_id BIGINT DEFAULT NULL;
ALTER TABLE swm_storage ADD COLUMN IF NOT EXISTS operation_date DATE DEFAULT NULL;
CREATE INDEX IF NOT EXISTS idx_storage_batch_no ON swm_storage(batch_no);
CREATE INDEX IF NOT EXISTS idx_storage_area ON swm_storage(storage_area);
CREATE INDEX IF NOT EXISTS idx_storage_date ON swm_storage(operation_date);
CREATE INDEX IF NOT EXISTS idx_storage_type ON swm_storage(storage_type);

CREATE TABLE IF NOT EXISTS swm_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_no VARCHAR(50) NOT NULL,
    batch_no VARCHAR(100) DEFAULT NULL,
    plate_number VARCHAR(20) DEFAULT NULL,
    vehicle_reg_no VARCHAR(50) DEFAULT NULL,
    driver_id BIGINT DEFAULT NULL,
    planned_route VARCHAR(255) DEFAULT NULL,
    from_location VARCHAR(100) DEFAULT NULL,
    to_location VARCHAR(100) DEFAULT NULL,
    transfer_time DATETIME DEFAULT NULL,
    transfer_weight DECIMAL(12,2) DEFAULT NULL,
    loading_time DATETIME DEFAULT NULL,
    shipping_confirmer_id BIGINT DEFAULT NULL,
    arrival_time DATETIME DEFAULT NULL,
    arrival_gross_weight DECIMAL(12,2) DEFAULT NULL,
    arrival_confirmer_id BIGINT DEFAULT NULL,
    unloading_time DATETIME DEFAULT NULL,
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS plate_number VARCHAR(20) DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS vehicle_reg_no VARCHAR(50) DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS planned_route VARCHAR(255) DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS transfer_time DATETIME DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS loading_time DATETIME DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS shipping_confirmer_id BIGINT DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS arrival_time DATETIME DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS arrival_gross_weight DECIMAL(12,2) DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS arrival_confirmer_id BIGINT DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS unloading_time DATETIME DEFAULT NULL;
ALTER TABLE swm_transfer ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1;
CREATE UNIQUE INDEX IF NOT EXISTS uk_transfer_no ON swm_transfer(transfer_no);
CREATE INDEX IF NOT EXISTS idx_transfer_batch_no ON swm_transfer(batch_no);
CREATE INDEX IF NOT EXISTS idx_transfer_time ON swm_transfer(transfer_time);
CREATE INDEX IF NOT EXISTS idx_transfer_status ON swm_transfer(status);

CREATE TABLE IF NOT EXISTS swm_testing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    testing_no VARCHAR(50) DEFAULT NULL,
    testing_type TINYINT NOT NULL DEFAULT 2,
    batch_no VARCHAR(100) DEFAULT NULL,
    receiving_id BIGINT DEFAULT NULL,
    treatment_id BIGINT DEFAULT NULL,
    plan_name VARCHAR(200) DEFAULT NULL,
    testing_date DATE DEFAULT NULL,
    planned_date DATE DEFAULT NULL,
    sample_name VARCHAR(200) DEFAULT NULL,
    testing_item VARCHAR(200) DEFAULT NULL,
    testing_method VARCHAR(200) DEFAULT NULL,
    testing_standard VARCHAR(200) DEFAULT NULL,
    standard_value VARCHAR(100) DEFAULT NULL,
    testing_value VARCHAR(100) DEFAULT NULL,
    testing_result VARCHAR(500) DEFAULT NULL,
    is_qualified TINYINT DEFAULT NULL,
    tester_id BIGINT DEFAULT NULL,
    plan_executor_id BIGINT DEFAULT NULL,
    reviewer_id BIGINT DEFAULT NULL,
    review_time TIMESTAMP DEFAULT NULL,
    review_opinion VARCHAR(500) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    is_deleted TINYINT DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_testing_no ON swm_testing(testing_no);
CREATE INDEX IF NOT EXISTS idx_testing_batch_no ON swm_testing(batch_no);
CREATE INDEX IF NOT EXISTS idx_testing_type ON swm_testing(testing_type);
CREATE INDEX IF NOT EXISTS idx_testing_receiving_id ON swm_testing(receiving_id);
CREATE INDEX IF NOT EXISTS idx_testing_status ON swm_testing(status);

CREATE TABLE IF NOT EXISTS swm_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    username VARCHAR(50) DEFAULT NULL,
    module VARCHAR(50) DEFAULT NULL,
    operation VARCHAR(50) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    level VARCHAR(20) DEFAULT 'NORMAL',
    request_method VARCHAR(10) DEFAULT NULL,
    request_url VARCHAR(255) DEFAULT NULL,
    request_params VARCHAR(2000) DEFAULT NULL,
    ip_address VARCHAR(50) DEFAULT NULL,
    cost_time BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE swm_operation_log ADD COLUMN IF NOT EXISTS level VARCHAR(20) DEFAULT 'NORMAL';

CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) DEFAULT NULL,
    description VARCHAR(200) DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_config_key (config_key)
);
