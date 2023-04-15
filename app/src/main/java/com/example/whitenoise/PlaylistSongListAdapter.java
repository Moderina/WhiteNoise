package com.example.whitenoise;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistSongListAdapter extends RecyclerView.Adapter<PlaylistSongListAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> psongList;


    @NonNull
    @Override
    public PlaylistSongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView name;
        public ViewHolder(View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.title);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
