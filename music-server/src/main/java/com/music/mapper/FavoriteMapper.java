package com.music.mapper;

import com.music.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    Favorite findByUserAndMusic(@Param("userId") Long userId, @Param("musicId") Long musicId);

    @Select("SELECT music_id FROM favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Long> findMusicIdsByUserId(Long userId);

    @Insert("INSERT INTO favorite(user_id, music_id, create_time) VALUES(#{userId}, #{musicId}, NOW())")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    int delete(@Param("userId") Long userId, @Param("musicId") Long musicId);

    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND music_id = #{musicId}")
    int exists(@Param("userId") Long userId, @Param("musicId") Long musicId);
}
