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

import com.example.ticketonlineapp.Model.ShowTime;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeScheduleAdapter extends RecyclerView.Adapter<TimeScheduleAdapter.ViewHolder> {
    private List<String> listDate;

    private List<String> listTime;
    private Cinema cinema;
    private Button preButton;
    private LayoutInflater layoutInflater = null;
    private int checkedPosition = -1;
    private static String prevType = "";
    private List<ShowTime> listSelect = new ArrayList<ShowTime>();
    private static int prevPosition = 0;
    private View timeView;
    private static View prevView;
    private ListView timelistView;
    private String dateBooked;
    private Film film;
    private Activity activity;
    private List<ShowTime> listShowTimeSelected;


    public TimeScheduleAdapter(List<String> listDate, @Nullable List<String> listTime, Film film, @Nullable Cinema cinema, @Nullable View view, @Nullable ListView timelistView, @Nullable Activity activity, List<ShowTime> listShowTimeSelected) {
        this.listDate = listDate;
        this.listTime = listTime;
        this.cinema = cinema;
        this.timeView = view;
        this.timelistView = timelistView;
        this.activity = activity;
        this.film = film;
        this.listShowTimeSelected = listShowTimeSelected;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button dateBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateBtn = (Button) itemView.findViewById(R.id.dateBtn);
            dateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (timeView != null) {

                        if (listTime == null) {

//                           
                            int count = 0;
                            int selectedIndex = -1;
                            List<ShowTime> listShowTime = ScheduledFilm.getInstance().listShowTime;


                            //  Timestamp timeSchedule = new Timestamp();
                            for (int i = 0; i < listShowTime.size(); i++) {
                                ShowTime showTime = listShowTime.get(i);
                                DateFormat dateFormat = new SimpleDateFormat("EEE\nd", Locale.ENGLISH);
                                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                                if(cinema.getCinemaID().equals(showTime.getCinemaID())
                                        && film.getId().equals(showTime.getFilmID())
                                        && dateFormat.format(showTime.getTimeBooked().toDate()).equals(ScheduledFilm.getInstance().dateBooked)

                                        && timeFormat.format(showTime.getTimeBooked().toDate()).equals(dateBtn.getText().toString())){
                                    selectedIndex = i;
                                }


                            }
                            if (selectedIndex == -1) {
                                List<String> bookedSeat = new ArrayList<String>();
                                Calendar calendar = Calendar.getInstance();
                                int date = calendar.get(Calendar.DATE);
                                int month = calendar.get(Calendar.MONTH);
                                int year = calendar.get(Calendar.YEAR);
                                String[] dateBook = ScheduledFilm.getInstance().dateBooked.split("\n");

                                String[] timeBook = dateBtn.getText().toString().split(":");
                                int dateSelect = Integer.parseInt(dateBook[1]);
                                calendar.set(Calendar.DATE, dateSelect);
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeBook[0]));
                                calendar.set(Calendar.MINUTE,Integer.parseInt(timeBook[1]));
                                calendar.set(Calendar.MILLISECOND, 0);
                                calendar.set(Calendar.SECOND, 0);

                                if(date <= dateSelect){
                                    calendar.set(Calendar.MONTH, month);
                                } else calendar.set(Calendar.MONTH, month + 1);
                                Timestamp timeSchedule = new Timestamp(calendar.getTime());
                                ScheduledFilm.getInstance().listShowTime.add(new ShowTime(cinema.getCinemaID(), film.getId(),bookedSeat, timeSchedule ));

                                dateBtn.setBackgroundColor(Color.TRANSPARENT);

                                dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.background_button));


                            } else {
                                ScheduledFilm.getInstance().listShowTime.remove(selectedIndex);

                                dateBtn.setBackgroundColor(Color.TRANSPARENT);

                                dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.bg_tabview_button));

                            }
                        }
//                        dateBtn.setBackgroundColor(Color.TRANSPARENT);
//
//                        dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.background_button));
                        prevType = cinema.getName();
                        prevView = timeView;
                        prevPosition = getAdapterPosition();

                    } else dateBooked = dateBtn.getText().toString();

                    if (listTime != null) {
                        List<String> listCinemaName = new ArrayList<String>();
                        FirebaseRequests.database.collection("Cinema").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> listDocs = queryDocumentSnapshots.getDocuments();

                                if (!dateBooked.equals(null)) {
                                    CinemaNameAdapter CinemaNameAdapter = new CinemaNameAdapter(activity, R.layout.cinema_booked_item, BookedInformation.getInstance().listCinema, BookedInformation.getInstance().film);
                                    timelistView.setAdapter(CinemaNameAdapter);



                                }
                            }
                        });
                    }
                }
            });
        }

        void Binding() {
//            if(checkedPosition != getAdapterPosition()){
            dateBtn.setBackgroundColor(Color.TRANSPARENT);
            dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.bg_tabview_button));
            //}
//            else {
//                dateBtn.setBackgroundColor(Color.TRANSPARENT);
//                dateBtn.setBackground(ContextCompat.getDrawable(dateBtn.getContext(), R.drawable.background_button));
//            }
        }
    }

    @NonNull
    @Override
    public TimeScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_booked_item, parent, false);
        return new TimeScheduleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeScheduleAdapter.ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("E\nd", Locale.ENGLISH);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        if (listTime != null) {
            holder.dateBtn.setText(listDate.get(position) + "\n" + listTime.get(position));

//            for(ShowTime showTime : ScheduledFilm.getInstance().listShowTime){

//                if(timeFormat.format(showTime.getTimeBooked().toDate()).equals(holder.dateBtn.getText().toString())){
//                    holder.dateBtn.setBackgroundColor(Color.TRANSPARENT);
//                    holder.dateBtn.setBackground(ContextCompat.getDrawable(holder.dateBtn.getContext(), R.drawable.bg_tabview_button));
//                }
//                else  holder.Binding();
//            }
        } else {
            holder.dateBtn.setText(listDate.get(position));

            Log.e("fd", listShowTimeSelected.size() + "");
            for(ShowTime show : listShowTimeSelected) {
                // Log.e("fd", timeFormat.format(show.getTimeBooked().toDate()) + " " +holder.dateBtn.getText().toString() +" " + dateFormat.format(show.getTimeBooked().toDate()) +ScheduledFilm.getInstance().dateBooked +  cinema.getCinemaID() + " " + show.getCinemaID() + film.getId() + " " + show.getFilmID());

                //Log.e("dff", dateFormat.format(show.getTimeBooked().toDate()));
                if (timeFormat.format(show.getTimeBooked().toDate()).equals(holder.dateBtn.getText().toString())
                        && cinema.getCinemaID().equals(show.getCinemaID())
                        && film.getId().equals(show.getFilmID())
                        && dateFormat.format(show.getTimeBooked().toDate()).equals(ScheduledFilm.getInstance().dateBooked)) {

                    listSelect.add(show);
                    holder.dateBtn.setEnabled(false);
                    holder.dateBtn.setBackgroundColor(Color.TRANSPARENT);
                    holder.dateBtn.setBackground(ContextCompat.getDrawable(holder.dateBtn.getContext(), R.drawable.background_disable));
                    break;
                } else holder.Binding();
            }
            if( ScheduledFilm.getInstance().listShowTime.size() > 0){
                for(ShowTime showTime : ScheduledFilm.getInstance().listShowTime){
                    // Log.e("binh",timeFormat.format(showTime.getTimeBooked().toDate())+ " " +  holder.dateBtn.getText().toString() + " "+ cinemaName+ " " + showTime.getNameCinema() + " " + dateFormat.format(showTime.getTimeBooked().toDate()) + " " + ScheduledFilm.getInstance().dateBooked);
                    if(timeFormat.format(showTime.getTimeBooked().toDate()).equals(holder.dateBtn.getText().toString())
                            && cinema.getCinemaID().equals(showTime.getCinemaID())
                            && dateFormat.format(showTime.getTimeBooked().toDate()).equals(ScheduledFilm.getInstance().dateBooked)){

                        holder.dateBtn.setBackgroundColor(Color.TRANSPARENT);
                        holder.dateBtn.setBackground(ContextCompat.getDrawable(holder.dateBtn.getContext(), R.drawable.background_button));

                        break;

                    } else {
                        if(holder.dateBtn.isEnabled()){
                            holder.Binding();
                        }
                    }

                }
            }




        }

    }

    @Override
    public int getItemCount() {
        return listDate.size();
    }


}