package com.example.ticketonlineapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.City.CinemaOfCity;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.City;
import com.example.ticketonlineapp.Model.ShowTime;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder>{
    List<City> listCity;

    Context context;
    public CityAdapter(List<City> listCity, Context context){
        this.context = context;
        this.listCity = listCity;
    }
    @NonNull
    @Override
    public CityAdapter.CityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new CityHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.CityHolder holder, int position) {
        City city = listCity.get(position);
        holder.cityName.setText(city.getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CinemaOfCity.class);
                intent.putExtra("city", city);
                context.startActivity(intent);
            }
        });
        holder.deleteCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                LayoutInflater factory = LayoutInflater.from(context);
                final View deleteDialogView = factory.inflate(R.layout.yes_no_dialog, null);
                TextView message = deleteDialogView.findViewById(R.id.message);
                message.setText("Do you sure to delete the city ?");
                alertDialog.setView(deleteDialogView);
                AlertDialog OptionDialog = alertDialog.create();
                OptionDialog.show();
                TextView Cancel = deleteDialogView.findViewById(R.id.Cancel_Button);
                TextView Delete = deleteDialogView.findViewById(R.id.DeleteButton);
                Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference cityRef = db.collection("City");
                        DocumentReference cityDocRef = cityRef.document(city.getID());
                        CollectionReference cinemaRef = db.collection("Cinema");
                        CollectionReference showtimeRef = db.collection("Showtime");
                        Query cinemaQuery = cinemaRef.whereEqualTo("CityID", city.getID());
                        cinemaRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Cinema> list = new ArrayList<>();
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    Cinema c = doc.toObject(Cinema.class);
                                    if (c.getCityID().equals(city.getID())) {
                                        list.add(c);
                                    }
                                }
                                showtimeRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    boolean isExisted = false;

                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (Cinema cinema : list) {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                                ShowTime showTime = doc.toObject(ShowTime.class);
                                                if (cinema.getCinemaID().equals(showTime.getCinemaID())) {
                                                    isExisted = true;
                                                }
                                            }
                                        }
                                        if (isExisted) {
                                            Toast.makeText(context, "Cinema of ciy is having showtime!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            cityRef.document(city.getID()).delete();
                                            for (Cinema item : list) {
                                                cinemaRef.document(item.getCinemaID()).delete();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        OptionDialog.dismiss();
                    }
                });
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OptionDialog.dismiss();
                    }
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return listCity.size();
    }
    public class CityHolder extends RecyclerView.ViewHolder{
        TextView cityName;
        ImageView deleteCity;
        View view;
        public CityHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
            deleteCity = itemView.findViewById(R.id.deleteCity);
            view = itemView;
        }
    }
}
