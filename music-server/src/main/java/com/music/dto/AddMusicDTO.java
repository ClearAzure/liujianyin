package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加歌曲到歌单请求")
public class AddMusicDTO {
    @Schema(description = "歌单ID")
    private Long playlistId;
    @Schema(description = "歌曲ID")
    private Long musicId;
}
