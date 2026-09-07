package com.music.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Artist {
    private Long id;
    private String name;
    private String avatarUrl;
    private String description;
    private LocalDateTime createTime;
}
