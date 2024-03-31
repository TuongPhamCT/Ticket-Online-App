package com.example.ticketonlineapp.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Adapter.CommentAdapter;
import com.example.ticketonlineapp.Adapter.FeelAdapter;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.Comment;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReviewFragment extends Fragment {

    Film film;
    RatingBar ratingBar;
    ArrayList<Comment> comments;
    ListView commentList;
    CollectionReference CommentRef;
    CommentAdapter commentAdapter;
    String messager;
    List<String> listFeel;
    LinearLayout rateLayout;
    List<String> listReact = new ArrayList<>();

    FirebaseUser currentUser;
    int height = 0;
    int rate = 0;
    Activity myActivity;

    public ReviewFragment(Film f) {
        film = f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myActivity =(Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comments = new ArrayList<>();
        commentList = getView().findViewById(R.id.ReviewList);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        ratingBar = getView().findViewById(R.id.rating);
        rateLayout = getView().findViewById(R.id.ratingFilm);
        listFeel = new ArrayList<>();
        listFeel.add(getEmoji(0x1F917) + " empathetic");
        listFeel.add(getEmoji(0x1F970) + " satisfied");
        listFeel.add(getEmoji(0x1F62D) + " emotion");
        listFeel.add(getEmoji(0x1F923) + " humorous");
        listFeel.add(getEmoji(0x1F929) + " overwhelmed");
        ratingFilm();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CommentRef = db.collection("Movies").document(film.getId()).collection("Comment");
        CommentRef.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                comments.clear();
                for (QueryDocumentSnapshot doc : value) {
                    comments.add(doc.toObject(Comment.class));
                }
                commentAdapter = new CommentAdapter(myActivity, R.layout.review_comment_view, comments, film);
                ViewGroup.LayoutParams params=commentList.getLayoutParams();
                params.height = BookedInformation.getInstance().height - rateLayout.getMeasuredHeight();
                commentList.setLayoutParams(params);
                int index = commentList.getFirstVisiblePosition();
                commentList.setAdapter(commentAdapter);
                View v = commentList.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - commentList.getPaddingTop());
                commentList.setSelectionFromTop(index, top);
            }
        });
    }

    void ratingFilm(){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rate = (int) v;
                if(rate > 0){
                    showDialogRate(rate);
                }
            }
        });
    }
    String getEmoji(int code){
        return new String(Character.toChars(code));
    }
    void showDialogRate(int rating){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_rating_layout);
        RecyclerView feelView = dialog.findViewById(R.id.yourFeel);
        RatingBar rateBar = dialog.findViewById(R.id.rating);
        TextView ratio = dialog.findViewById(R.id.ratio);
        EditText yourComment = dialog.findViewById(R.id.yourComment);
        Button submitBtn = dialog.findViewById(R.id.submitComment);
        TextView reviewTv = dialog.findViewById(R.id.reviewTv);
        rateBar.setRating(rating);
        LinearLayout layoutElement = dialog.findViewById(R.id.LinerLayout);

        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        ratio.setText(String.valueOf(rating) + "/5");
        switch (rating){
            case 1:
                messager = "So bad!";
                break;
            case 2:
                messager = "Bad!";
                break;
            case 3:
                messager = "Normal!";
                break;
            case 4:
                messager = "Great!";
                break;

            case 5:
                messager = "Excellent!!";
                break;
        }
        reviewTv.setText(messager);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        feelView.setLayoutManager(flexboxLayoutManager);
        feelView.setAdapter(new FeelAdapter(listFeel, listReact));
        dialog.show();

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate currentDate = LocalDate.now();
                Calendar calendar = Calendar.getInstance();
                Date current = calendar.getTime();
                Timestamp timestamp = new Timestamp(current);
                DocumentReference doc =  FirebaseRequests.database.collection("Movies").document(film.getId()).collection("Comment").document();
                try {
                    FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Users user = documentSnapshot.toObject(Users.class);
                            Comment comment = new Comment(user.getAvatar(), yourComment.getText().toString(), 0, 0, timestamp,(int) rateBar.getRating(), listReact, doc.getId(), user.getUserID());
                            doc.set(comment);
                        }
                    });
                    CommentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int count = queryDocumentSnapshots.size();
                            updateRateFilm(count, rateBar.getRating());
                        }
                    });
                }
                catch (Exception e){}
                ratingBar.setRating(0);
                dialog.dismiss();
                Toast.makeText(getContext(), "thanks for your comment!", Toast.LENGTH_SHORT).show();
            }
        });
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rate = (int) v;
                ratio.setText(String.valueOf(rate) + "/5");
                switch (rate){
                    case 1:
                        messager = "So bad!";
                        break;
                    case 2:
                        messager = "Bad!";
                        break;
                    case 3:
                        messager = "Normal!";
                        break;
                    case 4:
                        messager = "Great!";
                        break;
                    case 5:
                        messager = "Excellent!!";
                        break;
                }
                reviewTv.setText(messager);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                ratingBar.setRating(0);
            }
        });
    }
    void updateRateFilm(int countComment, float rate){

        DocumentReference doc =  FirebaseRequests.database.collection("Movies").document(film.getId());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Film film = documentSnapshot.toObject(Film.class);
                float vote = film.getVote();
                Log.e("tuong", vote + " " + countComment + " " + rate);
                vote = (vote * (countComment - 1) + rate)/ countComment;
                doc.update("vote", vote);
            }
        });

    }
}