package com.example.ticketonlineapp.Activity.User;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePasswordActivity extends AppCompatActivity {
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

    Button backBtn;
    EditText passwordET;
    EditText CurrentPasswordET;
    EditText confirmPasswordET;
    Button changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_screen);
        backBtn = findViewById(R.id.backbutton);
        passwordET=findViewById(R.id.password);
        confirmPasswordET=findViewById(R.id.confirmpassword);
        CurrentPasswordET=findViewById(R.id.currentPassword);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    void changePassword(){
        if(CurrentPasswordET.length()==0)
        {
            CurrentPasswordET.setError("Current Password is not empty!!!");
        }
        else if(passwordET.length()==0)
        {
            passwordET.setError("Password is not empty!!!");
        }
        else if(passwordET.length() < 6){
            passwordET.setError("Password should be at least 6 characters!!!");
        }
        else if(!confirmPasswordET.getText().toString().equals(passwordET.getText().toString()))
        {
            confirmPasswordET.setError("Password and confirmation passwords are not equals !!!");
        }
        else{
            AuthCredential credential = EmailAuthProvider.getCredential(FirebaseRequests.mAuth.getCurrentUser().getEmail(), CurrentPasswordET.getText().toString());
            FirebaseRequests.mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Update();

                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Current password is not correct!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    void Update()
    {
        String name = "new_password";
        if(!CurrentPasswordET.getText().toString().equals(passwordET.getText().toString()))
            UpdatePassword(passwordET.getText().toString());

    }

    void UpdatePassword(String newPassword)
    {
        FirebaseRequests.mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseRequests.mAuth.signOut();
                    Intent loginIntent = new Intent(ChangePasswordActivity.this, SignInActivity.class);
                    TaskStackBuilder.create(ChangePasswordActivity.this).addNextIntentWithParentStack(loginIntent).startActivities();
                } else {}
            }
        });
    }

    void UpdateError(String error)
    {
        Toast.makeText(ChangePasswordActivity.this, "Edit Profile failed : " + error,
                Toast.LENGTH_SHORT).show();
    }
}