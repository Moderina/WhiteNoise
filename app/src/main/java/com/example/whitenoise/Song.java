package com.example.whitenoise;

import android.net.Uri;

import java.io.Serializable;

public class Song implements Serializable {
    String path, imageURL;
    String title, artist;
    String full_name;
    int duration;
    int size;
    int[] waveform;
    int color;

    public Song(String path, String full_name, String title, String artist, int duration, int size) {
        this.path = path;
        this.full_name = full_name;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        imageURL="";
        this.color = createColor();
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

    public int[] getWaveform() { return waveform; }

    public void setWaveform(int[] w) { waveform = w; }

    public int getColor() {return color;}

    public void setColor(int col) { color = col; }

    private int createColor() {
        if (title.length() <= 3) {
            int color = 0xFFFF0000;
            return color;
        }
        int first = (title.charAt(3)-65)*4;
        int sec = (title.charAt(1)-65)*4;
        int third = (title.charAt(2)-65)*4;
        int color = (255 & 0xff) << 24 | (first & 0xff) << 16 | (third & 0xff) << 8 | (sec & 0xff);
        return color;
    }
}
