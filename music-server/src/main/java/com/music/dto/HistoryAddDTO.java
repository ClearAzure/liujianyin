package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "记录播放历史请求")
public class HistoryAddDTO {
    @Schema(description = "歌曲ID")
    private Long musicId;
}
