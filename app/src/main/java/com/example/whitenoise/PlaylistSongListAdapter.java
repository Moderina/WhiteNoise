package com.example.whitenoise;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.stream.Collectors;

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

    public static class ViewHolder extends RecyclerView.ViewHolder{

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
    public void onBindViewHolder(@NonNull PlaylistSongListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.name_change.setVisibility(View.VISIBLE);
                holder.playlistAdd.setVisibility(View.VISIBLE);
                int prev = selectedItem;
                selectedItem = position;
                notifyItemChanged(prev);
                return true;
            }
        });

        holder.name_change.setOnClickListener(view -> {
            ChangeSongNameWindow(holder, songData);
        });
//        holder.playlistAdd.setOnClickListener(view -> {
//            AddSongToPlaylist(holder, songData);
//        });
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

    private void ChangeSongNameWindow(PlaylistSongListAdapter.ViewHolder holder, Song songData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.fix_song_name, null);
        TextView original = view.findViewById(R.id.original_title);
        final EditText editTitle = view.findViewById(R.id.edit_title);
        final EditText editArtist = view.findViewById(R.id.edit_artist);
        Button saveButton = view.findViewById(R.id.save_button);

        original.setText(songData.getFull_name());
        editTitle.setText(holder.titleTextView.getText());
        editArtist.setText(holder.artistTextView.getText());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String artist = editArtist.getText().toString();
                holder.titleTextView.setText(title);
                holder.artistTextView.setText(artist);
                songData.title = title;
                songData.artist = artist;
                dialog.dismiss();
            }
        });
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
