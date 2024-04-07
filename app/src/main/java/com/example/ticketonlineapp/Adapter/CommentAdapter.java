package com.example.ticketonlineapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Comment;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends ArrayAdapter<Comment> {
    Context context;
    private int resource;
    List<Comment> commentList;
    List<String> likeComments = new ArrayList<>();
    List<String> dislikeComments = new ArrayList<>();

    Film film;
    int cellHeight = -1;
    public CommentAdapter (Context context, int resource, List<Comment> commentList, Film film)
    {
        super(context, resource, commentList);
        this.resource = resource;
        this.commentList = commentList;
        this.film = film;
    }

    void SetImage(Comment postItem, RoundedImageView imageView){
        Picasso.get()
                .load(postItem.getProfileUrl())
                .fit()
                .centerCrop()
                .into(imageView);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (convertView == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource, null);
        }

        ImageView likeBtn = v.findViewById(R.id.likeBtn);
        ImageView dislikeBtn = v.findViewById(R.id.dislikeBtn);
        DocumentReference userRef = FirebaseRequests.database.collection("Users").document(FirebaseRequests.mAuth.getUid());
        Comment comment = getItem(position);
        if (comment != null)
        {
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    Users user = value.toObject(Users.class);
                    likeComments = user.getLikeComments();
                    dislikeComments = user.getDislikeComments();
                    if(likeComments != null){
                        for(String id : likeComments){
                            if(id.equals(comment.getID())){
                                likeBtn.setImageResource(R.drawable.heart_fill_icon);
                                likeBtn.setTag(R.drawable.heart_fill_icon);
                                break;
                            }
                            else{
                                likeBtn.setImageResource(R.drawable.heart_icon);
                                likeBtn.setTag("bg");
                            }
                        }
                    }
                    if(dislikeComments != null){
                        for(String id: dislikeComments){
                            if(id.equals(comment.getID())){
                                Log.e("dfd", comment.getID());
                                dislikeBtn.setImageResource(R.drawable.dislike_fill_icon);
                                dislikeBtn.setTag(R.drawable.dislike_fill_icon);
                                break;
                            }
                            else{
                                Log.e("false", comment.getID());
                                dislikeBtn.setImageResource(R.drawable.dislike_icon);
                                dislikeBtn.setTag("cg");
                            }
                        }
                    }
                }
            });

            DocumentReference commentDoc =  FirebaseRequests.database.collection("Movies")
                    .document(film.getId()).collection("Comment")
                    .document(comment.getID());
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("like", "like");
                    if(likeBtn.getTag().equals("bg")){
                        likeBtn.setImageResource(R.drawable.heart_fill_icon);
                        likeBtn.setTag(R.drawable.heart_fill_icon);
                        HashMap<String, String> likeComment = new HashMap<>();
                        if(likeComments == null){
                            likeComments = new ArrayList<>();
                        }
                        likeComments.add(comment.getID());
                        userRef.update("likeComments", likeComments);
                        commentDoc.update("like", comment.getLike() + 1);
                        if(dislikeBtn.getTag().equals(R.drawable.dislike_fill_icon)){
                            dislikeBtn.setImageResource(R.drawable.dislike_icon);
                            dislikeBtn.setTag("cg");
                            dislikeComments.remove(comment.getID());
                            userRef.update("dislikeComments", dislikeComments);
                            commentDoc.update("dislike", comment.getDislike() - 1);
                        }
                    }else {
                        Log.e("dislike", "dislike");
                        likeBtn.setImageResource(R.drawable.heart_icon);
                        likeBtn.setTag("bg");
                        likeComments.remove(comment.getID());
                        userRef.update("likeComments", likeComments);
                        commentDoc.update("like", comment.getLike() - 1);
                    }
                }
            });
            dislikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(dislikeBtn.getTag().equals("cg")){
                        dislikeBtn.setImageResource(R.drawable.dislike_fill_icon);
                        dislikeBtn.setTag(R.drawable.dislike_icon);
                        if(dislikeComments == null){
                            dislikeComments = new ArrayList<>();
                        }
                        dislikeComments.add(comment.getID());
                        userRef.update("dislikeComments",dislikeComments);
                        commentDoc.update("dislike", comment.getDislike() + 1);
                        if(likeBtn.getTag().equals(R.drawable.heart_fill_icon)){
                            likeBtn.setImageResource(R.drawable.heart_icon);
                            likeBtn.setTag("bg");
                            likeComments.remove(comment.getID());
                            userRef.update("likeComments", likeComments);
                            commentDoc.update("like", comment.getLike() - 1);
                        }
                    }else {
                        dislikeBtn.setImageResource(R.drawable.dislike_icon);
                        dislikeBtn.setTag("cg");
                        dislikeComments.remove(comment.getID());
                        userRef.update("dislikeComments", dislikeComments);
                        commentDoc.update("dislike", comment.getDislike() - 1);
                    }
                }
            });

            String messager = "";
            RoundedImageView profile = v.findViewById(R.id.profile);
            TextView name = v.findViewById(R.id.name);
            TextView reviewText = v.findViewById(R.id.review_text);
            TextView likeNumber = v.findViewById(R.id.likeNumber);
            TextView dislikeNumber = v.findViewById(R.id.dislikeNumber);
            TextView timeStamp = v.findViewById(R.id.timeStamp);
            TextView rate = v.findViewById(R.id.rate);
            RecyclerView listReact = v.findViewById(R.id.listReact);
            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext()){
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            listReact.setLayoutManager(flexboxLayoutManager);
            listReact.setAdapter(new FeelAdapter(comment.getListReact(), null));
            SetImage(comment, profile);
            if(comment.getUserId()!= null){
                FirebaseRequests.database.collection("Users").document(comment.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Users user = value.toObject(Users.class);
                        name.setText(user.getName());
                    }
                });
            }
            reviewText.setText(comment.getReviewText());
            likeNumber.setText(String.valueOf(comment.getLike()));
            dislikeNumber.setText(String.valueOf(comment.getDislike()));
            SimpleDateFormat formatter =new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            timeStamp.setText(formatter.format(comment.getTimeStamp().toDate()));
            switch (comment.getRating()){
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
            rate.setText(String.valueOf(comment.getRating()) + "/5" +" " + messager);
        }
        return v;
    }
}