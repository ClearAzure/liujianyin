package com.music.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "歌单实体")
public class Playlist {
    @Schema(description = "歌单ID")
    private Long id;
    @Schema(description = "所属用户ID")
    private Long userId;
    @Schema(description = "歌单名称")
    private String name;
    @Schema(description = "歌单封面URL")
    private String coverUrl;
    @Schema(description = "歌单描述")
    private String description;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
