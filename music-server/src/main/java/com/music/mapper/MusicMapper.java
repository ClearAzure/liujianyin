package com.music.mapper;

import com.music.entity.Music;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 歌曲表数据访问接口。
 */
@Mapper
public interface MusicMapper {

    /**
     * 根据歌曲ID查询在架歌曲。
     *
     * @param id 歌曲ID
     * @return 歌曲实体，不存在或已下架返回 null
     */
    @Select("SELECT * FROM music WHERE id = #{id} AND status = 1")
    Music findById(Long id);

    /**
     * 按歌名关键字模糊搜索在架歌曲。
     *
     * @param keyword 搜索关键字
     * @return 匹配的歌曲列表
     */
    @Select("SELECT * FROM music WHERE name LIKE CONCAT('%', #{keyword}, '%') AND status = 1")
    List<Music> search(String keyword);
    //CONCAT() 是 MySQL 的字符串拼接函数。
    /**
     * 随机返回一首在架歌曲。
     *
     * @return 随机歌曲实体，无在架歌曲返回 null
     */
    @Select("SELECT * FROM music WHERE status = 1 ORDER BY RAND() LIMIT 1")
    Music random();

    /**
     * 热门推荐：按播放量倒序，播放量相同的按 id 升序保证顺序稳定（分页）。
     *
     * @param offset 偏移量
     * @param size 每页条数
     * @return 分页歌曲列表
     */
    @Select("SELECT * FROM music WHERE status = 1 ORDER BY play_count DESC, id ASC LIMIT #{offset}, #{size}")//如果 play_count 一样，再按照 id 从小到大。
    List<Music> findHotByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     * 在架歌曲总数（用于分页计算）。
     *
     * @return 在架歌曲条数
     */
    @Select("SELECT COUNT(*) FROM music WHERE status = 1")
    long countActive();

    /**
     * 新增歌曲（默认播放量 0、状态 1，自动回填自增ID）。
     *
     * @param music 歌曲实体
     * @return 影响行数
     */
    @Insert("INSERT INTO music(name, artist_id, album_id, cover_url, music_url, lyric_url, " +
            "duration, play_count, status, create_time) " +
            "VALUES(#{name}, #{artistId}, #{albumId}, #{coverUrl}, #{musicUrl}, #{lyricUrl}, " +
            "#{duration}, 0, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Music music);

    /**
     * 播放次数 +1。
     *
     * @param id 歌曲ID
     * @return 影响行数
     */
    @Update("UPDATE music SET play_count = play_count + 1 WHERE id = #{id}")
    int incrementPlayCount(Long id);
}
