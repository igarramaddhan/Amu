package com.ramz.igar.amu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.ramz.igar.amu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Album {
    private String albumTitle;
    private String artist;
    private String albumCover;
    private List<Song> songs;

    public Album(String albumTitle, String albumCover, String artist) {
        this.albumTitle = albumTitle;
        this.albumCover = albumCover;
        this.artist = artist;
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public String getArtist() { return artist; }



    public HashMap<String, Integer> getToolbarColor(Context context){
        int navigationBarColor, statusBarColor;
        if(albumCover==null){
            navigationBarColor = ContextCompat.getColor(context, R.color.colorPrimary);
            statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }else {
            Bitmap coverBitmap = BitmapFactory.decodeFile(albumCover);
            final Palette palette = Palette.from(coverBitmap).generate();
            navigationBarColor = palette.getDarkVibrantColor(ContextCompat.getColor(context, R.color.colorPrimary));
            statusBarColor = palette.getDarkMutedColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        HashMap<String, Integer> toolbarColor = new HashMap<>();
        toolbarColor.put("statusBarColor", statusBarColor);
        toolbarColor.put("navigationBarColor", navigationBarColor);
        return toolbarColor;
    }

    public Bitmap getCoverBitmap(Context context){
        Bitmap coverBitmap;
        if(albumCover==null){
            coverBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
        }else{
            coverBitmap = BitmapFactory.decodeFile(albumCover);
        }
        return coverBitmap;
    }
}
