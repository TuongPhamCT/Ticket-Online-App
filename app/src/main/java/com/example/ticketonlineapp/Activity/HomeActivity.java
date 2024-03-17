package com.example.ticketonlineapp.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Activity.Ticket.MyTicketActivity;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ActivityHomeBinding;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
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
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        setContentView(binding.getRoot());
        String[] listType = {"All", "Horror", "Action", "Drama", "War", "Comedy", "Crime"};
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        checkTypeUser();

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w(TAG, "Fetching FCM token failed", task.getException());
//                        return;
//                    }
//
//                    String token = task.getResult();
//                    Log.d(TAG, "FCM token: " + token);
//
//                    FirebaseMessaging.getInstance().subscribeToTopic("all")
//                            .addOnCompleteListener(subscribeTask -> {
//                                if (subscribeTask.isSuccessful()) {
//                                    Log.d(TAG, "Subscribed to topic 'all'");
//                                } else {
//                                    Log.w(TAG, "Subscription to topic 'all' failed", subscribeTask.getException());
//                                }
//                            });
//                });
        binding.searchField.setOnQueryTextFocusChangeListener((view, b) -> {
            if (b) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //TODO: search activity import
//                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });
        try {
            FirebaseRequests.database.collection("Users").
                    document(Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).getUid()).get().
                    addOnSuccessListener(documentSnapshot -> {
                        Users user = documentSnapshot.toObject(Users.class);

                        Picasso.get().load(user.getAvatar()).into(binding.accountImage);
                    });
        } catch (Exception e) {
        }
        binding.accountImage.setOnClickListener(view -> {
            //TODO: account import
//            Intent i = new Intent(getApplicationContext(), AccountActivity.class);
//            startActivity(i);
        });
        binding.AddService.setOnClickListener(v -> {
            //TODO: add service import
//            Intent i = new Intent(HomeActivity.this, AddService.class);
//            startActivity(i);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.listTypeMovie.setLayoutManager(layoutManager);
//        binding.listTypeMovie.setAdapter(new ListTypeAdapter(this, listType));
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
//                case R.id.walletPage:
//                    startActivity(new Intent(HomeActivity.this, MyWalletActivity.class));
//                    overridePendingTransition(0, 0);
//                    break;
                case R.id.ticketPage:
                    startActivity(new Intent(HomeActivity.this, MyTicketActivity.class));
                    overridePendingTransition(0, 0);
                    break;
//                case R.id.ReportPage:
////                    startActivity(new Intent(HomeActivity.this, ReportActivity.class));
////                    overridePendingTransition(0, 0);
////                    break;
//                case R.id.NotificationPage:
//                    startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
//                    overridePendingTransition(0, 0);
//                    break;
            }
            return true;
        });
        binding.DiscountViewAll.setOnClickListener(view -> {
            //TODO: Discount View All import
//            Intent i = new Intent(HomeActivity.this, DiscountViewAll.class);
//            startActivity(i);
        });
        binding.ServiceViewAll.setOnClickListener(view -> {
            //TODO: Service view all import
//            Intent i = new Intent(HomeActivity.this, ServiceViewAll.class);
//            startActivity(i);
        });
//        GetDiscounts();
        binding.cityViewAll.setOnClickListener(view -> {
            //TODO: city view all import
//            Intent i = new Intent(HomeActivity.this, CityViewAllActivity.class);
//            startActivity(i);
        });


        binding.AddDiscount.setOnClickListener(view -> {
            //TODO: Add discount import
//            Intent i = new Intent(HomeActivity.this, AddDiscount.class);
//            startActivity(i);
        });
        //checkAccountType();
        binding.addCity.setOnClickListener(view -> {
            //TODO: add city import
//            Intent intent = new Intent(HomeActivity.this, AddCityActivity.class);
//            startActivity(intent);
        });
        binding.viewAllPlayingBtn.setOnClickListener(view -> {
            //TODO: View all activity import
//            Intent intent = new Intent(HomeActivity.this, ViewAllActivity.class);
//            intent.putExtra("status", "playing");
//            startActivity(intent);
        });
        binding.viewAllComingBtn.setOnClickListener(view -> {
            //TODO: View all activity import
//            Intent intent = new Intent(HomeActivity.this, ViewAllActivity.class);
//            intent.putExtra("status", "coming");
//            startActivity(intent);
        });
        binding.viewAllExpiredBtn.setOnClickListener((View.OnClickListener) view -> {
            //TODO: View all activity import
//            Intent intent = new Intent(HomeActivity.this, ViewAllActivity.class);
//            intent.putExtra("status", "expired");
//            startActivity(intent);
        });

    }

    //get discount
//    void GetDiscounts() {
//
//        List<Discount> Discounts = new ArrayList<>();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        try {
//            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    Users currentUser = documentSnapshot.toObject(Users.class);
//
//                    if (((currentUser.getAccountType().toString()).equals("admin"))) {
//
//                        FirebaseFirestore.getInstance().collection(Discount.CollectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                List<Discount> listDiscounts = new ArrayList<Discount>();
//                                for (DocumentSnapshot doc : value) {
//                                    Discount f = doc.toObject(Discount.class);
//                                    listDiscounts.add(f);
//
//                                }
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
//                                PromotionAdapter promotionAdapter = new PromotionAdapter(listDiscounts, null);
//                                promotionView.setLayoutManager(linearLayoutManager);
//                                promotionView.setAdapter(promotionAdapter);
//
//                            }
//                        });
//                    } else {
//                        try {
//                            CollectionReference PromoRef = db.collection(UserAndDiscount.collectionName);
//                            Query query = PromoRef.whereEqualTo("userID", FirebaseRequest.mAuth.getUid());
//                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    List<String> listDiscountID = new ArrayList<>();
//                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                                        Log.d("onSuccess: ", doc.getId().toString());
//                                        listDiscountID.add(doc.get("discountID").toString());
//                                        //DocumentReference document = FirebaseRequest.database.collection(Discount.CollectionName).document(doc.get("discountID").toString());
//                                    }
//
//
//                                    if (listDiscountID.size() > 0) {
//                                        try {
//                                            Query query2 = db.collection(Discount.CollectionName);
//                                            query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onEvent(@Nullable QuerySnapshot discountvalue, @Nullable FirebaseFirestoreException error) {
//
//                                                    for (DocumentSnapshot doc : discountvalue) {
//                                                        Discount f = doc.toObject(Discount.class);
//                                                        if (listDiscountID.contains(f.getID()))
//                                                            Discounts.add(f);
//                                                    }
//
//                                                    //   LinearLayoutManager VerLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//                                                    // promotionView.setLayoutManager(VerLayoutManager);
//                                                    Intent intent = getIntent();
//                                                    PromotionAdapter promotionAdapter = new PromotionAdapter(Discounts, null);
//                                                    promotionView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
//                                                    promotionView.setAdapter(promotionAdapter);
//
//
//                                                }
//                                            });
//                                        } catch (Exception e) {
//                                        }
//                                    } else {
//                                        promotionView.setAdapter(new PromotionAdapter(new ArrayList<Discount>(), null));
//
//                                        if (Discounts.size() == 0) {
//                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
//                                            params.height = 0;
//
//                                            promotionView.setLayoutParams(params);
//                                        }
//                                        if (Discounts.size() == 1) {
//                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
//                                            params.height = 300;
//                                            promotionView.setLayoutParams(params);
//                                        }
//                                        if (Discounts.size() == 2) {
//                                            ViewGroup.LayoutParams params = promotionView.getLayoutParams();
//                                            params.height = 700;
//                                            promotionView.setLayoutParams(params);
//                                        }
//                                    }
//
//                                }
//                            });
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//        }
//
//    }

    void checkTypeUser() {
        try {
            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                Users currentUser = documentSnapshot.toObject(Users.class);

                if (((currentUser.getAccountType().toString()).equals("admin"))) {
//                    GetServices();
//                    GetCities();
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
                    binding.AddDiscount.setVisibility(View.INVISIBLE);
                    binding.AddService.setVisibility(View.GONE);
                    binding.addCity.setVisibility(View.GONE);
                    binding.ServiceHeader.setVisibility(View.GONE);
                    binding.cityHeader.setVisibility(View.GONE);
                    binding.searchField.setVisibility(View.GONE);
                    binding.cityView.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
        }
    }

//    void GetServices() {
//
//        List<Service> services = new ArrayList<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference ServiceRef = db.collection("Service");
//        ServiceRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    // this method is called when error is not null
//                    // and we get any error
//                    // in this case we are displaying an error message.
//                    Toast.makeText(HomeActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    services.clear();
//                    for (DocumentSnapshot documentSnapshot : value) {
//                        Service newService = documentSnapshot.toObject(Service.class);
//                        services.add(newService);
//                    }
//                    LinearLayoutManager VerLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//                    serviceView.setAdapter(new ServiceAdapter(services));
//                    serviceView.setLayoutManager(VerLayoutManager);
//                    if (services.size() == 0) {
//                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
//                        params.height = 0;
//                        serviceView.setLayoutParams(params);
//                    }
//                    if (services.size() == 1) {
//                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
//                        params.height = 400;
//                        serviceView.setLayoutParams(params);
//                    }
//                    if (services.size() == 2) {
//                        ViewGroup.LayoutParams params = serviceView.getLayoutParams();
//                        params.height = 800;
//                        serviceView.setLayoutParams(params);
//                    }
//                }
//            }
//        });
//    }
//
//    void GetCities() {
//        List<City> cities = new ArrayList<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference cityRef = db.collection("City");
//        cityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    // this method is called when error is not null
//                    // and we get any error
//                    // in this case we are displaying an error message.
//                    Toast.makeText(HomeActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    cities.clear();
//                    for (DocumentSnapshot documentSnapshot : value) {
//                        City newCity = documentSnapshot.toObject(City.class);
//                        cities.add(newCity);
//
//
//                    }
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
//                    cityView.setLayoutManager(linearLayoutManager);
//                    cityView.setAdapter(new CityAdapter(cities, HomeActivity.this));
////                    cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                        @Override
////                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////
////                            Intent intent = new Intent(HomeActivity.this, CinemaOfCity.class);
////                            intent.putExtra("city", cities.get(i));
////                            startActivity(intent);
////
////                        }
////                    });
//                    if (cities.size() == 0) {
//                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
//                        params.height = 0;
//                        cityView.setLayoutParams(params);
//                    }
//                    if (cities.size() == 1) {
//                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
//                        params.height = 400;
//                        cityView.setLayoutParams(params);
//                    }
//                    if (cities.size() == 2) {
//                        ViewGroup.LayoutParams params = cityView.getLayoutParams();
//                        params.height = 800;
//                        cityView.setLayoutParams(params);
//                    }
//                }
//            }
//        });
//    }

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