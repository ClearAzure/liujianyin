-- 数据库里 URL 存的是旧 IP，改前端/配置不影响老数据，需要手动改库
-- 只替换 host，MinIO 里的对象名（路径）保持不变
USE music_db;

-- 情况一：旧地址是云公网 IP
UPDATE music    SET cover_url = REPLACE(cover_url, 'http://1.95.127.236:9000', 'http://192.168.100.128:9000'),
                    music_url = REPLACE(music_url, 'http://1.95.127.236:9000', 'http://192.168.100.128:9000'),
                    lyric_url = REPLACE(lyric_url, 'http://1.95.127.236:9000', 'http://192.168.100.128:9000');
UPDATE album    SET cover_url  = REPLACE(cover_url,  'http://1.95.127.236:9000', 'http://192.168.100.128:9000');
UPDATE artist   SET avatar_url = REPLACE(avatar_url, 'http://1.95.127.236:9000', 'http://192.168.100.128:9000');
UPDATE playlist SET cover_url  = REPLACE(cover_url,  'http://1.95.127.236:9000', 'http://192.168.100.128:9000');
UPDATE user     SET avatar_url = REPLACE(avatar_url, 'http://1.95.127.236:9000', 'http://192.168.100.128:9000');

-- 情况二：如果之前是本地开发上传的，URL 可能是 localhost:9000，也一并替换
UPDATE music    SET cover_url = REPLACE(cover_url, 'http://localhost:9000', 'http://192.168.100.128:9000'),
                    music_url = REPLACE(music_url, 'http://localhost:9000', 'http://192.168.100.128:9000'),
                    lyric_url = REPLACE(lyric_url, 'http://localhost:9000', 'http://192.168.100.128:9000');
UPDATE album    SET cover_url  = REPLACE(cover_url,  'http://localhost:9000', 'http://192.168.100.128:9000');
UPDATE artist   SET avatar_url = REPLACE(avatar_url, 'http://localhost:9000', 'http://192.168.100.128:9000');
UPDATE playlist SET cover_url  = REPLACE(cover_url,  'http://localhost:9000', 'http://192.168.100.128:9000');
UPDATE user     SET avatar_url = REPLACE(avatar_url, 'http://localhost:9000', 'http://192.168.100.128:9000');
