package com.example.ticketonlineapp.Activity.User;

import static android.content.ContentValues.TAG;
import static com.example.ticketonlineapp.Database.FirebaseRequests.mAuth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
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

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    CheckNetwork checkNetwork = new CheckNetwork();
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkNetwork, filter);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(checkNetwork);
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

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;
    private static final int REQ_ONE_TAP = 2;
    private TextView forgotPasswordTv;
    CallbackManager callbackManager;
    AccessToken accessToken;
    boolean isLoggedIn;
    FirebaseAuth firebaseAuth;
    BeginSignInRequest signInRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_screen);
        callbackManager = CallbackManager.Factory.create();
        TextView signUp = findViewById(R.id.SignUp);
        emailET = findViewById(R.id.EmailET);
        passwordET = findViewById(R.id.PasswordET);
        LoginBtn = findViewById(R.id.LoginBtn);
        GoogleLogin = findViewById(R.id.GoogleLogin);
        FacebookLogin = findViewById(R.id.FacebookLogin);
        forgotPasswordTv = findViewById(R.id.ForgotPassword);
        passwordLayout = findViewById(R.id.layoutPassword);
        emailLayout = findViewById(R.id.emailLayout);
        LinearLayout layoutElement = findViewById(R.id.SignInLayout);
        firebaseAuth = FirebaseAuth.getInstance();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println(loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignInActivity.this, "Siging in with Facebook is FAIL !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SignInActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        FacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile , email"));
            }
        });
        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

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
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog layout_dialog = new Dialog(SignInActivity.this);
                layout_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                layout_dialog.setContentView(R.layout.forgot_password_dialog);
                Window window = layout_dialog.getWindow();
                if (window != null){
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams windowAttribute = window.getAttributes();
                    windowAttribute.gravity = Gravity.CENTER;
                    window.setAttributes(windowAttribute);
                    layout_dialog.show();
                    EditText mailEdit = layout_dialog.findViewById(R.id.emailEdit);
                    Button cancelBtn = layout_dialog.findViewById(R.id.cancelBtn);
                    Button resetBtn = layout_dialog.findViewById(R.id.resetPassBtn);
                    resetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userEmail = mailEdit.getText().toString();
                            if(TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                                Toast.makeText(SignInActivity.this, "Enter your email!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Check your email!", Toast.LENGTH_SHORT).show();
                                        layout_dialog.dismiss();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Unable to send, fail!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    });
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout_dialog.dismiss();
                        }
                    });
                }
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
                GoogleLogin();
            }
        });
        GoogleSignInOptions googleSignInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("154016756693-9bttk960qik82vukbbvln88s448af3f7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOption);
    }

    void GoogleLogin() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Launch the Google Sign-In UI to choose an account from the list
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            //Log.e(TAG , String.valueOf(data.getParcelableExtra()));
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, String.valueOf(e.getStatusCode()), e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Check Exist
                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            DocumentReference docIdRef = rootRef.collection("Users").document(user.getUid());
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(document.getId(), "onComplete: ");
                                            getUser(user.getUid());
                                            updateUI(user);
                                        } else {
                                            CreateUser(user);
                                            getUser(user.getUid());
                                            updateUI(user);
                                        }
                                    } else {
                                        Log.d(TAG, "Failed with: ", task.getException());
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    void CreateUser(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Users u = new Users(user.getUid(),   user.getDisplayName(),user.getEmail(),0, "User", user.getPhotoUrl().toString(), new ArrayList<>(), new ArrayList<>());

        FirebaseRequests.database.collection("Users").document(user.getUid())
                .set(u.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Check Exist
                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            DocumentReference docIdRef = rootRef.collection("Users").document(user.getUid());
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(document.getId(), "onComplete: ");
                                            getUser(user.getUid());
                                            updateUI(user);
                                        } else {
                                            CreateUser(user);
                                            getUser(user.getUid());
                                            updateUI(user);
                                        }
                                    } else {
                                        Log.d(TAG, "Failed with: ", task.getException());
                                    }
                                }
                            });
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    void SignUp() {
        Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(i);
    }
    void LoginWithEmail() {
        boolean error = false;
        if (emailET.length() == 0) {
            emailLayout.setError("Email is not empty!!!");
            error=true;
        }
        if (passwordET.length() == 0) {
            passwordLayout.setError("Password is not empty!!!");
            error=true;
        }

        if(!error)
            SignIn(emailET.getText().toString(), passwordET.getText().toString());
    }
    void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            getUser(user.getUid());
                            updateUI(user);
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            emailLayout.setError("Email or Password is incorrect");
                            passwordLayout.setError("Email or Password is incorrect");

                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        }
    }
    Users getUser(String id)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Users").document(id);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Users.currentUser = snapshot.toObject(Users.class);
                }
            }
        });
        return Users.currentUser;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            focusedView.clearFocus();
        }
    }

}