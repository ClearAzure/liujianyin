package com.music.service;

import com.music.vo.MusicVO;

import java.util.List;

/**
 * 音乐服务接口
 */
public interface MusicService {

    /**
     * 搜索音乐（按歌名关键字）
     *
     * @param keyword 搜索关键字
     * @return 匹配的歌曲列表
     */
    List<MusicVO> search(String keyword);

    /**
     * 获取音乐 VO（不增加播放次数）
     *
     * @param id 歌曲 ID
     * @return 歌曲信息
     */
    MusicVO getVO(Long id);

    /**
     * 增加播放次数
     *
     * @param id 歌曲 ID
     * @return 最新播放量
     */
    Long incrementPlayCount(Long id);

    /**
     * 随机返回一首歌曲
     *
     * @return 随机歌曲信息
     */
    MusicVO random();

    /**
     * 热门推荐（按播放量分页）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    com.music.vo.PageResult<MusicVO> hot(int page, int size);

    /**
     * 删除歌曲及其所有关联
     *
     * @param id 歌曲 ID
     */
    void delete(Long id);

    /**
     * 将歌曲实体转换为 VO
     *
     * @param music 歌曲实体
     * @return 歌曲 VO
     */
    MusicVO toVO(com.music.entity.Music music);
}
