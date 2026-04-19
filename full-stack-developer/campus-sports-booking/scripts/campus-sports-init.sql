-- ============================================================
-- 校园运动场地预约系统 - 数据库初始化脚本
-- 执行方式: mysql -u root -p < campus-sports-init.sql
-- 或在 MySQL 客户端中 source 执行
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_sports
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE campus_sports;

-- ----------------------------
-- 1. 场地分类表
-- ----------------------------
DROP TABLE IF EXISTS system_venue_category;
CREATE TABLE system_venue_category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称(篮球/羽毛球/乒乓球...)',
    icon        VARCHAR(255)          COMMENT '分类图标URL',
    sort_order  INT DEFAULT 0         COMMENT '排序',
    status      TINYINT DEFAULT 1     COMMENT '状态(0禁用/1正常)',
    created_at  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '场地分类表';

-- ----------------------------
-- 2. 场地表
-- ----------------------------
DROP TABLE IF EXISTS system_venue;
CREATE TABLE system_venue (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT      NOT NULL COMMENT '所属分类ID',
    name        VARCHAR(100) NOT NULL COMMENT '场地名称(如：篮球场-东区1号)',
    location    VARCHAR(255)          COMMENT '场地位置',
    capacity    INT                   COMMENT '容纳人数',
    description TEXT                  COMMENT '场地描述',
    image_url   VARCHAR(500)          COMMENT '场地图片',
    status      TINYINT DEFAULT 1     COMMENT '状态(0维护中/1正常/2停用)',
    created_at  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '场地表';

-- ----------------------------
-- 3. 时段表
-- ----------------------------
DROP TABLE IF EXISTS system_time_slot;
CREATE TABLE system_time_slot (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    venue_id    BIGINT      NOT NULL COMMENT '所属场地ID',
    slot_date   DATE        NOT NULL COMMENT '日期',
    start_time  TIME        NOT NULL COMMENT '开始时间',
    end_time    TIME        NOT NULL COMMENT '结束时间',
    max_bookings INT DEFAULT 1        COMMENT '最大可预约数(默认1)',
    booked_count INT DEFAULT 0       COMMENT '已预约数',
    status      TINYINT DEFAULT 1    COMMENT '状态(0不可用/1可预约/2已约满)',
    created_at  DATETIME   DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_venue_date_time (venue_id, slot_date, start_time, end_time),
    INDEX idx_venue_date (venue_id, slot_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '时段表';

-- ----------------------------
-- 4. 预约表
-- ----------------------------
DROP TABLE IF EXISTS system_reservation;
CREATE TABLE system_reservation (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL COMMENT '预约用户ID',
    time_slot_id BIGINT     NOT NULL COMMENT '预约时段ID',
    venue_id    BIGINT      NOT NULL COMMENT '预约场地ID(冗余)',
    status      TINYINT DEFAULT 1     COMMENT '状态(0已取消/1已预约/2已完成/3违约)',
    created_at  DATETIME   DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at DATETIME            COMMENT '取消时间',
    INDEX idx_user_id (user_id),
    INDEX idx_time_slot (time_slot_id),
    INDEX idx_venue_date (venue_id, status),
    INDEX idx_user_date (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '预约表';

-- ----------------------------
-- 5. 用户表
-- ----------------------------
DROP TABLE IF EXISTS system_user;
CREATE TABLE system_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名(学号/工号)',
    password    VARCHAR(255) NOT NULL COMMENT '密码(Bcrypt加密)',
    real_name   VARCHAR(50)           COMMENT '真实姓名',
    phone       VARCHAR(20)           COMMENT '手机号',
    email       VARCHAR(100)          COMMENT '邮箱',
    avatar      VARCHAR(500)          COMMENT '头像URL',
    status      TINYINT DEFAULT 1     COMMENT '状态(0禁用/1正常)',
    created_at  DATETIME   DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户表';

-- ----------------------------
-- 6. 角色表
-- ----------------------------
DROP TABLE IF EXISTS system_role;
CREATE TABLE system_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色代码',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    description VARCHAR(255)          COMMENT '角色描述',
    status      TINYINT DEFAULT 1     COMMENT '状态(0禁用/1正常)',
    created_at  DATETIME   DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色表';

-- ----------------------------
-- 7. 权限表（菜单+按钮+API）
-- ----------------------------
DROP TABLE IF EXISTS system_permission;
CREATE TABLE system_permission (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id     BIGINT DEFAULT 0      COMMENT '父权限ID(0为顶级菜单)',
    name          VARCHAR(50)  NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100)        COMMENT '权限编码',
    resource_type TINYINT DEFAULT 1    COMMENT '类型(1菜单/2按钮/3API)',
    path          VARCHAR(255)          COMMENT '路由路径/接口路径',
    icon          VARCHAR(100)          COMMENT '图标',
    sort_order    INT DEFAULT 0        COMMENT '排序',
    status        TINYINT DEFAULT 1     COMMENT '状态(0禁用/1正常)',
    created_at    DATETIME   DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '权限表';

-- ----------------------------
-- 8. 用户-角色关联表
-- ----------------------------
DROP TABLE IF EXISTS system_user_role;
CREATE TABLE system_user_role (
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id  BIGINT NOT NULL COMMENT '用户ID',
    role_id  BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户角色关联表';

-- ----------------------------
-- 9. 角色-权限关联表
-- ----------------------------
DROP TABLE IF EXISTS system_role_permission;
CREATE TABLE system_role_permission (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id       BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    INDEX idx_role (role_id),
    INDEX idx_perm (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色权限关联表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 插入场地分类
INSERT INTO system_venue_category (name, icon, sort_order, status) VALUES
('篮球', '/icons/basketball.png', 1, 1),
('羽毛球', '/icons/badminton.png', 2, 1),
('乒乓球', '/icons/pingpong.png', 3, 1),
('足球', '/icons/football.png', 4, 1),
('网球', '/icons/tennis.png', 5, 1);

-- 插入场地
INSERT INTO system_venue (category_id, name, location, capacity, description, status) VALUES
(1, '篮球场-东区1号', '东区体育场A1区', 20, '标准室外篮球场，塑胶地面', 1),
(1, '篮球场-西区2号', '西区体育场B2区', 20, '标准室外篮球场，夜间有灯光', 1),
(2, '羽毛球馆-综合馆A', '体育馆一楼A区', 10, '室内羽毛球馆，标准场地4片', 1),
(2, '羽毛球馆-综合馆B', '体育馆一楼B区', 6, '室内羽毛球馆，标准场地2片', 1),
(3, '乒乓球室-活动中心', '学生活动中心2楼', 16, '室内乒乓球室，球台8张', 1),
(4, '足球场-东区标准场', '东区体育场', 50, '标准11人制足球场', 1),
(5, '网球场-综合馆', '体育馆旁网球场', 8, '室外网球场，硬地场地2片', 1);

-- 插入用户 (密码均为 123456，使用 BCrypt 加密)
-- 实际密码: admin123 -> $2b$12$rsNcNSaJgQk19TZFygXH3uW9V/dCo5A7DcjZHZnEPLvdfve.fWJa6
INSERT INTO system_user (username, password, real_name, phone, email, status) VALUES
('admin', '$2b$12$rsNcNSaJgQk19TZFygXH3uW9V/dCo5A7DcjZHZnEPLvdfve.fWJa6', '系统管理员', '13800138000', 'admin@campus.edu', 1),
('student001', '$2b$12$EmVqiLFx7kfssD5C2/Dr/unhYUSM1x9GnDmQhee8gWCbj/cLO525e', '张三', '13900139000', 'student001@campus.edu', 1),
('student002', '$2b$12$EmVqiLFx7kfssD5C2/Dr/unhYUSM1x9GnDmQhee8gWCbj/cLO525e', '李四', '13900139001', 'student002@campus.edu', 1);

-- 插入角色
INSERT INTO system_role (role_code, role_name, description, status) VALUES
('ROLE_ADMIN', '管理员', '系统管理员，拥有所有权限', 1),
('ROLE_USER', '普通用户', '普通用户，可预约场地', 1);

-- 关联 admin -> 管理员
INSERT INTO system_user_role (user_id, role_id) VALUES (1, 1);
-- 关联 student001 -> 普通用户
INSERT INTO system_user_role (user_id, role_id) VALUES (2, 2);

-- 插入权限 (菜单结构)
INSERT INTO system_permission (parent_id, name, permission_code, resource_type, path, icon, sort_order, status) VALUES
-- 一级菜单
(0, '系统管理', 'system', 1, '/system', 'Setting', 100, 1),
(0, '场地管理', 'venue', 1, '/venue', 'Location', 50, 1),
(0, '预约管理', 'reservation', 1, '/reservation', 'Calendar', 40, 1),
(0, '个人中心', 'profile', 1, '/profile', 'User', 200, 1),
-- 系统管理子菜单
(1, '用户管理', 'system:user', 1, '/system/user', '', 1, 1),
(1, '角色管理', 'system:role', 1, '/system/role', '', 2, 1),
(1, '菜单管理', 'system:menu', 1, '/system/menu', '', 3, 1),
-- 场地管理子菜单
(2, '场地分类', 'venue:category', 1, '/venue/category', '', 1, 1),
(2, '场地列表', 'venue:list', 1, '/venue/list', '', 2, 1),
(2, '时段管理', 'venue:timeslot', 1, '/venue/timeslot', '', 3, 1),
-- 预约管理子菜单
(3, '我的预约', 'reservation:my', 1, '/reservation/my', '', 1, 1),
(3, '全部预约', 'reservation:all', 1, '/reservation/all', '', 2, 1);

-- 角色权限关联 (管理员拥有所有权限)
INSERT INTO system_role_permission (role_id, permission_id)
SELECT 1, id FROM system_permission WHERE status = 1;

-- 给普通用户分配基础权限 (我的预约)
INSERT INTO system_role_permission (role_id, permission_id)
SELECT 2, id FROM system_permission WHERE permission_code IN ('reservation:my', 'profile');

-- ============================================================
-- 完成
-- ============================================================
SELECT '数据库初始化完成！' AS result;
