package com.example.ticketonlineapp.Activity.User;

import android.os.Bundle;

import com.example.ticketonlineapp.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.splash_screen);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.splash_logo);
    }
}