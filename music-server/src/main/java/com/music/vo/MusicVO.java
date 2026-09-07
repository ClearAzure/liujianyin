package com.music.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicVO {//减少后台数据传输量，VO对象只包含前端需要的字段(去除了status和createTime字段)
    private Long id;
    private String name;
    private String artistName;//把artistId查到artist表的name
    private String albumName;//把albumId查到album表的name

    private String coverUrl;
    private String musicUrl;
    private String lyricUrl;

    private Integer duration;
    private Long playCount;
}
