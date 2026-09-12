package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建歌单请求")
public class PlaylistCreateDTO {
    @Schema(description = "歌单名称")
    private String name;
    @Schema(description = "歌单封面URL（可选）")
    private String coverUrl;
    @Schema(description = "歌单简介（可选）")
    private String description;
}
