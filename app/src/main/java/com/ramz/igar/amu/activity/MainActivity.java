package com.ramz.igar.amu.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramz.igar.amu.R;
import com.ramz.igar.amu.adapter.AlbumAdapter;
import com.ramz.igar.amu.model.Album;
import com.ramz.igar.amu.model.GlobalState;
import com.ramz.igar.amu.model.ItemOffDecoration;
import com.ramz.igar.amu.model.ItemSubscriber;
import com.ramz.igar.amu.model.Player;
import com.ramz.igar.amu.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    BottomSheetBehavior bottomSheetBehavior;
    //    private ArrayList<Song> playlist = GlobalState.getInstance().getPlaylist();
    private List<Album> albums = GlobalState.getInstance().getAlbums();
    private TextView status, songTitle, songTitleExpand;
    private RecyclerView recyclerView;
    private Player player;
    private ImageButton playButton;
    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = GlobalState.getInstance().getPlayer();
        BehaviorSubject<Boolean> isPlaying = player.getIsPlayingBehavior();
        BehaviorSubject<Song> currentSong = player.getCurrentSongBehavior();

        status = findViewById(R.id.status);
        recyclerView = findViewById(R.id.album_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        albumAdapter = new AlbumAdapter(MainActivity.this, albums);
        recyclerView.setAdapter(albumAdapter);
        ItemOffDecoration itemDecoration = new ItemOffDecoration(2, getResources().getDimensionPixelSize(R.dimen.item_offset), true, 0);
        recyclerView.addItemDecoration(itemDecoration);

        final View bottomSheet = findViewById(R.id.bottom_sheet);

        songTitle = findViewById(R.id.song_title);
        songTitleExpand = findViewById(R.id.song_title_expand);

        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        final ImageButton nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        final ImageButton prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(this);
        final ImageButton playButtonEx = findViewById(R.id.play_button_expand);
        playButtonEx.setOnClickListener(this);
        final ImageButton nextButtonEx = findViewById(R.id.next_button_expand);
        nextButtonEx.setOnClickListener(this);
        final ImageButton prevButtonEx = findViewById(R.id.prev_button_expand);
        prevButtonEx.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            status.setText(getResources().getString(R.string.already_granted));
            if (GlobalState.getInstance().getAlbums().isEmpty())
                getSongList(getApplicationContext());
            status.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(player.getPlayer().isPlaying() ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN);

//        final View bottomView = findViewById(R.id.bottom_sheet_sec);
        final View bottomViewChild = findViewById(R.id.bottom_sheet_sec_child);
        final View playerView = findViewById(R.id.player_view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                float alpha = newState == BottomSheetBehavior.STATE_COLLAPSED ? 1f : 0f;
                ObjectAnimator playAnimation = ObjectAnimator.ofFloat(playButton, "alpha", alpha);
                ObjectAnimator nextAnimation = ObjectAnimator.ofFloat(nextButton, "alpha", alpha);
                ObjectAnimator prevAnimation = ObjectAnimator.ofFloat(prevButton, "alpha", alpha);
                ObjectAnimator bottomChild = ObjectAnimator.ofFloat(bottomViewChild, "alpha", newState == BottomSheetBehavior.STATE_EXPANDED ? 1f : 0f);
                ObjectAnimator playerAnimation = ObjectAnimator.ofFloat(playerView, "alpha", alpha);
                AnimatorSet anim = new AnimatorSet();
                anim.playTogether(playAnimation, nextAnimation, prevAnimation, bottomChild, playerAnimation);
                anim.setDuration(500);
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    player.stop();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    playButton.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                    prevButton.setVisibility(View.GONE);
                } else {
                    playButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                }
                anim.start();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        ItemSubscriber<Boolean> isPlayingSubscriber = new ItemSubscriber<>(isPlaying);

        isPlayingSubscriber.subscibe(new ItemSubscriber.OnNext<Boolean>() {
            @Override
            public void onNextValue(Boolean value) {
                playButton.setImageResource(value ? R.drawable.ic_pause : R.drawable.ic_play);
                playButtonEx.setImageResource(value ? R.drawable.ic_pause_expand : R.drawable.ic_play_expand);
                if (value && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(!value && player.getCurrentSong() == null) bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                final LinearLayout linearLayout = findViewById(R.id.main_activity_root);
                ValueAnimator animation;

                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN && value) {
                    animation = ValueAnimator.ofInt(0, getResources().getDimensionPixelSize(R.dimen.bottom_sheet_top_height));
                } else {
                    animation = ValueAnimator.ofInt(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_top_height), 0);
                }

                animation.setDuration(500);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        linearLayout.setPadding(0, 0, 0, Integer.parseInt(valueAnimator.getAnimatedValue().toString()));
                    }
                });
                animation.start();

            }
        });

        ItemSubscriber<Song> songItemSubscriber = new ItemSubscriber<>(currentSong);

        songItemSubscriber.subscibe(new ItemSubscriber.OnNext<Song>() {
            @Override
            public void onNextValue(Song value) {
                songTitle.setText(value.getTitle());
                songTitleExpand.setText(value.getTitle());
                bottomSheet.setBackground(player.getPlayerBackground(getApplicationContext()));

            }
        });

    }

    public void getSongList(Context context) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            ArrayList<Song> songs = new ArrayList<>();
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                String thisPath = musicCursor.getString(pathColumn);

                String albumArt = "";
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID + "=?",
                        new String[]{String.valueOf(thisId)},
                        null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    }

                    songs.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisPath, albumArt));
                    cursor.close();
                }
            } while (musicCursor.moveToNext());
            HashMap<String, Album> tempAlbums = new HashMap<>();
            for (Song song : songs) {
                String key = song.getAlbum();
                String albumCover = song.getAlbumArt();
                String artist = song.getArtist();
                if (!tempAlbums.containsKey(key)) {
                    tempAlbums.put(key, new Album(key, albumCover, artist));
                    Album album = tempAlbums.get(key);
                    album.addSong(song);
                } else {
                    Album album = tempAlbums.get(key);
                    album.addSong(song);
                }
            }
            albums.addAll(tempAlbums.values());
            albumAdapter.update();
            musicCursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    status.setText(getResources().getString(R.string.granted));
                    if (GlobalState.getInstance().getAlbums().isEmpty())
                        getSongList(getApplicationContext());
                    status.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    status.setText(getResources().getString(R.string.denied));
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
            case R.id.play_button_expand: {
                if (player.isPlaying()) {
                    player.pause();
                } else if (!player.isPlaying()) {
                    player.play();
                }
                break;
            }
            case R.id.stop_button: {
                player.stop();
                break;
            }
            case R.id.next_button:
            case R.id.next_button_expand: {
                player.next();
                break;
            }
            case R.id.prev_button:
            case R.id.prev_button_expand: {
                player.prev();
                break;
            }
        }
    }
}
