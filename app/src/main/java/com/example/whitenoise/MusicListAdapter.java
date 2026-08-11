package com.example.whitenoise;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    MainActivity activity;
    ArrayList<Song> songList;
    ArrayList<Playlist> playlistsList;
    ArrayList<YtSong> ytSongsList;
    Intent notificationIntent;
    ExoPlayer player;
    YouTubePlayer youTubePlayer;
    Animation animation;
    PlayerViewManager playerViewManager;
    int selectedItem = RecyclerView.NO_POSITION;
    boolean yt_playing = false;
    boolean newload = true;

    public MusicListAdapter(MainActivity activity, ArrayList<Song> songList, ArrayList<Playlist> allPlaylists, ExoPlayer player, /*PlayerViewManager playerViewManager,*/ Intent notificationIntent) {
        this.activity = activity;
        this.songList = songList;
        this.playlistsList = allPlaylists;
        ytSongsList = new ArrayList<>();
        this.player = player;
//        this.playerViewManager = playerViewManager;
        this.notificationIntent = notificationIntent;
        animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
    }

    public static class ViewHolder0 extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView titleTextView, artistTextView;
        ConstraintLayout cardView;
        ImageView neonbar;
        ImageView name_change, playlistAdd, color_change;
        //        ImageView iconImageView;
        public ViewHolder0(View itemView) {

            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            artistTextView = itemView.findViewById(R.id.artist);
            cardView = itemView.findViewById(R.id.song_card);
            neonbar = itemView.findViewById(R.id.song_icon);
            name_change = itemView.findViewById(R.id.name_change);
            playlistAdd = itemView.findViewById(R.id.playlist_add);
            color_change = itemView.findViewById(R.id.color_change);
//            iconImageView = itemView.findViewById(R.id.icon);
        }
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder{ //nw cz ma byc static

        TextView name;
        public ViewHolder1(View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.title);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView title, artist;
        ImageView thumnail;
        public ViewHolder2(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
            thumnail = itemView.findViewById(R.id.thumnail);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        Log.wtf("debvil is read", String.valueOf(songList.size()));
        if (position < songList.size()) return 0;
        if (position < songList.size() + playlistsList.size()) return 1;
        return 2;
//        return position >= songList.size() ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.song_element, parent, false);
        view.findViewById(R.id.song_card).setAnimation(animation);
        switch(viewType)
        {
            case 0:
            default:
                return new ViewHolder0(view);

            case 1:
                view = LayoutInflater.from(activity).inflate(R.layout.playlist_element, parent, false);
                return new ViewHolder1(view);

            case 2:
                view = LayoutInflater.from(activity).inflate(R.layout.yt_song_element, parent, false);
                return  new ViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        switch(holder.getItemViewType())
        {
            case 0:
                ViewHolder0 holder0 = (ViewHolder0) holder;

                Song songData = songList.get(position);
                holder0.titleTextView.setText(songData.getTitle());

                holder0.artistTextView.setText(songData.getArtist());
                if(newload)
                    holder0.cardView.startAnimation(AnimationUtils.loadAnimation(holder0.itemView.getContext(), R.anim.fade_in));
                holder0.neonbar.getBackground().setColorFilter(songData.color, PorterDuff.Mode.SRC_ATOP);

                holder0.name_change.setVisibility(View.GONE);
                holder0.color_change.setVisibility(View.GONE);
                holder0.playlistAdd.setVisibility(View.GONE);

                holder0.itemView.setOnClickListener(view -> {
                    yt_playing = false;
//                    youTubePlayer.pause();
                    Log.wtf("path to self dest", songData.getPath());
                    if (!player.isPlaying()) {
                        player.setMediaItems(getMediaItems(), position, 0);
                        player.prepare();
                        player.play();
                        if (!isMyServiceRunning(Notification.class))
                            activity.startService(notificationIntent);
                        checkToKeepAppAlive();
                    }
                    else {
                        player.pause();
                        player.setMediaItems(getMediaItems(), position, 0);
                        player.prepare();
                        player.play();
                    }
                });

                holder0.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        holder0.name_change.setVisibility(View.VISIBLE);
                        holder0.color_change.setVisibility(View.VISIBLE);
                        holder0.playlistAdd.setVisibility(View.VISIBLE);
                        int prev = selectedItem;
                        selectedItem = position;
                        notifyItemChanged(prev);
                        return true;
                    }
                });

                holder0.name_change.setOnClickListener(view -> {
                    ChangeSongNameWindow(holder0, songData);
                });
                holder0.playlistAdd.setOnClickListener(view -> {
                    AddSongToPlaylist(holder0, songData);
                });
                holder0.color_change.setOnClickListener(view -> {
                    ChnageColorWindow(holder0, songData);
                });
                break;

            case 1:
                ViewHolder1 holder1 = (ViewHolder1) holder;
                Playlist playData = playlistsList.get(position - songList.size());
                holder1.name.setText(playData.getName());
                break;

            case 2:
                ViewHolder2 holder2 = (ViewHolder2) holder;
                YtSong song = ytSongsList.get(position - songList.size() - playlistsList.size());
                holder2.title.setText(song.getTitle());
                holder2.artist.setText(song.getArtist());
                Glide.with(activity)
                        .load(song.getImageURL())
                        .into(holder2.thumnail);

                holder2.itemView.setOnClickListener(view -> {
                    yt_playing = true;
                    player.pause();
                    youTubePlayer.loadVideo(song.getVideoID(), 0f);
                });
        }

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkToKeepAppAlive() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.wtf("losser", "losser");
                if (!player.isPlaying()) {
                    activity.stopService(notificationIntent);
                }
                handler.postDelayed(this, 600000);
            }
        };
        handler.postDelayed(runnableCode, 600000);
    }

    private void ChangeSongNameWindow(ViewHolder0 holder, Song songData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.fix_song_name, null);
        TextView original = view.findViewById(R.id.original_title);
        final EditText editTitle = view.findViewById(R.id.edit_title);
        final EditText editArtist = view.findViewById(R.id.edit_artist);
        Button saveButton = view.findViewById(R.id.save_button);

        original.setText(songData.getFull_name());
        editTitle.setText(holder.titleTextView.getText());
        editArtist.setText(holder.artistTextView.getText());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String artist = editArtist.getText().toString();
                holder.titleTextView.setText(title);
                holder.artistTextView.setText(artist);
                songData.title = title;
                songData.artist = artist;
                dialog.dismiss();
            }
        });
    }

    private void AddSongToPlaylist(ViewHolder0 holder, Song songData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.add_song_to_playlist, null);
        final EditText new_name = view.findViewById(R.id.new_playlist_name);
        Button saveButton = view.findViewById(R.id.save_button);
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.playlists_to_choose);

        ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(activity, R.layout.addtoplaylist_element, playlistsList.stream().map(Playlist::getName).collect(Collectors.toList()));
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = new_name.getText().toString();
                Log.wtf("istniejem", autoCompleteTextView.getText().toString());
                if(!autoCompleteTextView.getText().toString().isEmpty()) {
                    for (Playlist playlist: playlistsList) {
                        if (playlist.getName().equals(autoCompleteTextView.getText().toString())) {
                            playlist.addSong(songData);
                            break;
                        }
                    }
                }
                else {
                    int color = createColor(title);
                    Playlist playlist = new Playlist(title, color);
                    playlist.addSong(songData);
                    playlistsList.add(playlist);
                }
                dialog.dismiss();
                activity.allPlaylists = playlistsList;
            }
        });
    }
    private void ChnageColorWindow(ViewHolder0 holder, Song songData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.color_picker, null);
        final ColorPicker picker = view.findViewById(R.id.picker);
        final SVBar svBar = view.findViewById(R.id.svbar);
        picker.addSVBar(svBar);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                songData.setColor(color);
                holder.neonbar.getBackground().setColorFilter(songData.color, PorterDuff.Mode.SRC_ATOP);
//                newload = false;
//                notifyDataSetChanged();
            }
        });
    }

    private int createColor(String name) {
        if (name.length() <= 3) {
            int color = 0xFFFF0000;
            return color;
        }
        int first = (name.charAt(3)-65)*4;
        int sec = (name.charAt(1)-65)*4;
        int third = (name.charAt(2)-65)*4;
        int color = (255 & 0xff) << 24 | (first & 0xff) << 16 | (third & 0xff) << 8 | (sec & 0xff);
        return color;
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for(Song song : songList) {
//            Log.wtf("words as tools", song.getImageURL());
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getPath())
                    .setMediaMetadata(getMetadata(song))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetadata(Song song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .setAlbumArtist(song.toString()) //image URL
                .setComposer(song.getPath())
                .build();
    }

    public void setYTPlayer(YouTubePlayer yt) {
        youTubePlayer = yt;
    }

    public void playlistSongs(ArrayList<String> songs) {
        ArrayList<Song> temp = new ArrayList<>();
        for (String uri : songs) {
            Log.wtf("warning", uri);
            for (Song song : songList)  {
                Log.wtf(song.getTitle(), song.getPath());
                if (song.getPath() == uri) temp.add(song);
            }
        }
        songList = temp;
        Log.wtf("tohgut", songList.get(0).getTitle());
        notifyDataSetChanged();

    }


    public void filterSongs(ArrayList<Song> filteredList, ArrayList<Playlist> filteredplay) {
        songList = filteredList;
        playlistsList = filteredplay;
        if (songList.size() > 0 || playlistsList.size() > 0) ytSongsList.clear();
        notifyDataSetChanged();
    }

    public void loadYtSongs(ArrayList<YtSong> ytSongs) {
        ytSongsList = ytSongs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songList.size() + playlistsList.size() + ytSongsList.size();
    }

    public boolean getCurrentPlayer() {return yt_playing;}

    public ArrayList<Playlist> getUpdatedPlaylists() {
        Log.wtf("losing", "failing");
        for (Playlist pl : playlistsList) {
            Log.wtf("dammit", pl.getName());
        }
        return playlistsList;
    }
}
