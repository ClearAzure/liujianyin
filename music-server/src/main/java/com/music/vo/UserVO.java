package com.music.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String signature;
}
