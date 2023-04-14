package com.example.whitenoise;

import android.annotation.SuppressLint;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{

    MainActivity activity;
    ArrayList<Song> songList;
    ArrayList<Playlist> playlistsList;
    Context context;
    Intent notificationIntent;
    ExoPlayer player;
    Animation animation;
//    Music_PlayerActivity musicPlayer;
    ConstraintLayout playerView, miniplayerView;
    PlayerViewManager playerViewManager;
    int selectedItem = RecyclerView.NO_POSITION;

    public MusicListAdapter(MainActivity activity, ArrayList<Song> songList, ArrayList<Playlist> allPlaylists, Context context, ExoPlayer player, ConstraintLayout playerView, ConstraintLayout miniplayerView, PlayerViewManager playerViewManager, Intent notificationIntent) {
        this.activity = activity;
        this.songList = songList;
        this.playlistsList = allPlaylists;
        this.context = context;
        this.player = player;
        this.playerView = playerView;
        this.miniplayerView = miniplayerView;
        this.playerViewManager = playerViewManager;
        this.notificationIntent = notificationIntent;
        animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_in);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_element, parent, false);
        view.findViewById(R.id.song_card).setAnimation(animation);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView titleTextView, artistTextView;
        CardView cardView;
        ImageView neonbar;
        ImageView name_change, playlistAdd;
        //        ImageView iconImageView;
        public ViewHolder(View itemView) {

            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            artistTextView = itemView.findViewById(R.id.artist);
            cardView = itemView.findViewById(R.id.song_card);
            neonbar = itemView.findViewById(R.id.side_line);
            name_change = itemView.findViewById(R.id.name_change);
            playlistAdd = itemView.findViewById(R.id.playlist_add);
//            iconImageView = itemView.findViewById(R.id.icon);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song songData = songList.get(position);
        int color = createColor(songData);
        holder.titleTextView.setText(songData.getTitle());
        Shader textshader = new LinearGradient(0, 0, holder.titleTextView.getTextSize(), 0,
                new int[]{color, Color.WHITE},
                new float[]{0, 1},
                Shader.TileMode.CLAMP);
        holder.titleTextView.getPaint().setShader(textshader);

        holder.artistTextView.setText(songData.getArtist());
        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in));
        holder.neonbar.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        holder.name_change.setVisibility(View.GONE);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                Media_Player.getInstance().reset();
////                Media_Player.currentIndex = holder.getAdapterPosition();
//                Intent intent = new Intent(context, Music_PlayerActivity.class);
//                intent.putExtra("LIST", songList);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                Toast.makeText(context, intent.getComponent().getClassName(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(context, "Deep end", Toast.LENGTH_SHORT).show();
                holder.name_change.setVisibility(View.VISIBLE);
                holder.playlistAdd.setVisibility(View.VISIBLE);
                int prev = selectedItem;
                selectedItem = position;
                notifyItemChanged(prev);
                return true;
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Log.wtf("path to self dest", songData.getPath());
            if (!player.isPlaying()) {
                miniplayerView.setVisibility(View.VISIBLE);
                activity.startService(notificationIntent);
                player.setMediaItems(getMediaItems(), position, 0);
                player.prepare();
                player.play();
                checkToKeepAppAlive();
            }
            else {
                player.pause();
                player.setMediaItems(getMediaItems(), position, 0);
                player.prepare();
                player.play();
            }
//            playerViewManager.loadSongData(songData);



            Toast.makeText(activity, songData.getPath(), Toast.LENGTH_SHORT).show();

        });

        holder.name_change.setOnClickListener(view -> {
            ChangeSongNameWindow(holder, songData);
        });
        holder.playlistAdd.setOnClickListener(view -> {AddSongToPlaylist(holder, songData);});
    }

    private void checkToKeepAppAlive() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Log.wtf("loser", "loser");
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

    private void ChangeSongNameWindow(ViewHolder holder, Song songData) {
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

    private void AddSongToPlaylist(ViewHolder holder, Song songData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.add_song_to_playlist, null);
        final EditText new_name = view.findViewById(R.id.new_playlist_name);
        Button saveButton = view.findViewById(R.id.save_button);

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
                String title = new_name.getText().toString();
                Playlist playlist = new Playlist(title);
                playlist.addSong(songData.getPath());
                dialog.dismiss();
                playlistsList.add(playlist);
                for (Playlist pl : playlistsList) {
                    Log.wtf("dammit", pl.getName());
                }
            }
        });
    }

    private int createColor(Song songData) {
        if (songData.getTitle().length() < 3) {
            int color = 0xFFFF0000;
            return color;
        }
        int first = (songData.getTitle().charAt(3)-65)*4;
        int sec = (songData.getTitle().charAt(1)-65)*4;
        int third = (songData.getTitle().charAt(2)-65)*4;
        int color = (255 & 0xff) << 24 | (first & 0xff) << 16 | (third & 0xff) << 8 | (sec & 0xff);
        return color;
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();

        for(Song song : songList) {
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




    public void filterSongs(ArrayList<Song> filteredList) {
        songList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public ArrayList<Playlist> getUpdatedPlaylists() {
        Log.wtf("losing", "failing");
        for (Playlist pl : playlistsList) {
            Log.wtf("dammit", pl.getName());
        }
        return playlistsList;
    }
}
