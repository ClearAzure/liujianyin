package com.music.service.impl;

import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.FavoriteMapper;
import com.music.mapper.HistoryMapper;
import com.music.mapper.MusicMapper;
import com.music.mapper.PlaylistMapper;
import com.music.service.MusicService;
import com.music.vo.MusicVO;
import com.music.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 音乐服务实现类
 */
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicMapper musicMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final FavoriteMapper favoriteMapper;
    private final HistoryMapper historyMapper;

    @Override
    public List<MusicVO> search(String keyword) {
        List<Music> list = musicMapper.search(keyword);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Long incrementPlayCount(Long id) {
        musicMapper.incrementPlayCount(id);
        Music music = musicMapper.findById(id);
        return music != null ? music.getPlayCount() : 0L;
    }

    @Override
    public MusicVO getVO(Long id) {
        Music music = musicMapper.findById(id);
        if (music == null) {
            return null;
        }
        return toVO(music);
    }

    @Override
    public MusicVO random() {
        Music music = musicMapper.random();
        if (music == null) {
            return null;
        }
        return toVO(music);
    }

    @Override
    public PageResult<MusicVO> hot(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 20;

        int offset = (page - 1) * size;

        List<Music> list = musicMapper.findHotByPage(offset, size);

        List<MusicVO> vos = list.stream().map(this::toVO).collect(Collectors.toList());

        long total = musicMapper.countActive();

        return PageResult.of(vos, total, page, size);
    }

    @Override
    @Transactional
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

    @Override
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
