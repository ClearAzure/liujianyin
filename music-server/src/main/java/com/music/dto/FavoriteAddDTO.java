package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "收藏歌曲请求")
public class FavoriteAddDTO {
    @Schema(description = "歌曲ID")
    private Long musicId;
}
