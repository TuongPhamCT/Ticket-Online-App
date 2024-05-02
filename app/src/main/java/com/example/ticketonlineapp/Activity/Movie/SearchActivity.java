package com.example.ticketonlineapp.Activity.Movie;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.ListSearchAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
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

    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView listSearchResult;
    private List<Film> listFilm;
    private TextView closeBtn;
    private ListSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_screen);
        searchView = findViewById(R.id.searchField);
        listSearchResult = findViewById(R.id.listSearchResult);
        closeBtn = findViewById(R.id.closeBtn);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(true);
        searchView.requestFocusFromTouch();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboardShortcutsHelper();
                finish();
            }
        });
        listFilm = new ArrayList<Film>();
        listSearchResult.setHasFixedSize(true);
        listSearchResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchView.clearFocus();
        adapter = new ListSearchAdapter(listFilm, SearchActivity.this);
        listSearchResult.setAdapter(adapter);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        ConstraintLayout layoutElement = findViewById(R.id.SearchLayout);

        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void filterList(String s) {
        listFilm = new ArrayList<>();
        List<Film> listResult = new ArrayList<>();
        FirebaseRequests.database.collection("Movies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc : listDocs){
                    Film film = doc.toObject(Film.class);
                    listFilm.add(film);
                }

                for(Film f : listFilm){
                    if(f.getName().toLowerCase().contains(s.toLowerCase()) && !s.equals("")){
                        listResult.add(f);
                    }
                }

                adapter.setFilterList(listResult);
            }
        });


    }
    void addListFilm(){
        FirebaseRequests.database.collection("Movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> listDocs = value.getDocuments();
                for(DocumentSnapshot doc : listDocs){
                    Film film = doc.toObject(Film.class);
                    listFilm.add(film);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            focusedView.clearFocus();
        }
    }
}