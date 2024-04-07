package com.example.ticketonlineapp.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.Booking.BookedActivity;
import com.example.ticketonlineapp.Activity.Booking.ShowTimeScheduleActivity;
import com.example.ticketonlineapp.Adapter.Helper;
import com.example.ticketonlineapp.Adapter.VideoAdapter;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AboutMovie extends Fragment {
    Film film;
    RecyclerView videoListView;
    String[] videoList;
    public AboutMovie(Film f) {
        film = f;
    }

    public AboutMovie() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_movie, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView description = getView().findViewById(R.id.descriptionTV);
        description.setText(film.getDescription());
        Button BookBt = getView().findViewById(R.id.BookBt);

        if(film.getMovieBeginDate().toDate().after(Helper.getCurrentDate()) ||film.getMovieEndDate().toDate().before(Helper.getCurrentDate()) ) {
            BookBt.setVisibility(View.GONE);
        }
        else BookBt.setVisibility(View.VISIBLE);

        videoListView = getView().findViewById(R.id.videoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false);
        VideoAdapter videoAdapter = new VideoAdapter();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference VideoRef = db.collection("Movies").document(film.getId());
        VideoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Film filmModel = snapshot.toObject(Film.class);
                    videoAdapter.setVideoIdList(filmModel.getTrailer());
                    videoListView.setAdapter(videoAdapter);
                    videoListView.setLayoutManager(linearLayoutManager);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        try{
            if(Users.currentUser!=null)
                if(((Users.currentUser.getAccountType().toString()).equals("admin")))
                {
                    BookBt.setText("Schedule");
                    BookBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            schedule();
                        }
                    });
                }
                else {
                    BookBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            book();
                        }
                    });
                }
        }
        catch (Exception e)
        {
            BookBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    book();
                }
            });
        }


    }
    void book()
    {
        Intent i = new Intent(getView().getContext(), BookedActivity.class);
        i.putExtra("selectedFilm", film);
        i.putExtra("nameFilm", film.getName());
        getView().getContext().startActivity(i);
    }
    void schedule()
    {
        Intent i = new Intent(getView().getContext(), ShowTimeScheduleActivity.class);
        i.putExtra("selectedFilm", film);
        i.putExtra("nameFilm", film.getName());
        getView().getContext().startActivity(i);
    }
}