package com.music.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Playlist {
    private Long id;
    private Long userId;
    private String name;
    private String coverUrl;
    private String description;
    private LocalDateTime createTime;
}
