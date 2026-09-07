package com.music.mapper;

import com.music.entity.Playlist;
import com.music.entity.PlaylistMusic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlaylistMapper {

    @Select("SELECT * FROM playlist WHERE id = #{id}")
    Playlist findById(Long id);

    @Select("SELECT * FROM playlist WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Playlist> findByUserId(Long userId);

    @Insert("INSERT INTO playlist(user_id, name, cover_url, description, create_time) " +
            "VALUES(#{userId}, #{name}, #{coverUrl}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Playlist playlist);

    @Update("UPDATE playlist SET name = #{name}, cover_url = #{coverUrl} WHERE id = #{id}")
    int update(Playlist playlist);

    @Delete("DELETE FROM playlist WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    // 按 sort_order 再按自增 id 排序，保证“添加顺序”稳定，最后一条即“最近添加”
    @Select("SELECT music_id FROM playlist_music WHERE playlist_id = #{playlistId} ORDER BY sort_order, id")
    List<Long> findMusicIdsByPlaylistId(Long playlistId);

    @Insert("INSERT INTO playlist_music(playlist_id, music_id, sort_order, create_time) " +
            "VALUES(#{playlistId}, #{musicId}, 0, NOW())")
    int insertMusic(PlaylistMusic playlistMusic);

    @Delete("DELETE FROM playlist_music WHERE playlist_id = #{playlistId} AND music_id = #{musicId}")
    int deleteMusic(@Param("playlistId") Long playlistId, @Param("musicId") Long musicId);

    @Select("SELECT COUNT(*) FROM playlist_music WHERE playlist_id = #{playlistId} AND music_id = #{musicId}")
    int existsMusic(@Param("playlistId") Long playlistId, @Param("musicId") Long musicId);
}
