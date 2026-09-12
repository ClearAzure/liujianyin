package com.music.mapper;

import com.music.entity.Album;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
     * 按 专辑名 + 歌手ID 查询专辑（用于上传时去重）。
     *
     * @param name 专辑名
     * @param artistId 歌手ID
     * @return 专辑实体，不存在返回 null
     */
    @Select("SELECT * FROM album WHERE name = #{name} AND artist_id = #{artistId}")
    Album findByNameAndArtistId(@Param("name") String name, @Param("artistId") Long artistId);

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

    /**
     * 查询所有专辑（按创建时间倒序）。
     *
     * @return 专辑列表
     */
    @Select("SELECT * FROM album ORDER BY create_time DESC")
    List<Album> findAll();

    /**
     * 查询某歌手的专辑。
     *
     * @param artistId 歌手ID
     * @return 专辑列表
     */
    @Select("SELECT * FROM album WHERE artist_id = #{artistId} ORDER BY create_time DESC")
    List<Album> findByArtistId(Long artistId);

    /**
     * 统计某专辑的在架歌曲数。
     *
     * @param albumId 专辑ID
     * @return 歌曲数
     */
    @Select("SELECT COUNT(*) FROM music WHERE album_id = #{albumId} AND status = 1")
    Long countMusicByAlbumId(Long albumId);

    /**
     * 更新专辑封面。
     *
     * @param id 专辑ID
     * @param coverUrl 封面URL
     * @return 影响行数
     */
    @Update("UPDATE album SET cover_url = #{coverUrl} WHERE id = #{id}")
    int updateCover(@Param("id") Long id, @Param("coverUrl") String coverUrl);
}
