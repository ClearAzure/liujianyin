package com.music.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlaylistVO {
    private Long id;
    private String name;
    private String coverUrl;
    private String description;
    private List<MusicVO> songs;
}
