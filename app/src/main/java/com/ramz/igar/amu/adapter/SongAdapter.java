package com.ramz.igar.amu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramz.igar.amu.R;
import com.ramz.igar.amu.model.GlobalState;
import com.ramz.igar.amu.model.Player;
import com.ramz.igar.amu.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private View songView;
        private TextView title;
        private TextView artist;
        private TextView duration;
        private SongViewHolder(View v){
            super(v);
            songView = v.findViewById(R.id.song_card);
            title = v.findViewById(R.id.card_title);
            artist = v.findViewById(R.id.card_artist);
            duration = v.findViewById(R.id.card_duration);
        }
    }

    public SongAdapter (List<Song> songs){
        this.songs= songs;
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_card, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        final int pos = position;
        Song song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.duration.setText(song.getSongDuration());
        holder.songView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalState.getInstance().setPlaylist(songs);
                Player player = GlobalState.getInstance().getPlayer();
                player.startPlay(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
