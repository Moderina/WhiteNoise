package com.example.whitenoise;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.masoudss.lib.WaveformSeekBar;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    PlayerViewManager view_manager;
    Intent notificationIntent;
    IntentFilter filter;

    //main variables
    RecyclerView recyclerView, playlistRecyclerView, playlistSongListRecyclerView;
    MusicListAdapter songListAdapter;
    PlaylistAdapter playlistAdapter;
    SearchView searchView;
    ImageView barLeftBtn, barRightBtn;
    ArrayList<Song> allSongs = new ArrayList<>();
    ArrayList<Playlist> allPlaylists = new ArrayList<>();
    ArrayList<Song> serialized = new ArrayList<>();
    ArrayList<Playlist> pserialized = new ArrayList<>();
    TextView noMusicTextView;

    //main layouts
    ConstraintLayout playerView, recyclerViewLayout, appBarView, miniPlayerView, playlistView, playlistRecyclerLayout;


    //music View variables
    TextView playerCloseBtn;
    TextView songTitle;
    ImageView prevBtn, nextBtn, playPauseBtn, repeatBtn, playlistBtn, musicIcon;
    TextView miniSongTitle, miniArtist;
    ImageView miniNextBtn, miniPlayPauseBtn, miniMusicIcon;
    ConstraintLayout  homeControlWrapper, headWrapper, seekbarWrapper;
//    SeekBar seekbar;
    WaveformSeekBar seekbar;
    ProgressBar progressBar;
    TextView currentTime, durationTime;

    //dependencies
    ExoPlayer player;

    //permissions
    ActivityResultLauncher<String> storagePermissionLauncher;
    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    ActivityResultLauncher<String> BTPermissionLauncher;
    final String BTpermission = Manifest.permission.BLUETOOTH_CONNECT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                fetch_songs();
            } else userResponse();
        });
        storagePermissionLauncher.launch(permission);


        BTPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (!granted) BTuserResponse();
        });
        BTPermissionLauncher.launch(BTpermission);



        getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(485937, 199));
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(37489, 199));

        recyclerView = findViewById(R.id.recycler_view);
        playlistRecyclerView = findViewById(R.id.playlist_recycler_view);
        playlistSongListRecyclerView = findViewById(R.id.playlist_songlist_recyclerview);
        noMusicTextView = findViewById(R.id.no_songs);
        searchView = findViewById(R.id.search_view);
        barLeftBtn = findViewById(R.id.knuck);
        barRightBtn = findViewById(R.id.knuck2);

        //big player
        playerCloseBtn = findViewById(R.id.backBtn);
        songTitle = findViewById(R.id.song_Title);
        prevBtn = findViewById(R.id.pre);
        nextBtn = findViewById(R.id.next);
        playPauseBtn = findViewById(R.id.pauseplay);
        musicIcon = findViewById(R.id.music_icon_big);
        seekbar = findViewById(R.id.seek_bar);
        currentTime = findViewById(R.id.current_time);
        durationTime = findViewById(R.id.max_time);


        //mini player
        miniSongTitle = findViewById(R.id.mini_title);
        miniPlayPauseBtn = findViewById(R.id.play_pause_btn);
        miniArtist = findViewById(R.id.mini_artist);
        progressBar = findViewById(R.id.progress_bar);
        miniMusicIcon = findViewById(R.id.mini_song_icon);

        //wrappers
        recyclerViewLayout = findViewById(R.id.recycler_layout);
        playlistRecyclerLayout = findViewById(R.id.playlist_recycler_layout);
        playlistView = findViewById(R.id.playlist_view);
        playerView = findViewById(R.id.player_view);
        appBarView = findViewById(R.id.appbar);
        miniPlayerView = findViewById(R.id.mini_player);

        playlistRecyclerLayout.setVisibility(View.GONE);
        playlistView.setVisibility(View.GONE);



        player = new ExoPlayer.Builder(this).build();

        view_manager = new PlayerViewManager(this, player, playerView, miniPlayerView, playerCloseBtn, songTitle, prevBtn, nextBtn, playPauseBtn, repeatBtn, playlistBtn, musicIcon, miniSongTitle, miniArtist, miniNextBtn, miniPlayPauseBtn, miniMusicIcon, seekbar, progressBar, currentTime, durationTime);
        notificationIntent = new Intent(this, Notification.class);


        deserializeData();
        searchViewChange(searchView);
        appControls();
        playerControls();
        loadSongsView();
    }

    @Override
    protected void onDestroy() {
        allPlaylists = songListAdapter.getUpdatedPlaylists();
        serializeSongData();
        super.onDestroy();
        if (player.isPlaying()) player.stop();
        player.release();
        stopService(notificationIntent);
    }

    private void serializeSongData() {
        try {
            FileOutputStream fos = openFileOutput("songData.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allSongs);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fos = openFileOutput("playlistData.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allPlaylists);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deserializeData() {
        try {
            FileInputStream fis = openFileInput("songData.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            serialized = (ArrayList<Song>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = openFileInput("playlistData.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            allPlaylists = (ArrayList<Playlist>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (serialized != null) {
//            for (Song song : serialized) {
//                Log.wtf("stuck", "Title: " + song.getTitle() + ", Artist: " + song.getArtist() + ", Duration: " + song.getDuration());
//            }
//        }
    }


    // VIEW CONTROL - SONG SEARCHING
    public void searchViewChange(SearchView searchView) {

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchView.setQueryHint("How you feelin");
                ImageView i1 = findViewById(R.id.knuck);
                ImageView i2 = findViewById(R.id.knuck2);
                ConstraintLayout constraintLayout = findViewById(R.id.appbar);
                ConstraintLayout mini_player = findViewById(R.id.mini_player);
                int padding_in_dp = (int)(constraintLayout.getPaddingLeft()/ getResources().getDisplayMetrics().density);

                if (padding_in_dp == 50) {
                    int padding_in_px = (int)(10 * getResources().getDisplayMetrics().density + 0.5f);
                    constraintLayout.setPadding(padding_in_px, 0,padding_in_px,0);
                    i1.setScaleX(0.6f);
                    i1.setScaleY(0.6f);
                    i2.setScaleX(0.6f);
                    i2.setScaleY(0.6f);
                    if (playerView.getVisibility() == View.GONE && player.isPlaying()) mini_player.setVisibility(View.GONE);

                }
                else {
                    int padding_in_px = (int)(50 * getResources().getDisplayMetrics().density + 0.5f);
                    constraintLayout.setPadding(padding_in_px,0,padding_in_px,0);
                    i1.setScaleX(1f);
                    i1.setScaleY(1f);
                    i2.setScaleX(1f);
                    i2.setScaleY(1f);
                    if (playerView.getVisibility() == View.GONE && player.isPlaying()) mini_player.setVisibility(View.VISIBLE);
                }
            }
        });

        SearchSong(searchView);
    }

    private void appControls() {

        barLeftBtn.setOnClickListener(view -> {loadSongsView();});
        barRightBtn.setOnClickListener(view -> {loadPlaylistView();});
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (playerView.getVisibility() == View.VISIBLE) {
                    playerView.setVisibility(View.GONE);
                    miniPlayerView.setVisibility(View.VISIBLE);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadSongsView() {
        playerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        Log.wtf("up", String.valueOf(playlistRecyclerView));
        playlistRecyclerLayout.setVisibility(View.GONE);
        playlistSongListRecyclerView.setVisibility(View.GONE);
    }


    private void loadPlaylistView() {
        playerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        playlistRecyclerLayout.setVisibility(View.VISIBLE);
        playlistSongListRecyclerView.setVisibility(View.GONE);

//        playlistAdapter.onViewFocused(allPlaylists);

//        allPlaylists.clear();
//        allPlaylists = songListAdapter.getUpdatedPlaylists();
        for (Playlist pl : allPlaylists) {
            Log.wtf("stop this pain tonight", pl.getName());
        }
    }


    private void playerControls() {
        songTitle.setSelected(true);
        miniSongTitle.setSelected(true);
    }



    private void fetch_songs() {
        ArrayList<Song> songs = new ArrayList<>();
        Uri mediaStoreUri;

        mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";


        try(Cursor cursor = getContentResolver().query(mediaStoreUri, projection, selection, null, sortOrder)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);


            while(cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumIdColumn);
                String fullpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                String uri = String.valueOf(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id));

                String[] nameartist = nameNartist(title, title);

                Song song = new Song(fullpath, title, nameartist[0], nameartist[1], duration, size);

                songs.add(song);
            }
            for (Song ssong : serialized) {
                for (Song song : songs) {
                    if (Objects.equals(ssong.getPath(), song.getPath())) {
                        song.title = ssong.getTitle();
                        song.artist = ssong.getArtist();
                    }
                }
            }

        }
        showSongs(songs);
    }

    private void showSongs(ArrayList<Song> songs) {
        if (songs.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
            return;
        }
        noMusicTextView.setVisibility(View.INVISIBLE);
        allSongs.clear();
        allSongs.addAll(songs);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistSongListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        songListAdapter = new MusicListAdapter(this, allSongs, allPlaylists, getApplicationContext(), player, view_manager, notificationIntent);
        recyclerView.setAdapter(songListAdapter);
        playlistSongListRecyclerView.setAdapter(songListAdapter);

        playlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        playlistAdapter = new PlaylistAdapter(this, allPlaylists, playlistView, songListAdapter);
        playlistRecyclerView.setAdapter(playlistAdapter);

    }


    private void SearchSong(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) Log.wtf("empty vessel", newText.toLowerCase());
                filterSongs(newText.toLowerCase());
                return true;
            }
        });
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

    public String[] nameNartist (String title, String artist) {

        if(title.contains("-")) {
            artist = title.substring(0, title.indexOf("-"));
            title = title.substring(title.indexOf("-")+2, title.lastIndexOf("."));
            if(title.contains("[")) {
                title = title.substring(0, title.lastIndexOf("["));
            }
        }
        if(title.contains("(Lyric")) {
            title = title.substring(0, title.indexOf("(Lyric"));
        }
        if(title.contains("(Official")) {
            title = title.substring(0, title.indexOf("(Official"));
        }
        if(artist.contains("(Official")) {
            artist = artist.substring(0, artist.indexOf("(Official"));
        }

        return new String[]{title, artist};
    }


    private void userResponse() {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            fetch_songs();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (shouldShowRequestPermissionRationale(permission)) {
                Toast.makeText(MainActivity.this, "ALLOW ACCESS LOSER", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void BTuserResponse() {
        if (ContextCompat.checkSelfPermission(this, BTpermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 111);
        }
    }

}