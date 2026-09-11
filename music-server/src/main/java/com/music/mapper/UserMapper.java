package com.music.mapper;

import com.music.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户表数据访问接口。
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户ID查询用户。
     *
     * @param id 用户ID
     * @return 用户实体，不存在返回 null
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户实体，不存在返回 null
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 新增用户（默认状态 1，自动回填自增ID）。
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @Insert("INSERT INTO user(username, password, email, nickname, status, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{nickname}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户昵称/头像/签名。
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @Update("UPDATE user SET nickname = #{nickname}, avatar_url = #{avatarUrl}, " +
            "signature = #{signature}, update_time = NOW() WHERE id = #{id}")
    int update(User user);
}
