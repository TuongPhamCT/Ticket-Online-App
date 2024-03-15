package com.example.ticketonlineapp.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseRequests {
    public static FirebaseAuth mAuth;
    public static FirebaseFirestore database = FirebaseFirestore.getInstance();
}
