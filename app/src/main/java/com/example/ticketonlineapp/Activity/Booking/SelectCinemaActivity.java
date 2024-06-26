package com.example.ticketonlineapp.Activity.Booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.CinemaNameAdapter;
import com.example.ticketonlineapp.Adapter.TimeBookedAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.City;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.ShowTime;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectCinemaActivity extends AppCompatActivity {

    CheckNetwork networkChangeListener = new CheckNetwork();
    private List<String> listCity;
    private AutoCompleteTextView countryAutoTv;
    private RecyclerView dayRecycleView;
    private FirebaseFirestore firestore;
    private ListView cinemaLv;
    private ImageButton nextBtn;
    private TextView nameFilmTv;
    private Film selectedFilm;
    private Button backBtn;
    protected static String binhdd;
    private String monthName;
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cinema);
        backBtn = (Button) findViewById(R.id.backbutton);
        firestore = FirebaseFirestore.getInstance();
        countryAutoTv = (AutoCompleteTextView) findViewById(R.id.countryAutoTv);
        listCity = new ArrayList<String>();
        updateShowtimeValid();
        loadListCity();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        dayRecycleView = (RecyclerView) findViewById(R.id.dayRecycleView);
        Calendar calendar = Calendar.getInstance();

//        Timestamp time = new Timestamp(calendar.getTime());
//        Log.e("ff", (time + 1).toDate().toString());
        String[]dateName={"Sat", "Sun", "Mon","Tue","Wed", "Thu", "Fri", };
        List<String> listDate = new ArrayList<String>();
        List<String> listTime = new ArrayList<String>();
        //Toast.makeText(this, String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) , Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        selectedFilm = intent.getParcelableExtra("selectedFilm");
        if(selectedFilm == null) {
            selectedFilm = new Film("1","1","1","1","1", 2.5F,"1","1","1",Timestamp.now() , Timestamp.now() , new ArrayList<>());
        }
        int countDate = calendar.get(Calendar.DAY_OF_WEEK);
        int countTime = calendar.get(Calendar.DATE);
        int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i = 0; i < 13; i++){
            if (countDate > 6) countDate = countDate - 7;
            if (countTime > dayOfMonth) {
                countTime = 1;

            }
            listDate.add(dateName[countDate]);
            listTime.add(String.valueOf(countTime));
            countDate++;
            countTime++;
        }
        cinemaLv = (ListView) findViewById(R.id.cinemaLv);
        TimeBookedAdapter timeBookedAdapter = new TimeBookedAdapter(listDate, listTime,null, null, null, cinemaLv, SelectCinemaActivity.this);
        dayRecycleView.setAdapter(new TimeBookedAdapter(listDate, listTime, null,null, null, cinemaLv, SelectCinemaActivity.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dayRecycleView.setLayoutManager(layoutManager);

        // String[] listCinemaName = {"Central Park CGV", "FX Sudirman XXI", "Kelapa Gading IMAX"};

        nameFilmTv = (TextView) findViewById(R.id.nameFilmtv);
        nameFilmTv.setText(selectedFilm.getName());

        nextBtn = (ImageButton) findViewById(R.id.btnNext);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!BookedInformation.getInstance().isTimeSelected|| !BookedInformation.getInstance().isDateSelected || !BookedInformation.getInstance().isCitySelected ){
                    Toast.makeText(SelectCinemaActivity.this, "Please choose time and date!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(SelectCinemaActivity.this, ChooseSeatActivity.class);
                    intent.putExtra("selectedFilm", selectedFilm);
                    intent.putExtra("cinema", (Parcelable) BookedInformation.getInstance().cinema);
                    intent.putExtra("dateBooked", BookedInformation.getInstance().dateBooked);
                    intent.putExtra("timeBooked", BookedInformation.getInstance().timeBooked);
                    startActivity(intent);
                }

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookedInformation.getInstance().isDateSelected = false;
                BookedInformation.getInstance().isTimeSelected = false;
                BookedInformation.getInstance().isCitySelected = false;
                BookedInformation.getInstance().timeBooked = "";
                BookedInformation.getInstance().prevPosition = -1;
                finish();
            }
        });
    }

    void loadListCity(){
        firestore.collection("City").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        List<City> list = new ArrayList<City>();
                                                                        if (!queryDocumentSnapshots.isEmpty()) {
                                                                            List<DocumentSnapshot> listDoc = queryDocumentSnapshots.getDocuments();
                                                                            for (DocumentSnapshot doc : listDoc) {
                                                                                list.add(doc.toObject(City.class));
                                                                                listCity.add(String.valueOf(doc.get("Name")));

                                                                            }
                                                                            countryAutoTv.setAdapter(new ArrayAdapter<String>(SelectCinemaActivity.this, R.layout.country_item, listCity));
                                                                            countryAutoTv.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_background_1)));

                                                                            countryAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                                @Override
                                                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                                                    BookedInformation.getInstance().isCitySelected = true;

                                                                                    List<Cinema> listCinema = new ArrayList<Cinema>();
                                                                                    FirebaseRequests.database.collection("Cinema").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                            List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();
                                                                                            for(DocumentSnapshot doc : listDocs){
                                                                                                if(doc.get("CityID").equals(list.get(i).getID())){
                                                                                                    Cinema cinemaItem = doc.toObject(Cinema.class);
                                                                                                    listCinema.add(cinemaItem);

                                                                                                }
                                                                                            }
                                                                                            CinemaNameAdapter CinemaNameAdapter = new CinemaNameAdapter(SelectCinemaActivity.this, R.layout.cinema_booked_item,listCinema, selectedFilm);
                                                                                            cinemaLv.setAdapter(CinemaNameAdapter);


                                                                                            //Helper.getListViewSize(cinemaLv, BookedActivity.this);




                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
        );
    }

    @Override
    public void onBackPressed() {
        BookedInformation.getInstance().isDateSelected = false;
        BookedInformation.getInstance().isTimeSelected = false;
        BookedInformation.getInstance().isCitySelected = false;
        BookedInformation.getInstance().timeBooked = "";
        BookedInformation.getInstance().prevPosition = -1;

        super.onBackPressed();
    }

    void updateShowtimeValid(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        FirebaseRequests.database.collection("Showtime").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots){
                    ShowTime showTime = doc.toObject(ShowTime.class);
                    Timestamp timeBook = showTime.getTimeBooked();
                    if(timeBook.toDate().before(date)){
                        FirebaseRequests.database.collection("Showtime").document(doc.getId()).delete();
                    }
                }
            }
        });


    }
}