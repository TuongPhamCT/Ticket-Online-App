package com.example.ticketonlineapp.Activity.User;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

public class EditAccountActivity extends AppCompatActivity {
    CheckNetwork checkNetwork = new CheckNetwork();
    EditText fullNameET;
    EditText emailET;

    ImageView addImage;
    RoundedImageView avatarImg;
    UploadTask uploadTask;
    String cinemaImg;
    Uri filePath;
    String img;
    FirebaseUser currentUser = FirebaseRequests.mAuth.getCurrentUser();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    ActivityResultLauncher<Intent> activityLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data.getData() != null) {
                            filePath = data.getData();

                            avatarImg.setImageURI(filePath);
                            img = UUID.randomUUID().toString();

                            StorageReference ref
                                    = storageReference
                                    .child(img);
                            ref.putFile(filePath).addOnSuccessListener(
                                    taskSnapshot -> Toast.makeText(EditAccountActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditAccountActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

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
        setContentView(R.layout.activity_edit_account);
        addImage = findViewById(R.id.add_image);
        avatarImg = findViewById(R.id.avatar_img);
        fullNameET = findViewById(R.id.full_name);
        emailET = findViewById(R.id.email_address);

        FirebaseRequests.database.collection("Users").document(Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Users user = documentSnapshot.toObject(Users.class);
            assert user != null;
            fullNameET.setText(user.getName());
            emailET.setText(user.getEmail());
            Picasso.get().load(user.getAvatar()).into(avatarImg);
        });
        addImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityLaunch.launch(intent);
        });
        Button backBt = findViewById(R.id.backbutton);
        backBt.setOnClickListener(view -> finish());
        Button UpdateBtn = findViewById(R.id.UpdateBtn);
        UpdateBtn.setOnClickListener(view -> UpdateUser());

    }

    void UpdateUser() {
        if (fullNameET.length() == 0) {
            fullNameET.setError("Full Name is not empty!!!");
        } else if (emailET.length() == 0) {
            emailET.setError("Email is not empty!!!");
        } else {
            Update();
            finish();
        }
        updateAvatar();
    }

    void Update() {
        if (!emailET.getText().toString().equals(Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).getEmail()))
            UpdateEmail();
        if (!fullNameET.getText().toString().equals(FirebaseRequests.mAuth.getCurrentUser().getDisplayName()))
            UpdateFullName();
    }

    void UpdateFullName() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullNameET.getText().toString())
                .build();
        Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                UpdateError("Full Name");
            }
        });
        FirebaseRequests.database.collection("Users").document(currentUser.getUid()).update("Name", fullNameET.getText().toString());

    }

    void UpdateEmail() {
        Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser())
                .verifyBeforeUpdateEmail(emailET.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseRequests.mAuth.signOut();
                        Intent loginIntent = new Intent(EditAccountActivity.this, SignInActivity.class);
                        TaskStackBuilder.create(EditAccountActivity.this).addNextIntentWithParentStack(loginIntent).startActivities();
                    } else {
                        UpdateError("Email");
                    }
                });
        FirebaseRequests.database.collection("Users").document(currentUser.getUid()).update("Email", emailET.getText().toString());
    }

    void UpdateError(String error) {
        Toast.makeText(EditAccountActivity.this, "Edit Profile failed : " + error,
                Toast.LENGTH_SHORT).show();
    }

    void updateAvatar() {
        if (filePath != null) {
            StorageReference ref = storageReference.child(img);
            uploadTask = ref.putFile(filePath);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    cinemaImg = task.getResult().toString();
                    CollectionReference cinemaCollection = FirebaseFirestore.getInstance().collection("Users");
                    DocumentReference doc;
                    doc = cinemaCollection.document(currentUser.getUid());

                    doc.update("avatar", cinemaImg);
                }
            });
        }
    }
}