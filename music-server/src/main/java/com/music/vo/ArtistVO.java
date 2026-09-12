package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "歌手信息")
public class ArtistVO {
    @Schema(description = "歌手ID")
    private Long id;
    @Schema(description = "歌手名称")
    private String name;
    @Schema(description = "歌手头像URL")
    private String avatarUrl;
    @Schema(description = "歌手简介")
    private String description;
    @Schema(description = "歌曲数量")
    private Long musicCount;
    @Schema(description = "歌手歌曲列表（详情时返回）")
    private List<MusicVO> songs;
}
