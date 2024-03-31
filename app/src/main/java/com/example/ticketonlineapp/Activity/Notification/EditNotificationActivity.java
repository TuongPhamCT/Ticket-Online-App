package com.example.ticketonlineapp.Activity.Notification;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Model.ExtraIntent;
import com.example.ticketonlineapp.Model.Notification;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNotificationActivity extends AppCompatActivity {
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

    TextInputEditText Description;
    TextInputEditText Heading;
    Notification notification;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_notification_screen);
        Intent intent = getIntent();
        notification = intent.getParcelableExtra(ExtraIntent.notification);
        backBtn = findViewById(R.id.backbutton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("dss", "fd");
                finish();
            }
        });
        AddNotification();
    }
    void AddNotification(){
        Description= findViewById(R.id.DescriptionET);
        Heading=findViewById(R.id.HeadingET);
        LinearLayoutCompat layoutElement = findViewById(R.id.AddNotificationLayout);

        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        if(notification!=null)
        {
            Heading.setText(notification.getHeading());
            Description.setText(notification.getDescription());
        }
        AppCompatButton SaveBtn = findViewById(R.id.SaveBtn);
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                if(Description.length()==0)
                {
                    Description.setError("Description is not empty!!!");
                    error=true;
                }
                if(Heading.length()==0)
                {
                    Heading.setError("Heading is not empty!!!");
                    error=true;
                }
                if(!error)
                {
                    AddNotificationToFirebase();
                    finish();
                }
            }

            private void AddNotificationToFirebase() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference doc;
                if(notification!=null)
                    doc= db.collection(Notification.CollectionName).document(notification.getID());
                else
                    doc= db.collection(Notification.CollectionName).document();
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

                Notification notification = new Notification(doc.getId(), Heading.getText().toString(),  Description.getText().toString(),currentUser.getDisplayName(), Timestamp.now());
                doc.set(notification.toJson())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });
    }
}