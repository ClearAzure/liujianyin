package com.music.service;

import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.exception.BusinessException;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.vo.ArtistVO;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistMapper artistMapper;
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
     * 更新歌手头像。
     *
     * @param id 歌手ID
     * @param avatarUrl 新头像URL
     * @throws BusinessException 歌手不存在时抛出
     */
    public void updateAvatar(Long id, String avatarUrl) {
        Artist artist = artistMapper.findById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在");
        }
        artistMapper.updateAvatar(id, avatarUrl);
    }
}
