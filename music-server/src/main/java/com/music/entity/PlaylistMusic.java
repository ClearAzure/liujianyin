package com.music.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlaylistMusic {
    private Long id;
    private Long playlistId;
    private Long musicId;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
