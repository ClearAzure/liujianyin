package com.music.service.impl;

import com.music.entity.Favorite;
import com.music.exception.BusinessException;
import com.music.mapper.FavoriteMapper;
import com.music.service.FavoriteService;
import com.music.service.MusicService;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final MusicService musicService;

    @Override
    public void add(Long userId, Long musicId) {
        if (favoriteMapper.exists(userId, musicId) > 0) {
            throw new BusinessException("已收藏过该歌曲");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setMusicId(musicId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void remove(Long userId, Long musicId) {
        favoriteMapper.delete(userId, musicId);
    }

    @Override
    public List<MusicVO> list(Long userId) {
        List<Long> musicIds = favoriteMapper.findMusicIdsByUserId(userId);
        List<MusicVO> result = new ArrayList<>();
        for (Long mid : musicIds) {
            MusicVO mv = musicService.getVO(mid);
            if (mv != null) result.add(mv);
        }
        return result;
    }
}
