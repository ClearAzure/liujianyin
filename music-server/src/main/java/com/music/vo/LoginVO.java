package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录结果")
public class LoginVO {
    @Schema(description = "JWT 令牌")
    private String token;
    @Schema(description = "用户信息")
    private UserVO userInfo;
}
