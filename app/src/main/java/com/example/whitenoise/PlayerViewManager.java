package com.example.whitenoise;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.masoudss.lib.SeekBarOnProgressChanged;
import com.masoudss.lib.WaveformSeekBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerViewManager extends ConstraintLayout {

    Context context;
    ExoPlayer exoPlayer;
    ConstraintLayout playerView, miniPlayerView;

    TextView playerCloseBtn;
    TextView songTitle;
    ImageView prevBtn, nextBtn, playPause, repeatBtn, playlistBtn, musicIcon;
    TextView miniSongTitle, miniArtist;
    ImageView miniNextBtn, miniPlayPauseBtn, miniMusicIcon;
    WaveformSeekBar seekbar;
    ProgressBar progressBar;
    TextView currentTime, durationTime;
    int repeatMode = 0;
    String url;
    OkHttpClient httpclient;
    boolean Btstatus;

    public PlayerViewManager(@NonNull Context context, ExoPlayer exoPlayer, ConstraintLayout playerView, ConstraintLayout miniPlayerView, TextView playerCloseBtn, TextView songTitle, ImageView prevBtn, ImageView nextBtn, ImageView playPause, ImageView repeatBtn, ImageView playlistBtn, ImageView musicIcon, TextView miniSongTitle, TextView miniArtist, ImageView miniNextBtn, ImageView miniPlayPauseBtn, ImageView miniMusicIcon, WaveformSeekBar seekbar, ProgressBar progressBar, TextView currentTime, TextView durationTime) {
        super(context);
        this.context = context;
        this.exoPlayer = exoPlayer;
        this.playerView = playerView;
        this.miniPlayerView = miniPlayerView;
        this.playerCloseBtn = playerCloseBtn;
        this.songTitle = songTitle;
        this.prevBtn = prevBtn;
        this.nextBtn = nextBtn;
        this.playPause = playPause;
        this.repeatBtn = repeatBtn;
        this.playlistBtn = playlistBtn;
        this.musicIcon = musicIcon;
        this.miniSongTitle = miniSongTitle;
        this.miniArtist = miniArtist;
        this.miniNextBtn = miniNextBtn;
        this.miniPlayPauseBtn = miniPlayPauseBtn;
        this.miniMusicIcon = miniMusicIcon;
        this.seekbar = seekbar;
        this.progressBar = progressBar;
        this.currentTime = currentTime;
        this.durationTime = durationTime;
        Btstatus = false;

        playerControls();
        playerEvents();
        httpclient = new OkHttpClient();
    }

    private void playerEvents() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                loadSongData(mediaItem);
                updatePlayerProgress();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY) {
                    updatePlayerProgress();
                    long realDurationMillis = exoPlayer.getDuration();
                    durationTime.setText(convertMMSS(realDurationMillis));
                    progressBar.setMax((int) realDurationMillis);
                    seekbar.setMaxProgress((int) realDurationMillis / 1000);
                }
            }
        });
    }


    private void playerControls() {
        playerCloseBtn.setOnClickListener(view -> exitPlayerView());
        miniPlayerView.setOnClickListener(view -> openPlayerView());
        seekbar.setOnProgressChanged((waveformSeekBar, progress, user) -> {
            if (user) {
                exoPlayer.seekTo((long) progress * 1000);
            }
        });

        playPause.setOnClickListener(view -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                playPause.setImageResource(R.drawable.play_icon);
            } else {
                exoPlayer.play();
                playPause.setImageResource(R.drawable.pause_icon);
            }
        });
        prevBtn.setOnClickListener(view -> {
            try {
                exoPlayer.seekTo(exoPlayer.getPreviousMediaItemIndex(), 0);
                exoPlayer.prepare();
                exoPlayer.play();
            } catch (Exception e) {
            }
            ;

        });
        nextBtn.setOnClickListener(view -> {
            try {
                exoPlayer.seekTo(exoPlayer.getNextMediaItemIndex(), 0);
                exoPlayer.prepare();
                exoPlayer.play();
            } catch (Exception e) {
            }
            ;

        });

        miniPlayPauseBtn.setOnClickListener(view -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                miniPlayPauseBtn.setImageResource(R.drawable.play_icon);
                playPause.setImageResource(R.drawable.play_icon);
            } else {
                exoPlayer.play();
                miniPlayPauseBtn.setImageResource(R.drawable.pause_icon);
                playPause.setImageResource(R.drawable.pause_icon);
            }
        });
    }

    private void updatePlayerProgress() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer.isPlaying()) {
                    currentTime.setText(convertMMSS(exoPlayer.getCurrentPosition()));
                    progressBar.setProgress((int) exoPlayer.getCurrentPosition());
                    seekbar.setProgress((float) (exoPlayer.getCurrentPosition() / 1000.0));
                    if(!isBluetoothHeadsetConnected() && Btstatus) {
                        Log.wtf("paranoid", "BT DISCONNECTED");
                        playPause.performClick();
                        Btstatus = false;
                    }
                    else if (isBluetoothHeadsetConnected() && !Btstatus) {
                        Btstatus = true;
                    }
                }
                handler.postDelayed(this, 10);
            }
        };
        handler.post(runnableCode);
    }


    public void loadSongData(MediaItem mediaItem) {
        miniPlayerView.setVisibility(VISIBLE);
        songTitle.setText(mediaItem.mediaMetadata.title);
        miniSongTitle.setText(mediaItem.mediaMetadata.title);
        miniArtist.setText(mediaItem.mediaMetadata.artist);

        progressBar.setProgress(0);

        playPause.setImageResource(R.drawable.pause_icon);
        miniPlayPauseBtn.setImageResource(R.drawable.pause_icon);
        assert mediaItem.localConfiguration != null;
        seekbar.setSampleFrom(String.valueOf(mediaItem.localConfiguration.uri));
        loadImage();
    }

    public void loadImage() {
//        url = "https://www.google.com/search?q="+ songTitle.getText() + " " + miniArtist.getText() + " album" +"&tbm=isch";
        String readyQuery = songTitle.getText() + "+" + miniArtist.getText() + "";
        readyQuery = readyQuery.replaceAll(" ", "+");
        url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCwtzt1pkN224u3iVuPD3_Tnkt9m1qxzbo&cx=85ddd9c7a287347a8&q=" + readyQuery + "&searchType=image&imgType=photo";
        Log.wtf("spirit", url);
        Request request = new Request.Builder().url(url).build();
        httpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();

//                    myResponse = myResponse.substring(myResponse.indexOf("src=\"")+5);
//                    if (myResponse.startsWith("/images")) {
//                        myResponse = myResponse.substring(myResponse.indexOf("src=\"")+5);
//                        myResponse = myResponse.substring(0, myResponse.indexOf("\""));

//                    }
//                    final String mresponse = myResponse;
                    try {
                        JSONObject json = new JSONObject(myResponse);
                        JSONArray items = json.getJSONArray("items");
                        String imageUrl = items.getJSONObject(0).getString("link");
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                        .into(musicIcon);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                        .into(miniMusicIcon);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void exitPlayerView() {
        playerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);
    }

    private void openPlayerView() {
        playerView.setVisibility(View.VISIBLE);
        miniPlayerView.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    public static String convertMMSS(Long duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
//        String time;
//        int h = (int) (duration/(3600000));
//        int m = (int) ((duration%(60*60*1000))/(60000));
//        int s = (int) ((duration%(60*60*1000)%(1000%60))/1000);
//        if (h>1) {time = h+":"+m+":"+s;}
//        else time = m+":"+s;
//        return  time;

    }
}
