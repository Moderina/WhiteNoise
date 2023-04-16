package com.example.whitenoise;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    String name;
    ArrayList<String> songs = new ArrayList<>();

    public Playlist(String name) {
        this.name = name;
    }

    public Playlist(String name, ArrayList<String> songs) {
        this.name = name;
        this.songs.addAll(songs);
    }

    public void addSong(String song) {
        songs.add(song);
    }

    public void removeSong(String song) {
        songs.remove(song);
    }

    public ArrayList<String> getSongList() {return songs;}

    public String getName() {return name;}
}
