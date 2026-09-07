-- ============================================
-- 琉涧音 V1.0 数据库初始化脚本
-- MySQL 8.0+
-- ============================================

CREATE DATABASE IF NOT EXISTS music_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE music_db;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL                 COMMENT '用户名',
  `password`    VARCHAR(255) NOT NULL                 COMMENT 'BCrypt加密密码',
  `email`       VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
  `nickname`    VARCHAR(50)  DEFAULT NULL             COMMENT '昵称',
  `avatar_url`  VARCHAR(255) DEFAULT NULL             COMMENT '头像地址',
  `signature`   VARCHAR(255) DEFAULT NULL             COMMENT '个性签名',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '账号状态 1=正常 0=禁用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email`    (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 歌手表
-- ============================================
CREATE TABLE IF NOT EXISTS `artist` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '歌手ID',
  `name`        VARCHAR(100) NOT NULL                 COMMENT '歌手名称',
  `avatar_url`  VARCHAR(255) DEFAULT NULL             COMMENT '头像',
  `description` TEXT         DEFAULT NULL             COMMENT '介绍',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_artist_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌手表';

-- ============================================
-- 3. 专辑表
-- ============================================
CREATE TABLE IF NOT EXISTS `album` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '专辑ID',
  `name`         VARCHAR(100) NOT NULL                 COMMENT '专辑名称',
  `cover_url`    VARCHAR(255) DEFAULT NULL             COMMENT '封面',
  `artist_id`    BIGINT       NOT NULL                 COMMENT '歌手ID',
  `publish_time` DATE         DEFAULT NULL             COMMENT '发布时间',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_album_name`    (`name`),
  KEY `idx_album_artist`  (`artist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专辑表';

-- ============================================
-- 4. 歌曲表
-- ============================================
CREATE TABLE IF NOT EXISTS `music` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '歌曲ID',
  `name`        VARCHAR(100) NOT NULL                 COMMENT '歌曲名称',
  `artist_id`   BIGINT       NOT NULL                 COMMENT '歌手ID',
  `album_id`    BIGINT       DEFAULT NULL             COMMENT '专辑ID',
  `cover_url`   VARCHAR(255) DEFAULT NULL             COMMENT '封面地址(MinIO)',
  `music_url`   VARCHAR(255) DEFAULT NULL             COMMENT 'mp3地址(MinIO)',
  `lyric_url`   VARCHAR(255) DEFAULT NULL             COMMENT '歌词地址(MinIO)',
  `duration`    INT          NOT NULL DEFAULT 0       COMMENT '歌曲时长(秒)',
  `play_count`  BIGINT       NOT NULL DEFAULT 0       COMMENT '播放次数',
  `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '状态 1=正常 0=下架',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_music_name`   (`name`),
  KEY `idx_music_artist` (`artist_id`),
  KEY `idx_music_album`  (`album_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲表';

-- ============================================
-- 5. 歌单表
-- ============================================
CREATE TABLE IF NOT EXISTS `playlist` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '歌单ID',
  `user_id`     BIGINT       NOT NULL                 COMMENT '创建者ID',
  `name`        VARCHAR(100) NOT NULL                 COMMENT '歌单名字',
  `cover_url`   VARCHAR(255) DEFAULT NULL             COMMENT '封面',
  `description` VARCHAR(255) DEFAULT NULL             COMMENT '描述',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_playlist_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌单表';

-- ============================================
-- 6. 歌单歌曲关系表
-- ============================================
CREATE TABLE IF NOT EXISTS `playlist_music` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT 'ID',
  `playlist_id` BIGINT   NOT NULL                 COMMENT '歌单ID',
  `music_id`    BIGINT   NOT NULL                 COMMENT '歌曲ID',
  `sort_order`  INT      NOT NULL DEFAULT 0       COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `idx_plm_playlist` (`playlist_id`),
  KEY `idx_plm_music`    (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌单歌曲关系表';

-- ============================================
-- 7. 收藏表
-- ============================================
CREATE TABLE IF NOT EXISTS `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT 'ID',
  `user_id`     BIGINT   NOT NULL                 COMMENT '用户ID',
  `music_id`    BIGINT   NOT NULL                 COMMENT '歌曲ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_music` (`user_id`, `music_id`),
  KEY `idx_fav_user`  (`user_id`),
  KEY `idx_fav_music` (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 8. 播放历史表
-- ============================================
CREATE TABLE IF NOT EXISTS `play_history` (
  `id`              BIGINT   NOT NULL AUTO_INCREMENT  COMMENT 'ID',
  `user_id`         BIGINT   NOT NULL                 COMMENT '用户ID',
  `music_id`        BIGINT   NOT NULL                 COMMENT '歌曲ID',
  `play_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
  `duration_played` INT      NOT NULL DEFAULT 0       COMMENT '已播放时长(秒)',
  PRIMARY KEY (`id`),
  KEY `idx_ph_user_time` (`user_id`, `play_time` DESC),
  KEY `idx_ph_music`     (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放历史表';

-- ============================================
-- 9. 推荐歌曲表（Phase 2 启用，Phase 1 建表）
-- ============================================
CREATE TABLE IF NOT EXISTS `recommend_music` (
  `id`             BIGINT   NOT NULL AUTO_INCREMENT  COMMENT 'ID',
  `music_id`       BIGINT   NOT NULL                 COMMENT '歌曲ID',
  `recommend_type` VARCHAR(20) NOT NULL              COMMENT '推荐类型: daily/hot/editor',
  `sort_order`     INT      NOT NULL DEFAULT 0       COMMENT '排序',
  `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_rm_type_sort` (`recommend_type`, `sort_order`),
  KEY `idx_rm_music`     (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐歌曲表';
