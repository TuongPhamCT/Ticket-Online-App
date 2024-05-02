package com.example.ticketonlineapp.Activity.City;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.ticketonlineapp.Activity.Helper.NlpUtils;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.CityViewAllAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.City;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.CityViewAllScreenBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CityViewAllActivity extends AppCompatActivity {
    CityViewAllScreenBinding binding;
    private ListView cityView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CityViewAllScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cityView = (ListView) findViewById(R.id.cityView);
        // below line is to call set on query text listener method.
        binding.searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        binding.BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("City").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<City> cities = new ArrayList<>();
                for (DocumentSnapshot doc : value) {
                    City city = doc.toObject(City.class);
                    cities.add(city);
                }
                cityView.setAdapter(new CityViewAllAdapter(CityViewAllActivity.this, R.layout.city_item, cities));
                cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent = new Intent(CityViewAllActivity.this, CinemaOfCity.class);
                        intent.putExtra("city", cities.get(i));
                        startActivity(intent);
                    }
                });
            }
        });

        binding.AddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CityViewAllActivity.this, AddCityActivity.class);
                startActivity(i);
            }
        });
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<City> filteredlist = new ArrayList<City>();
        FirebaseRequests.database.collection("City").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<City> list = new ArrayList<City>();
                for (DocumentSnapshot doc : value) {
                    list.add(doc.toObject(City.class));
                }
                for (City item : list) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (NlpUtils.removeAccent(item.getName().toLowerCase()).contains(NlpUtils.removeAccent(text.toLowerCase()))) {
                        // if the item is matched we are
                        // adding it to our filtered list.
                        filteredlist.add(item);
                    }
                }
                cityView.setAdapter(new CityViewAllAdapter(CityViewAllActivity.this, R.layout.city_item, filteredlist));
            }
        });
    }
}