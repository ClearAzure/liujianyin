package com.music.service;

import com.music.entity.PlayHistory;
import com.music.mapper.HistoryMapper;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryMapper historyMapper;
    private final MusicService musicService;

    /**
     * 记录一次播放历史。
     *
     * @param userId 用户ID
     * @param musicId 歌曲ID
     */
    public void add(Long userId, Long musicId) {
        PlayHistory history = new PlayHistory();
        history.setUserId(userId);
        history.setMusicId(musicId);
        history.setDurationPlayed(0);//TODO 实际播放时间,前端应该在按下下一首的时候传递当前播放时间(或者直接新建一个表处理?)
        historyMapper.insert(history);
    }

    /**
     * 查询用户最近播放的歌曲（按歌曲去重，最近 50 首）。
     *
     * @param userId 用户ID
     * @return 最近播放的歌曲列表
     */
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
