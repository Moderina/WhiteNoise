package com.example.whitenoise;

import android.net.Uri;

import java.io.Serializable;

public class Song implements Serializable {
    String path, imageURL;
    String title, artist;
    String full_name;
    int duration;
    int size;

    public Song(String path, String full_name, String title, String artist, int duration, int size) {
        this.path = path;
        this.full_name = full_name;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        imageURL="";
    }

    public String getPath() {
        return path;
    }

    public String getFull_name() {return  full_name;}

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public String getImageURL() {return imageURL; }

    public void setImageURL(String url) { imageURL = url; }
}
