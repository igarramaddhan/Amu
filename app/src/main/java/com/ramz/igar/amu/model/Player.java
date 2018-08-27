package com.ramz.igar.amu.model;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class Player {

    private boolean isPlaying = false;
    private Song currentSong = null;
    private MediaPlayer player = new MediaPlayer();
    private List<Song> playlist;
    private int currentIndex = 0;

    Player(List<Song> playlist) {
        this.playlist = playlist;
    }

    public void startPlay(int position) {
        try {
            if (currentSong != null) {
                player.stop();
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
        Log.d("PLAYER", "PLAY");
    }

    public void pause() {
        player.pause();
        isPlaying = false;
        Log.d("PLAYER", "PAUSE");
    }

    public void stop() {
        if(isPlaying) {
            player.stop();
            player.reset();
            currentSong = null;
            isPlaying = false;
        }
    }

    public void next(){
        stop();
        startPlay(currentIndex + 1 == playlist.size() ? 0 : currentIndex + 1);
        Log.d("PLAYER", "NEXT");
    }

    public void prev(){
        stop();
        startPlay(currentIndex - 1 == -1 ? playlist.size() - 1 : currentIndex - 1);
        Log.d("PLAYER", "PREV");
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public MediaPlayer getPlayer() {
        return player;
    }
}
