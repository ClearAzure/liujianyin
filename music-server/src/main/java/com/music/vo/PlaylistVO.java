package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "歌单信息")
public class PlaylistVO {
    @Schema(description = "歌单ID")
    private Long id;
    @Schema(description = "歌单名称")
    private String name;
    @Schema(description = "歌单封面URL")
    private String coverUrl;
    @Schema(description = "歌单描述")
    private String description;
    @Schema(description = "歌单内歌曲列表")
    private List<MusicVO> songs;
}
