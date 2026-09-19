package com.music.service.impl;

import com.music.entity.Album;
import com.music.entity.Artist;
import com.music.entity.Music;
import com.music.mapper.AlbumMapper;
import com.music.mapper.ArtistMapper;
import com.music.mapper.MusicMapper;
import com.music.service.FileService;
import com.music.service.SongUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 歌曲上传服务实现类
 */
@Service
@RequiredArgsConstructor
public class SongUploadServiceImpl implements SongUploadService {

    private final FileService fileService;
    private final MusicMapper musicMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public Map<String, Object> uploadSong(
            String name,
            String artistName,
            String albumName,
            Integer duration,
            MultipartFile musicFile,
            MultipartFile coverFile,
            MultipartFile lyricFile,
            MultipartFile artistAvatarFile) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);

        // 1. 查找或创建歌手
        Artist artist = artistMapper.findByName(artistName);
        if (artist == null) {
            artist = new Artist();
            artist.setName(artistName);
            artistMapper.insert(artist);
        }
        // 若提供了歌手头像，上传并更新到歌手
        if (artistAvatarFile != null && !artistAvatarFile.isEmpty()) {
            String avatarUrl = fileService.uploadCover(artistAvatarFile);
            artistMapper.updateAvatar(artist.getId(), avatarUrl);
            result.put("artistAvatarUrl", avatarUrl);
        }
        result.put("artistId", artist.getId());
        result.put("artistName", artist.getName());

        // 2. 查找或创建专辑（按 专辑名 + 歌手 ID 去重）
        Album album = null;
        boolean albumCreated = false;
        if (albumName != null && !albumName.isBlank()) {
            album = albumMapper.findByNameAndArtistId(albumName, artist.getId());
            if (album == null) {
                album = new Album();
                album.setName(albumName);
                album.setArtistId(artist.getId());
                albumMapper.insert(album);
                albumCreated = true;
            }
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

        // 只在"本次新建"的专辑上设默认封面（已存在专辑的封面不动）
        if (albumCreated && coverUrl != null) {
            albumMapper.updateCover(album.getId(), coverUrl);
        }

        // 4. 写入 music 表
        Music music = new Music();

        music.setName(name);
        music.setArtistId(artist.getId());
        if (album != null) music.setAlbumId(album.getId());

        music.setMusicUrl(musicUrl);
        music.setCoverUrl(coverUrl);
        music.setLyricUrl(lyricUrl);

        music.setDuration(duration != null ? duration : 0);

        musicMapper.insert(music);
        result.put("musicId", music.getId());

        result.put("success", true);
        return result;
    }
}
