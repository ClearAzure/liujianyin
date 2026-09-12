package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "专辑信息")
public class AlbumVO {
    @Schema(description = "专辑ID")
    private Long id;
    @Schema(description = "专辑名称")
    private String name;
    @Schema(description = "专辑封面URL")
    private String coverUrl;
    @Schema(description = "歌手ID")
    private Long artistId;
    @Schema(description = "歌手名称")
    private String artistName;
    @Schema(description = "发布时间")
    private LocalDate publishTime;
    @Schema(description = "歌曲数量")
    private Long musicCount;
    @Schema(description = "专辑歌曲列表（详情时返回）")
    private List<MusicVO> songs;
}
