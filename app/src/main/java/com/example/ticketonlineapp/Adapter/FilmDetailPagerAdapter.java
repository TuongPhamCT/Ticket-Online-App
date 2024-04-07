package com.example.ticketonlineapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketonlineapp.Fragment.AboutMovie;
import com.example.ticketonlineapp.Fragment.ReviewFragment;
import com.example.ticketonlineapp.Model.Film;

public class FilmDetailPagerAdapter extends FragmentStateAdapter {
    Film film;
    int tabIndex;

    public FilmDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, Film f, int tabIndex) {
        super(fragmentActivity);
        film = f;
        this.tabIndex = tabIndex;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==1)
            return new ReviewFragment(film);
        else
            return new AboutMovie(film);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}