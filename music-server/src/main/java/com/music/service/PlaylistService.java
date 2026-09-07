package com.music.service;

import com.music.entity.Playlist;
import com.music.entity.PlaylistMusic;
import com.music.exception.BusinessException;
import com.music.mapper.PlaylistMapper;
import com.music.vo.MusicVO;
import com.music.vo.PlaylistVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistMapper playlistMapper;
    private final MusicService musicService;

    public Playlist create(Long userId, String name) {
        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setName(name);
        playlistMapper.insert(playlist);
        return playlist;
    }

    public List<PlaylistVO> getMyPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.findByUserId(userId);
        List<PlaylistVO> result = new ArrayList<>();
        for (Playlist pl : playlists) {
            result.add(toVO(pl));
        }
        return result;
    }

    public PlaylistVO getDetail(Long playlistId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) {
            throw new BusinessException("歌单不存在");
        }
        return toVO(playlist);
    }

    public void addMusic(Long playlistId, Long musicId) {
        if (playlistMapper.existsMusic(playlistId, musicId) > 0) {
            throw new BusinessException("歌曲已在歌单中");
        }
        PlaylistMusic pm = new PlaylistMusic();
        pm.setPlaylistId(playlistId);
        pm.setMusicId(musicId);
        playlistMapper.insertMusic(pm);
    }

    public void delete(Long playlistId, Long userId) {
        playlistMapper.delete(playlistId, userId);
    }

    private PlaylistVO toVO(Playlist playlist) {
        List<Long> musicIds = playlistMapper.findMusicIdsByPlaylistId(playlist.getId());
        List<MusicVO> songs = new ArrayList<>();
        for (Long mid : musicIds) {
            MusicVO mv = musicService.getDetail(mid);
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
