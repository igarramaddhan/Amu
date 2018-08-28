package com.ramz.igar.amu.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramz.igar.amu.R;
import com.ramz.igar.amu.adapter.SongAdapter;
import com.ramz.igar.amu.model.Album;
import com.ramz.igar.amu.model.GlobalState;
import com.ramz.igar.amu.model.ItemSubscriber;
import com.ramz.igar.amu.model.Player;
import com.ramz.igar.amu.model.Song;

import io.reactivex.subjects.BehaviorSubject;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    BottomSheetBehavior bottomSheetBehavior;
    private TextView songTitle, songTitleExpand;
    private ImageButton playButton;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Album album = GlobalState.getInstance().getAlbums().get(getIntent().getIntExtra("albumId", 0));

        player = GlobalState.getInstance().getPlayer();
        BehaviorSubject<Boolean> isPlaying = player.getIsPlayingBehavior();
        BehaviorSubject<Song> currentSong = player.getCurrentSongBehavior();

        RecyclerView recyclerView = findViewById(R.id.song_recycler_view);
        ImageView imageView = findViewById(R.id.expandedImage);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        SongAdapter songAdapter = new SongAdapter(album.getSongs());
        recyclerView.setAdapter(songAdapter);

        Bitmap coverBitmap = BitmapFactory.decodeFile(album.getAlbumCover());
        imageView.setImageBitmap(coverBitmap);
        Palette palette = Palette.from(coverBitmap).generate();
        int color1 = palette.getDarkVibrantColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundDark));
        int color2 = palette.getDarkMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        collapsingToolbarLayout.setStatusBarScrimColor(color2);
        collapsingToolbarLayout.setContentScrimColor(color1);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        collapsingToolbarLayout.setTitle(album.getAlbumTitle());
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(player.getPlayer().isPlaying() ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN);

        final View bottomView = findViewById(R.id.bottom_sheet_sec);
        final View bottomViewChild = findViewById(R.id.bottom_sheet_sec_child);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                float alpha = newState == BottomSheetBehavior.STATE_COLLAPSED ? 1f : 0f;
                ObjectAnimator playAnimation = ObjectAnimator.ofFloat(playButton, "alpha", alpha);
                ObjectAnimator nextAnimation = ObjectAnimator.ofFloat(nextButton, "alpha", alpha);
                ObjectAnimator prevAnimation = ObjectAnimator.ofFloat(prevButton, "alpha", alpha);
                ObjectAnimator bottomChild = ObjectAnimator.ofFloat(bottomViewChild, "alpha", newState == BottomSheetBehavior.STATE_EXPANDED ? 1f : 0f);
                AnimatorSet anim = new AnimatorSet();
                anim.playTogether(playAnimation, nextAnimation, prevAnimation, bottomChild);
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
                int colorFrom = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
                int colorTo = ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundDark);
                Log.d("Color", "" + colorTo);
                bottomView.setBackgroundColor(interpolateColor(slideOffset, colorFrom, colorTo));
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
            }
        });

        ItemSubscriber<Song> songItemSubscriber = new ItemSubscriber<>(currentSong);

        songItemSubscriber.subscibe(new ItemSubscriber.OnNext<Song>() {
            @Override
            public void onNextValue(Song value) {
                songTitle.setText(value.getTitle());
                songTitleExpand.setText(value.getTitle());
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private int interpolateColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }
}
