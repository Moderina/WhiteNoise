package com.example.whitenoise;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    String name;
    int color;
    ArrayList<Song> songs = new ArrayList<>();

    public Playlist(String name, int color) {

        this.name = name;
        this.color = color;
    }

    public Playlist(String name, ArrayList<Song> songs) {
        this.name = name;
        this.songs.addAll(songs);
    }

    public void addSong(Song song) {
        Log.wtf("add", song.getTitle());
        songs.add(song);

        for (Song son : songs)  {
            Log.wtf(son.getTitle(), son.getPath());
        }
    }

    public void removeSong(String song) {
        songs.remove(song);
    }

    public ArrayList<Song> getSongList() {return songs;}

    public String getName() {return name;}
}
