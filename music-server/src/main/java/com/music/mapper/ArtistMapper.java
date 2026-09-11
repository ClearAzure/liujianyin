package com.music.mapper;

import com.music.entity.Artist;
import org.apache.ibatis.annotations.*;

/**
 * 歌手表数据访问接口。
 */
@Mapper
public interface ArtistMapper {

    /**
     * 根据歌手ID查询歌手。
     *
     * @param id 歌手ID
     * @return 歌手实体，不存在返回 null
     */
    @Select("SELECT * FROM artist WHERE id = #{id}")
    Artist findById(Long id);

    /**
     * 根据歌手名查询歌手。
     *
     * @param name 歌手名
     * @return 歌手实体，不存在返回 null
     */
    @Select("SELECT * FROM artist WHERE name = #{name}")
    Artist findByName(String name);

    /**
     * 新增歌手（自动回填自增ID）。
     *
     * @param artist 歌手实体
     * @return 影响行数
     */
    @Insert("INSERT INTO artist(name, avatar_url, description, create_time) " +
            "VALUES(#{name}, #{avatarUrl}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Artist artist);
}
