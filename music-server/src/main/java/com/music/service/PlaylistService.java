package com.music.service;

import com.music.entity.Playlist;
import com.music.vo.PlaylistVO;

import java.util.List;

/**
 * 歌单服务接口
 */
public interface PlaylistService {

    /**
     * 为指定用户创建歌单
     *
     * @param userId    用户 ID
     * @param name      歌单名称
     * @param coverUrl  歌单封面 URL
     * @param description 歌单简介
     * @return 创建后的歌单实体
     */
    Playlist create(Long userId, String name, String coverUrl, String description);

    /**
     * 查询用户的所有歌单（含歌曲列表）
     *
     * @param userId 用户 ID
     * @return 歌单列表
     */
    List<PlaylistVO> getMyPlaylists(Long userId);

    /**
     * 获取歌单详情（含歌曲列表）
     *
     * @param playlistId 歌单 ID
     * @return 歌单信息
     */
    PlaylistVO getDetail(Long playlistId);

    /**
     * 向歌单添加歌曲
     *
     * @param playlistId 歌单 ID
     * @param musicId    歌曲 ID
     */
    void addMusic(Long playlistId, Long musicId);

    /**
     * 从歌单移除歌曲
     *
     * @param playlistId 歌单 ID
     * @param userId     操作者用户 ID
     * @param musicId    歌曲 ID
     */
    void removeMusic(Long playlistId, Long userId, Long musicId);

    /**
     * 修改歌单信息
     *
     * @param playlistId  歌单 ID
     * @param userId      操作者用户 ID
     * @param name        新名称
     * @param coverUrl    新封面 URL
     * @param description 新简介
     */
    void update(Long playlistId, Long userId, String name, String coverUrl, String description);

    /**
     * 删除歌单及其下的所有歌曲关联
     *
     * @param playlistId 歌单 ID
     * @param userId     操作者用户 ID
     */
    void delete(Long playlistId, Long userId);
}
