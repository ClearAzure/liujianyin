create table playlist_music
(
    id          bigint auto_increment comment 'ID'
        primary key,
    playlist_id bigint                             not null comment '姝屽崟ID',
    music_id    bigint                             not null comment '姝屾洸ID',
    sort_order  int      default 0                 not null comment '鎺掑簭',
    create_time datetime default CURRENT_TIMESTAMP not null comment '娣诲姞鏃堕棿'
)
    comment '姝屽崟姝屾洸鍏崇郴琛';

create index idx_plm_music
    on playlist_music (music_id);

create index idx_plm_playlist
    on playlist_music (playlist_id);

