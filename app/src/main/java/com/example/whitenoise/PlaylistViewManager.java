package com.example.whitenoise;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistViewManager {
    ConstraintLayout playlistView;
    RecyclerView playlistRecyclerView, playlistSongListRecyclerView;
    TextView backBtn, playlistName;

    public PlaylistViewManager(ConstraintLayout playlist_view, RecyclerView playlistRecyclerView, RecyclerView playlistSongListRecyclerView, TextView backBtn, TextView playlistName) {
        this.playlistView = playlist_view;
        this.playlistRecyclerView = playlistRecyclerView;
        this.playlistSongListRecyclerView = playlistSongListRecyclerView;
        this.backBtn = backBtn;
        this.playlistName = playlistName;

        this.backBtn.setOnClickListener(view -> {playlistView.setVisibility(View.GONE);});
        playlistView.setVisibility(View.GONE);

    }
}
