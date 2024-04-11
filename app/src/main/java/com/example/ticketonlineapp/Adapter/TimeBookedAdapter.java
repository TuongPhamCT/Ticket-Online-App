package com.example.ticketonlineapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.BookedInformation;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.ScheduledFilm;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TimeBookedAdapter extends RecyclerView.Adapter<TimeBookedAdapter.ViewHolder> {
    private List<String> listDate;
    private List<String> listTime;
    private Cinema cinema;
    private Button preButton;
    private int checkedPosition = -1;
    private static String prevType = "";

    private static int prevPosition = -1;
    private View timeView;
    private static View prevView;
    private ListView timelistView;
    private Activity activity;
    private Film film;


    private List<Integer> listSelected = new ArrayList<Integer>();
    public TimeBookedAdapter(List<String> listDate, @Nullable List<String> listTime, @Nullable Film film, @Nullable Cinema cinema, @Nullable View view, @Nullable ListView timelistView, @Nullable Activity activity) {
        this.listDate = listDate;
        this.listTime = listTime;
        this.cinema = cinema;
        this.timeView = view;
        this.timelistView = timelistView;
        this.activity = activity;
        this.film = film;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        Button dateBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateBtn = (Button) itemView.findViewById(R.id.dateBtn);
            dateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(timeView != null){

                        if(listTime == null){
                            //if(prevType != cinema.getName()){
                            if(prevView != null){
                                TextView tv = (TextView) prevView.findViewById(R.id.cinemaName);
                                RecyclerView rv = (RecyclerView) prevView.findViewById(R.id.listTime);
                                Log.e("f", tv.getText().toString());
                                if(BookedInformation.getInstance().prevPosition >-1){
                                    View v = rv.getLayoutManager().findViewByPosition(BookedInformation.getInstance().prevPosition);
                                    Button btn  = v.findViewById(R.id.dateBtn);
                                    btn.setBackgroundColor(Color.TRANSPARENT);
                                    btn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.bg_tabview_button));
                                }
                            }
                            // }
                            BookedInformation.getInstance().isTimeSelected = true;
                            BookedInformation.getInstance().timeBooked = dateBtn.getText().toString();
                            BookedInformation.getInstance().cinema = cinema;
                        }
                        prevType = cinema.getName();
                        prevView = timeView;
                        BookedInformation.getInstance().prevPosition = getAdapterPosition();

                    }

                    else BookedInformation.getInstance().dateBooked = dateBtn.getText().toString();
                    if(film != null){
                        ScheduledFilm.getInstance().dateBooked = BookedInformation.getInstance().dateBooked;
                        ScheduledFilm.getInstance().isDateSelected = true;
                    }


                    BookedInformation.getInstance().isDateSelected = true;
                    dateBtn.setBackgroundColor(Color.TRANSPARENT);

                    dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.background_button));

                    if(checkedPosition!=getAdapterPosition()){
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                    if(listTime != null){
                        List<String> listCinemaName = new ArrayList<String>();



                        FirebaseRequests.database.collection("Cinema").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();

                                if(!BookedInformation.getInstance().dateBooked.equals(null)){

                                    CinemaNameAdapter CinemaNameAdapter = new CinemaNameAdapter(activity, R.layout.cinema_booked_item,BookedInformation.getInstance().listCinema, BookedInformation.getInstance().film);
                                    timelistView.setAdapter(CinemaNameAdapter);


                                    // Helper.getListViewSize(timelistView, activity);
                                }



                            }
                        });
                    }

                }
            });
        }

        void Binding(){
            if(checkedPosition != getAdapterPosition()){
                dateBtn.setBackgroundColor(Color.TRANSPARENT);
                dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.bg_tabview_button));
            }
            else {
                dateBtn.setBackgroundColor(Color.TRANSPARENT);
                dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.background_button));
            }


        }
    }
    @NonNull
    @Override
    public TimeBookedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_booked_item, parent, false);
        return new TimeBookedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeBookedAdapter.ViewHolder holder, int position) {
        if(listTime != null){
            holder.dateBtn.setText(listDate.get(position) + "\n" + listTime.get(position));
            holder.Binding();

        }
        else{
            holder.dateBtn.setText(listDate.get(position));

            if(holder.dateBtn.getText().toString().equals(BookedInformation.getInstance().timeBooked) && cinema.getName().equals(prevType) ){
                holder.dateBtn.setBackgroundColor(Color.TRANSPARENT);
                holder.dateBtn.setBackground(ContextCompat.getDrawable(holder.dateBtn.getContext(), R.drawable.background_button));
            }
            else holder.Binding();

        }


    }

    @Override
    public int getItemCount() {
        return listDate.size();
    }


}
