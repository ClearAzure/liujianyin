package com.music.service;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.vo.AlbumVO;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;
    private final MusicMapper musicMapper;
    private final MusicService musicService;

    /**
     * 查询所有专辑（含歌曲数，不含歌曲列表）。
     *
     * @return 专辑列表
     */
    public List<AlbumVO> list() {
        List<Album> albums = albumMapper.findAll();
        return albums.stream().map(a -> AlbumVO.builder()
                .id(a.getId())
                .name(a.getName())
                .coverUrl(resolveCover(a))
                .artistId(a.getArtistId())
                .artistName(resolveArtistName(a.getArtistId()))//查询歌手名称,没有就为空字符串
                .publishTime(a.getPublishTime())
                .musicCount(albumMapper.countMusicByAlbumId(a.getId()))//查询专辑歌曲数(详细歌曲列表信息在专辑详情接口中)
                .build()).collect(Collectors.toList());
    }

    /**
     * 获取专辑详情（含歌曲列表）。
     *
     * @param id 专辑ID
     * @return 专辑信息
     * @throws BusinessException 专辑不存在时抛出
     */
    public AlbumVO getDetail(Long id) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在");
        }
        List<Music> musics = musicMapper.findByAlbumId(id);
        List<MusicVO> songs = musics.stream()
                .map(musicService::toVO)
                .collect(Collectors.toList());//查询歌曲并且转成vo对象,方便封装一起返回
        return AlbumVO.builder()
                .id(album.getId())
                .name(album.getName())
                .coverUrl(resolveCover(album))
                .artistId(album.getArtistId())
                .artistName(resolveArtistName(album.getArtistId()))
                .publishTime(album.getPublishTime())
                .musicCount((long) songs.size())
                .songs(songs)//这是专辑详情接口中返回的歌曲列表(专属)
                .build();
    }

    /**
     * 更新专辑封面。
     *
     * @param id 专辑ID
     * @param coverUrl 新封面URL
     * @throws BusinessException 专辑不存在时抛出
     */
    public void updateCover(Long id, String coverUrl) {
        Album album = albumMapper.findById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在");
        }
        albumMapper.updateCover(id, coverUrl);
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
