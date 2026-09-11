package com.music.mapper;

import com.music.entity.Playlist;
import com.music.entity.PlaylistMusic;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 歌单表与歌单歌曲关联表数据访问接口。
 */
@Mapper
public interface PlaylistMapper {

    /**
     * 根据歌单ID查询歌单。
     *
     * @param id 歌单ID
     * @return 歌单实体，不存在返回 null
     */
    @Select("SELECT * FROM playlist WHERE id = #{id}")
    Playlist findById(Long id);

    /**
     * 查询用户的所有歌单（按创建时间倒序）。
     *
     * @param userId 用户ID
     * @return 歌单列表
     */
    @Select("SELECT * FROM playlist WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Playlist> findByUserId(Long userId);

    /**
     * 新增歌单（自动回填自增ID）。
     *
     * @param playlist 歌单实体
     * @return 影响行数
     */
    @Insert("INSERT INTO playlist(user_id, name, cover_url, description, create_time) " +
            "VALUES(#{userId}, #{name}, #{coverUrl}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Playlist playlist);

    /**
     * 更新歌单名称/封面。
     *
     * @param playlist 歌单实体
     * @return 影响行数
     */
    @Update("UPDATE playlist SET name = #{name}, cover_url = #{coverUrl} WHERE id = #{id}")
    int update(Playlist playlist);

    /**
     * 删除歌单（需匹配用户ID，保证只能删除自己的歌单）。
     *
     * @param id 歌单ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM playlist WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 查询歌单内的歌曲ID列表（按 sort_order 再按自增 id 排序，保证“添加顺序”稳定，最后一条即“最近添加”）。
     *
     * @param playlistId 歌单ID
     * @return 歌曲ID列表
     */
    @Select("SELECT music_id FROM playlist_music WHERE playlist_id = #{playlistId} ORDER BY sort_order, id")
    List<Long> findMusicIdsByPlaylistId(Long playlistId);

    /**
     * 向歌单添加歌曲关联。
     *
     * @param playlistMusic 歌单歌曲关联实体
     * @return 影响行数
     */
    @Insert("INSERT INTO playlist_music(playlist_id, music_id, sort_order, create_time) " +
            "VALUES(#{playlistId}, #{musicId}, 0, NOW())")
    int insertMusic(PlaylistMusic playlistMusic);

    /**
     * 从歌单移除歌曲关联。
     *
     * @param playlistId 歌单ID
     * @param musicId 歌曲ID
     * @return 影响行数
     */
    @Delete("DELETE FROM playlist_music WHERE playlist_id = #{playlistId} AND music_id = #{musicId}")
    int deleteMusic(@Param("playlistId") Long playlistId, @Param("musicId") Long musicId);

    /**
     * 删除歌单下的所有歌曲关联（删除歌单前先清理中间表，避免产生孤儿数据）。
     *
     * @param playlistId 歌单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM playlist_music WHERE playlist_id = #{playlistId}")
    int deleteMusicByPlaylistId(Long playlistId);

    /**
     * 判断歌曲是否已在歌单中（返回记录条数）。
     *
     * @param playlistId 歌单ID
     * @param musicId 歌曲ID
     * @return 记录条数（0 表示不在歌单中）
     */
    @Select("SELECT COUNT(*) FROM playlist_music WHERE playlist_id = #{playlistId} AND music_id = #{musicId}")
    int existsMusic(@Param("playlistId") Long playlistId, @Param("musicId") Long musicId);
}
