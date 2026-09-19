package com.music.service;

import com.music.dto.LoginDTO;
import com.music.dto.RegisterDTO;
import com.music.exception.BusinessException;
import com.music.vo.LoginVO;
import com.music.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 注册新用户
     *
     * @param dto 注册信息
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 登录结果（token + 用户信息）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 修改用户信息
     *
     * @param userId    用户 ID
     * @param nickname  新昵称
     * @param avatarUrl 新头像 URL
     * @param signature 新签名
     * @return 更新后的用户信息
     */
    UserVO updateUserInfo(Long userId, String nickname, String avatarUrl, String signature);

    /**
     * 切换用户角色（管理员/普通用户）
     *
     * @param userId 用户 ID
     * @return 切换后的用户信息
     */
    UserVO toggleRole(Long userId);

    /**
     * 判断用户是否为管理员
     *
     * @param userId 用户 ID
     * @return true=管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 校验管理员权限
     *
     * @param userId 用户 ID
     * @throws BusinessException 非管理员时抛出
     */
    void requireAdmin(Long userId);
}
