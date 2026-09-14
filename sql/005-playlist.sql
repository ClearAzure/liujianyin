create table playlist
(
    id          bigint auto_increment comment '姝屽崟ID'
        primary key,
    user_id     bigint                             not null comment '鍒涘缓鑰匢D',
    name        varchar(100)                       not null comment '姝屽崟鍚嶅瓧',
    cover_url   varchar(255)                       null comment '灏侀潰',
    description varchar(255)                       null comment '鎻忚堪',
    create_time datetime default CURRENT_TIMESTAMP not null comment '鍒涘缓鏃堕棿'
)
    comment '姝屽崟琛';

create index idx_playlist_user
    on playlist (user_id);

