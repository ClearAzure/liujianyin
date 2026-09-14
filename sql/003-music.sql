create table music
(
    id          bigint auto_increment comment '姝屾洸ID'
        primary key,
    name        varchar(100)                       not null comment '姝屾洸鍚嶇О',
    artist_id   bigint                             not null comment '姝屾墜ID',
    album_id    bigint                             null comment '涓撹緫ID',
    cover_url   varchar(255)                       null comment '灏侀潰鍦板潃(MinIO)',
    music_url   varchar(255)                       null comment 'mp3鍦板潃(MinIO)',
    lyric_url   varchar(255)                       null comment '姝岃瘝鍦板潃(MinIO)',
    duration    int      default 0                 not null comment '姝屾洸鏃堕暱(绉?',
    play_count  bigint   default 0                 not null comment '鎾?斁娆℃暟',
    status      tinyint  default 1                 not null comment '鐘舵? 1=姝ｅ父 0=涓嬫灦',
    create_time datetime default CURRENT_TIMESTAMP not null comment '涓婁紶鏃堕棿'
)
    comment '姝屾洸琛';

create index idx_music_album
    on music (album_id);

create index idx_music_artist
    on music (artist_id);

create index idx_music_name
    on music (name);

