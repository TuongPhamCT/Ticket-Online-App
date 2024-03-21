package com.example.ticketonlineapp.Activity.User;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.example.ticketonlineapp.databinding.ActivityEditAccountBinding;
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
    ActivityEditAccountBinding binding;
    CheckNetwork checkNetwork = new CheckNetwork();
    Uri filePath;
    UploadTask uploadTask;
    String cinemaImg;
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

                            binding.avatarImg.setImageURI(filePath);
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

        binding = ActivityEditAccountBinding.inflate(getLayoutInflater());
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        setContentView(binding.getRoot());

        FirebaseRequests.database.collection("Users").document(Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Users user = documentSnapshot.toObject(Users.class);
            assert user != null;
            binding.fullName.setText(user.getName());
            binding.emailAddress.setText(user.getEmail());
            Picasso.get().load(user.getAvatar()).into(binding.avatarImg);
        });
        binding.addImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityLaunch.launch(intent);
        });
        binding.backButton.setOnClickListener(view -> finish());
        binding.UpdateBtn.setOnClickListener(view -> UpdateUser());

    }

    void UpdateUser() {
        if (binding.fullName.length() == 0) {
            binding.fullName.setError("Full Name is not empty!!!");
        } else if (binding.emailAddress.length() == 0) {
            binding.emailAddress.setError("Email is not empty!!!");
        } else {
            Update();
            finish();
        }
        updateAvatar();
    }

    void Update() {
        if (!binding.emailAddress.getText().toString().equals(Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).getEmail()))
            UpdateEmail();
        if (!binding.fullName.getText().toString().equals(FirebaseRequests.mAuth.getCurrentUser().getDisplayName()))
            UpdateFullName();
    }

    void UpdateFullName() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.fullName.getText().toString())
                .build();
        Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                UpdateError("Full Name");
            }
        });
        FirebaseRequests.database.collection("Users").document(currentUser.getUid()).update("Name", binding.fullName.getText().toString());

    }

    void UpdateEmail() {
        Objects.requireNonNull(FirebaseRequests.mAuth.getCurrentUser())
                .verifyBeforeUpdateEmail(binding.emailAddress.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseRequests.mAuth.signOut();
                        Intent loginIntent = new Intent(EditAccountActivity.this, SignInActivity.class);
                        TaskStackBuilder.create(EditAccountActivity.this).addNextIntentWithParentStack(loginIntent).startActivities();
                    } else {
                        UpdateError("Email");
                    }
                });
        FirebaseRequests.database.collection("Users").document(currentUser.getUid()).update("Email", binding.emailAddress.getText().toString());
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