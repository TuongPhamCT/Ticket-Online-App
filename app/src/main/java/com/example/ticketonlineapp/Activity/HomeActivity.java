package com.example.ticketonlineapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticketonlineapp.Activity.City.AddCityActivity;
import com.example.ticketonlineapp.Activity.City.CityViewAllActivity;
import com.example.ticketonlineapp.Activity.Discount.AddDiscountActivity;
import com.example.ticketonlineapp.Activity.Discount.ViewAllDiscountActivity;
import com.example.ticketonlineapp.Activity.Movie.SearchActivity;
import com.example.ticketonlineapp.Activity.Movie.ViewAllMovieActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Activity.Notification.NotificationActivity;
import com.example.ticketonlineapp.Activity.Report.ReportActivity;
import com.example.ticketonlineapp.Activity.Service.AddServiceActivity;
import com.example.ticketonlineapp.Activity.Service.ServiceViewAll;
import com.example.ticketonlineapp.Activity.Ticket.MyTicketActivity;
import com.example.ticketonlineapp.Activity.User.AccountActivity;
import com.example.ticketonlineapp.Activity.Wallet.MyWalletActivity;
import com.example.ticketonlineapp.Adapter.CityAdapter;
import com.example.ticketonlineapp.Adapter.ListTypeAdapter;
import com.example.ticketonlineapp.Adapter.PromotionAdapter;
import com.example.ticketonlineapp.Adapter.ServiceAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.City;
import com.example.ticketonlineapp.Model.Discount;
import com.example.ticketonlineapp.Model.Service;
import com.example.ticketonlineapp.Model.UserAndDiscount;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ActivityHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


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

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    private RecyclerView typeListView;
    private RecyclerView posterRecyclerView;
    private RecyclerView promotionView;
    private SearchView searchView;
    private ViewPager2 typeMoviePage;
    private BottomNavigationView bottomNavigationView;
    private TabLayout typeMovieLayout;
    private BottomNavigationView bottomNavigation;
    private ActivityHomeBinding binding;
    private ImageView accountImage;
    private ImageView addDiscount;
    private TextView viewAllPlayingBtn;
    private TextView viewAllComingBtn;
    private TextView viewExpiredBtn;
    private ImageView addService;
    private RecyclerView serviceView;
    private TextView viewAllCity;
    private ImageView addCity;
    private RecyclerView cityView;

    ConstraintLayout serviceHeader;
    ConstraintLayout cityHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        setContentView(binding.getRoot());
        String[] listType = {"All", "Horror", "Action", "Drama", "War", "Comedy", "Crime"};
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        accountImage = findViewById(R.id.accountImage);
        addDiscount = findViewById(R.id.AddDiscount);
        addDiscount = findViewById(R.id.AddDiscount);
        viewAllPlayingBtn = findViewById(R.id.viewAllPlayingBtn);
        viewAllComingBtn = findViewById(R.id.viewAllComingBtn);
        viewExpiredBtn=findViewById(R.id.viewAllExpiredBtn);
        promotionView = findViewById(R.id.promotionView);
        searchView=findViewById(R.id.searchField);
        serviceView = findViewById(R.id.ServiceView);
        addService = findViewById(R.id.AddService);
        addCity = findViewById(R.id.addCity);
        cityView = findViewById(R.id.cityView);
        viewAllCity = findViewById(R.id.cityViewAll);
        cityHeader = findViewById(R.id.cityHeader);
        serviceHeader = findViewById(R.id.ServiceHeader);

        checkTypeUser();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                    {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM token: " + token);

                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                            .addOnCompleteListener(subscribeTask -> {
                                if (subscribeTask.isSuccessful()) {
                                    Log.d(TAG, "Subscribed to topic 'all'");
                                } else {
                                    Log.w(TAG, "Subscription to topic 'all' failed", subscribeTask.getException());
                                }
                            });
                });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                }
            }
        });
        try{
            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users user = documentSnapshot.toObject(Users.class);
                    Picasso.get().load(user.getAvatar()).into(accountImage);
                }
            });
        }
        catch (Exception e)
        {}
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(i);
            }
        });
        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AddServiceActivity.class);
                startActivity(i);
            }
        });
        typeListView = (RecyclerView) findViewById(R.id.listTypeMovie);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        typeListView.setLayoutManager(layoutManager);
        typeListView.setAdapter(new ListTypeAdapter(this, listType));
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.walletPage:
                    startActivity(new Intent(HomeActivity.this, MyWalletActivity.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.ticketPage:
                    startActivity(new Intent(HomeActivity.this, MyTicketActivity.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.ReportPage:
                    startActivity(new Intent(HomeActivity.this, ReportActivity.class));
                    overridePendingTransition(0, 0);
                    break;
                case R.id.NotificationPage:
                    startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });
        binding.DiscountViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ViewAllDiscountActivity.class);
                startActivity(i);
            }
        });
        binding.ServiceViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ServiceViewAll.class);
                startActivity(i);
            }
        });
        GetDiscounts();
        viewAllCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, CityViewAllActivity.class);
                startActivity(i);
            }
        });



        addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, AddDiscountActivity.class);
                startActivity(i);
            }
        });
        //checkAccountType();
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddCityActivity.class);
                startActivity(intent);
            }
        });
        viewAllPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ViewAllMovieActivity.class);
                intent.putExtra("status", "playing");
                startActivity(intent);
            }
        });
        viewAllComingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ViewAllMovieActivity.class);
                intent.putExtra("status", "coming");
                startActivity(intent);
            }
        });
        viewExpiredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ViewAllMovieActivity.class);
                intent.putExtra("status", "expired");
                startActivity(intent);
            }
        });
    }

    void GetDiscounts() {
        List<Discount> Discounts = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try{
            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users currentUser = documentSnapshot.toObject(Users.class);
                    if(((currentUser.getAccountType().toString()).equals("admin"))){
                        FirebaseFirestore.getInstance().collection(Discount.CollectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                List<Discount> listDiscounts = new ArrayList<Discount>();
                                for (DocumentSnapshot doc : value) {
                                    Discount f = doc.toObject(Discount.class);
                                    listDiscounts.add(f);
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                                PromotionAdapter promotionAdapter = new PromotionAdapter(listDiscounts, null);
                                promotionView.setLayoutManager(linearLayoutManager);
                                promotionView.setAdapter(promotionAdapter);
                            }
                        });
                    } else {
                        try{
                            CollectionReference PromoRef = db.collection(UserAndDiscount.collectionName);
                            Query query = PromoRef.whereEqualTo("userID", FirebaseRequests.mAuth.getUid());
                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<String> listDiscountID = new ArrayList<>();
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        Log.d( "onSuccess: ", doc.getId().toString());
                                        listDiscountID.add(doc.get("discountID").toString());
                                    }
                                    if (listDiscountID.size() > 0) {
                                        try {
                                            Query query2 = db.collection(Discount.CollectionName);
                                            query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot discountvalue, @Nullable FirebaseFirestoreException error) {

                                                    for (DocumentSnapshot doc : discountvalue) {
                                                        Discount f = doc.toObject(Discount.class);
                                                        if(listDiscountID.contains(f.getID()))
                                                            Discounts.add(f);
                                                    }
                                                    Intent intent = getIntent();
                                                    PromotionAdapter promotionAdapter = new PromotionAdapter(Discounts, null);
                                                    promotionView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                                                    promotionView.setAdapter(promotionAdapter);
                                                }
                                            });
                                        }
                                        catch (Exception e){}
                                    } else
                                    {
                                        promotionView.setAdapter(new PromotionAdapter(new ArrayList<Discount>(), null));

                                        if (Discounts.size() == 0) {
                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
                                            params.height = 0;
                                            promotionView.setLayoutParams(params);
                                        }
                                        if (Discounts.size() == 1) {
                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
                                            params.height = 300;
                                            promotionView.setLayoutParams(params);
                                        }
                                        if (Discounts.size() == 2) {
                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
                                            params.height = 700;
                                            promotionView.setLayoutParams(params);
                                        }
                                    }

                                }
                            });
                        }
                        catch (Exception e){}
                    }
                }
            });

        }
        catch (Exception e){}

    }

    void checkTypeUser() {
        try {
            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users currentUser = documentSnapshot.toObject(Users.class);

                    if (((currentUser.getAccountType().toString()).equals("admin"))) {
                        GetServices();
                        GetCities();
                        Menu menu = binding.bottomNavigation.getMenu();
                        MenuItem ReportPage = menu.findItem(R.id.ReportPage);
                        MenuItem WalletPage = menu.findItem(R.id.walletPage);
                        MenuItem TicketPage = menu.findItem(R.id.ticketPage);
                        TicketPage.setVisible(false);
                        WalletPage.setVisible(false);
                        ReportPage.setVisible(true);
                    } else {
                        ViewGroup.LayoutParams params = binding.AddDiscount.getLayoutParams();
                        params.height = 0;
                        binding.AddDiscount.setLayoutParams(params);
                        addDiscount.setVisibility(View.INVISIBLE);
                        addService.setVisibility(View.GONE);
                        addCity.setVisibility(View.GONE);
                        serviceHeader.setVisibility(View.GONE);
                        cityHeader.setVisibility(View.GONE);
                        serviceView.setVisibility(View.GONE);
                        cityView.setVisibility(View.GONE);
                    }
                }
            });
        }
        catch (Exception e){}
    }

    void GetServices() {
        List<Service> services = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ServiceRef = db.collection("Service");
        ServiceRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // this method is called when error is not null
                    // and we get any error
                    // in this case we are displaying an error message.
                    Toast.makeText(HomeActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    services.clear();
                    for (DocumentSnapshot documentSnapshot : value) {
                        Service newService = documentSnapshot.toObject(Service.class);
                        services.add(newService);
                    }
                    LinearLayoutManager VerLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    serviceView.setAdapter(new ServiceAdapter(services));
                    serviceView.setLayoutManager(VerLayoutManager);
                    if (services.size() == 0) {
                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
                        params.height = 0;
                        serviceView.setLayoutParams(params);
                    }
                    if (services.size() == 1) {
                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
                        params.height = 400;
                        serviceView.setLayoutParams(params);
                    }
                    if (services.size() == 2) {
                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
                        params.height = 800;
                        serviceView.setLayoutParams(params);
                    }
                }
            }
        });
    }

    void GetCities() {
        List<City> cities = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cityRef = db.collection("City");
        cityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(HomeActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    cities.clear();
                    for (DocumentSnapshot documentSnapshot : value) {
                        City newCity = documentSnapshot.toObject(City.class);
                        cities.add(newCity);


                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                    cityView.setLayoutManager(linearLayoutManager);
                    cityView.setAdapter(new CityAdapter(cities, HomeActivity.this));
                    if (cities.size() == 0) {
                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
                        params.height = 0;
                        cityView.setLayoutParams(params);
                    }
                    if (cities.size() == 1) {
                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
                        params.height = 400;
                        cityView.setLayoutParams(params);
                    }
                    if (cities.size() == 2) {
                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
                        params.height = 800;
                        cityView.setLayoutParams(params);
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dismissKeyboard();
        clearFocus();
    }

    private void clearFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}