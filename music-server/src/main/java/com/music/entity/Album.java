package com.music.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Album {
    private Long id;
    private String name;
    private String coverUrl;
    private Long artistId;
    private LocalDate publishTime;
    private LocalDateTime createTime;
}
