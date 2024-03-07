package com.example.ticketonlineapp.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ticketonlineapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class ConfirmationProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_profile_screen);
        RoundedImageView avatar = (RoundedImageView) findViewById(R.id.profilePic);
        TextView name = (TextView) findViewById(R.id.txtExampleName);
        name.setText("Arya Wịaya");
        avatar.setImageResource(R.drawable.avatar);
        Button createAccountBtn = findViewById(R.id.btnCreateAccount);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ImageView backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Back Button!!!");
            }
        });
    }
}