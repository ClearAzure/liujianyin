package com.music.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LoginVO {
    private String token;
    private UserVO userInfo;
}
