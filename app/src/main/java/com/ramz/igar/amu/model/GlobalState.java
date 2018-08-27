package com.ramz.igar.amu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalState {
    private static GlobalState instance;

    public static GlobalState getInstance(){
        if(instance==null){
            instance = new GlobalState();
        }
        return instance;
    }

    private Player player;
    private ArrayList<Song> playlist = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();

    private GlobalState(){
        this.player = new Player(this.playlist);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlaylist(List<Song> songs) {
        resetPlaylist();
        this.playlist.addAll(songs);
    }

    public void resetPlaylist(){
        this.playlist.clear();
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }

    public List<Song> getAlbumSongs(int position){
        return albums.get(position).getSongs();
    }
}
