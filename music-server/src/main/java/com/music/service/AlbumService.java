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
import org.springframework.transaction.annotation.Transactional;

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
                .description(a.getDescription())
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
                .description(album.getDescription())
                .artistId(album.getArtistId())
                .artistName(resolveArtistName(album.getArtistId()))
                .publishTime(album.getPublishTime())
                .musicCount((long) songs.size())
                .songs(songs)//这是专辑详情接口中返回的歌曲列表(专属)
                .build();
    }

    /**
     * 更新专辑名称/封面/简介。
     *
     * @param id 专辑ID
     * @param name 新名称（为空则不修改）
     * @param coverUrl 新封面URL（为空则不修改）
     * @param description 新简介（为空则不修改；传空串表示清空）
     * @throws BusinessException 专辑不存在时抛出
     */
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

    /**
     * 删除专辑：先解除其下歌曲的专辑归属（歌曲保留），再删专辑本体。
     *
     * @param id 专辑ID
     * @throws BusinessException 专辑不存在时抛出
     */
    @Transactional//事务管理
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
