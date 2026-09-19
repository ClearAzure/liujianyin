package com.music.service.impl;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.service.AlbumService;
import com.music.service.MusicService;
import com.music.vo.AlbumVO;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 专辑服务实现类
 */
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;
    private final MusicMapper musicMapper;
    private final MusicService musicService;

    @Override
    public List<AlbumVO> list() {
        List<Album> albums = albumMapper.findAll();
        return albums.stream().map(a -> AlbumVO.builder()
                .id(a.getId())
                .name(a.getName())
                .coverUrl(resolveCover(a))
                .description(a.getDescription())
                .artistId(a.getArtistId())
                .artistName(resolveArtistName(a.getArtistId()))
                .publishTime(a.getPublishTime())
                .musicCount(albumMapper.countMusicByAlbumId(a.getId()))
                .build()).collect(Collectors.toList());
    }

    @Override
    public AlbumVO getDetail(Long id) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在");
        }
        List<Music> musics = musicMapper.findByAlbumId(id);
        List<MusicVO> songs = musics.stream()
                .map(musicService::toVO)
                .collect(Collectors.toList());
        return AlbumVO.builder()
                .id(album.getId())
                .name(album.getName())
                .coverUrl(resolveCover(album))
                .description(album.getDescription())
                .artistId(album.getArtistId())
                .artistName(resolveArtistName(album.getArtistId()))
                .publishTime(album.getPublishTime())
                .musicCount((long) songs.size())
                .songs(songs)
                .build();
    }

    @Override
    public void update(Long id, String name, String coverUrl, String description) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在");
        }
        if (name != null && !name.isBlank()) {
            album.setName(name.trim());
        }
        if (coverUrl != null) {
            album.setCoverUrl(coverUrl);
        }
        if (description != null) {
            album.setDescription(description.trim());
        }
        albumMapper.update(album);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在");
        }
        musicMapper.clearAlbumIdByAlbumId(id);
        albumMapper.delete(id);
    }

    private String resolveArtistName(Long artistId) {
        if (artistId == null) {
            return "";
        }
        Artist artist = artistMapper.findById(artistId);
        return artist != null ? artist.getName() : "";
    }

    private String resolveCover(Album album) {
        if (album.getCoverUrl() != null && !album.getCoverUrl().isBlank()) {
            return album.getCoverUrl();
        }
        return musicMapper.findFirstCoverByAlbumId(album.getId());
    }
}
