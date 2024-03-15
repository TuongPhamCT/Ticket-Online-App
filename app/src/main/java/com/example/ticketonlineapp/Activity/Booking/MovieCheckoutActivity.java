package com.example.ticketonlineapp.Activity.Booking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ticketonlineapp.R;

public class MovieCheckoutActivity extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_checkout);
        backBtn = findViewById(R.id.btnBack);

        backBtn.setOnClickListener(view -> finish());
    }
}