package com.jiangruicheng.btlight.adapter;


import java.io.Serializable;

/**
 * Created by jiang_ruicheng on 15/11/16.
 */
public class MusicInfo implements Serializable {
    private String id;
    private String title;
    private String url;

    private String albums;
    private String artists;

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
