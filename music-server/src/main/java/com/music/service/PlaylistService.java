package com.music.service;

import com.music.entity.Playlist;
import com.music.entity.PlaylistMusic;
import com.music.exception.BusinessException;
import com.music.mapper.PlaylistMapper;
import com.music.vo.MusicVO;
import com.music.vo.PlaylistVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistMapper playlistMapper;
    private final MusicService musicService;

    /**
     * 为指定用户创建歌单。
     *
     * @param userId 用户ID
     * @param name 歌单名称
     * @param coverUrl 歌单封面URL（可空）
     * @param description 歌单简介（可空）
     * @return 创建后的歌单实体（含自增ID）
     */
    public Playlist create(Long userId, String name, String coverUrl, String description) {
        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setName(name);
        playlist.setCoverUrl(coverUrl);
        playlist.setDescription(description);
        playlistMapper.insert(playlist);
        return playlist;
    }

    /**
     * 查询用户的所有歌单（含歌曲列表）。
     *
     * @param userId 用户ID
     * @return 歌单列表
     */
    public List<PlaylistVO> getMyPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.findByUserId(userId);
        List<PlaylistVO> result = new ArrayList<>();
        for (Playlist pl : playlists) {
            result.add(toVO(pl));
        }
        return result;
    }

    /**
     * 获取歌单详情（含歌曲列表）。
     *
     * @param playlistId 歌单ID
     * @return 歌单信息
     * @throws BusinessException 歌单不存在时抛出
     */
    public PlaylistVO getDetail(Long playlistId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) {
            throw new BusinessException("歌单不存在");
        }
        return toVO(playlist);
    }

    /**
     * 向歌单添加歌曲（若已存在则报错）。
     *
     * @param playlistId 歌单ID
     * @param musicId 歌曲ID
     * @throws BusinessException 歌曲已在歌单中时抛出
     */
    public void addMusic(Long playlistId, Long musicId) {
        if (playlistMapper.existsMusic(playlistId, musicId) > 0) {
            throw new BusinessException("歌曲已在歌单中");
        }
        PlaylistMusic pm = new PlaylistMusic();
        pm.setPlaylistId(playlistId);
        pm.setMusicId(musicId);
        playlistMapper.insertMusic(pm);
    }

    /**
     * 从歌单移除歌曲（需校验歌单所有权）。
     *
     * @param playlistId 歌单ID
     * @param userId 操作者用户ID
     * @param musicId 歌曲ID
     * @throws BusinessException 歌单不存在或无权操作时抛出
     */
    public void removeMusic(Long playlistId, Long userId, Long musicId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new BusinessException("歌单不存在或无权操作");
        }
        playlistMapper.deleteMusic(playlistId, musicId);
    }

    /**
     * 修改歌单名称/封面/简介（需校验歌单所有权）。
     *
     * @param playlistId 歌单ID
     * @param userId 操作者用户ID
     * @param name 新名称（为空则不修改）
     * @param coverUrl 新封面URL（为空则不修改）
     * @param description 新简介（为空则不修改；传空串表示清空）
     * @throws BusinessException 歌单不存在或无权操作时抛出
     */
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

    /**
     * 删除歌单及其下的所有歌曲关联（需是歌单所有者）。
     *
     * @param playlistId 歌单ID
     * @param userId 操作者用户ID
     * @throws BusinessException 歌单不存在或无权操作时抛出
     */
    @Transactional//事务管理
    public void delete(Long playlistId, Long userId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null || !playlist.getUserId().equals(userId)) {
            throw new BusinessException("歌单不存在或无权操作");//校验身份
        }
        // 先清掉中间表里的关联记录，再删歌单本体，避免留下孤儿数据
        playlistMapper.deleteMusicByPlaylistId(playlistId);
        playlistMapper.delete(playlistId, userId);
    }

    /**
     * 歌单实体转 VO（含解析歌曲列表）。
     *
     * @param playlist 歌单实体
     * @return 歌单信息 VO
     */
    private PlaylistVO toVO(Playlist playlist) {
        //根据中间表查询歌单下的歌曲ID列表，然后通过MusicService获取每首歌的VO，最后组装成PlaylistVO返回
        List<Long> musicIds = playlistMapper.findMusicIdsByPlaylistId(playlist.getId());
        //歌曲列表
        List<MusicVO> songs = new ArrayList<>();

        for (Long mid : musicIds) {
            MusicVO mv = musicService.getVO(mid);
            if (mv != null) songs.add(mv);
        }

        return PlaylistVO.builder()
                .id(playlist.getId())//歌单ID
                .name(playlist.getName())//歌单名称
                .coverUrl(playlist.getCoverUrl())//歌单封面URL
                .description(playlist.getDescription())//歌单描述
                .songs(songs)//歌单下的歌曲列表(刚刚查的)
                .build();
    }
}
