package com.music.service;

import com.music.vo.MusicVO;

import java.util.List;

/**
 * 播放历史服务接口
 */
public interface HistoryService {

    /**
     * 记录一次播放历史
     *
     * @param userId  用户 ID
     * @param musicId 歌曲 ID
     */
    void add(Long userId, Long musicId);

    /**
     * 查询用户最近播放的歌曲（按歌曲去重）
     *
     * @param userId 用户 ID
     * @return 最近播放的歌曲列表
     */
    List<MusicVO> list(Long userId);
}
