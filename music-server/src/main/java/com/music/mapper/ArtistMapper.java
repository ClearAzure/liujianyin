package com.music.mapper;

import com.music.entity.Artist;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ArtistMapper {

    @Select("SELECT * FROM artist WHERE id = #{id}")
    Artist findById(Long id);

    @Select("SELECT * FROM artist WHERE name = #{name}")
    Artist findByName(String name);

    @Insert("INSERT INTO artist(name, avatar_url, description, create_time) " +
            "VALUES(#{name}, #{avatarUrl}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Artist artist);
}
