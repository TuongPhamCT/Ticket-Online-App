package com.example.ticketonlineapp.Activity.City;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketonlineapp.Activity.Cinema.AddCinemaActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.CinemaOfCityAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.City;
import com.example.ticketonlineapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CinemaOfCity extends AppCompatActivity {
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

    private ListView cinemaLv;
    private ImageView addCinema;
    private TextView nameCity;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_of_city_screen);
        cinemaLv = findViewById(R.id.cinemaLv);
        addCinema = findViewById(R.id.addCinema);
        nameCity = findViewById(R.id.nameCity);
        backBtn = findViewById(R.id.backbutton);
        Intent intent = getIntent();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        City city = intent.getParcelableExtra("city");
        nameCity.setText(city.getName());
        FirebaseRequests.database.collection("Cinema").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Cinema> listCinema  = new ArrayList<>();
                for(DocumentSnapshot doc : value){
                    Cinema cinema = doc.toObject(Cinema.class);
                    if(cinema.getCityID().equals(city.getID())){
                        listCinema.add(cinema);
                    }
                }
                cinemaLv.setAdapter(new CinemaOfCityAdapter(CinemaOfCity.this, R.layout.cinema_of_city_item, listCinema));
            }
        });

        addCinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CinemaOfCity.this, AddCinemaActivity.class);
                intent.putExtra("cityID", city.getID());
                startActivity(intent);
            }
        });
    }
}