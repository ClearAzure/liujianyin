package com.music.service.impl;

import com.music.dto.LoginDTO;
import com.music.dto.RegisterDTO;
import com.music.entity.User;
import com.music.exception.BusinessException;
import com.music.mapper.UserMapper;
import com.music.service.UserService;
import com.music.utils.JwtUtil;
import com.music.vo.LoginVO;
import com.music.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
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

    @Override
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
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return LoginVO.builder()
                .token(token)
                .userInfo(toVO(user))
                .build();
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toVO(user);
    }

    @Override
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

    @Override
    @Transactional
    public UserVO toggleRole(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        int newRole = (user.getRole() != null && user.getRole() == 1) ? 0 : 1;
        userMapper.updateRole(userId, newRole);
        return getUserInfo(userId);
    }

    @Override
    public boolean isAdmin(Long userId) {
        User user = userMapper.findById(userId);
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    @Override
    public void requireAdmin(Long userId) {
        if (!isAdmin(userId)) {
            throw new BusinessException(403, "无权限操作");
        }
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
