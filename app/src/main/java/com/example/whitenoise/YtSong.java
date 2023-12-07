package com.example.whitenoise;

import java.net.URL;

public class YtSong {
    String videoID;
    String title, artist;
    String imageURL;

    public YtSong(String vidid, String tit, String art, String url)
    {
        videoID = vidid;
        title = tit;
        artist = art;
        imageURL = url;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
