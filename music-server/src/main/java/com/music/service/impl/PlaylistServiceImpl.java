package com.music.service.impl;

import com.music.entity.Playlist;
import com.music.entity.PlaylistMusic;
import com.music.exception.BusinessException;
import com.music.mapper.PlaylistMapper;
import com.music.service.MusicService;
import com.music.service.PlaylistService;
import com.music.vo.MusicVO;
import com.music.vo.PlaylistVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌单服务实现类
 */
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistMapper playlistMapper;
    private final MusicService musicService;

    @Override
    public Playlist create(Long userId, String name, String coverUrl, String description) {
        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setName(name);
        playlist.setCoverUrl(coverUrl);
        playlist.setDescription(description);
        playlistMapper.insert(playlist);
        return playlist;
    }

    @Override
    public List<PlaylistVO> getMyPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.findByUserId(userId);
        List<PlaylistVO> result = new ArrayList<>();
        for (Playlist pl : playlists) {
            result.add(toVO(pl));
        }
        return result;
    }

    @Override
    public PlaylistVO getDetail(Long playlistId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) {
            throw new BusinessException("歌单不存在");
        }
        return toVO(playlist);
    }

    @Override
    public void addMusic(Long playlistId, Long musicId) {
        if (playlistMapper.existsMusic(playlistId, musicId) > 0) {
            throw new BusinessException("歌曲已在歌单中");
        }
        PlaylistMusic pm = new PlaylistMusic();
        pm.setPlaylistId(playlistId);
        pm.setMusicId(musicId);
        playlistMapper.insertMusic(pm);
    }

    @Override
    public void removeMusic(Long playlistId, Long userId, Long musicId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new BusinessException("歌单不存在或无权操作");
        }
        playlistMapper.deleteMusic(playlistId, musicId);
    }

    @Override
    public void update(Long playlistId, Long userId, String name, String coverUrl, String description) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new BusinessException("歌单不存在或无权操作");
        }
        if (name != null && !name.isBlank()) {
            playlist.setName(name.trim());
        }
        if (coverUrl != null) {
            playlist.setCoverUrl(coverUrl);
        }
        if (description != null) {
            playlist.setDescription(description.trim());
        }
        playlistMapper.update(playlist);
    }

    @Override
    @Transactional
    public void delete(Long playlistId, Long userId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new BusinessException("歌单不存在或无权操作");
        }
        // 先清掉中间表里的关联记录，再删歌单本体，避免留下孤儿数据
        playlistMapper.deleteMusicByPlaylistId(playlistId);
        playlistMapper.delete(playlistId, userId);
    }

    /**
     * 歌单实体转 VO（含解析歌曲列表）
     *
     * @param playlist 歌单实体
     * @return 歌单信息 VO
     */
    private PlaylistVO toVO(Playlist playlist) {
        //根据中间表查询歌单下的歌曲 ID 列表，然后通过 MusicService 获取每首歌的 VO，最后组装成 PlaylistVO 返回
        List<Long> musicIds = playlistMapper.findMusicIdsByPlaylistId(playlist.getId());
        //歌曲列表
        List<MusicVO> songs = new ArrayList<>();

        for (Long mid : musicIds) {
            MusicVO mv = musicService.getVO(mid);
            if (mv != null) songs.add(mv);
        }

        return PlaylistVO.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .coverUrl(playlist.getCoverUrl())
                .description(playlist.getDescription())
                .songs(songs)
                .build();
    }
}
