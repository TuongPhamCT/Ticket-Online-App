package com.example.ticketonlineapp.Adapter;



import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.Movie.InformationFilmActivity;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.ExtraIntent;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {
    List<Film> listPoster;

    public PosterAdapter(List<Film> listPoster) {
        this.listPoster = listPoster;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.posterItem);
        }
    }
    @NonNull
    @Override
    public PosterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item, parent, false);
        return new PosterAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterAdapter.ViewHolder holder, int position) {
        Picasso.get().load(listPoster.get(position).getPosterImage()).into(holder.imageView);
        Film f =listPoster.get(position);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.imageView.getContext(), InformationFilmActivity.class);
                i.putExtra(ExtraIntent.film, f);
                BookedInformation.getInstance().film = f;
                holder.imageView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPoster.size();
    }
}
