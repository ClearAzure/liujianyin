package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "歌曲信息")
public class MusicVO {
    @Schema(description = "歌曲ID")
    private Long id;
    @Schema(description = "歌曲名称")
    private String name;
    @Schema(description = "歌手名称")
    private String artistName;
    @Schema(description = "专辑名称")
    private String albumName;
    @Schema(description = "封面图URL")
    private String coverUrl;
    @Schema(description = "音频文件URL")
    private String musicUrl;
    @Schema(description = "歌词文件URL")
    private String lyricUrl;
    @Schema(description = "时长（秒）")
    private Integer duration;
    @Schema(description = "播放次数")
    private Long playCount;
}
