package com.music.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistory {
    private Long id;

    private Long userId;
    private Long musicId;

    private LocalDateTime playTime;
    private Integer durationPlayed;// 实际播放时间，单位：秒
}
