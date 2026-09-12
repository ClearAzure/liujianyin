package com.music.service;

import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.FavoriteMapper;
import com.music.mapper.HistoryMapper;
import com.music.mapper.MusicMapper;
import com.music.mapper.PlaylistMapper;
import com.music.vo.MusicVO;
import com.music.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicMapper musicMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final FavoriteMapper favoriteMapper;
    private final HistoryMapper historyMapper;

    /**
     * 按关键字搜索在架歌曲，返回歌曲信息列表（已解析歌手/专辑名称）。
     *
     * @param keyword 搜索关键字，歌名模糊匹配
     * @return 匹配的歌曲列表，无结果时返回空列表
     */
    public List<MusicVO> search(String keyword) {
        List<Music> list = musicMapper.search(keyword);
        return list.stream().map(this::toVO).collect(Collectors.toList());//将 Music 实体转换为 MusicVO，并收集为列表返回这里用了stream流
    }

    /**
     * 记录一次播放：播放次数 +1 并返回最新值
     *
     * @param id 歌曲ID
     * @return 累加后的最新播放量，歌曲不存在时返回 0
     */
    public Long incrementPlayCount(Long id) {
        musicMapper.incrementPlayCount(id);
        Music music = musicMapper.findById(id);
        return music != null ? music.getPlayCount() : 0L;
    }

    /**
     * 只构建 VO，不增加播放次数。供歌单/收藏/历史等“列表展示”场景使用，
     * 避免仅仅是浏览列表就让每首歌的播放次数 +1。
     *
     * @param id 歌曲ID
     * @return 歌曲信息，歌曲不存在时返回 null
     */
    public MusicVO getVO(Long id) {
        Music music = musicMapper.findById(id);
        if (music == null) {
            return null;
        }
        return toVO(music);
    }

    /**
     * 随机返回一首在架歌曲。
     *
     * @return 随机歌曲信息，无在架歌曲时返回 null
     */
    public MusicVO random() {
        Music music = musicMapper.random();
        if (music == null) {
            return null;
        }
        return toVO(music);
    }

    /**
     * 热门推荐：按播放量倒序分页返回在架歌曲。
     *
     * @param page 页码（小于 1 时按 1 处理）
     * @param size 每页条数（小于 1 时按 20 处理）
     * @return 分页结果，含歌曲列表、总条数、当前页、每页大小
     */
    public PageResult<MusicVO> hot(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 20;

        int offset = (page - 1) * size;//计算偏移量(已跳过多少首),用于 SQL 分页查询

        List<Music> list = musicMapper.findHotByPage(offset, size);//offset 和 size 是给 SQL 分页用的

        List<MusicVO> vos = list.stream().map(this::toVO).collect(Collectors.toList());

        long total = musicMapper.countActive();

        return PageResult.of(vos, total, page, size);
    }

    /**
     * 删除歌曲及其所有关联（歌单/收藏/历史），需管理员权限。
     *
     * @param id 歌曲ID
     * @throws BusinessException 歌曲不存在时抛出
     */
    @Transactional//事务管理
    public void delete(Long id) {
        Music music = musicMapper.findById(id);
        if (music == null) {
            throw new BusinessException("歌曲不存在");
        }
        // 先清引用，再删本体，避免孤儿数据
        playlistMapper.deleteMusicByMusicId(id);
        favoriteMapper.deleteByMusicId(id);
        historyMapper.deleteByMusicId(id);
        musicMapper.deleteById(id);
    }

    /**
     * 将歌曲实体转换为 VO，解析歌手/专辑名称。
     *
     * @param music 歌曲实体
     * @return 歌曲信息 VO
     */
    public MusicVO toVO(Music music) {
        // 解析歌手名称
        String artistName = "";
        if (music.getArtistId() != null) {
            Artist artist = artistMapper.findById(music.getArtistId());
            if (artist != null) artistName = artist.getName();
        }
        // 解析专辑名称
        String albumName = "";
        if (music.getAlbumId() != null) {
            var album = albumMapper.findById(music.getAlbumId());
            if (album != null) albumName = album.getName();
        }

        return MusicVO.builder()
                .id(music.getId())
                .name(music.getName())
                .artistId(music.getArtistId())
                .albumId(music.getAlbumId())
                .artistName(artistName)
                .albumName(albumName)
                .coverUrl(music.getCoverUrl())
                .musicUrl(music.getMusicUrl())
                .lyricUrl(music.getLyricUrl())
                .duration(music.getDuration())
                .playCount(music.getPlayCount())
                .build();
    }
}
