package com.example.whitenoise;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongListAdapter extends RecyclerView.Adapter<PlaylistSongListAdapter.ViewHolder> {

    MainActivity activity;
    public ArrayList<Song> psongList;
    ExoPlayer player;
    PlayerViewManager playerViewManager;
    Intent notificationIntent;
    int color;
    int selectedItem = RecyclerView.NO_POSITION;

    public PlaylistSongListAdapter(MainActivity context, ExoPlayer player, Intent notificationIntent) {
        this.activity = context;
        this.player = player;
//        this.playerViewManager = playerViewManager;
        this.notificationIntent = notificationIntent;
    }


    @NonNull
    @Override
    public PlaylistSongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.song_element, parent, false);
//        view.findViewById(R.id.song_card).setAnimation(animation);
        return new PlaylistSongListAdapter.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView titleTextView, artistTextView;
        CardView cardView;
        ImageView neonbar;
        ImageView name_change, playlistAdd;
        public ViewHolder(View itemView) {

            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            artistTextView = itemView.findViewById(R.id.artist);
            cardView = itemView.findViewById(R.id.song_card);
            neonbar = itemView.findViewById(R.id.side_line);
            name_change = itemView.findViewById(R.id.name_change);
            playlistAdd = itemView.findViewById(R.id.playlist_add);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongListAdapter.ViewHolder holder, int position) {
        Song songData = psongList.get(position);
        holder.titleTextView.setText(songData.getTitle());

        holder.artistTextView.setText(songData.getArtist());
        holder.neonbar.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        holder.name_change.setVisibility(View.GONE);
        holder.playlistAdd.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(view -> {
            Log.wtf("path to self dest", songData.getPath());
            if (!player.isPlaying()) {
                player.setMediaItems(getMediaItems(), position, 0);
                player.prepare();
                player.play();
                if (!isMyServiceRunning(Notification.class))
                    activity.startService(notificationIntent);
                checkToKeepAppAlive();
            }
            else {
                player.pause();
                player.setMediaItems(getMediaItems(), position, 0);
                player.prepare();
                player.play();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void playlistData(ArrayList<Song> songs, int color) {
        this.color = color;
        ArrayList<Song> temp = new ArrayList<>();
        for (Song s : songs) {
            Log.wtf("warning", s.getTitle());
        }
        psongList = songs;
        Log.wtf("tohgut", psongList.get(0).getTitle());
        notifyDataSetChanged();

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkToKeepAppAlive() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.wtf("losser", "losser");
                if (!player.isPlaying()) {
                    activity.stopService(notificationIntent);
                }
                handler.postDelayed(this, 600000);
            }
        };
        handler.postDelayed(runnableCode, 600000);
    }



    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();

        for(Song song : psongList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getPath())
                    .setMediaMetadata(getMetadata(song))
                    .build();

            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetadata(Song song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .build();
    }


    @Override
    public int getItemCount() {
        return psongList.size();
    }
}
