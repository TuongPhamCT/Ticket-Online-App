package com.example.ticketonlineapp.Activity.Booking;
import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Activity.Ticket.MyTicketActivity;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ActivitySuccessCheckoutBinding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuccessCheckoutActivity extends AppCompatActivity {
    ActivitySuccessCheckoutBinding binding;
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
        binding = ActivitySuccessCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnToMyTicket.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MyTicketActivity.class);
            startActivity(i);
        });
        binding.txtToHome.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        });
    }
}