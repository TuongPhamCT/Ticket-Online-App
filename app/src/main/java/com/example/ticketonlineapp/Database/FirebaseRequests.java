package com.example.ticketonlineapp.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseRequests {
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore database = FirebaseFirestore.getInstance();
}
