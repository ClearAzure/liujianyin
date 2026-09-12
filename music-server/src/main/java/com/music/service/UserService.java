package com.music.service;

import com.music.dto.LoginDTO;
import com.music.dto.RegisterDTO;
import com.music.entity.User;
import com.music.exception.BusinessException;
import com.music.mapper.UserMapper;
import com.music.utils.JwtUtil;
import com.music.vo.LoginVO;
import com.music.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;//BCrypt 哈希加密
    private final JwtUtil jwtUtil;

    /**
     * 注册新用户：校验用户名唯一，密码 BCrypt 加密后写入数据库。
     *
     * @param dto 注册信息（用户名、密码、邮箱）
     * @throws BusinessException 用户名已存在时抛出
     */
    public void register(RegisterDTO dto) {
        User exist = userMapper.findByUsername(dto.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getUsername());
        user.setRole(0);
        userMapper.insert(user);
    }

    /**
     * 用户登录：校验账号密码与状态，生成并返回 JWT token。
     *
     * @param dto 登录信息（用户名、密码）
     * @return 登录结果（token + 用户信息）
     * @throws BusinessException 用户名或密码错误、账号被禁用时抛出
     */
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());//登录成功后生成 JWT
        return LoginVO.builder()
                .token(token)
                .userInfo(toVO(user))
                .build();
    }

    /**
     * 根据用户ID查询用户信息。
     *
     * @param userId 用户ID
     * @return 用户信息
     * @throws BusinessException 用户不存在时抛出
     */
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toVO(user);
    }

    /**
     * 修改用户昵称/头像/签名。
     *
     * @param userId 用户ID
     * @param nickname 新昵称（为空则不修改）
     * @param avatarUrl 新头像URL（为空则不修改）
     * @param signature 新个性签名（为空则不修改；传空串表示清空）
     * @return 更新后的用户信息
     * @throws BusinessException 用户不存在时抛出
     */
    public UserVO updateUserInfo(Long userId, String nickname, String avatarUrl, String signature) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname.trim());
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (signature != null) {
            user.setSignature(signature.trim());
        }
        userMapper.update(user);
        return getUserInfo(userId);
    }

    /**
     * 用户实体转 VO。
     *
     * @param user 用户实体
     * @return 用户信息 VO
     */
    /**
     * 判断用户是否为管理员。
     *
     * @param userId 用户ID
     * @return true=管理员
     */
    public boolean isAdmin(Long userId) {
        User user = userMapper.findById(userId);
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    /**
     * 校验管理员权限，非管理员抛异常。
     *
     * @param userId 用户ID
     * @throws BusinessException 非管理员时抛出（403）
     */
    public void requireAdmin(Long userId) {
        if (!isAdmin(userId)) {
            throw new BusinessException(403, "无权限操作");
        }
    }

    /**
     * 切换当前用户角色（管理员 ↔ 普通用户）。演示用。
     *
     * @param userId 用户ID
     * @return 切换后的用户信息
     */
    public UserVO toggleRole(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        int newRole = (user.getRole() != null && user.getRole() == 1) ? 0 : 1;
        userMapper.updateRole(userId, newRole);
        return getUserInfo(userId);
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .signature(user.getSignature())
                .role(user.getRole())
                .build();
    }
}
