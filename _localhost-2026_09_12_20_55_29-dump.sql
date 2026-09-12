-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: music_db
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓撹緫ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '涓撹緫鍚嶇О',
  `cover_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灏侀潰',
  `artist_id` bigint NOT NULL COMMENT '姝屾墜ID',
  `publish_time` date DEFAULT NULL COMMENT '鍙戝竷鏃堕棿',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_album_name` (`name`),
  KEY `idx_album_artist` (`artist_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='涓撹緫琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (6,'小夜曲',NULL,6,NULL,'2026-08-10 18:44:33'),(8,'夏烟火',NULL,8,NULL,'2026-09-08 12:49:44'),(9,'卡农D',NULL,9,NULL,'2026-09-08 13:00:36'),(10,'黑色磁带',NULL,10,NULL,'2026-09-12 13:11:02'),(11,'洛天依精选集',NULL,10,NULL,'2026-09-12 13:29:47'),(12,'洛天依精选集','http://localhost:9000/cover/cover/e8327d7a.jpg',11,NULL,'2026-09-12 18:11:03');
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist`
--

DROP TABLE IF EXISTS `artist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '姝屾墜ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姝屾墜鍚嶇О',
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '澶村儚',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '浠嬬粛',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_artist_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姝屾墜琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist`
--

LOCK TABLES `artist` WRITE;
/*!40000 ALTER TABLE `artist` DISABLE KEYS */;
INSERT INTO `artist` VALUES (6,'直到凌晨也无法安眠',NULL,NULL,'2026-08-10 18:44:33'),(7,'宗次郎','http://localhost:9000/cover/cover/1f5d3733.jpg',NULL,'2026-09-03 11:27:06'),(8,'LIKPIA',NULL,NULL,'2026-09-08 12:49:44'),(9,'dylanf',NULL,NULL,'2026-09-08 13:00:36'),(10,'洛天依','http://localhost:9000/cover/cover/09db9457.jpg',NULL,'2026-09-12 13:11:02'),(11,'烟花燕QianSYp','http://localhost:9000/cover/cover/02a34639.jpg',NULL,'2026-09-12 18:11:03');
/*!40000 ALTER TABLE `artist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `music_id` bigint NOT NULL COMMENT '姝屾洸ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鏀惰棌鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_music` (`user_id`,`music_id`),
  KEY `idx_fav_user` (`user_id`),
  KEY `idx_fav_music` (`music_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏀惰棌琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (7,3,12,'2026-09-07 23:21:03'),(8,3,15,'2026-09-08 13:01:28'),(10,3,16,'2026-09-12 13:15:25');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `music`
--

DROP TABLE IF EXISTS `music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '姝屾洸ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姝屾洸鍚嶇О',
  `artist_id` bigint NOT NULL COMMENT '姝屾墜ID',
  `album_id` bigint DEFAULT NULL COMMENT '涓撹緫ID',
  `cover_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灏侀潰鍦板潃(MinIO)',
  `music_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'mp3鍦板潃(MinIO)',
  `lyric_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姝岃瘝鍦板潃(MinIO)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '姝屾洸鏃堕暱(绉?',
  `play_count` bigint NOT NULL DEFAULT '0' COMMENT '鎾?斁娆℃暟',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '鐘舵? 1=姝ｅ父 0=涓嬫灦',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '涓婁紶鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_music_name` (`name`),
  KEY `idx_music_artist` (`artist_id`),
  KEY `idx_music_album` (`album_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姝屾洸琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music`
--

LOCK TABLES `music` WRITE;
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` VALUES (12,'小夜曲',6,7,'http://localhost:9000/cover/cover/559c7d4c.jpg','http://localhost:9000/music/music/9328ab0d.mp3','http://localhost:9000/lyric/lyric/77d9a5e9.lrc',245,81,1,'2026-08-10 19:08:31'),(13,'与你同在',7,NULL,'http://localhost:9000/cover/cover/34484e2f.jpg','http://localhost:9000/music/music/a3f8458a.mp3',NULL,0,10,1,'2026-09-03 11:27:07'),(14,'夏烟火',8,8,'http://localhost:9000/cover/cover/5ce080cc.jpg','http://localhost:9000/music/music/67a5ff20.mp3',NULL,0,11,1,'2026-09-08 12:49:45'),(15,'卡农',9,9,'http://localhost:9000/cover/cover/7ac6aa10.jpg','http://localhost:9000/music/music/78e501d8.mp3',NULL,301,8,1,'2026-09-08 13:00:36'),(16,'南京夜无电波讯号',10,10,'http://localhost:9000/cover/cover/8099a012.jpg','http://localhost:9000/music/music/40e1cafb.mp3','http://localhost:9000/lyric/lyric/5b3778c4.lrc',178,2,1,'2026-09-12 13:11:04'),(17,'蝴蝶',10,11,'http://localhost:9000/cover/cover/6cffb3c8.jpg','http://localhost:9000/music/music/c82cfede.mp3','http://localhost:9000/lyric/lyric/a1e37b04.lrc',220,1,1,'2026-09-12 13:29:48'),(18,'少女的伤春悲秋',11,12,'http://localhost:9000/cover/cover/e8327d7a.jpg','http://localhost:9000/music/music/c3d2b98e.mp3','http://localhost:9000/lyric/lyric/0fbbdf51.lrc',348,0,1,'2026-09-12 18:11:04');
/*!40000 ALTER TABLE `music` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_history`
--

DROP TABLE IF EXISTS `play_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `play_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `music_id` bigint NOT NULL COMMENT '姝屾洸ID',
  `play_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鎾?斁鏃堕棿',
  `duration_played` int NOT NULL DEFAULT '0' COMMENT '宸叉挱鏀炬椂闀?绉?',
  PRIMARY KEY (`id`),
  KEY `idx_ph_user_time` (`user_id`,`play_time` DESC),
  KEY `idx_ph_music` (`music_id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鎾?斁鍘嗗彶琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_history`
--

LOCK TABLES `play_history` WRITE;
/*!40000 ALTER TABLE `play_history` DISABLE KEYS */;
INSERT INTO `play_history` VALUES (1,3,11,'2026-08-10 18:44:48',0),(2,3,1,'2026-08-10 18:46:18',0),(3,3,11,'2026-08-10 18:46:34',0),(4,3,11,'2026-08-10 18:53:23',0),(5,3,11,'2026-08-10 19:00:08',0),(6,3,11,'2026-08-10 19:00:52',0),(7,3,11,'2026-08-10 19:06:37',0),(8,3,12,'2026-08-10 19:08:36',0),(9,3,12,'2026-08-10 19:09:16',0),(10,3,12,'2026-08-10 19:51:22',0),(11,3,12,'2026-08-10 19:53:47',0),(12,3,12,'2026-08-10 20:13:10',0),(13,3,12,'2026-08-10 20:59:26',0),(14,3,12,'2026-08-10 21:09:25',0),(15,4,12,'2026-08-31 14:40:36',0),(16,4,12,'2026-08-31 14:40:42',0),(17,4,12,'2026-08-31 14:42:42',0),(18,4,12,'2026-08-31 14:47:06',0),(19,4,12,'2026-09-02 13:37:51',0),(20,3,12,'2026-09-03 09:48:43',0),(21,3,12,'2026-09-03 09:48:51',0),(22,3,13,'2026-09-03 11:27:15',0),(23,3,12,'2026-09-03 12:17:19',0),(24,3,13,'2026-09-03 12:26:32',0),(25,3,12,'2026-09-03 12:26:35',0),(26,3,13,'2026-09-03 12:26:35',0),(27,3,13,'2026-09-03 12:26:37',0),(28,3,12,'2026-09-03 12:26:49',0),(29,3,12,'2026-09-03 12:26:55',0),(30,3,12,'2026-09-03 12:28:53',0),(31,3,12,'2026-09-03 12:28:56',0),(32,3,12,'2026-09-03 12:39:55',0),(33,3,13,'2026-09-03 20:04:36',0),(34,3,12,'2026-09-03 20:04:40',0),(35,3,13,'2026-09-03 20:04:42',0),(36,3,12,'2026-09-03 20:13:16',0),(37,3,13,'2026-09-03 20:13:43',0),(38,3,12,'2026-09-03 20:14:05',0),(39,3,13,'2026-09-03 20:14:07',0),(40,3,12,'2026-09-03 20:26:53',0),(41,3,12,'2026-09-03 20:31:20',0),(42,3,12,'2026-09-03 20:31:23',0),(43,3,13,'2026-09-03 20:42:13',0),(44,3,12,'2026-09-05 04:12:59',0),(45,3,13,'2026-09-05 04:17:00',0),(46,3,12,'2026-09-05 04:17:07',0),(47,3,12,'2026-09-05 04:17:27',0),(48,3,13,'2026-09-05 04:17:30',0),(49,3,12,'2026-09-05 04:18:19',0),(50,3,13,'2026-09-05 04:18:23',0),(51,3,12,'2026-09-05 04:18:25',0),(52,3,12,'2026-09-05 11:45:06',0),(53,3,13,'2026-09-05 11:45:27',0),(54,3,13,'2026-09-05 11:45:46',0),(55,3,12,'2026-09-05 11:45:52',0),(56,3,12,'2026-09-05 11:46:03',0),(57,3,13,'2026-09-05 11:46:07',0),(58,3,13,'2026-09-05 11:46:20',0),(59,3,13,'2026-09-05 11:46:37',0),(60,3,12,'2026-09-05 11:46:43',0),(61,3,12,'2026-09-05 11:47:28',0),(62,3,13,'2026-09-05 11:47:55',0),(63,3,12,'2026-09-05 11:53:45',0),(64,3,13,'2026-09-05 11:53:53',0),(65,3,12,'2026-09-05 11:53:56',0),(66,3,12,'2026-09-05 11:54:18',0),(67,3,13,'2026-09-05 11:54:23',0),(68,3,12,'2026-09-05 11:54:54',0),(69,3,13,'2026-09-05 11:54:58',0),(70,3,12,'2026-09-05 11:56:03',0),(71,3,12,'2026-09-05 11:56:27',0),(72,3,12,'2026-09-05 11:56:35',0),(73,3,13,'2026-09-05 11:57:08',0),(74,3,12,'2026-09-05 11:57:19',0),(75,3,13,'2026-09-05 11:57:23',0),(76,3,12,'2026-09-05 11:57:28',0),(77,3,13,'2026-09-05 11:57:34',0),(78,3,12,'2026-09-05 11:57:41',0),(79,3,12,'2026-09-05 11:58:52',0),(80,3,13,'2026-09-05 11:58:56',0),(81,3,12,'2026-09-05 12:03:38',0),(82,3,13,'2026-09-05 12:04:58',0),(83,3,12,'2026-09-05 12:04:59',0),(84,3,13,'2026-09-05 12:06:04',0),(85,3,12,'2026-09-05 12:06:21',0),(86,3,12,'2026-09-05 12:06:28',0),(87,3,13,'2026-09-05 12:06:33',0),(88,3,12,'2026-09-05 12:06:46',0),(89,3,13,'2026-09-05 12:11:28',0),(90,3,12,'2026-09-05 12:13:47',0),(91,3,13,'2026-09-05 12:13:52',0),(92,3,12,'2026-09-05 12:13:57',0),(93,3,13,'2026-09-05 12:14:04',0),(94,3,12,'2026-09-05 12:14:07',0),(95,3,13,'2026-09-05 12:14:09',0),(96,3,12,'2026-09-05 12:14:23',0),(97,3,12,'2026-09-07 22:39:03',0),(98,3,12,'2026-09-08 12:34:55',0),(99,3,13,'2026-09-08 12:45:07',0),(100,3,12,'2026-09-08 12:45:09',0),(101,3,13,'2026-09-08 12:45:10',0),(102,3,12,'2026-09-08 12:45:11',0),(103,3,13,'2026-09-08 12:45:12',0),(104,3,12,'2026-09-08 12:45:21',0),(105,3,13,'2026-09-08 12:45:23',0),(106,3,15,'2026-09-08 13:00:41',0),(107,3,12,'2026-09-08 13:05:42',0),(108,3,15,'2026-09-08 13:12:42',0),(109,3,14,'2026-09-08 13:12:50',0),(110,3,13,'2026-09-08 13:13:47',0),(111,3,12,'2026-09-08 13:13:49',0),(112,3,14,'2026-09-08 13:13:51',0),(113,3,13,'2026-09-08 13:19:07',0),(114,3,14,'2026-09-08 13:19:14',0),(115,3,13,'2026-09-08 13:19:21',0),(116,3,12,'2026-09-08 13:19:21',0),(117,3,12,'2026-09-08 13:19:25',0),(118,3,13,'2026-09-08 13:19:31',0),(119,3,12,'2026-09-08 13:19:32',0),(120,3,13,'2026-09-08 13:19:43',0),(121,3,14,'2026-09-08 13:19:46',0),(122,3,15,'2026-09-08 13:19:47',0),(123,3,12,'2026-09-08 13:19:47',0),(124,3,13,'2026-09-08 13:19:48',0),(125,3,14,'2026-09-08 13:19:48',0),(126,3,15,'2026-09-08 13:19:49',0),(127,3,12,'2026-09-08 13:19:49',0),(128,3,12,'2026-09-08 13:21:00',0),(129,3,13,'2026-09-08 13:21:03',0),(130,3,14,'2026-09-08 13:21:05',0),(131,3,15,'2026-09-08 13:21:05',0),(132,3,12,'2026-09-08 13:21:06',0),(133,3,13,'2026-09-08 13:21:06',0),(134,3,14,'2026-09-08 13:21:12',0),(135,3,13,'2026-09-08 13:21:14',0),(136,3,12,'2026-09-08 13:22:16',0),(137,3,13,'2026-09-08 13:27:45',0),(138,3,14,'2026-09-08 13:27:46',0),(139,3,12,'2026-09-08 13:28:45',0),(140,3,13,'2026-09-08 13:28:48',0),(141,3,14,'2026-09-08 13:28:49',0),(142,3,15,'2026-09-08 13:28:51',0),(143,3,14,'2026-09-08 13:28:52',0),(144,3,15,'2026-09-08 13:28:55',0),(145,3,14,'2026-09-08 13:28:55',0),(146,3,15,'2026-09-08 13:28:57',0),(147,3,14,'2026-09-08 13:28:58',0),(148,3,15,'2026-09-08 13:28:59',0),(149,3,14,'2026-09-08 13:29:00',0),(150,3,14,'2026-09-08 18:50:24',0),(151,3,14,'2026-09-08 18:52:18',0),(152,3,15,'2026-09-08 18:56:08',0),(153,3,12,'2026-09-08 19:01:09',0),(154,3,13,'2026-09-08 19:05:58',0),(155,3,14,'2026-09-08 19:09:11',0),(156,3,15,'2026-09-08 19:13:01',0),(157,3,13,'2026-09-08 19:22:59',0),(158,3,13,'2026-09-08 19:23:12',0),(159,3,12,'2026-09-08 19:36:20',0),(160,3,13,'2026-09-08 19:36:25',0),(161,3,15,'2026-09-08 19:36:28',0),(162,3,12,'2026-09-10 12:05:25',0),(163,3,16,'2026-09-12 13:11:13',0),(164,3,17,'2026-09-12 13:30:13',0),(165,3,16,'2026-09-12 13:37:34',0),(166,3,13,'2026-09-12 13:40:32',0),(167,3,12,'2026-09-12 20:25:18',0);
/*!40000 ALTER TABLE `play_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '姝屽崟ID',
  `user_id` bigint NOT NULL COMMENT '鍒涘缓鑰匢D',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姝屽崟鍚嶅瓧',
  `cover_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灏侀潰',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鎻忚堪',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_playlist_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姝屽崟琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (1,1,'华语经典',NULL,NULL,'2026-08-10 18:04:46'),(2,1,'学习音乐',NULL,NULL,'2026-08-10 18:04:46'),(3,2,'我的收藏',NULL,NULL,'2026-08-10 18:04:46'),(4,3,'纯音乐',NULL,NULL,'2026-09-03 09:29:06'),(5,3,'虚拟歌姬','http://localhost:9000/cover/cover/f724ab5c.jpg','洛天依/星尘/诗岸','2026-09-07 23:21:23');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_music`
--

DROP TABLE IF EXISTS `playlist_music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_music` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `playlist_id` bigint NOT NULL COMMENT '姝屽崟ID',
  `music_id` bigint NOT NULL COMMENT '姝屾洸ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '鎺掑簭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣诲姞鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_plm_playlist` (`playlist_id`),
  KEY `idx_plm_music` (`music_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='姝屽崟姝屾洸鍏崇郴琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_music`
--

LOCK TABLES `playlist_music` WRITE;
/*!40000 ALTER TABLE `playlist_music` DISABLE KEYS */;
INSERT INTO `playlist_music` VALUES (1,1,1,0,'2026-08-10 18:04:46'),(2,1,2,1,'2026-08-10 18:04:46'),(3,1,7,2,'2026-08-10 18:04:46'),(4,1,8,3,'2026-08-10 18:04:46'),(5,2,5,0,'2026-08-10 18:04:46'),(6,2,6,1,'2026-08-10 18:04:46'),(7,3,1,0,'2026-08-10 18:04:46'),(8,3,3,1,'2026-08-10 18:04:46'),(9,3,9,2,'2026-08-10 18:04:46'),(11,4,15,0,'2026-09-08 13:01:14'),(12,4,14,0,'2026-09-08 13:10:06'),(13,6,13,0,'2026-09-08 19:23:02'),(14,5,12,0,'2026-09-11 21:54:11');
/*!40000 ALTER TABLE `playlist_music` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommend_music`
--

DROP TABLE IF EXISTS `recommend_music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommend_music` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `music_id` bigint NOT NULL COMMENT '姝屾洸ID',
  `recommend_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鎺ㄨ崘绫诲瀷: daily/hot/editor',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '鎺掑簭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_rm_type_sort` (`recommend_type`,`sort_order`),
  KEY `idx_rm_music` (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鎺ㄨ崘姝屾洸琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommend_music`
--

LOCK TABLES `recommend_music` WRITE;
/*!40000 ALTER TABLE `recommend_music` DISABLE KEYS */;
/*!40000 ALTER TABLE `recommend_music` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛鍚',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt鍔犲瘑瀵嗙爜',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '閭??',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鏄电О',
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '澶村儚鍦板潃',
  `signature` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '涓??绛惧悕',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '璐﹀彿鐘舵? 1=姝ｅ父 0=绂佺敤',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色 0=普通用户 1=管理员',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (3,'ClearAzure','$2a$10$WJVSKus/YVl2JfkP.rMDUufg5vuPvzq7VejeW8fHpbS2WRUQjMrEa','clearazure0@gmail','ClearAzure','http://localhost:9000/cover/cover/a6f5dbc4.png',NULL,1,'2026-08-10 18:24:46','2026-09-12 20:52:57',1),(5,'admin','$2a$10$KUiLpU2DPb8GEiWnPDYuS.5RrsOO0HgcYGV75EvLbbhr4sDu1vlDG',NULL,'管理员',NULL,NULL,1,'2026-09-12 20:52:45','2026-09-12 20:52:45',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-12 20:55:30
