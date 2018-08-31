package com.ramz.igar.amu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.ramz.igar.amu.R;

import java.io.IOException;
import java.util.List;

import io.reactivex.subjects.BehaviorSubject;

public class Player {

    private BehaviorSubject<Boolean> isPlayingBehavior = BehaviorSubject.create();
    private BehaviorSubject<Song> currentSongBehavior = BehaviorSubject.create();
    private boolean isPlaying = false;
    private Song currentSong = null;
    private MediaPlayer player = new MediaPlayer();
    private List<Song> playlist;
    private int currentIndex = 0;

    Player(final List<Song> playlist) {
        this.playlist = playlist;
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentIndex + 1 == playlist.size()){
                    stop();
                }else {
                    next();
                }
            }
        });
    }

    public void startPlay(int position) {
        try {
            if (currentSong != null) {
                player.stop();
                Log.d("PLAYER", "STOP SONG");
                player.reset();
                currentSong = null;
            }
            Song current = playlist.get(position);
            currentIndex = position;
            currentSong = current;
            player.setDataSource(current.getPath());
            player.prepare();
            player.start();
            isPlaying = true;
            newValue();

            currentSongBehavior.onNext(currentSong);
            Log.d("PLAYER", "START PLAYING: " + currentSong.getTitle());

        } catch (IOException e) {
            Log.e("ERROR", "ERROR ON START PLAY");
            e.printStackTrace();
        }
    }


    public void play() {
        try {
            player.start();
        } catch (IllegalArgumentException e) {
            Log.e("ERROR", "ERROR ON PLAY");
            e.printStackTrace();
        }
        isPlaying = true;
        newValue();
        currentSongBehavior.onNext(currentSong);
        Log.d("PLAYER", "PLAY");
    }

    public void pause() {
        player.pause();
        isPlaying = false;
        newValue();
        Log.d("PLAYER", "PAUSE");
    }

    public void stop() {
        if (isPlaying) {
            player.stop();
            player.reset();
            currentSong = null;
            isPlaying = false;
            newValue();
            Log.d("PLAYER", "STOP");
        }
    }

    public void next() {
//        stop();
        startPlay(currentIndex + 1 == playlist.size() ? 0 : currentIndex + 1);
        Log.d("PLAYER", "NEXT");
    }

    public void prev() {
//        stop();
        startPlay(currentIndex - 1 == -1 ? playlist.size() - 1 : currentIndex - 1);
        Log.d("PLAYER", "PREV");
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public BehaviorSubject<Boolean> getIsPlayingBehavior() {
        return isPlayingBehavior;
    }

    public BehaviorSubject<Song> getCurrentSongBehavior() {
        return currentSongBehavior;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    private void newValue() {
        isPlayingBehavior.onNext(this.isPlaying);
    }

    public GradientDrawable getPlayerBackground(Context context) {
        int accent = ContextCompat.getColor(context, R.color.colorAccent);
        GradientDrawable gd = null;
        if(currentSong!=null){
            int backgroundColor2;
            int backgroundColor = backgroundColor2 = accent;


            Bitmap coverBitmap = BitmapFactory.decodeFile(currentSong.getAlbumArt());
            if (coverBitmap != null) {
                Palette palette = Palette.from(coverBitmap).generate();
                backgroundColor = palette.getMutedColor(accent);
                backgroundColor2 = palette.getDarkMutedColor(accent);
            }

            gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{backgroundColor2, backgroundColor});
            gd.setCornerRadius(0f);
        }
        return gd;
    }
}
