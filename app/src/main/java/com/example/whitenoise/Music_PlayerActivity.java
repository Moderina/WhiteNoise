package com.example.whitenoise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Music_PlayerActivity extends AppCompatActivity {

    TextView title, current_time, max_time;
    SeekBar seekbar;
    ImageView pauseplay, next, pre, icon;
    ArrayList<Song> songList;
    Song songData;
    MediaPlayer mediaPlayer = Media_Player.getInstance();
    ExoPlayer player;
    int position;
    Context context;
    Layout playerViewLayout;


    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();

        for(Song song : songList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getPath())
                    .setMediaMetadata(getMetadata(song))
                    .build();

            mediaItems.add(mediaItem);
        }
        Log.wtf("LOOK", mediaItems.toString());
        return mediaItems;
    }

    private MediaMetadata getMetadata(Song song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .build();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        setResourcesWithMusic();

        Music_PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    current_time.setText(convertMMSS(mediaPlayer.getCurrentPosition()+""));

//                    if(mediaPlayer.isPlaying()){
//                        pauseplay.setImageResource(R.drawable.pause);
//
//                    }else{
//                        pauseplay.setImageResource(R.drawable.play);
//                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progess, boolean user) {
//                if(mediaPlayer!=null && user){
//                    mediaPlayer.seekTo(progess);
//                }
////                if(player!=null && user) {
////                    player.seekTo(progess);
////                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "ROCK BOTTOM", Toast.LENGTH_SHORT).show();
//        player.release();
        super.onDestroy();
    }

    void setResourcesWithMusic(){
        songData = songList.get(Media_Player.currentIndex);
        title.setText(songData.getTitle());
        max_time.setText(convertMMSS(Integer.toString(songData.getDuration())));

//        pauseplay.setOnClickListener(v -> pausePlay());
//        next.setOnClickListener(v -> playNext());
//        pre.setOnClickListener(v -> playPre());

        playMusic();
    }

    public void playMusic(){

        mediaPlayer.reset();
        try{
//            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekbar.setProgress(0);
            seekbar.setMax(mediaPlayer.getDuration());
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    private void playNext(){
        if(Media_Player.currentIndex == songList.size()-1){
            return;
        }

        Media_Player.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPre(){

        if(Media_Player.currentIndex == 0){
            return;
        }

        Media_Player.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){

        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public void setSongData(Song song) {
        this.songData = song;
    }



    public static String convertMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}