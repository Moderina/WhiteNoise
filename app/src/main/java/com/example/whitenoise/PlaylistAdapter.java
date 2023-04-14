package com.example.whitenoise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

    Context context;
    ArrayList<Playlist> allPlaylists;

    public PlaylistAdapter(Context context, ArrayList<Playlist> allPlaylists) {
        this.context = context;
        this.allPlaylists = allPlaylists;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_element, parent, false);
//        view.findViewById(R.id.song_card).setAnimation(animation);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView name, artistTextView;
        CardView cardView;
        ImageView icon;
        ImageView name_change, playlistAdd;
        //        ImageView iconImageView;
        public ViewHolder(View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.title);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlistData = allPlaylists.get(position);
        Log.wtf("fuck", playlistData.getName());
        holder.name.setText(playlistData.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
