package com.music.mapper;

import com.music.entity.Album;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AlbumMapper {

    @Select("SELECT * FROM album WHERE id = #{id}")
    Album findById(Long id);

    @Insert("INSERT INTO album(name, cover_url, artist_id, publish_time, create_time) " +
            "VALUES(#{name}, #{coverUrl}, #{artistId}, #{publishTime}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Album album);
}
