package com.music.mapper;

import com.music.entity.Artist;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    /**
     * 查询所有歌手（按名称排序）。
     *
     * @return 歌手列表
     */
    @Select("SELECT * FROM artist ORDER BY name")
    List<Artist> findAll();

    /**
     * 统计某歌手的在架歌曲数。
     *
     * @param artistId 歌手ID
     * @return 歌曲数
     */
    @Select("SELECT COUNT(*) FROM music WHERE artist_id = #{artistId} AND status = 1")
    Long countMusicByArtistId(Long artistId);

    /**
     * 更新歌手头像。
     *
     * @param id 歌手ID
     * @param avatarUrl 头像URL
     * @return 影响行数
     */
    @Update("UPDATE artist SET avatar_url = #{avatarUrl} WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);

    /**
     * 更新歌手（名称/头像/简介）。
     *
     * @param artist 歌手实体（含要更新的字段）
     * @return 影响行数
     */
    @Update("UPDATE artist SET name = #{name}, avatar_url = #{avatarUrl}, description = #{description} WHERE id = #{id}")
    int update(Artist artist);

    /**
     * 删除歌手。
     *
     * @param id 歌手ID
     * @return 影响行数
     */
    @Delete("DELETE FROM artist WHERE id = #{id}")
    int delete(Long id);
}
