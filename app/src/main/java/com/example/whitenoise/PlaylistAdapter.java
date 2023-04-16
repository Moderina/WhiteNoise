package com.example.whitenoise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

    Context context;
    ArrayList<Playlist> playlistList;
    ConstraintLayout playlistView;
    MusicListAdapter songListAdapter;

    public PlaylistAdapter(Context context, ArrayList<Playlist> allPlaylists, ConstraintLayout playlistView, MusicListAdapter songListAdapter) {
        this.context = context;
        this.playlistList = allPlaylists;
        this.playlistView = playlistView;
        this.songListAdapter = songListAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.wtf("delusions", "of my mind");
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_element, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView name;
        public ViewHolder(View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.title);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlistData = playlistList.get(position);
        Log.wtf("fuck", playlistData.getName());
        holder.name.setText(playlistData.getName());

        holder.itemView.setOnClickListener(view -> {createPlaylistSongList(playlistData);});
    }

    private void createPlaylistSongList(Playlist playlist) {
        playlistView.setVisibility(View.VISIBLE);
        Log.wtf("keep", "stop");
        songListAdapter.playlistSongs(playlist.getSongList());
    }

//    public void onViewFocused(ArrayList<Playlist> allPlaylists) {
//        this.playlistList = allPlaylists;
//        for (Playlist pl : playlistList) {
//            Log.wtf("wish i hated you", pl.getName());
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

}
