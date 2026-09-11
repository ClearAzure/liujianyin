package com.music.mapper;

import com.music.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏表数据访问接口。
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 查询用户的某条收藏记录。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     * @return 收藏记录，不存在返回 null
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    Favorite findByUserAndMusic(@Param("userId") Long userId, @Param("musicId") Long musicId);

    /**
     * 查询用户收藏的歌曲ID列表（按收藏时间倒序）。
     *
     * @param userId 用户ID
     * @return 歌曲ID列表
     */
    @Select("SELECT music_id FROM favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Long> findMusicIdsByUserId(Long userId);

    /**
     * 新增收藏记录。
     *
     * @param favorite 收藏实体
     * @return 影响行数
     */
    @Insert("INSERT INTO favorite(user_id, music_id, create_time) VALUES(#{userId}, #{musicId}, NOW())")
    int insert(Favorite favorite);

    /**
     * 删除收藏记录。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     * @return 影响行数
     */
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    int delete(@Param("userId") Long userId, @Param("musicId") Long musicId);

    /**
     * 判断用户是否已收藏某歌曲（返回记录条数）。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     * @return 记录条数（0 表示未收藏）
     */
    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    int exists(@Param("userId") Long userId, @Param("musicId") Long musicId);
}
