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
    PlaylistSongListAdapter playlistSongListAdapter;
    PlaylistViewManager playlistViewManager;

    public PlaylistAdapter(Context context, ArrayList<Playlist> allPlaylists, PlaylistViewManager playlistViewManager, PlaylistSongListAdapter playlistSongListAdapter) {
        this.context = context;
        this.playlistList = allPlaylists;
        this.playlistViewManager = playlistViewManager;
        this.playlistSongListAdapter = playlistSongListAdapter;
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

        playlistViewManager.playlistView.setVisibility(View.VISIBLE);
        Log.wtf("keep", String.valueOf(playlistSongListAdapter));
        playlistSongListAdapter.playlistData(playlist.getSongList(), playlist.color);
        playlistViewManager.playlistName.setText(playlist.name);
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

}
