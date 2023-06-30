package com.example.whitenoise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;

public class SongListFragment extends Fragment {
    TextView noMusicTextView;

    RecyclerView recyclerView;
    MusicListAdapter songListAdapter;
    ArrayList<Song> allSongs = new ArrayList<>();;
    ArrayList<Playlist> allPlaylists;
    ExoPlayer player;
    Intent notificationIntent;

//    public static SongListFragment newInstance(ArrayList<Song> allSongs, ArrayList<Playlist> allPlaylists, ViewManager view_manager, Intent notificationIntent) {
//
//        SongListFragment fragment = new SongListFragment();
//        Bundle args = new Bundle();
//        args.putParcelable("allsongs", allSongs);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_recycler, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MainActivity ma = (MainActivity) getActivity();
        allSongs = ma.allSongs;
        allPlaylists = ma.allPlaylists;
        player = ma.player;
        notificationIntent = ma.notificationIntent;

        noMusicTextView = view.findViewById(R.id.no_songs);


        songListAdapter = new MusicListAdapter((MainActivity) getActivity(), allSongs, allPlaylists, player, notificationIntent);
        recyclerView.setAdapter(songListAdapter);

        if (allSongs.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        noMusicTextView.setVisibility(View.INVISIBLE);

        return view;
    }



    public void filterSongs(String query) {

        ArrayList<Song> filteredList = new ArrayList<>();

        if (allSongs.size() > 0) {
            for (Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(query) || song.getArtist().toLowerCase().contains(query)) {
                    filteredList.add(song);
                }
            }
            if (filteredList.size() == 0) noMusicTextView.setVisibility(View.VISIBLE);
            else noMusicTextView.setVisibility(View.INVISIBLE);
            if (songListAdapter != null) {
                songListAdapter.filterSongs(filteredList);
            }


        }
    }
}
