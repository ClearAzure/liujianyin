package com.music.mapper;

import com.music.entity.Music;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MusicMapper {

    @Select("SELECT * FROM music WHERE id = #{id} AND status = 1")
    Music findById(Long id);

    @Select("SELECT * FROM music WHERE name LIKE CONCAT('%', #{keyword}, '%') AND status = 1")
    List<Music> search(String keyword);

    @Select("SELECT * FROM music WHERE status = 1 ORDER BY RAND() LIMIT 1")
    Music random();

    @Insert("INSERT INTO music(name, artist_id, album_id, cover_url, music_url, lyric_url, " +
            "duration, play_count, status, create_time) " +
            "VALUES(#{name}, #{artistId}, #{albumId}, #{coverUrl}, #{musicUrl}, #{lyricUrl}, " +
            "#{duration}, 0, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Music music);

    @Update("UPDATE music SET play_count = play_count + 1 WHERE id = #{id}")
    int incrementPlayCount(Long id);
}
