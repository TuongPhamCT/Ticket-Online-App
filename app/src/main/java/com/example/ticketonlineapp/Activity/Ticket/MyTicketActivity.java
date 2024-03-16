package com.example.ticketonlineapp.Activity.Ticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Adapter.TicketListAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Ticket;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyTicketActivity extends AppCompatActivity {
    ListView listView;
    FirebaseFirestore firestore;
    ArrayList<Ticket> arrayList = new ArrayList<Ticket>();
    TicketListAdapter adapter;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ticket_screen);

        listView = (ListView) findViewById(R.id.ListViewTicket);
        Button newTicket = (Button) findViewById(R.id.buttonNewsTicket);
        Button expiredTicket = (Button) findViewById(R.id.buttonExpiredTicket);
        Button allTicket = (Button) findViewById(R.id.buttonAllTicket);

        firestore = FirebaseFirestore.getInstance();

        allTicket.setText("All");
        allTicket.setSelected(true);

        loadListTicket("all");
        allTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTicket.setSelected(true);
                newTicket.setSelected(false);
                expiredTicket.setSelected(false);
                allTicket.setText("All");
                newTicket.setText(null);
                expiredTicket.setText(null);

                arrayList.clear();

                loadListTicket("all");
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

                arrayList.clear();

                loadListTicket("new");

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
                arrayList.clear();
                loadListTicket("expire");

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
//                case R.id.walletPage:
  //                  startActivity(new Intent(MyTicketActivity.this, MyWalletActivity.class));
    //                overridePendingTransition(0,0);
      //              break;
        //        case R.id.ReportPage:
          //          startActivity(new Intent(MyTicketActivity.this, ReportActivity.class));
            //        overridePendingTransition(0,0);
              //      break;
                //case R.id.NotificationPage:
                  //  startActivity(new Intent(MyTicketActivity.this, NotificationActivity.class));
                    //overridePendingTransition(0, 0);
                   // break;
            }
            return true;
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = listView.getItemAtPosition(i);
                Ticket ticket = (Ticket) o;
                DocumentReference film =  FirebaseRequests.database.collection("Movies").document(ticket.getFilmID());
                film.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        //Intent a = new Intent(getApplicationContext(),TicketDetailActivity.class);
                        //a.putExtra("ticket",ticket );
                        //startActivity(a);
                    }
                });
            }
        });
    }
    void loadListTicket(String type) {
        firestore.collection("Ticket").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    Log.e("ticket", "ticket");
                    List<DocumentSnapshot> listDoc = value.getDocuments();
                    Calendar calendar = Calendar.getInstance();
                    Date currentTime= calendar.getTime();
                    switch(type){
                        case "all":
                            arrayList.clear();
                            for (DocumentSnapshot doc : listDoc){
                                if(doc.get("userID").equals(FirebaseRequests.mAuth.getUid())){
                                    Ticket _ticket = doc.toObject(Ticket.class);
                                    arrayList.add(_ticket);
                                }

                            }
                            break;
                        case "new":
                            arrayList.clear();
                            for (DocumentSnapshot doc : listDoc){
                                if(doc.get("userID").equals(FirebaseRequests.mAuth.getUid())) {

                                    if (currentTime.before(doc.getTimestamp("time").toDate())) {
                                        Ticket _ticket = doc.toObject(Ticket.class);
                                        arrayList.add(_ticket);
                                    }
                                }
                            }

                            break;
                        case "expire":
                            arrayList.clear();
                            for (DocumentSnapshot doc : listDoc){
                                if(doc.get("userID").equals(FirebaseRequests.mAuth.getUid())) {
                                    if (currentTime.after(doc.getTimestamp("time").toDate()) || currentTime.equals(doc.getTimestamp("time").toDate())) {
                                        Ticket _ticket = doc.toObject(Ticket.class);
                                        arrayList.add(_ticket);
                                    }
                                }
                            }
                            break;
                    }
                    adapter = new TicketListAdapter(getApplicationContext(), R.layout.list_ticket_view, arrayList);
                    listView.setAdapter(adapter);
                }

            }
        });

    }
}