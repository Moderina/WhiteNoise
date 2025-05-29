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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.masoudss.lib.WaveformSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    PlayerViewManager view_manager;
    YTViewManager yt_view_manager;
    Intent notificationIntent;

    //main variables
    RecyclerView playlistRecyclerView;
    SearchView searchView;
    String searchquery;
    ImageView barLeftBtn, barRightBtn;
    ArrayList<Song> allSongs = new ArrayList<>();
    ArrayList<Playlist> allPlaylists = new ArrayList<>();
    ArrayList<Song> serialized = new ArrayList<>();
    ArrayList<Playlist> pserialized = new ArrayList<>();


    //Fragments
    MainScreen mainScreenFragment;
    SongListFragment songListFragment;
    PlaylistListFragment playlistListFragment;

    //main layouts
    ConstraintLayout playerView, ytPlayerView, appBarView, miniPlayerView;


    //music View variables
    TextView playerCloseBtn, playerCloseBtnYT;
    TextView songTitle;
    ImageView background1, background2;
    ImageView prevBtn, nextBtn, playPauseBtn, repeatBtn, playlistBtn, musicIcon;
    TextView miniSongTitle, miniArtist;
    ImageView miniNextBtn, miniPlayPauseBtn, miniMusicIcon;
    WaveformSeekBar seekbar;
    ProgressBar progressBar;
    TextView currentTime, durationTime;

    //dependencies
    ExoPlayer player;
    YouTubePlayerView youTubePlayerView;

    //permissions
    ActivityResultLauncher<String> storagePermissionLauncher;
    final String permission = Manifest.permission.READ_MEDIA_AUDIO;
    ActivityResultLauncher<String> BTPermissionLauncher;
    final String BTpermission = Manifest.permission.BLUETOOTH_CONNECT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
//            if (granted) {
//                Log.wtf("main", String.valueOf(Thread.currentThread().getId()));
////                ExecutorService executor = Executors.newSingleThreadExecutor();
////                executor.execute(this::fetch_songs);
//                fetch_songs();
//            } else userResponse();
//        });
//        storagePermissionLauncher.launch(permission);
        CheckStoragePermission();


//        BTPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
//            if (!granted) BTuserResponse();
//        });
//        BTPermissionLauncher.launch(BTpermission);



        getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(37489, 199));
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(37489, 199));


        searchView = findViewById(R.id.search_view);
        barLeftBtn = findViewById(R.id.knuck);
        barRightBtn = findViewById(R.id.knuck2);

        //big player
        background1 = findViewById(R.id.background1);
        background2 = findViewById(R.id.background2);
        playerCloseBtn = findViewById(R.id.backBtn);
        songTitle = findViewById(R.id.song_Title);
        prevBtn = findViewById(R.id.pre);
        nextBtn = findViewById(R.id.next);
        repeatBtn = findViewById(R.id.exo_repeat_toggle);
        playPauseBtn = findViewById(R.id.pauseplay);
        musicIcon = findViewById(R.id.music_icon_big);
        seekbar = findViewById(R.id.seek_bar);
        currentTime = findViewById(R.id.current_time);
        durationTime = findViewById(R.id.max_time);

        //YT player
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        playerCloseBtnYT = findViewById(R.id.backBtn_yt);

        //mini player
        miniSongTitle = findViewById(R.id.mini_title);
        miniPlayPauseBtn = findViewById(R.id.play_pause_btn);
        miniArtist = findViewById(R.id.mini_artist);
        progressBar = findViewById(R.id.progress_bar);
        miniMusicIcon = findViewById(R.id.mini_song_icon);

        playerView = findViewById(R.id.player_view);
        ytPlayerView = findViewById(R.id.yt_player_view);
        appBarView = findViewById(R.id.appbar);
        miniPlayerView = findViewById(R.id.mini_player);



        player = new ExoPlayer.Builder(this).build();

        view_manager = new PlayerViewManager(this, player, playerView, miniPlayerView, background1, background2, playerCloseBtn, songTitle, prevBtn, nextBtn, playPauseBtn, repeatBtn, playlistBtn, musicIcon, miniSongTitle, miniArtist, miniNextBtn, miniPlayPauseBtn, miniMusicIcon, seekbar, progressBar, currentTime, durationTime);
        yt_view_manager = new YTViewManager(this, youTubePlayerView, ytPlayerView, miniPlayerView, background1, background2, playerCloseBtnYT);
        notificationIntent = new Intent(this, Notification.class);

        deserializeData();
        setMiniPlayerListener();
        appControls();
        loadMainView();
        loadSongsView();
        searchViewChange(searchView);
        playerControls();
    }

    private void setMiniPlayerListener() {
        miniPlayerView.setOnClickListener(view -> {
            if(songListFragment == null) return;
            this.searchView.setIconified(true);
            this.searchView.onActionViewCollapsed();
            StaticClass.hideKeyboardFrom(this, view);
            if(songListFragment.getCurrentPlayer()) yt_view_manager.openPlayerView();
            else view_manager.openPlayerView();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf("closing", "closing");
//        allPlaylists = songListAdapter.getUpdatedPlaylists();
        serializeSongData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) player.stop();
        player.release();
        stopService(notificationIntent);
    }

    private void serializeSongData() {
        try {
            FileOutputStream fos = openFileOutput("songData.ser", Context.MODE_PRIVATE);
            Log.wtf("file", String.valueOf(fos));
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
            Log.wtf("file", "no sense");
        } catch (Exception e) {
            Log.wtf("dupa", "no i dupa");
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
    }


    // VIEW CONTROL - SONG SEARCHING
    public void searchViewChange(SearchView searchView) {

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.wtf("doom", "change who i am");
                loadSongsView();
                playerView.setVisibility(View.GONE);
                ytPlayerView.setVisibility(View.GONE);
                searchView.setQueryHint("How you feelin");
            }
        });

        SearchSong(searchView);
    }

    private void appControls() {

        barLeftBtn.setOnClickListener(view -> {loadMainView();});
        barRightBtn.setOnClickListener(view -> {loadPlaylistView();});
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (playerView.getVisibility() == View.VISIBLE || ytPlayerView.getVisibility() == View.VISIBLE) {
                    playerView.setVisibility(View.GONE);
                    ytPlayerView.setVisibility(View.GONE);
                    miniPlayerView.setVisibility(View.VISIBLE);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadMainView() {
        searchView.setIconified(true);
        playerView.setVisibility(View.GONE);
        ytPlayerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);
        if (mainScreenFragment == null) {
            mainScreenFragment = new MainScreen();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentLayout, mainScreenFragment);
        fragmentTransaction.commit();
    }

    private void loadSongsView() {
        playerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);

        if (songListFragment == null) {
            songListFragment = new SongListFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentLayout, songListFragment);
        fragmentTransaction.commit();
    }

    private void loadPlaylistView() {
        searchView.setIconified(true);
        playerView.setVisibility(View.GONE);
        miniPlayerView.setVisibility(View.VISIBLE);
        if (playlistListFragment == null) {
            playlistListFragment = new PlaylistListFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentLayout, playlistListFragment);
        fragmentTransaction.commit();
//        for (Playlist pl : allPlaylists) {
//            Log.wtf("stop this pain tonight", pl.getName());
//        }
    }


    private void playerControls() {
        songTitle.setSelected(true);
        miniSongTitle.setSelected(true);
    }



    private void fetch_songs() {
        ArrayList<Song> songs = new ArrayList<>();
        Uri mediaStoreUri;
        Log.wtf("fetching", String.valueOf(Thread.currentThread().getId()));
        mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

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
            if (cursor == null) Log.wtf("cursor", "is null");
            assert cursor != null;
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
            Log.wtf("songs: ", String.valueOf(songs.size()));
            for (Song ssong : serialized) {
                for (Song song : songs) {
                    if (Objects.equals(ssong.getPath(), song.getPath())) {
                        song.title = ssong.getTitle();
                        song.artist = ssong.getArtist();
                        song.setImageURL(ssong.getImageURL());
                        song.setWaveform(ssong.getWaveform());
                    }
                }
            }

        }
        showSongs(songs);
    }

    private void showSongs(ArrayList<Song> songs) {
        if (songs.isEmpty()) {
            return;
        }
        allSongs.clear();
        allSongs.addAll(songs);

    }


    private void SearchSong(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty())
                {
                    searchquery = newText.toLowerCase();
                    songListFragment.filterSongs(searchquery);
                    return true;
                }
                return false;
            }
        });
    }



    public String[] nameNartist (String title, String artist) {
        if(title.contains("["))
        {
            title = title.substring(0, title.lastIndexOf("["));
        }
        artist = title;
        if(title.contains("-")) {
            artist = title.substring(0, title.indexOf("-"));
            title = title.substring(title.indexOf("-")+2);
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

    public Song getSong(){
        MediaItem current = player.getCurrentMediaItem();
        for (Song song:allSongs) {
            if (song.getPath().equals(current.mediaMetadata.composer)) {
                return song;
            }
        }
        return null;
    }


    private void CheckStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(() -> {
            Log.wtf("storage:", "perm granted");
                fetch_songs();
//            });
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    123);
//            if (shouldShowRequestPermissionRationale(permission)) {
//                Toast.makeText(MainActivity.this, "ALLOW ACCESS LOSER", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void BTuserResponse() {
        if (ContextCompat.checkSelfPermission(this, BTpermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 111);
        }
    }

}