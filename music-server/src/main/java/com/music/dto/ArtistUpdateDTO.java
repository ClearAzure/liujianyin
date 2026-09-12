package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新歌手请求")
public class ArtistUpdateDTO {
    @Schema(description = "歌手头像URL")
    private String avatarUrl;
}
