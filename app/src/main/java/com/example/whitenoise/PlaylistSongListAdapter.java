package com.example.whitenoise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
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

import java.util.ArrayList;

public class PlaylistSongListAdapter extends RecyclerView.Adapter<PlaylistSongListAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> psongList;
    ExoPlayer player;
    PlayerViewManager playerViewManager;
    Intent notificationIntent;
    int color;
    int selectedItem = RecyclerView.NO_POSITION;

    public PlaylistSongListAdapter(Context context, ExoPlayer player, PlayerViewManager playerViewManager, Intent notificationIntent) {
        this.context = context;
        this.player = player;
        this.playerViewManager = playerViewManager;
        this.notificationIntent = notificationIntent;
    }


    @NonNull
    @Override
    public PlaylistSongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_element, parent, false);
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
    }

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

    @Override
    public int getItemCount() {
        return 0;
    }
}
