package com.music.service;

import com.music.vo.MusicVO;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 收藏歌曲
     *
     * @param userId  用户 ID
     * @param musicId 歌曲 ID
     */
    void add(Long userId, Long musicId);

    /**
     * 取消收藏
     *
     * @param userId  用户 ID
     * @param musicId 歌曲 ID
     */
    void remove(Long userId, Long musicId);

    /**
     * 查询用户的收藏歌曲列表
     *
     * @param userId 用户 ID
     * @return 收藏的歌曲列表
     */
    List<MusicVO> list(Long userId);
}
