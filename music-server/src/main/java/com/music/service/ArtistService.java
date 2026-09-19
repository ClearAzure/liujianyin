package com.music.service;

import com.music.entity.Artist;
import com.music.vo.ArtistVO;

import java.util.List;

/**
 * 歌手服务接口
 */
public interface ArtistService {

    /**
     * 查询所有歌手（含歌曲数）
     *
     * @return 歌手列表
     */
    List<ArtistVO> list();

    /**
     * 获取歌手详情（含歌曲列表）
     *
     * @param id 歌手 ID
     * @return 歌手信息
     */
    ArtistVO getDetail(Long id);

    /**
     * 更新歌手信息
     *
     * @param id          歌手 ID
     * @param name        新名称
     * @param avatarUrl   新头像 URL
     * @param description 新简介
     */
    void update(Long id, String name, String avatarUrl, String description);

    /**
     * 删除歌手及其名下所有歌曲/专辑
     *
     * @param id 歌手 ID
     */
    void delete(Long id);
}
