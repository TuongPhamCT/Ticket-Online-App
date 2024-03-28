package com.example.ticketonlineapp.Activity.Movie;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.Helper;
import com.example.ticketonlineapp.Adapter.ViewAllMovieAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewAllMovieActivity extends AppCompatActivity {
    CheckNetwork checkNetwork = new CheckNetwork();
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkNetwork, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(checkNetwork);
        super.onStop();
    }

    GridView filmGridview;
    Button backBtn;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_movie_screen);
        filmGridview = findViewById(R.id.filmGridView);
        backBtn = findViewById(R.id.backbutton);
        title = findViewById(R.id.titleViewAll);
        List<Film> listFilm = new ArrayList<Film>();
        Intent intent = getIntent();
        String status = intent.getStringExtra("status");
        if(status.equals("playing")){
            title.setText("Now Playing");
        }
        else if(status.equals("coming")) {
            title.setText("Coming Soon");
        }
        else
        {
            title.setText("Expired");
            ImageView addMovie= findViewById(R.id.AddMovie);
            addMovie.setVisibility(View.GONE);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkAccountType();
        FirebaseRequests.database.collection("Movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                listFilm.clear();
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Film f = documentSnapshot.toObject(Film.class);
                    try {
                        if(status.equals("playing")){
                            if(f.getMovieBeginDate().toDate().before(Helper.getCurrentDate())
                                    &&f.getMovieEndDate().toDate().after(Helper.getCurrentDate()))
                            {
                                if(BookedInformation.getInstance().typeFilm.equals("All")){
                                    listFilm.add(f);
                                }
                                else if (f.getGenre().contains(BookedInformation.getInstance().typeFilm)) {
                                    listFilm.add(f);
                                } else {
                                }
                            }
                        }
                        else  if(status.equals("coming")){
                            if(f.getMovieBeginDate().toDate().after(currentDate)){
                                if(BookedInformation.getInstance().typeFilm.equals("All")){
                                    listFilm.add(f);
                                }
                                else if (f.getGenre().contains(BookedInformation.getInstance().typeFilm)) {
                                    listFilm.add(f);
                                } else {
                                }
                            }
                        }
                        else
                        {
                            if(f.getMovieEndDate().toDate().before(currentDate)){
                                if(BookedInformation.getInstance().typeFilm.equals("All")){
                                    listFilm.add(f);
                                }
                                else if (f.getGenre().contains(BookedInformation.getInstance().typeFilm)) {
                                    listFilm.add(f);
                                } else {
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {}
                }
                filmGridview.setAdapter(new ViewAllMovieAdapter(listFilm, ViewAllMovieActivity.this));
            }
        });


    }
    void checkAccountType()
    {
        ImageView addMovie= findViewById(R.id.AddMovie);

        try{
            Log.d("account type", Users.currentUser.getAccountType());
            if(Users.currentUser!=null)
                if((!(Users.currentUser.getAccountType().toString()).equals("admin")))
                {
                    ViewGroup.LayoutParams params = addMovie.getLayoutParams();
                    params.height = 0;
                    addMovie.setLayoutParams(params);
                    addMovie.setVisibility(View.INVISIBLE);
                }
                else {
                    addMovie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(ViewAllMovieActivity.this, AddMovieActivity.class);
                            startActivity(i);
                        }
                    });
                }
        }
        catch (Exception e)
        {
            ViewGroup.LayoutParams params = addMovie.getLayoutParams();
            params.height = 0;
            addMovie.setLayoutParams(params);
            addMovie.setVisibility(View.INVISIBLE);
        }
    }
}