package com.example.ticketonlineapp.Activity.Discount;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.DiscountAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Discount;
import com.example.ticketonlineapp.Model.UserAndDiscount;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ViewAllDiscountScreenBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllDiscountActivity extends AppCompatActivity {
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
    ViewAllDiscountScreenBinding binding;
    private ListView promotionView;
    List<Discount> Discounts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ViewAllDiscountScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        promotionView =(ListView) findViewById(R.id.promotionView);
        ConstraintLayout layoutElement = findViewById(R.id.SearchLayout);

        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        binding.searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

        if(Users.currentUser!=null)
        {
            FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users currentUser = documentSnapshot.toObject(Users.class);
                    if(((currentUser.getAccountType().toString()).equals("admin"))){
                        FirebaseFirestore.getInstance().collection(Discount.CollectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                List<Discount> listDiscounts = new ArrayList<Discount>();
                                Discounts = new ArrayList<>();
                                for(DocumentSnapshot doc : value){
                                    Discount f = doc.toObject(Discount.class);
                                    Discounts.add(f);
                                }
                                DiscountAdapter discountAdapter = new DiscountAdapter(ViewAllDiscountActivity.this,R.layout.discount_item,Discounts);
                                promotionView.setAdapter(discountAdapter);

                            }
                        });
                    } else{
                        CollectionReference PromoRef = db.collection(UserAndDiscount.collectionName);
                        Query query = PromoRef.whereEqualTo("userID", FirebaseRequests.mAuth.getUid());
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<String> listDiscountID = new ArrayList<>();
                                for(DocumentSnapshot doc : queryDocumentSnapshots){
                                    listDiscountID.add(doc.get("discountID").toString());
                                }
                                if(listDiscountID.size() > 0){
                                    Query query2 = db.collection(Discount.CollectionName).whereIn("ID", listDiscountID);
                                    query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            for(DocumentSnapshot doc : value){
                                                Discount f = doc.toObject(Discount.class);
                                                Discounts.add(f);
                                            }

                                            Intent intent = getIntent();
                                            DiscountAdapter discountAdapter = new DiscountAdapter(ViewAllDiscountActivity.this,R.layout.discount_item,Discounts);
                                            promotionView.setAdapter(discountAdapter);
                                            Double totalBook = intent.getDoubleExtra("total", 0);
                                            if( totalBook != 0){
                                                promotionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        double finalTotal = totalBook * (1 - Discounts.get(i).getDiscountRate() /100);
                                                        intent.putExtra("total", finalTotal);
                                                        intent.putExtra("nameDiscount", Discounts.get(i).getName());
                                                        intent.putExtra("idDiscount", Discounts.get(i).getID());
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else   promotionView.setAdapter(new DiscountAdapter(ViewAllDiscountActivity.this,R.layout.discount_item,new ArrayList<Discount>()));
                            }
                        });
                    }
                }
            });
        }


        binding.AddDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewAllDiscountActivity.this, AddDiscountActivity.class);
                startActivity(i);
            }
        });
        checkAccountType();
    }
    void checkAccountType()
    {
        try{
            Log.d("account type", Users.currentUser.getAccountType());
            if(Users.currentUser!=null)
                if((!(Users.currentUser.getAccountType().toString()).equals("admin")))
                {
                    ViewGroup.LayoutParams params = binding.AddDiscount.getLayoutParams();
                    params.height = 0;
                    binding.AddDiscount.setLayoutParams(params);
                    binding.AddDiscount.setVisibility(View.INVISIBLE);
                }
        }
        catch (Exception e)
        {
            ViewGroup.LayoutParams params = binding.AddDiscount.getLayoutParams();
            params.height = 0;
            binding.AddDiscount.setLayoutParams(params);
            binding.AddDiscount.setVisibility(View.INVISIBLE);
        }
    }
    private void filter(String text) {
        ArrayList<Discount> filteredlist = new ArrayList<Discount>();
        for (Discount item : Discounts) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        promotionView.setAdapter(new DiscountAdapter(ViewAllDiscountActivity.this,R.layout.discount_item, filteredlist));
    }
}