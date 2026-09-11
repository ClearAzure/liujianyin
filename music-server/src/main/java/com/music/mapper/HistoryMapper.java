package com.music.mapper;

import com.music.entity.PlayHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 播放历史表数据访问接口。
 */
@Mapper
public interface HistoryMapper {

    /**
     * 查询用户最近播放的歌曲ID列表（按歌曲去重，按最近播放时间倒序，最多 limit 条）。
     *
     * @param userId 用户ID
     * @param limit 最大返回条数
     * @return 歌曲ID列表
     */
    @Select("SELECT music_id FROM play_history WHERE user_id = #{userId} " +
            "GROUP BY music_id ORDER BY MAX(play_time) DESC LIMIT #{limit}")
    List<Long> findRecentMusicIds(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 新增播放历史记录。
     *
     * @param history 播放历史实体
     * @return 影响行数
     */
    @Insert("INSERT INTO play_history(user_id, music_id, play_time, duration_played) " +
            "VALUES(#{userId}, #{musicId}, NOW(), #{durationPlayed})")
    int insert(PlayHistory history);
}
