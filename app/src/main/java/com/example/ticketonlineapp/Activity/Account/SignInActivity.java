package com.example.ticketonlineapp.Activity.Account;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketonlineapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onStop() {
        super.onStop();
    }
    EditText emailET;
    EditText passwordET;
    Button LoginBtn;
    ImageView GoogleLogin;
    ImageView FacebookLogin;
    TextInputLayout passwordLayout;
    TextInputLayout emailLayout;
    String acType;

    private static final int RC_SIGN_IN = 9001;
    private static final int REQ_ONE_TAP = 2;
    private TextView forgotPasswordTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_in_screen);
        TextView signUp = findViewById(R.id.SignUp);
        emailET = findViewById(R.id.EmailET);
        passwordET = findViewById(R.id.PasswordET);
        LoginBtn = findViewById(R.id.LoginBtn);
        GoogleLogin = findViewById(R.id.GoogleLogin);
        FacebookLogin = findViewById(R.id.FacebookLogin);
        GoogleLogin.setVisibility(View.GONE);
        FacebookLogin.setVisibility(View.GONE);
        forgotPasswordTv = findViewById(R.id.ForgotPassword);
        passwordLayout = findViewById(R.id.layoutPassword);
        emailLayout = findViewById(R.id.emailLayout);
        LinearLayout layoutElement = findViewById(R.id.SignInLayout); // Replace with your actual layout element ID

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError("");
                emailLayout.setHelperText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               /* if(passwordET.length() < 8){
                    passwordLayout.setHelperText("Enter minimum 8 characters");
                    passwordLayout.setError("");
                }
                else{
                    passwordLayout.setHelperText("");
                    passwordLayout.setError("");
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginWithEmail();
            }
        });
        GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        FacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void FacebookLogin() {

    }

    void SignUp() {
    }

    void LoginWithEmail() {

    }

    void SignIn(String email, String password) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}