package com.example.ticketonlineapp.Activity.Booking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Adapter.UserServiceAdapter;
import com.example.ticketonlineapp.Model.Service;
import com.example.ticketonlineapp.Model.ServiceInTicket;
import com.example.ticketonlineapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {

    CheckNetwork networkChangeListener = new CheckNetwork();
    ListView serviceLV;
    List<Service> listService;
    List<ServiceInTicket> listServiceInTicket;
    TextView totalService;
    ImageButton nextBtn;
    Button backBtn;
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
        setContentView(R.layout.activity_service);
        serviceLV = findViewById(R.id.serviceLv);
        totalService = findViewById(R.id.totalService);
        nextBtn = findViewById(R.id.btnNext);
        backBtn = findViewById(R.id.backbutton);
        listService = new ArrayList<Service>();
        listServiceInTicket = new ArrayList<>();


        FirebaseFirestore.getInstance().collection("Service").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> listDocs = value.getDocuments();
                for(DocumentSnapshot doc : listDocs){
                    Service service = doc.toObject(Service.class);
                    listService.add(service);
                }
                serviceLV.setAdapter(new UserServiceAdapter(ServiceActivity.this, R.layout.service_item, listService, totalService, listServiceInTicket));

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceActivity.this, MovieCheckoutActivity.class);
                Intent getIntent = getIntent();
                Bundle bundle = getIntent.getExtras();
                bundle.putString("total service", totalService.getText().toString());
                intent.putExtras(bundle);
                intent.putExtra("listService", (Serializable) listServiceInTicket);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}