package com.ramz.igar.amu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramz.igar.amu.R;
import com.ramz.igar.amu.activity.AlbumActivity;
import com.ramz.igar.amu.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<Album> albums;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder holder, int position) {
        final int pos = position;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("albumId", pos);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.imageView, "albumArt");
                context.startActivity(intent, options.toBundle());
            }
        });

        Album album = albums.get(position);
        holder.imageView.setImageBitmap(album.getCoverBitmap(context));
        holder.titleTextView.setText(album.getAlbumTitle());
        holder.artistTextView.setText(album.getAlbumTitle());
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void update() {
        notifyDataSetChanged();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView titleTextView;
        TextView artistTextView;

        private AlbumViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.album_card);
            imageView = itemView.findViewById(R.id.cover_art);
            titleTextView = itemView.findViewById(R.id.album_title);
            artistTextView = itemView.findViewById(R.id.album_artist);
        }
    }
}
