-- ============================================
-- 琉涧音 种子测试数据
-- 需要先运行 init.sql 建库建表
-- ============================================

USE music_db;

-- 插入测试用户 (密码都是 123456，使用BCrypt加密)
INSERT INTO `user` (username, password, email, nickname, status, create_time, update_time)
VALUES
  ('test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'test@qq.com', '测试用户', 1, NOW(), NOW()),
  ('clearazure', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'clearazure@qq.com', 'ClearAzure', 1, NOW(), NOW());

-- 插入歌手
INSERT INTO `artist` (name, avatar_url, create_time) VALUES
  ('周杰伦', NULL, NOW()),
  ('林俊杰', NULL, NOW()),
  ('邓紫棋', NULL, NOW()),
  ('陈奕迅', NULL, NOW()),
  ('Taylor Swift', NULL, NOW());

-- 插入专辑
INSERT INTO `album` (name, artist_id, publish_time, create_time) VALUES
  ('叶惠美', 1, '2003-07-31', NOW()),
  ('她说', 2, '2010-12-08', NOW()),
  ('摩天动物园', 3, '2019-12-27', NOW()),
  ('U-87', 4, '2005-06-07', NOW()),
  ('1989', 5, '2014-10-27', NOW());

-- 插入歌曲（URL留空，需要自行上传mp3后补充）
INSERT INTO `music` (name, artist_id, album_id, cover_url, music_url, lyric_url, duration, play_count, status, create_time) VALUES
  ('晴天', 1, 1, NULL, NULL, NULL, 269, 0, 1, NOW()),
  ('七里香', 1, 1, NULL, NULL, NULL, 299, 0, 1, NOW()),
  ('她说', 2, 2, NULL, NULL, NULL, 312, 0, 1, NOW()),
  ('修炼爱情', 2, 2, NULL, NULL, NULL, 282, 0, 1, NOW()),
  ('光年之外', 3, 3, NULL, NULL, NULL, 235, 0, 1, NOW()),
  ('泡沫', 3, 3, NULL, NULL, NULL, 259, 0, 1, NOW()),
  ('浮夸', 4, 4, NULL, NULL, NULL, 282, 0, 1, NOW()),
  ('十年', 4, 4, NULL, NULL, NULL, 206, 0, 1, NOW()),
  ('Shake It Off', 5, 5, NULL, NULL, NULL, 219, 0, 1, NOW()),
  ('Blank Space', 5, 5, NULL, NULL, NULL, 231, 0, 1, NOW());

-- 插入歌单
INSERT INTO `playlist` (user_id, name, create_time) VALUES
  (1, '华语经典', NOW()),
  (1, '学习音乐', NOW()),
  (2, '我的收藏', NOW());

-- 歌单-歌曲关系
INSERT INTO `playlist_music` (playlist_id, music_id, sort_order, create_time) VALUES
  (1, 1, 0, NOW()),
  (1, 2, 1, NOW()),
  (1, 7, 2, NOW()),
  (1, 8, 3, NOW()),
  (2, 5, 0, NOW()),
  (2, 6, 1, NOW()),
  (3, 1, 0, NOW()),
  (3, 3, 1, NOW()),
  (3, 9, 2, NOW());
