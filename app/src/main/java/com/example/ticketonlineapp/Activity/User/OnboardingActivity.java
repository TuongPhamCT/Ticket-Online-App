package com.example.ticketonlineapp.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.ticketonlineapp.R.string.CheckUsed;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.R;

public class OnboardingActivity extends AppCompatActivity {

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
        setContentView(R.layout.onboarding_screen);

        SetSharedReference();

        Button getStartedBtn = findViewById(R.id.getStartedBtn);
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });

        TextView signIn = findViewById(R.id.SignInBtn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(i);
            }
        });
    }
    void SetSharedReference()
    {
        SharedPreferences sharedPref = getSharedPreferences("shared_prefs",0);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.clear();
        editor.putBoolean(String.valueOf(CheckUsed), true);
        editor.apply();
        editor.commit();
    }
}