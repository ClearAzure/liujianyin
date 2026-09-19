package com.music.service.impl;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.service.ArtistService;
import com.music.service.MusicService;
import com.music.vo.ArtistVO;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 歌手服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final MusicMapper musicMapper;
    private final MusicService musicService;

    @Override
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

    @Override
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
                .songs(songs)
                .build();
    }

    @Override
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

    @Override
    @Transactional
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
