create table album
(
    id           bigint auto_increment comment '涓撹緫ID'
        primary key,
    name         varchar(100)                       not null comment '涓撹緫鍚嶇О',
    cover_url    varchar(255)                       null comment '灏侀潰',
    artist_id    bigint                             not null comment '姝屾墜ID',
    publish_time date                               null comment '鍙戝竷鏃堕棿',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '鍒涘缓鏃堕棿'
)
    comment '涓撹緫琛';

create index idx_album_artist
    on album (artist_id);

create index idx_album_name
    on album (name);

ALTER TABLE album ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '专辑简介';

