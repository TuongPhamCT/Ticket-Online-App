package com.example.ticketonlineapp.Activity.User;

import static com.example.ticketonlineapp.Database.FirebaseRequests.mAuth;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.splash_screen);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.splash_logo);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            getUser(mAuth.getCurrentUser().getUid());
        }
        try {
            Map config = new HashMap();
            config.put("cloud_name", "dwvdph8s5");
            config.put("secure", true);
            config.put( "api_key", "437937192235927");
            config.put( "api_secret", "YG9kLh6H5Yo7telUqUnmW2cacPM");
            MediaManager.init(this, config);
        }
        catch (Exception e) {}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                SharedPreferences sharedPref = getSharedPreferences("shared_prefs",0);
                boolean IsUsed = sharedPref.getBoolean(String.valueOf(R.string.CheckUsed),false);
                if(IsUsed) {
                    if (mAuth.getCurrentUser() != null) {
                        i = new Intent(SplashActivity.this, HomeActivity.class);
                    }
                    else
                        i = new Intent(SplashActivity.this, SignInActivity.class);
                }
                else
                    i = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(i);
                finish();
            }

        }, 5*1000); // wait for 5 seconds
    }

    Integer getUser(String id)
    {
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("Users").document(id);
            Log.d("ID",id);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) { if (error != null) {
                    return;
                }
                    if (snapshot != null && snapshot.exists()) {
                        Users.currentUser = snapshot.toObject(Users.class);
                        Log.d("Email",Users.currentUser.getEmail());
                    }
                }
            });
            return 0;
        }
    }
}