package com.music.service;

import com.music.entity.Favorite;
import com.music.exception.BusinessException;
import com.music.mapper.FavoriteMapper;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final MusicService musicService;

    /**
     * 收藏歌曲（已收藏则报错）。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     * @throws BusinessException 已收藏过该歌曲时抛出
     */
    public void add(Long userId, Long musicId) {
        if (favoriteMapper.exists(userId, musicId) > 0) {
            throw new BusinessException("已收藏过该歌曲");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setMusicId(musicId);
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     */
    public void remove(Long userId, Long musicId) {
        favoriteMapper.delete(userId, musicId);
    }

    /**
     * 查询用户的收藏歌曲列表。
     *
     * @param userId 用户ID
     * @return 收藏的歌曲列表
     */
    public List<MusicVO> list(Long userId) {
        List<Long> musicIds = favoriteMapper.findMusicIdsByUserId(userId);
        //id列表转换为MusicVO列表
        List<MusicVO> result = new ArrayList<>();
        for (Long mid : musicIds) {
            MusicVO mv = musicService.getVO(mid);
            if (mv != null) result.add(mv);//非空才添加到结果列表
        }
        return result;
    }
}
