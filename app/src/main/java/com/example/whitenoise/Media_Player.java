package com.example.whitenoise;

import android.media.MediaPlayer;

public class Media_Player {
    static MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance==null){
            instance = new MediaPlayer();
        }
        return instance;
    }
    public static int currentIndex = -1;
}
