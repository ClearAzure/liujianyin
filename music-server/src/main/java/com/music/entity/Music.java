package com.music.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Music {//对应数据库中的music表
    //基本信息 , id 是主键，name是音乐名称，artistId是艺术家id，albumId是专辑id
    private Long id;
    private String name;
    private Long artistId;
    private Long albumId;
    //资源链接，coverUrl是封面图片链接，musicUrl是音乐文件链接，lyricUrl是歌词文件链接
    private String coverUrl;
    private String musicUrl;
    private String lyricUrl;
    //其他信息，duration是音乐时长，playCount是播放次数，status是状态，createTime是创建时间
    private Integer duration;
    private Long playCount;
    // status 1:正常 0:删除 和创建时间
    private Integer status;
    private LocalDateTime createTime;
}
