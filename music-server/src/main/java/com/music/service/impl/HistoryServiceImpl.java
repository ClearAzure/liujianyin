package com.music.service.impl;

import com.music.entity.PlayHistory;
import com.music.mapper.HistoryMapper;
import com.music.service.HistoryService;
import com.music.service.MusicService;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史服务实现类
 */
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryMapper historyMapper;
    private final MusicService musicService;

    @Override
    public void add(Long userId, Long musicId) {
        PlayHistory history = new PlayHistory();
        history.setUserId(userId);
        history.setMusicId(musicId);
        history.setDurationPlayed(0);
        historyMapper.insert(history);
    }

    @Override
    public List<MusicVO> list(Long userId) {
        List<Long> musicIds = historyMapper.findRecentMusicIds(userId, 50);
        List<MusicVO> result = new ArrayList<>();
        for (Long mid : musicIds) {
            MusicVO mv = musicService.getVO(mid);
            if (mv != null) result.add(mv);
        }
        return result;
    }
}
