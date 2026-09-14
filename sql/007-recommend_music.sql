create table recommend_music
(
    id             bigint auto_increment comment 'ID'
        primary key,
    music_id       bigint                             not null comment '姝屾洸ID',
    recommend_type varchar(20)                        not null comment '鎺ㄨ崘绫诲瀷: daily/hot/editor',
    sort_order     int      default 0                 not null comment '鎺掑簭',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '鍒涘缓鏃堕棿'
)
    comment '鎺ㄨ崘姝屾洸琛';

create index idx_rm_music
    on recommend_music (music_id);

create index idx_rm_type_sort
    on recommend_music (recommend_type, sort_order);

