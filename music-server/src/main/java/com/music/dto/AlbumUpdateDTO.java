package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新专辑请求")
public class AlbumUpdateDTO {
    @Schema(description = "专辑封面URL")
    private String coverUrl;
}
