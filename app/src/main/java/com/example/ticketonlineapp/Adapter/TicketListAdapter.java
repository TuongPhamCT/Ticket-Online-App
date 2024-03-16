package com.example.ticketonlineapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Ticket;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketListAdapter extends ArrayAdapter<Ticket> {
    private Context context;
    private int resource;
    private List<Ticket> tickets;
    public TicketListAdapter(Context context, int resource, List<Ticket> tickets)
    {
        super(context, resource, tickets);
        this.resource = resource;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View converView, @Nullable ViewGroup parent)
    {
        View v;
        v = LayoutInflater.from(this.getContext()).inflate(R.layout.list_ticket_view, null);
        Ticket ve = getItem(position);

        if (ve!=null) {
            TextView nameTextView = (TextView) v.findViewById(R.id.tvName);
            TextView timeTextView = (TextView) v.findViewById(R.id.tvTime);
            TextView studioTextView = (TextView) v.findViewById(R.id.tvStudio);
            RoundedImageView posterRImageView = (RoundedImageView) v.findViewById(R.id.ivPoster);
            DocumentReference film =  FirebaseRequests.database.collection("Movies").document(ve.getFilmID());
            film.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (nameTextView != null)
                        nameTextView.setText(value.get("name").toString());
                    if (timeTextView != null){
                        Timestamp time = ve.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("hh:mm, E MMM dd", Locale.ENGLISH);
                        timeTextView.setText(dateFormat.format(time.toDate()));

                    }

                    if (studioTextView != null){
                        FirebaseRequests.database.collection("Cinema").document(ve.getCinemaID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                studioTextView.setText(documentSnapshot.get("Name").toString());
                            }
                        });
                    }

                    Picasso.get().load(value.get("PosterImage").toString()).into(posterRImageView);
                }
            });

        }
        return v;
    }
}