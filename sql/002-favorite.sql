create table favorite
(
    id          bigint auto_increment comment 'ID'
        primary key,
    user_id     bigint                             not null comment '鐢ㄦ埛ID',
    music_id    bigint                             not null comment '姝屾洸ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '鏀惰棌鏃堕棿',
    constraint uk_user_music
        unique (user_id, music_id)
)
    comment '鏀惰棌琛';

create index idx_fav_music
    on favorite (music_id);

create index idx_fav_user
    on favorite (user_id);

