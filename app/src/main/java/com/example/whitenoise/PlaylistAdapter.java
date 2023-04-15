package com.example.whitenoise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

    Context context;
    ArrayList<Playlist> playlistList;
    RecyclerView playlistSongListRecyclerView;

    public PlaylistAdapter(Context context, ArrayList<Playlist> allPlaylists, RecyclerView playlistSongListRecyclerView) {
        this.context = context;
        this.playlistList = allPlaylists;
        this.playlistSongListRecyclerView = playlistSongListRecyclerView;
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

        holder.itemView.setOnClickListener(view -> {createPlaylistSongList();});
    }

    private void createPlaylistSongList() {
        playlistSongListRecyclerView.setVisibility(View.VISIBLE);
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
