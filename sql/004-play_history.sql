create table play_history
(
    id              bigint auto_increment comment 'ID'
        primary key,
    user_id         bigint                             not null comment '鐢ㄦ埛ID',
    music_id        bigint                             not null comment '姝屾洸ID',
    play_time       datetime default CURRENT_TIMESTAMP not null comment '鎾?斁鏃堕棿',
    duration_played int      default 0                 not null comment '宸叉挱鏀炬椂闀?绉?'
)
    comment '鎾?斁鍘嗗彶琛';

create index idx_ph_music
    on play_history (music_id);

create index idx_ph_user_time
    on play_history (user_id asc, play_time desc);

