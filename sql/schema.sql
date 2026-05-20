-- ============================================================
-- 在线图书商城 - 数据库建表方案
-- 数据库名: bookstore
-- ============================================================

CREATE DATABASE IF NOT EXISTS bookstore
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE bookstore;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE user (
    user_id     INT           NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    username    VARCHAR(50)   NOT NULL                 COMMENT '用户名',
    password    VARCHAR(100)  NOT NULL                 COMMENT '密码（加密存储）',
    phone       VARCHAR(20)   NOT NULL                 COMMENT '手机号',
    email       VARCHAR(100)  DEFAULT NULL             COMMENT '邮箱地址',
    email_verified TINYINT    NOT NULL DEFAULT 0       COMMENT '邮箱验证状态：0-未验证，1-已验证',
    role        TINYINT       NOT NULL DEFAULT 1       COMMENT '角色：1-普通用户，2-管理员',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 图书分类表
-- ============================================================
CREATE TABLE category (
    category_id   INT          NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    parent_id     INT          NOT NULL DEFAULT 0      COMMENT '父分类ID（0表示一级分类）',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- ============================================================
-- 3. 图书表
-- ============================================================
CREATE TABLE book (
    book_id     INT            NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    book_name   VARCHAR(100)   NOT NULL                COMMENT '图书名称',
    author      VARCHAR(50)    NOT NULL                COMMENT '作者',
    category_id INT            NOT NULL                COMMENT '所属分类ID',
    price       DECIMAL(10,2)  NOT NULL                COMMENT '图书价格',
    stock       INT            NOT NULL DEFAULT 0      COMMENT '库存数量',
    cover_img   VARCHAR(255)   DEFAULT NULL            COMMENT '封面图片路径',
    description TEXT           DEFAULT NULL            COMMENT '图书描述',
    status      TINYINT        NOT NULL DEFAULT 1      COMMENT '上架状态：1-上架，0-下架',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (book_id),
    KEY idx_category_id (category_id),
    KEY idx_book_name (book_name),
    KEY idx_status (status),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- ============================================================
-- 4. 购物车表
-- ============================================================
CREATE TABLE cart (
    cart_id     INT      NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    user_id     INT      NOT NULL                COMMENT '所属用户ID',
    book_id     INT      NOT NULL                COMMENT '图书ID',
    quantity    INT      NOT NULL DEFAULT 1      COMMENT '购买数量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (cart_id),
    UNIQUE KEY uk_user_book (user_id, book_id),
    KEY idx_user_id (user_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_cart_book  FOREIGN KEY (book_id) REFERENCES book(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ============================================================
-- 5. 订单主表
-- ============================================================
CREATE TABLE orders (
    order_id         VARCHAR(32)    NOT NULL                COMMENT '订单号（UUID生成）',
    user_id          INT            NOT NULL                COMMENT '下单用户ID',
    total_price      DECIMAL(10,2)  NOT NULL                COMMENT '订单总金额',
    receiver_name    VARCHAR(50)    NOT NULL                COMMENT '收货人姓名',
    receiver_phone   VARCHAR(20)    NOT NULL                COMMENT '收货人电话',
    receiver_address VARCHAR(255)   NOT NULL                COMMENT '收货地址',
    order_status     TINYINT        NOT NULL DEFAULT 0      COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已收货，4-已取消',
    pay_time         DATETIME       DEFAULT NULL            COMMENT '支付时间',
    deliver_time     DATETIME       DEFAULT NULL            COMMENT '发货时间',
    receive_time     DATETIME       DEFAULT NULL            COMMENT '收货时间',
    cancel_time      DATETIME       DEFAULT NULL            COMMENT '取消时间',
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (order_id),
    KEY idx_user_id (user_id),
    KEY idx_order_status (order_status),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- ============================================================
-- 6. 订单明细表
-- ============================================================
CREATE TABLE order_item (
    order_item_id INT            NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
    order_id      VARCHAR(32)    NOT NULL                COMMENT '所属订单号',
    book_id       INT            NOT NULL                COMMENT '图书ID',
    book_name     VARCHAR(100)   NOT NULL                COMMENT '图书名称（快照）',
    price         DECIMAL(10,2)  NOT NULL                COMMENT '购买时单价',
    quantity      INT            NOT NULL                COMMENT '购买数量',
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (order_item_id),
    KEY idx_order_id (order_id),
    CONSTRAINT fk_order_item_orders FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_order_item_book   FOREIGN KEY (book_id)   REFERENCES book(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ============================================================
-- 7. 收藏表
-- ============================================================
CREATE TABLE favorite (
    favorite_id INT      NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id     INT      NOT NULL                COMMENT '用户ID',
    book_id     INT      NOT NULL                COMMENT '图书ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_book (user_id, book_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_favorite_book  FOREIGN KEY (book_id) REFERENCES book(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
