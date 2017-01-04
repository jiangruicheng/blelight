package com.jiangruicheng.btlight.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jiang_ruicheng on 15/11/16.
 */
public class SetMusicInFo {


    public final static String ARTIST = "ARTIST";
    public final static String ALBUMS = "ALBUMS";
    public final static String ALLMUSICNAME = "ALLMUSICNAME";
    public final static String ALLMUSIC = "ALLMUSIC";

    public static HashMap getMusicInFo(Context context, String MusicInfo) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null)
            switch (MusicInfo) {
                case ARTIST:
                    return getmartist_music(context);
                case ALBUMS:
                    return getmalbums_music(context);
            }
        if (cursor != null)
            cursor.close();
        return null;
    }

    @Nullable
    public static ArrayList getMedioList(Context context, String MusicInfo) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null)
            switch (MusicInfo) {
                case ARTIST:
                    return getmartist(context);
                case ALBUMS:
                    return getalbum(context);
                case ALLMUSIC:
                    return getallmusic(context);
                case ALLMUSICNAME:
                    return getmusicname(context);

            }
        if (cursor != null)
            cursor.close();
        return null;
    }

    private static ArrayList<String> getmusicname(Context context) {
        ArrayList<String> musicname = new ArrayList();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        String name;
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            musicname.add(name);
        }
        if (cursor != null)
            cursor.close();
        return musicname;
    }

    private static ArrayList<String> getmartist(Context context) {
        ArrayList<String> arttist_name = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Artists.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio
                    .Artists.ARTIST));
            arttist_name.add(art);
        }
        arttist_name.add(MediaStore.UNKNOWN_STRING);
        if (cursor != null)
            cursor.close();
        return arttist_name;
    }

    private static ArrayList<String> getalbum(Context context) {
        ArrayList<String> albums_name = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio
                    .Albums.ALBUM));
            albums_name.add(album);
        }
        albums_name.add(MediaStore.UNKNOWN_STRING);
        if (cursor != null)
            cursor.close();
        return albums_name;
    }

    private static HashMap<String, ArrayList<MusicInfo>> getmartist_music(Context context) {
        HashMap<String, ArrayList<MusicInfo>> martist_music = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Artists.EXTERNAL_CONTENT_URI, null, null, null, null);
        ArrayList<MusicInfo> unkonwnmusic = new ArrayList<>();
        Cursor unknow = context.getContentResolver().query(MediaStore.Audio.Media
                .EXTERNAL_CONTENT_URI, null, null, null, null);
        while (unknow.moveToNext()) {
            String unkonw = unknow.getString(unknow.getColumnIndexOrThrow
                    (MediaStore.Audio.Artists.ARTIST));
            if (MediaStore.UNKNOWN_STRING.equals(unkonw)) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setTitle(unknow.getString(unknow.getColumnIndex
                        (MediaStore.Audio.Media.TITLE)));
                musicInfo.setId(unknow.getString(unknow.getColumnIndex(MediaStore
                        .Audio.Media._ID)));
                musicInfo.setUrl(unknow.getString(unknow.getColumnIndex(MediaStore
                        .Audio.Media.DATA)));
                musicInfo.setArtists(unknow.getString(unknow.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST)));
                musicInfo.setAlbums(unknow.getString(unknow.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM)));
                unkonwnmusic.add(musicInfo);
            }
        }
        unknow.close();
        while (cursor.moveToNext()) {
            String art = cursor.getString(cursor.getColumnIndexOrThrow
                    (MediaStore.Audio
                            .Artists.ARTIST));
            // arttist_name.add(art);
            String id = cursor.getString(cursor.getColumnIndex
                    (MediaStore.Audio.Artists._ID));
            ArrayList<MusicInfo> musicInfos = new ArrayList<>();
            Cursor allmusic = context.getContentResolver().query(MediaStore
                    .Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media
                    .ARTIST_ID + "=" + id, null, null);
            while (allmusic.moveToNext()) {

                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setTitle(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.TITLE)));
                musicInfo.setId(allmusic.getString(allmusic.getColumnIndex(MediaStore
                        .Audio.Media._ID)));
                musicInfo.setUrl(allmusic.getString(allmusic.getColumnIndex(MediaStore
                        .Audio.Media.DATA)));
                musicInfo.setArtists(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST)));
                musicInfo.setAlbums(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM)));
                musicInfos.add(musicInfo);
            }
            martist_music.put(art, musicInfos);
            allmusic.close();
        }
        martist_music.put(MediaStore.UNKNOWN_STRING, unkonwnmusic);
        cursor.close();

        return martist_music;
    }

    private static HashMap<String, List<MusicInfo>> getmalbums_music(Context context) {
        HashMap<String, List<MusicInfo>> malbums_music = new HashMap<>();
        Cursor albums = context.getContentResolver().query(MediaStore.Audio
                .Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
        ArrayList<MusicInfo> unkonwnmusic = new ArrayList<>();
        Cursor unkonw = context.getContentResolver().query(MediaStore.Audio.Media
                .EXTERNAL_CONTENT_URI, null, null, null, null);
        while (unkonw.moveToNext()) {
            String album = unkonw.getString(unkonw.getColumnIndexOrThrow(MediaStore.Audio
                    .Albums.ALBUM));
            if (MediaStore.UNKNOWN_STRING.equals(album)) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setTitle(unkonw.getString(unkonw.getColumnIndex
                        (MediaStore.Audio.Media.TITLE)));
                musicInfo.setId(unkonw.getString(unkonw.getColumnIndex(MediaStore
                        .Audio.Media._ID)));
                musicInfo.setUrl(unkonw.getString(unkonw.getColumnIndex(MediaStore
                        .Audio.Media.DATA)));
                musicInfo.setArtists(unkonw.getString(unkonw.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST)));
                musicInfo.setAlbums(unkonw.getString(unkonw.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM)));
                unkonwnmusic.add(musicInfo);
            }
        }
        unkonw.close();
        while (albums.moveToNext()) {
            String album = albums.getString(albums.getColumnIndex(MediaStore.Audio
                    .Albums.ALBUM));

            // albums_name.add(album);
            String id = albums.getString(albums.getColumnIndex(MediaStore.Audio
                    .Albums._ID));
            ArrayList<MusicInfo> musicInfos = new ArrayList<>();
            Cursor allmusic = context.getContentResolver().query(MediaStore
                    .Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media
                    .ALBUM_ID + "=" + id, null, null);
            while (allmusic.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setTitle(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.TITLE)));
                musicInfo.setId(allmusic.getString(allmusic.getColumnIndex(MediaStore
                        .Audio.Media._ID)));
                musicInfo.setUrl(allmusic.getString(allmusic.getColumnIndex(MediaStore
                        .Audio.Media.DATA)));
                musicInfo.setArtists(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST)));
                musicInfo.setAlbums(allmusic.getString(allmusic.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM)));
                musicInfos.add(musicInfo);
            }
            malbums_music.put(album, musicInfos);
            allmusic.close();
        }
        malbums_music.put(MediaStore.UNKNOWN_STRING, unkonwnmusic);
        albums.close();
        return malbums_music;
    }

    private static ArrayList<MusicInfo> getallmusic(Context context) {
        ArrayList<MusicInfo> musicInfoList = new ArrayList();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio
                .Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.TITLE)));
            musicInfo.setId(cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media._ID)));
            musicInfo.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.DATA)));
            musicInfo.setArtists(cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.ARTIST)));
            musicInfo.setAlbums(cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.ALBUM)));

            musicInfoList.add(musicInfo);

        }
        cursor.close();
        return musicInfoList;
    }

}
