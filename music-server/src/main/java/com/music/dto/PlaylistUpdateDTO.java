package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改歌单请求")
public class PlaylistUpdateDTO {
    @Schema(description = "歌单名称")
    private String name;
    @Schema(description = "歌单封面URL")
    private String coverUrl;
}
