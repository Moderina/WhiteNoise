package com.example.whitenoise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongListFragment extends Fragment {
    ConstraintLayout noMusicTextView;
    Button ytButton;
    ProgressBar loading;
    String query;

    RecyclerView recyclerView;
    MusicListAdapter songListAdapter;
    ArrayList<Song> allSongs;
    ArrayList<Playlist> allPlaylists;
    ExoPlayer player;
    YouTubePlayerView youTubePlayerView;
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
        youTubePlayerView = ma.youTubePlayerView;
        notificationIntent = ma.notificationIntent;

        noMusicTextView = view.findViewById(R.id.no_songs);
        ytButton = view.findViewById(R.id.yt_search);
        loading = view.findViewById(R.id.loading_icon);

        songListAdapter = new MusicListAdapter((MainActivity) getActivity(), allSongs, allPlaylists, player, notificationIntent);
        recyclerView.setAdapter(songListAdapter);
        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
            songListAdapter.setYTPlayer(youTubePlayer);
        });

        if (allSongs.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        noMusicTextView.setVisibility(View.INVISIBLE);

        ytButton.setOnClickListener(view1 -> {
            noMusicTextView.setVisibility(view1.INVISIBLE);
            loading.setVisibility(view1.VISIBLE);
            ytSearching();
        });

        return view;
    }

    private void ytSearching() {
        OkHttpClient HttpClient = new OkHttpClient();
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=" + query + "&key=" + getResources().getString(R.string.yt);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        HttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                loading.setVisibility(View.INVISIBLE);
                ArrayList<YtSong> ytSongs = new ArrayList<>();
                if (response.isSuccessful()) {
                    // Parse the JSON response to get video details
                    String myResponse = response.body().string();
                    // You can use a JSON library to parse the response (e.g., Gson).

                    try {
                        JSONObject json = new JSONObject(myResponse);
                        JSONArray items = json.getJSONArray("items");
                        for(int i = 0; i< items.length(); i++) {
                            Log.wtf("item:   ", items.getJSONObject(i).toString());
                            JSONObject item = items.getJSONObject(i);
                            String vidID = item.getJSONObject("id").getString("videoId");
                            String title = item.getJSONObject("snippet").getString("title");
                            String artist = item.getJSONObject("snippet").getString("channelTitle");
                            String thumbnail = item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                            Log.wtf(".", items.getJSONObject(i).toString());
                            YtSong song = new YtSong(vidID, title, artist, thumbnail);
                            ytSongs.add(song);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                songListAdapter.loadYtSongs(ytSongs);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    public void filterSongs(String query) {

        this.query = query;
        loading.setVisibility(View.INVISIBLE);
        ArrayList<Song> filteredList = new ArrayList<>();
        ArrayList<Playlist> filteredplay = new ArrayList<>();

        if (allSongs.size() > 0 || allPlaylists.size() > 0) {
            for (Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(query) || song.getArtist().toLowerCase().contains(query)) {
                    filteredList.add(song);
                }
            }
            for(Playlist play : allPlaylists)
            {
                if(play.getName().toLowerCase().contains(query)) filteredplay.add(play);
            }
            Log.wtf("playlist number: ", String.valueOf(filteredplay.size()));
            if (filteredList.size() == 0 && filteredplay.size() == 0) {
                noMusicTextView.setVisibility(View.VISIBLE);
                ytButton.setVisibility(View.VISIBLE);
            }
            else {
                noMusicTextView.setVisibility(View.INVISIBLE);
                ytButton.setVisibility(View.INVISIBLE);
            }
            if (songListAdapter != null) {
                songListAdapter.filterSongs(filteredList, filteredplay);
            }
        }
    }

    public boolean getCurrentPlayer() {return songListAdapter.getCurrentPlayer();}
}
