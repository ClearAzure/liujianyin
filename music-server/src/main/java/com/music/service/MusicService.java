package com.music.service;

import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicMapper musicMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    public List<MusicVO> search(String keyword) {
        List<Music> list = musicMapper.search(keyword);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public MusicVO getDetail(Long id) {
        Music music = musicMapper.findById(id);
        if (music == null) {
            return null;
        }
        musicMapper.incrementPlayCount(id);
        return toVO(music);
    }

    public MusicVO random() {
        Music music = musicMapper.random();
        if (music == null) {
            return null;
        }
        return toVO(music);
    }

    public MusicVO toVO(Music music) {
        String artistName = "";
        if (music.getArtistId() != null) {// 如果艺术家ID不为空，则查询艺术家名称(根据外键把artistId查到artist表的name)
            Artist artist = artistMapper.findById(music.getArtistId());
            if (artist != null) artistName = artist.getName();
        }
        String albumName = "";
        if (music.getAlbumId() != null) {// 如果专辑ID不为空，则查询专辑名称(根据外键把albumId查到album表的name)
            var album = albumMapper.findById(music.getAlbumId());
            if (album != null) albumName = album.getName();
        }
        return MusicVO.builder()// builder模式创建MusicVO对象
                .id(music.getId())
                .name(music.getName())
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
