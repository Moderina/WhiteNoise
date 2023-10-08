package com.example.whitenoise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.masoudss.lib.WaveformSeekBar;

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

    Animation minimize, maximize;

    TextView playerCloseBtn;
    TextView songTitle;
    ImageView prevBtn, nextBtn, playPause, repeatBtn, playlistBtn, musicIcon;
    TextView miniSongTitle, miniArtist;
    ImageView miniNextBtn, miniPlayPauseBtn, miniMusicIcon;
    WaveformSeekBar seekbar;
    ProgressBar progressBar;
    TextView currentTime, durationTime;
    int repeatMode = 0;
    Song song;
    String url = "";
    int imagenumber = 0;
    OkHttpClient httpclient;
    boolean Btstatus;
    final Handler handler = new Handler(Looper.getMainLooper());


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
        minimize = AnimationUtils.loadAnimation(context, R.anim.minimize);
        maximize = AnimationUtils.loadAnimation(context, R.anim.maximize);
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
//                seekbar.set
//                seekbar.
                if(reason == 2)
                {
                    Log.wtf("i dont", "belong here");
                }
                imagenumber=0;
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
        musicIcon.setOnClickListener(view -> loadImage(""));
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
            } catch (Exception ignored) {}
        });
        nextBtn.setOnClickListener(view -> {
            try {
                exoPlayer.seekTo(exoPlayer.getNextMediaItemIndex(), 0);
                exoPlayer.prepare();
                exoPlayer.play();
            } catch (Exception ignored) {}
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

        musicIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                ((MainActivity) context).SaveSongImage(url);
                song.setImageURL(url);
                Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void updatePlayerProgress() {
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
        song = ((MainActivity) context).getSong();
        if (song == null) return;
        if(song.waveform == null)
            Log.wtf("after", "no waveform");
        else
            Log.wtf("after", song.waveform.toString());
        if(playerView.getVisibility() == GONE)
            miniPlayerView.setVisibility(VISIBLE);
        songTitle.setText(mediaItem.mediaMetadata.title);
        miniSongTitle.setText(mediaItem.mediaMetadata.title);
        miniArtist.setText(mediaItem.mediaMetadata.artist);
        updateText((String)mediaItem.mediaMetadata.title, (String)mediaItem.mediaMetadata.artist);

        progressBar.setProgress(0);

        playPause.setImageResource(R.drawable.pause_icon);
        miniPlayPauseBtn.setImageResource(R.drawable.pause_icon);
//        assert mediaItem.localConfiguration != null;
        if(song.getWaveform() == null)
        {
            seekbar.setSampleFrom(String.valueOf(mediaItem.localConfiguration.uri));
            song.setWaveform(seekbar.getSample());
        }
        else
            seekbar.setSample(song.getWaveform());
        Log.wtf("he pretends",song.getImageURL());
        loadImage(song.getImageURL());
    }

    public void loadImage(String address) {
        if (address != "") {
            url = address;
            Log.wtf("spirit", url);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                            .into(musicIcon);
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                            .into(miniMusicIcon);
                }
            });
            return;
        }
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

                    try {
                        JSONObject json = new JSONObject(myResponse);
                        JSONArray items = json.getJSONArray("items");
                        String imageUrl = items.getJSONObject(imagenumber).getString("link");
                        url = imageUrl;
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
                        imagenumber++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        imagenumber = 0;
                    }

                }
            }
        });
    }

    private void exitPlayerView() {
        miniPlayerView.startAnimation(maximize);
        playerView.startAnimation(minimize);
        miniPlayerView.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
    }

    private void openPlayerView() {

        miniPlayerView.startAnimation(minimize);
        playerView.startAnimation(maximize);
        playerView.setVisibility(View.VISIBLE);
        miniPlayerView.setVisibility(GONE);
    }

    private void updateText(String title, String artist) {
        Runnable runnable = new Runnable() {
            int index = 0;
            @Override
            public void run() {
                if (index < title.length()) {
                    miniSongTitle.setText(title.substring(0, index + 1));
                    index++;
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(runnable, 100);
        runnable = new Runnable() {
            int index = 0;
            @Override
            public void run() {
                if (index < artist.length()) {
                    miniArtist.setText(artist.substring(0, index + 1));
                    index++;
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(runnable, 100);

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

    }
}
