package com.music.service;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.vo.ArtistVO;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final MusicMapper musicMapper;
    private final MusicService musicService;

    /**
     * 查询所有歌手（含歌曲数，不含歌曲列表）。
     *
     * @return 歌手列表
     */
    public List<ArtistVO> list() {
        List<Artist> artists = artistMapper.findAll();
        return artists.stream().map(a -> ArtistVO.builder()
                .id(a.getId())
                .name(a.getName())
                .avatarUrl(a.getAvatarUrl())
                .description(a.getDescription())
                .musicCount(artistMapper.countMusicByArtistId(a.getId()))
                .build()).collect(Collectors.toList());
    }

    /**
     * 获取歌手详情（含歌曲列表）。
     *
     * @param id 歌手ID
     * @return 歌手信息
     * @throws BusinessException 歌手不存在时抛出
     */
    public ArtistVO getDetail(Long id) {
        Artist artist = artistMapper.findById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在");
        }
        List<Music> musics = musicMapper.findByArtistId(id);
        List<MusicVO> songs = musics.stream()
                .map(musicService::toVO)
                .collect(Collectors.toList());
        return ArtistVO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .avatarUrl(artist.getAvatarUrl())
                .description(artist.getDescription())
                .musicCount((long) songs.size())
                .songs(songs)//歌手详情返回歌曲列表
                .build();
    }

    /**
     * 更新歌手名称/头像/简介。
     *
     * @param id 歌手ID
     * @param name 新名称（为空则不修改）
     * @param avatarUrl 新头像URL（为空则不修改）
     * @param description 新简介（为空则不修改；传空串表示清空）
     * @throws BusinessException 歌手不存在时抛出
     */
    public void update(Long id, String name, String avatarUrl, String description) {
        Artist artist = artistMapper.findById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在");
        }
        if (name != null && !name.isBlank()) {
            artist.setName(name.trim());
        }
        if (avatarUrl != null) {
            artist.setAvatarUrl(avatarUrl);
        }
        if (description != null) {
            artist.setDescription(description.trim());
        }
        artistMapper.update(artist);
    }

    /**
     * 删除歌手：级联删除名下所有歌曲、专辑，再删歌手本体。
     *
     * @param id 歌手ID
     * @throws BusinessException 歌手不存在时抛出
     */
    @Transactional//事务管理
    public void delete(Long id) {
        Artist artist = artistMapper.findById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在");
        }
        // 1. 删除名下所有歌曲（复用 MusicService.delete，会清理收藏/歌单/历史关联）
        List<Music> songs = musicMapper.findByArtistId(id);
        for (Music song : songs) {
            musicService.delete(song.getId());
        }
        // 2. 删除名下所有专辑
        List<Album> albums = albumMapper.findByArtistId(id);
        for (Album album : albums) {
            albumMapper.delete(album.getId());
        }
        // 3. 删除歌手本体
        artistMapper.delete(id);
    }
}
