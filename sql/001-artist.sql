create table artist
(
    id          bigint auto_increment comment '姝屾墜ID'
        primary key,
    name        varchar(100)                       not null comment '姝屾墜鍚嶇О',
    avatar_url  varchar(255)                       null comment '澶村儚',
    description text                               null comment '浠嬬粛',
    create_time datetime default CURRENT_TIMESTAMP not null comment '鍒涘缓鏃堕棿'
)
    comment '姝屾墜琛';

create index idx_artist_name
    on artist (name);

