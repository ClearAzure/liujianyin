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

    public Map<String, Object> uploadSong(
            String name,
            String artistName,
            String albumName,
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
        music.setDuration(0);  // 简单起见暂不解析时长
        musicMapper.insert(music);
        result.put("musicId", music.getId());

        result.put("success", true);
        return result;
    }
}
