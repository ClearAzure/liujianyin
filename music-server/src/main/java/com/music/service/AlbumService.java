package com.music.service;

import com.music.entity.Album;
import com.music.vo.AlbumVO;

import java.util.List;

/**
 * 专辑服务接口
 */
public interface AlbumService {

    /**
     * 查询所有专辑（含歌曲数）
     *
     * @return 专辑列表
     */
    List<AlbumVO> list();

    /**
     * 获取专辑详情（含歌曲列表）
     *
     * @param id 专辑 ID
     * @return 专辑信息
     */
    AlbumVO getDetail(Long id);

    /**
     * 更新专辑信息
     *
     * @param id          专辑 ID
     * @param name        新名称
     * @param coverUrl    新封面 URL
     * @param description 新简介
     */
    void update(Long id, String name, String coverUrl, String description);

    /**
     * 删除专辑
     *
     * @param id 专辑 ID
     */
    void delete(Long id);
}
