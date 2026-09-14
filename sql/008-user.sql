create table user
(
    id          bigint auto_increment comment '鐢ㄦ埛ID'
        primary key,
    username    varchar(50)                        not null comment '鐢ㄦ埛鍚',
    password    varchar(255)                       not null comment 'BCrypt鍔犲瘑瀵嗙爜',
    email       varchar(100)                       null comment '閭??',
    nickname    varchar(50)                        null comment '鏄电О',
    avatar_url  varchar(255)                       null comment '澶村儚鍦板潃',
    signature   varchar(255)                       null comment '涓??绛惧悕',
    status      tinyint  default 1                 not null comment '璐﹀彿鐘舵? 1=姝ｅ父 0=绂佺敤',
    create_time datetime default CURRENT_TIMESTAMP not null comment '鍒涘缓鏃堕棿',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '鏇存柊鏃堕棿',
    role        tinyint  default 0                 not null comment '角色 0=普通用户 1=管理员',
    constraint uk_email
        unique (email),
    constraint uk_username
        unique (username)
)
    comment '鐢ㄦ埛琛';

