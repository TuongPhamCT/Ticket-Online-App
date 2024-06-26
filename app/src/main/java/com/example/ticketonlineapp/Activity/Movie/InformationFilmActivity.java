package com.example.ticketonlineapp.Activity.Movie;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.FilmDetailPagerAdapter;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.ExtraIntent;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;


public class InformationFilmActivity extends AppCompatActivity {
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

    ImageView backgroundImage;
    TextView nameTV;
    ImageView PosterImage;
    ConstraintLayout mainLayout;
    RatingBar ratingBar;
    TextView voteTV;
    TextView genreTV;
    TextView durationTime;
    TabLayout tabLayout;
    ViewPager2 pager;
    Film f;
    ImageView EditMovie;
    LinearLayoutCompat topView;

    FilmDetailPagerAdapter filmDetailPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_film_screen);
        Intent intent = getIntent();
        f = intent.getParcelableExtra(ExtraIntent.film);
        topView = findViewById(R.id.topView);
        mainLayout = findViewById(R.id.mainLayout);
        backgroundImage = findViewById(R.id.backgroundImage);
        nameTV= findViewById(R.id.filmName);
        PosterImage= findViewById(R.id.PosterImage);
        ratingBar = findViewById(R.id.rating);
        ratingBar.setIsIndicator(true);
        voteTV = findViewById(R.id.vote);
        genreTV = findViewById(R.id.genre);
        durationTime = findViewById(R.id.durationTime);
        ImageView btnBack = findViewById(R.id.btnBack);
        pager=findViewById(R.id.pager);
        tabLayout=findViewById(R.id.tab_layout);
        getFilm(f.getId());
        EditMovie=findViewById(R.id.EditMovie);
        if(Users.currentUser.getAccountType().equals("admin")){
            EditMovie.setVisibility(View.VISIBLE);
        }
        else EditMovie.setVisibility(View.GONE);

        EditMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InformationFilmActivity.this, EditMovieActivity.class);
                i.putExtra(ExtraIntent.film, f);
                startActivity(i);
            }
        });

        refreshScreen();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(),true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
    void getFilm(String id)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Movies").document(id);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) { if (error != null) {
                refreshScreen();
                return;
            }
                if (snapshot != null && snapshot.exists()) {
                    f = snapshot.toObject(Film.class);
                    nameTV.setText(f.getName());

                    Picasso.get().load(f.getBackGroundImage()).fit().centerCrop().into(backgroundImage);
                    Picasso.get().load(f.getPosterImage()).fit().centerCrop().into(PosterImage);

                    ratingBar.setRating(f.getVote());
                    DecimalFormat df = new DecimalFormat("0.0");
                    voteTV.setText("(" + df.format(f.getVote()) +")");
                    genreTV.setText(f.getGenre());
                    refreshScreen();
                    durationTime.setText(f.getDurationTime());
                }
            }
        });
    }
    void refreshScreen()
    {
        nameTV.setText(f.getName());

        Picasso.get().load(f.getBackGroundImage()).fit().centerCrop().into(backgroundImage);

        Picasso.get().load(f.getPosterImage()).fit().centerCrop().into(PosterImage);

        ratingBar.setRating(f.getVote());
        DecimalFormat df = new DecimalFormat("0.0");
        voteTV.setText("(" + df.format(f.getVote()) +")");
        genreTV.setText(f.getGenre());
        durationTime.setText(f.getDurationTime());
        filmDetailPagerAdapter = new FilmDetailPagerAdapter(this, f, tabLayout.getSelectedTabPosition());
        pager.setAdapter(filmDetailPagerAdapter);
        pager.setOffscreenPageLimit(3);

        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BookedInformation.getInstance().height = mainLayout.getMeasuredHeight() - topView.getMeasuredHeight() - tabLayout.getMeasuredHeight();
            }
        });
    }
}