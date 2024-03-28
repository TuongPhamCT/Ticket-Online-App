package com.example.ticketonlineapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ticketonlineapp.Activity.Movie.InformationFilmActivity;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.ExtraIntent;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewAllMovieAdapter extends BaseAdapter {
    private Context context;
    private List<Film> listFilm;

    public ViewAllMovieAdapter(List<Film> listFilm, Context context) {
        this.listFilm = listFilm;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listFilm.size();
    }

    @Override
    public Object getItem(int i) {
        return listFilm.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_film_item, null);
            holder = new ViewHolder();
            holder.imageFilm = (ImageView) view.findViewById(R.id.imgFilm);
            holder.nameFilm = (TextView) view.findViewById(R.id.nameFilm);
            holder.item = (LinearLayout) view.findViewById(R.id.filmItem);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.nameFilm.setText(listFilm.get(i).getName());
        Film f = listFilm.get(i);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), InformationFilmActivity.class);
                i.putExtra(ExtraIntent.film, f);
                BookedInformation.getInstance().film = f;
                view.getContext().startActivity(i);
            }
        });
        Picasso.get()
                .load(listFilm.get(i).getPosterImage())

                .into(holder.imageFilm);
        return view;

    }
    static class ViewHolder {
        ImageView imageFilm;
        TextView nameFilm;
        LinearLayout item;
    }
}