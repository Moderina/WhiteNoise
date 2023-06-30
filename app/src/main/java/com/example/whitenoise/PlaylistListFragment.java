package com.example.whitenoise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class PlaylistListFragment extends Fragment {

    RecyclerView playlistsrecyclerView, playlistsongsrecyclerview;
    PlaylistAdapter playlistAdapter;
    PlaylistSongListAdapter playlistSongListAdapter;
    PlaylistViewManager playlistViewManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_recyclerview, container, false);
        playlistsrecyclerView = view.findViewById(R.id.playlist_recycler_view);
        playlistsrecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        return super.onCreateView(inflater, container, savedInstanceState);
        playlistsongsrecyclerview = view.findViewById(R.id.playlist_songlist_recyclerview);
        playlistsongsrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        playlistViewManager = new PlaylistViewManager(view.findViewById(R.id.playlist_view), playlistsrecyclerView, playlistsongsrecyclerview, view.findViewById(R.id.playlist_backBtn), view.findViewById(R.id.playlist_name));

        MainActivity ma = (MainActivity) getActivity();

        playlistSongListAdapter = new PlaylistSongListAdapter(ma, ma.player, ma.notificationIntent);
        playlistAdapter = new PlaylistAdapter(getContext(), ma.allPlaylists, playlistViewManager, playlistSongListAdapter);
        playlistsrecyclerView.setAdapter(playlistAdapter);
        playlistsongsrecyclerview.setAdapter(playlistSongListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(playlistsongsrecyclerview);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            Collections.swap(playlistSongListAdapter.psongList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
