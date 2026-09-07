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

    public void add(Long userId, Long musicId) {
        if (favoriteMapper.exists(userId, musicId) > 0) {
            throw new BusinessException("已收藏过该歌曲");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setMusicId(musicId);
        favoriteMapper.insert(favorite);
    }

    public void remove(Long userId, Long musicId) {
        favoriteMapper.delete(userId, musicId);
    }

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
