package com.music.mapper;

import com.music.entity.PlayHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HistoryMapper {

    @Select("SELECT DISTINCT music_id FROM play_history WHERE user_id = #{userId} " +
            "ORDER BY MAX(play_time) DESC LIMIT #{limit}")
    List<Long> findRecentMusicIds(@Param("userId") Long userId, @Param("limit") int limit);

    @Insert("INSERT INTO play_history(user_id, music_id, play_time, duration_played) " +
            "VALUES(#{userId}, #{musicId}, NOW(), #{durationPlayed})")
    int insert(PlayHistory history);
}
