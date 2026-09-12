package com.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改用户信息请求")
public class UserUpdateDTO {
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatarUrl;
    @Schema(description = "个性签名")
    private String signature;
}
