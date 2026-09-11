package com.music.service;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongUploadService {

    private final FileService fileService;
    private final MusicMapper musicMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    /**
     * 一键上传歌曲：查找/创建歌手、创建专辑、上传音乐/封面/歌词文件、写入 music 表。
     *
     * @param name 歌曲名
     * @param artistName 歌手名（不存在则自动创建）
     * @param albumName 专辑名（可空）
     *
     * @param duration 时长（秒，可空）//前端自动计算
     *
     * @param musicFile 音乐文件（可空）
     * @param coverFile 封面图片（可空）
     * @param lyricFile 歌词文件（可空）
     *
     * @return 上传结果，含 name、 artistId、artistName、    albumId、albumName、  musicUrl、   coverUrl、lyricUrl、  musicId、    success 等字段
     */
    public Map<String, Object> uploadSong(
            String name,
            String artistName,
            String albumName,

            Integer duration,

            MultipartFile musicFile,
            MultipartFile coverFile,
            MultipartFile lyricFile) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);

        // 1. 查找或创建歌手
        Artist artist = artistMapper.findByName(artistName);
        if (artist == null) {
            artist = new Artist();
            artist.setName(artistName);
            artistMapper.insert(artist);
        }
        result.put("artistId", artist.getId());
        result.put("artistName", artist.getName());

        // 2. 查找或创建专辑
        Album album = null;
        // 通过 name + artistId 简单匹配
        // 这里直接用 INSERT 做简单处理，实际项目应加判断
        if (albumName != null && !albumName.isBlank()) {
            album = new Album();
            album.setName(albumName);
            album.setArtistId(artist.getId());
            albumMapper.insert(album);
            result.put("albumId", album.getId());
            result.put("albumName", album.getName());
        }

        // 3. 上传文件
        String musicUrl = null;
        String coverUrl = null;
        String lyricUrl = null;

        if (musicFile != null && !musicFile.isEmpty()) {
            musicUrl = fileService.uploadMusic(musicFile);
            result.put("musicUrl", musicUrl);
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = fileService.uploadCover(coverFile);
            result.put("coverUrl", coverUrl);
        }
        if (lyricFile != null && !lyricFile.isEmpty()) {
            lyricUrl = fileService.uploadLyric(lyricFile);
            result.put("lyricUrl", lyricUrl);
        }

        // 4. 写入 music 表
        Music music = new Music();

        music.setName(name);
        music.setArtistId(artist.getId());
        if (album != null) music.setAlbumId(album.getId());

        music.setMusicUrl(musicUrl);
        music.setCoverUrl(coverUrl);
        music.setLyricUrl(lyricUrl);

        music.setDuration(duration != null ? duration : 0);  // 时长由前端随表单上传（浏览器解码器计算）

        musicMapper.insert(music);
        result.put("musicId", music.getId());

        result.put("success", true);
        return result;
    }
}
