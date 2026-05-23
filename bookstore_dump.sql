-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: bookstore
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `address_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address_detail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_default` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,2,'Zhang San','13811111111','Beijing Chaoyang Road 1',1,'2026-05-20 16:20:35'),(2,6,'陈俊西','18010582820','内江师范学院',1,'2026-05-20 16:21:25'),(3,6,'陈俊西','18010582820','成都市',0,'2026-05-20 16:22:01');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `book_id` int NOT NULL AUTO_INCREMENT COMMENT '鍥句功ID',
  `book_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍥句功鍚嶇О',
  `author` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浣滆?',
  `category_id` int NOT NULL COMMENT '鎵?睘鍒嗙被ID',
  `price` decimal(10,2) NOT NULL COMMENT '鍥句功浠锋牸',
  `stock` int NOT NULL DEFAULT '0' COMMENT '搴撳瓨鏁伴噺',
  `cover_img` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灏侀潰鍥剧墖璺?緞',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '鍥句功鎻忚堪',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '涓婃灦鐘舵?锛?-涓婃灦锛?-涓嬫灦',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`book_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_book_name` (`book_name`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍥句功琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (2,'三体','刘慈欣',5,93.00,200,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/e89c2448_123.jpg','一部宏大的科幻史诗，讲述地球人类文明与三体文明的首次接触。',1,'2026-05-19 17:01:07','2026-05-20 15:02:38'),(3,'百年孤独','马尔克斯',6,55.00,80,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/d12191c6_124.jpg','魔幻现实主义代表作，讲述布恩迪亚家族七代人的传奇故事。',1,'2026-05-19 17:01:07','2026-05-20 15:20:48'),(4,'人类简史','赫拉利',7,68.00,120,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','从十万年前有生命迹象开始到21世纪的人类发展史。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(5,'算法导论','CLRS',9,128.00,50,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','计算机算法领域的经典教材，涵盖各种常用算法与数据结构。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(6,'Spring实战','Craig Walls',9,89.00,60,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','全面介绍Spring框架的核心特性与实战技巧。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(7,'经济学原理','曼昆',10,75.00,90,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','全球最受欢迎的经济学入门教材，通俗易懂。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(8,'平凡的世界','路遥',5,39.80,150,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','以中国70到80年代为背景，刻画了社会各阶层普通人的形象。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(9,'深入理解Java虚拟机','周志明',9,79.00,70,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','深度解析JVM原理、内存管理、类加载机制与性能调优。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(10,'苏菲的世界','贾德',8,38.00,110,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','以小说的形式讲解西方哲学史，哲学入门经典。',1,'2026-05-19 17:01:07','2026-05-20 15:10:56'),(11,'斗破','土豆',5,35.00,200,'https://web-mybookstore.oss-cn-chengdu.aliyuncs.com/covers/ba0ebc32_123.jpg','莫欺少年穷',1,'2026-05-19 20:41:29','2026-05-20 15:10:56');
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` int NOT NULL AUTO_INCREMENT COMMENT '璐?墿杞﹂」ID',
  `user_id` int NOT NULL COMMENT '鎵?睘鐢ㄦ埛ID',
  `book_id` int NOT NULL COMMENT '鍥句功ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '璐?拱鏁伴噺',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uk_user_book` (`user_id`,`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `fk_cart_book` (`book_id`),
  CONSTRAINT `fk_cart_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璐?墿杞﹁〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (2,2,5,1,'2026-05-19 17:01:07','2026-05-19 17:01:07'),(3,3,2,1,'2026-05-19 17:01:07','2026-05-19 17:01:07'),(4,3,3,1,'2026-05-19 17:01:07','2026-05-19 17:01:07'),(5,3,7,2,'2026-05-19 17:01:07','2026-05-19 17:01:07');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '鍒嗙被ID',
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍒嗙被鍚嶇О',
  `parent_id` int NOT NULL DEFAULT '0' COMMENT '鐖跺垎绫籌D锛?琛ㄧず涓?骇鍒嗙被锛',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鍥句功鍒嗙被琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'文学小说',0,'2026-05-19 17:01:07'),(2,'人文社科',0,'2026-05-19 17:01:07'),(3,'科学技术',0,'2026-05-19 17:01:07'),(4,'经济管理',0,'2026-05-19 17:01:07'),(5,'中国当代小说',1,'2026-05-19 17:01:07'),(6,'外国文学',1,'2026-05-19 17:01:07'),(7,'历史',2,'2026-05-19 17:01:07'),(8,'哲学',2,'2026-05-19 17:01:07'),(9,'计算机',3,'2026-05-19 17:01:07'),(10,'经济学',4,'2026-05-19 17:01:07');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `favorite_id` int NOT NULL AUTO_INCREMENT COMMENT '鏀惰棌ID',
  `user_id` int NOT NULL COMMENT '鐢ㄦ埛ID',
  `book_id` int NOT NULL COMMENT '鍥句功ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_user_book` (`user_id`,`book_id`),
  KEY `fk_favorite_book` (`book_id`),
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏀惰棌琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,2,2,'2026-05-19 17:01:07'),(2,2,5,'2026-05-19 17:01:07'),(4,3,4,'2026-05-19 17:01:07'),(5,3,9,'2026-05-19 17:01:07'),(8,6,2,'2026-05-20 16:11:43'),(10,6,3,'2026-05-20 16:22:06');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT COMMENT '璁㈠崟鏄庣粏ID',
  `order_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎵?睘璁㈠崟鍙',
  `book_id` int NOT NULL COMMENT '鍥句功ID',
  `book_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍥句功鍚嶇О锛堝揩鐓э級',
  `price` decimal(10,2) NOT NULL COMMENT '璐?拱鏃跺崟浠',
  `quantity` int NOT NULL COMMENT '璐?拱鏁伴噺',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`order_item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `fk_order_item_book` (`book_id`),
  CONSTRAINT `fk_order_item_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `fk_order_item_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璁㈠崟鏄庣粏琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '璁㈠崟鍙凤紙UUID鐢熸垚锛',
  `user_id` int NOT NULL COMMENT '涓嬪崟鐢ㄦ埛ID',
  `total_price` decimal(10,2) NOT NULL COMMENT '璁㈠崟鎬婚噾棰',
  `receiver_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏀惰揣浜哄?鍚',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏀惰揣浜虹數璇',
  `receiver_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏀惰揣鍦板潃',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '璁㈠崟鐘舵?锛?-寰呮敮浠橈紝1-宸叉敮浠橈紝2-宸插彂璐э紝3-宸叉敹璐э紝4-宸插彇娑',
  `pay_time` datetime DEFAULT NULL COMMENT '鏀?粯鏃堕棿',
  `deliver_time` datetime DEFAULT NULL COMMENT '鍙戣揣鏃堕棿',
  `receive_time` datetime DEFAULT NULL COMMENT '鏀惰揣鏃堕棿',
  `cancel_time` datetime DEFAULT NULL COMMENT '鍙栨秷鏃堕棿',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璁㈠崟涓昏〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛鍚',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '瀵嗙爜锛堝姞瀵嗗瓨鍌?級',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎵嬫満鍙',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱地址',
  `email_verified` tinyint NOT NULL DEFAULT '0' COMMENT '邮箱验证状态',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '瑙掕壊锛?-鏅??鐢ㄦ埛锛?-绠＄悊鍛',
  `token` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录令牌',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `raw_password` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '明文密码（仅超级管理员可见）',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$xVwUrT8Ikw6saEnwOUKQXOK8rr5pD50Q/T4EDagHNv/km7XbfA8jK','13800000000',NULL,0,3,'074c6d03874d4c4eb3725678a9f95f14','2026-05-19 17:01:07','2026-05-20 17:07:24','123456'),(2,'zhangsan','$2a$10$xVwUrT8Ikw6saEnwOUKQXOK8rr5pD50Q/T4EDagHNv/km7XbfA8jK','13811111111',NULL,0,1,'6955087624914c45ad09c3cffe9bdf42','2026-05-19 17:01:07','2026-05-20 16:20:35',NULL),(3,'lisi','$2a$10$xVwUrT8Ikw6saEnwOUKQXOK8rr5pD50Q/T4EDagHNv/km7XbfA8jK','13822222222',NULL,0,1,NULL,'2026-05-19 17:01:07','2026-05-19 18:05:39',NULL),(4,'wangwu','$2a$10$xVwUrT8Ikw6saEnwOUKQXOK8rr5pD50Q/T4EDagHNv/km7XbfA8jK','13833333333',NULL,0,1,NULL,'2026-05-19 17:01:07','2026-05-19 18:05:39',NULL),(5,'testuser','$2a$10$uDP1L8ESiYvtHaOS8GcTv.xaaMyqiAybkznQBUgjMNPVlgxq4/noW','13855555555',NULL,0,1,NULL,'2026-05-19 18:07:10','2026-05-19 18:07:10',NULL),(6,'chenjunxi','$2a$10$6vFcH1xf/hzH47BwNI.EaOKEFEeh04SWrMS4eSvJVyaz4en6y1Cu2','18384567894','807498486@qq.com',1,1,'6aa281fe73f746ea80243df2fd936cb5','2026-05-20 00:53:15','2026-05-20 17:02:58',NULL),(8,'user111','$2a$10$vSwr9goDO5cGS/fG3aPRROOZhRQb76t94zxiKR4i9u.OalJCHzCZS','18478547854',NULL,1,2,'e6b62d48f7374ded88803312ab7cb5fe','2026-05-20 15:21:44','2026-05-20 15:59:30','111222'),(12,'manager1','$2a$10$3qf31x8O96VI3r7VN3F8EeraeSFSznFiWxLV5gpzNtzCacvK1kv..','',NULL,1,2,'87bf3dac44424eeab93b180512bc93f5','2026-05-20 15:46:52','2026-05-20 15:47:40',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'bookstore'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-23 21:23:29
