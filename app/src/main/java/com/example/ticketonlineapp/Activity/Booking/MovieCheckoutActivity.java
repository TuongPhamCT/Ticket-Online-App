package com.example.ticketonlineapp.Activity.Booking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketonlineapp.Activity.Discount.ViewAllDiscountActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.MovieCheckoutAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.CheckoutFilmModel;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Ticket;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ActivityMovieCheckoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MovieCheckoutActivity extends AppCompatActivity {
    ActivityMovieCheckoutBinding binding;
    CheckNetwork checkNetwork = new CheckNetwork();
    private String idDiscount;
    Film film;
    double total;
    String date;
    String timeBooked;
    Cinema cinema;
    String[] listDate;
    String listSeat;
    String price;

    //    List<ServiceInTicket> listService;
    MovieCheckoutAdapter adapter;
    List<String> seats;
    ArrayList<CheckoutFilmModel> movie = new ArrayList<>();
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result != null && result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {

                    double newTotal = result.getData().getDoubleExtra("total", 0);
                    String name = result.getData().getStringExtra("nameDiscount");
                    idDiscount = result.getData().getStringExtra("idDiscount");
                    binding.selectTv.setVisibility(View.GONE);
                    binding.selectVoucherBtn.setText(name);
                    binding.TotalValue.setText(Math.round(newTotal) + " VNĐ");
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieCheckoutBinding.inflate(getLayoutInflater());
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        setContentView(binding.getRoot());
        binding.selectVoucherBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MovieCheckoutActivity.this, ViewAllDiscountActivity.class);
            intent.putExtra("total", total);
            startForResult.launch(intent);
        });

        FirebaseRequests.database.collection("Users").
                document(FirebaseRequests.mAuth.getUid()).
                get().addOnCompleteListener(task -> {
                    DocumentSnapshot doc = task.getResult();
                    binding.YourWalletValue.setText(String.valueOf(doc.get("Wallet")));
                });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        listService =(List<ServiceInTicket>) bundle.getSerializable("listService");
        addServiceToLayout();

        film = bundle.getParcelable("selectedFilm");
        cinema = bundle.getParcelable("cinema");
        binding.CinemaValue.setText(cinema.getName());
        date = bundle.getString("dateBooked");
        timeBooked = bundle.getString("timeBooked");
        listDate = date.split("\n");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
        String month_name = month_date.format(cal.getTime());
        binding.DateTimeValue.setText(listDate[0] + " " + month_name + " " + listDate[1] + ", " + timeBooked);
        Random rdn = new Random();


        FirebaseRequests.database.collection("Ticket").
                get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();
                    int id = 0;
                    for (DocumentSnapshot doc : listDocs) {
                        do {
                            id = rdn.nextInt(90000000) + 10000000;
                        }
                        while (
                                id == Integer.parseInt(String.valueOf(doc.get("idorder")))
                        );
                    }
                    binding.IDOrderValue.setText(String.valueOf(id));
                });
        int id;
        seats = bundle.getStringArrayList("seats");
        listSeat = "";
        for (int i = 0; i < seats.size(); i++) {
            if (i != seats.size() - 1) {
                listSeat += seats.get(i) + ", ";
            } else {
                listSeat += seats.get(i);

            }

        }
        price = bundle.getString("price");
        total = Integer.parseInt(price) + Integer.parseInt(bundle.getString("total service"));
        //price = intent.getStringExtra("price");
        int initPrice = Integer.parseInt(price) / seats.size();
        binding.PriceValue.setText(initPrice + " x " + seats.size());
        binding.SeatNumberValue.setText(listSeat);
        binding.TotalValue.setText(Math.round(total) + " VNĐ");
        DecimalFormat df = new DecimalFormat("0.0");
        movie.add(new CheckoutFilmModel(film.getName(), film.getVote(), film.getGenre(), film.getDurationTime(), film.getPosterImage()));
        adapter = new MovieCheckoutAdapter(getApplicationContext(), R.layout.checkout_movie_item, movie);
        binding.movieInfoView.setAdapter(adapter);
        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnCheckout.setOnClickListener(view -> FirebaseRequests.database.collection("Users").
                document(FirebaseRequests.mAuth.getUid()).get().addOnCompleteListener(task -> {
                    DocumentSnapshot doc = task.getResult();
                    int totalWallet = Integer.parseInt(String.valueOf(doc.get("Wallet")));
                    if (Math.round(total) <= totalWallet) {
                        totalWallet -= (int) Math.round(total);
                        if (idDiscount != null) {
                            FirebaseRequests.database.collection("UserAndDiscount").
                                    whereEqualTo("discountID", idDiscount).
                                    whereEqualTo("userID", FirebaseRequests.mAuth.getUid()).
                                    addSnapshotListener((value, error) -> {
                                        for (DocumentSnapshot doc1 : value) {
                                            doc1.getReference().delete();
                                        }
                                    });

                        }
                        FirebaseRequests.database.collection("Users").
                                document(FirebaseRequests.mAuth.getUid()).
                                update("Wallet", totalWallet);
                        FirebaseRequests.database.collection("Showtime").get().
                                addOnSuccessListener(queryDocumentSnapshots -> {
                                    List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot doc12 : listDocs) {
                                        Timestamp time = doc12.getTimestamp("timeBooked");
                                        DateFormat dateFormat = new SimpleDateFormat("EEE\nd", Locale.ENGLISH);
                                        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                                        DateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
                                        String month_name1 = monthFormat.format(time.toDate());
                                        //  String timeBook = timeBooked + ", " + listDate[0] + " "+month_name+" " + listDate[1];

                                        // Log.e("f",doc.get("nameCinema").toString() + " " +cinemaName + doc.get("nameFilm").toString()+ " " +film.getName() +timeFormat.format(time.toDate()) + " " + timeBooked + dateFormat.format(time.toDate()) + date  );
                                        if (doc12.get("cinemaID").toString().
                                                equals(cinema.getCinemaID()) && doc12.get("filmID").toString().
                                                equals(film.getId()) && timeFormat.format(time.toDate()).
                                                equals(timeBooked) && dateFormat.format(time.toDate()).
                                                equals(date)) {
                                            List<String> listSeats = (List<String>) doc12.get("bookedSeat");

                                            listSeats.addAll(seats);
                                            FirebaseRequests.database.collection("Showtime").
                                                    document(doc12.getId()).
                                                    update("bookedSeat", listSeats);
                                            Ticket ticket;
                                            DocumentReference ticketDoc = FirebaseRequests.database.
                                                    collection("Ticket").document();
                                            if (idDiscount != null) {
                                                ticket = new Ticket(time, cinema.getCinemaID(), listSeat,
                                                        String.valueOf(Math.round(total)) + " VNĐ",
                                                        binding.IDOrderValue.getText().toString(), film.getId(),
                                                        FirebaseRequests.mAuth.getUid(), idDiscount,
                                                        ticketDoc.getId());
                                            } else ticket = new Ticket(time, cinema.getCinemaID(),
                                                    listSeat, Math.round(total) + " VNĐ",
                                                    binding.IDOrderValue.getText().toString(), film.getId(),
                                                    FirebaseRequests.mAuth.getUid(), "",
                                                    ticketDoc.getId());

                                            ticketDoc.set(ticket);
                                            addServiceToDb(ticketDoc);


                                        }
                                    }
                                });
                        BookedInformation.getInstance().isCitySelected = false;
                        BookedInformation.getInstance().isDateSelected = false;
                        BookedInformation.getInstance().timeBooked = "";
                        Intent i = new Intent(getApplicationContext(), SuccessCheckoutActivity.class);
                        startActivity(i);
                    } else
                        Toast.makeText(MovieCheckoutActivity.this, "Your wallet is not enough!", Toast.LENGTH_SHORT).show();
                }));
    }

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

    void addServiceToDb(DocumentReference doc) {
//        CollectionReference serviceRef = doc.collection("ServiceInTicket");
//        for(ServiceInTicket service : listService){
//            serviceRef.document().set(service);
//        }
    }

    void addServiceToLayout() {
//        for(ServiceInTicket serviceInTicket : listService){
//            View con = LayoutInflater.from(this).inflate(R.layout.service_ticket_item, null);
//            TextView name = con.findViewById(R.id.name);
//            TextView value = con.findViewById(R.id.value);
//            FirebaseRequest.database.collection("Service").document(serviceInTicket.getServiceID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    Service service = documentSnapshot.toObject(Service.class);
//                    name.setText(service.getDetail());
//                    value.setText(service.getPrice()  + " x " + String.valueOf(serviceInTicket.getCount()));
//
//                }
//            });
//
//
//            checkoutInfo.addView(con);
//        }
    }
}