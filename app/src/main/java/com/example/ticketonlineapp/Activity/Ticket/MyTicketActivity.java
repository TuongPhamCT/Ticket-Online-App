package com.example.ticketonlineapp.Activity.Ticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyTicketActivity extends AppCompatActivity {
    ListView listView;
    FirebaseFirestore firestore;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ticket_screen);

        Button newTicket = (Button) findViewById(R.id.buttonNewsTicket);
        Button expiredTicket = (Button) findViewById(R.id.buttonExpiredTicket);
        Button allTicket = (Button) findViewById(R.id.buttonAllTicket);

        allTicket.setText("All");
        allTicket.setSelected(true);


        allTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTicket.setSelected(true);
                newTicket.setSelected(false);
                expiredTicket.setSelected(false);
                allTicket.setText("All");
                newTicket.setText(null);
                expiredTicket.setText(null);


            }
        });

        newTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTicket.setSelected(false);
                newTicket.setSelected(true);
                expiredTicket.setSelected(false);
                allTicket.setText(null);
                newTicket.setText("New");
                expiredTicket.setText(null);


            }
        });

        expiredTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTicket.setSelected(false);
                newTicket.setSelected(false);
                expiredTicket.setSelected(true);
                allTicket.setText(null);
                newTicket.setText(null);
                expiredTicket.setText("Expired");


            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.getMenu().findItem(R.id.ticketPage).setChecked(true);
        try{if (Users.currentUser != null)
            if (((Users.currentUser.getAccountType().toString()).equals("admin"))) {
                Menu menu = bottomNavigationView.getMenu();
                MenuItem ReportPage = menu.findItem(R.id.ReportPage);
                MenuItem WalletPage = menu.findItem(R.id.walletPage);
                WalletPage.setVisible(false);
                ReportPage.setVisible(true);
            }
        }
        catch (Exception e){}
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homePage:
                    startActivity(new Intent(MyTicketActivity.this, HomeActivity.class));
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });

    }
}