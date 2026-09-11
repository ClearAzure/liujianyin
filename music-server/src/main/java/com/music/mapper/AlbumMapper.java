package com.music.mapper;

import com.music.entity.Album;
import org.apache.ibatis.annotations.*;

/**
 * 专辑表数据访问接口。
 */
@Mapper
public interface AlbumMapper {

    /**
     * 根据专辑ID查询专辑。
     *
     * @param id 专辑ID
     * @return 专辑实体，不存在返回 null
     */
    @Select("SELECT * FROM album WHERE id = #{id}")
    Album findById(Long id);

    /**
     * 新增专辑（自动回填自增ID）。
     *
     * @param album 专辑实体
     * @return 影响行数
     */
    @Insert("INSERT INTO album(name, cover_url, artist_id, publish_time, create_time) " +
            "VALUES(#{name}, #{coverUrl}, #{artistId}, #{publishTime}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Album album);
}
