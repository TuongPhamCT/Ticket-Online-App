package com.example.ticketonlineapp.Activity.Report;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.ThemeUtils;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.Helper.Formater;
import com.example.ticketonlineapp.Activity.HomeActivity;
import com.example.ticketonlineapp.Activity.Network.CheckNetwork;
import com.example.ticketonlineapp.Activity.Notification.NotificationActivity;
import com.example.ticketonlineapp.Activity.Ticket.MyTicketActivity;
import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Cinema;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Ticket;
import com.example.ticketonlineapp.Model.Users;
import com.example.ticketonlineapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class ReportActivity extends AppCompatActivity {
    CheckNetwork checkNetwork = new CheckNetwork();
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

    private BottomNavigationView bottomNavigationView;
    private AutoCompleteTextView CinemaAutoTv;
    private AutoCompleteTextView MonthAutoTv;
    private AutoCompleteTextView YearAutoTv;
    Button BeginDateCalendarButton;
    Button EndDateCalendarButton;
    Button viewChartBtn;
    Timestamp BeginDate;
    Timestamp EndDate;
    private FirebaseFirestore firestore;
    private CollectionReference MovieRef;
    List<Cinema> cinemas = new ArrayList<>();
    private List<Film> films= new ArrayList<>();
    ImageButton control;
    TextView TotalPrice;
    List<String> cinemaNames = new ArrayList<>();
    String selectedCinema="All Cinema";
    int SelectedMonth = 0;
    int SelectedYear = 0;
    int total = 0;
    int total_price = 0;
    int index = 0;
    int temp = 0;
    LocalDate localBeginDate;
    LocalDate localEndDate;

    BarChart chart;
    ArrayList<BarEntry> listEntries = new ArrayList<>();
    ArrayList<String> listLabels = new ArrayList<>();

    LinearLayout legendLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_screen);
        legendLayout = findViewById(R.id.legendLayout);
        firestore = FirebaseFirestore.getInstance();
        control = findViewById(R.id.controlBtn);
        MovieRef = firestore.collection("Movies");
        TotalPrice=findViewById(R.id.totalPrice);
        chart = (BarChart) findViewById(R.id.chart);
        viewChartBtn = findViewById(R.id.viewChartBtn);
        BeginDateCalendarButton = findViewById(R.id.BeginDateCalendar);
        EndDateCalendarButton = findViewById(R.id.EndDateCalendar);
        BeginDateCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeginDateCalendarDialog();
                dismissKeyboard(v);
            }
        });
        EndDateCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDateCalendarDialog();
                dismissKeyboard(v);
            }
        });
        viewChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BeginDate == null || EndDate == null){
                    Toast.makeText(ReportActivity.this, "Please choose begin date and end date!", Toast.LENGTH_LONG).show();
                }
                else if(BeginDate.toDate().after(EndDate.toDate())){

                    Toast.makeText(ReportActivity.this, "End date is before begin date!", Toast.LENGTH_SHORT).show();
                }
                else{
                    LoadFilms();
                }
            }
        });
        ControlButton();
        LoadCinema();
        BottomNavigation();
        Search();
    }
    void dismissKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    private void showBeginDateCalendarDialog() {
        Calendar calendar = Calendar.getInstance();
        if(localBeginDate!=null)
            calendar.set(localBeginDate.getYear(),localBeginDate.getMonthValue()-1,localBeginDate.getDayOfMonth());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this,R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                        String date = String.valueOf(selectedDay) + "/" + String.valueOf(selectedMonth + 1) + "/" + String.valueOf(selectedYear);
                        BeginDateCalendarButton.setText(date);
                    }
                }, year, month, dayOfMonth) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                Button positiveButton = getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = getButton(DialogInterface.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker datePicker = getDatePicker();
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
                        localBeginDate = LocalDate.of(year, month+1, dayOfMonth);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month); // Note: Calendar.MONTH is zero-based
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        BeginDate = new Timestamp(calendar.getTime());
                        BeginDateCalendarButton.setText(date);dismiss();
                    }
                });

                positiveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                layoutParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                positiveButton.setLayoutParams(layoutParams);
                negativeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_background_1));
            }
        };
        datePickerDialog.show();
    }
    private void showEndDateCalendarDialog() {

        Calendar calendar = Calendar.getInstance();
        if(localEndDate!=null)
            calendar.set(localEndDate.getYear(),localEndDate.getMonthValue()-1,localEndDate.getDayOfMonth());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this,R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                        String date = String.valueOf(selectedDay) + "/" + String.valueOf(selectedMonth + 1) + "/" + String.valueOf(selectedYear);
                        EndDateCalendarButton.setText(date);
                    }
                }, year, month, dayOfMonth) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);


                Button positiveButton = getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = getButton(DialogInterface.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker datePicker = getDatePicker();
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
                        localEndDate = LocalDate.of(year, month+1, dayOfMonth);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month); // Note: Calendar.MONTH is zero-based
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        EndDate = new Timestamp(calendar.getTime());
                        EndDateCalendarButton.setText(date);dismiss();
                    }
                });

                positiveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                layoutParams.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                positiveButton.setLayoutParams(layoutParams);
                negativeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_background_1));
            }
        };

        datePickerDialog.show();
    }

    private void ControlButton() {
        ConstraintLayout fliter = findViewById(R.id.Filter);

        ViewGroup.LayoutParams params = fliter.getLayoutParams();
        params.height = 0;
        fliter.setLayoutParams(params);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("fd", BeginDate.toDate().toString());
                if(fliter.getVisibility()==View.VISIBLE) {
                    fliter.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams params = fliter.getLayoutParams();
                    params.height = 0;
                    fliter.setLayoutParams(params);
                }
                else{
                    fliter.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = fliter.getLayoutParams();
                    params.height = 200;
                    fliter.setLayoutParams(params);
                }

            }
        });

    }

    void LoadCinema()
    {
        cinemaNames.add("All Cinema");
        CinemaAutoTv = findViewById(R.id.CinemaFilter);

        FirebaseRequests.database.collection("Cinema").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> listDocs = value.getDocuments();
                for(DocumentSnapshot doc : listDocs){
                    Cinema cinema = doc.toObject(Cinema.class);
                    cinemaNames.add(cinema.getName());

                }
                CinemaAutoTv.setAdapter(new ArrayAdapter<String>(ReportActivity.this, R.layout.dropdown_item, cinemaNames));
                CinemaAutoTv.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_color)));
            }
        });

        CinemaAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCinema = parent.getItemAtPosition(position).toString();
            }
        });
    }
    void LoadMonth()
    {
        List<String> months =new ArrayList<>();
        months.add("Month");
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        MonthAutoTv = findViewById(R.id.MonthFilter);
        MonthAutoTv.setAdapter(new ArrayAdapter<String>(ReportActivity.this, R.layout.dropdown_item, months));
        MonthAutoTv.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_color)));
        MonthAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectMonth = parent.getItemAtPosition(position).toString();
                SelectedMonth= months.indexOf(selectMonth);

                LoadFilms();
            }
        });
    }
    void LoadYear()
    {
        List<String> Years =new ArrayList<>();
        Years.add("Year");
        Years.add("2022");
        Years.add("2023");
        Years.add("2024");

        YearAutoTv = findViewById(R.id.YearFilter);
        YearAutoTv.setAdapter(new ArrayAdapter<String>(ReportActivity.this, R.layout.dropdown_item, Years));
        YearAutoTv.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_color)));
        YearAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectYear = parent.getItemAtPosition(position).toString();

                if(selectYear.equals("Year"))
                    SelectedYear=0;
                else
                    SelectedYear= Integer.parseInt(selectYear);
                LoadFilms();
            }
        });
    }
    void  LoadFilms()
    {
        RecyclerView filmReports=findViewById(R.id.FilmReports);

        MovieRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                films.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Film f = documentSnapshot.toObject(Film.class);
                    films.add(f);
                }

                legendLayout.removeAllViews();
                setListRetry(films);
            }
        });
    }
    void setTotalPrice(){

        total = 0;
        firestore.collection("Cinema").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Cinema c = documentSnapshot.toObject(Cinema.class);
                    if(selectedCinema.equals("All Cinema")){
                        cinemas.add(c);
                    }

                    else if(c.getName().equals(selectedCinema))
                        cinemas.add(c);
                }
            }
        });

        firestore.collection("Ticket").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Ticket s = documentSnapshot.toObject(Ticket.class);
                    String seat = s.getSeat();
                    int count = seat.split(", ").length;

                    Date date = s.getTime().toDate();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    int TicketYear = calendar.get(Calendar.YEAR);
                    int TicketMonth = calendar.get(Calendar.MONTH) + 1;

                    for (int i = 0; i < cinemas.size(); i++)
                        if (cinemas.get(i).getCinemaID().equals(s.getCinemaID())) {
                            if(SelectedMonth==0)
                            {
                                if(SelectedYear == 0)
                                {
                                    total += cinemas.get(i).getPrice() * count;

                                }
                                else if(TicketYear==SelectedYear)
                                {
                                    total += cinemas.get(i).getPrice() * count;
                                }
                            }
                            else if(TicketMonth==SelectedMonth)
                            {
                                if(SelectedYear == 0)
                                {

                                    total += cinemas.get(i).getPrice() * count;
                                }
                                else if(TicketYear==SelectedYear)
                                {

                                    total += cinemas.get(i).getPrice() * count;
                                }
                            }
                        }

                }
                TotalPrice.setText(String.valueOf(total));
            }
        });
    }

    void BottomNavigation()
    {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.getMenu().findItem(R.id.ReportPage).setChecked(true);
        try{if (Users.currentUser != null)
            if (((Users.currentUser.getAccountType().toString()).equals("admin"))) {
                Menu menu = bottomNavigationView.getMenu();
                MenuItem ReportPage = menu.findItem(R.id.ReportPage);
                MenuItem WalletPage = menu.findItem(R.id.walletPage);
                MenuItem TicketPage = menu.findItem(R.id.ticketPage);
                TicketPage.setVisible(false);
                WalletPage.setVisible(false);
                ReportPage.setVisible(true);
            }
        }
        catch (Exception e){}
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homePage:
                    startActivity(new Intent(ReportActivity.this, HomeActivity.class));
                    overridePendingTransition(0,0);
                    break;
                case R.id.ticketPage:
                    startActivity(new Intent(ReportActivity.this, MyTicketActivity.class));
                    overridePendingTransition(0,0);
                    break;
                case R.id.NotificationPage:
                    startActivity(new Intent(ReportActivity.this, NotificationActivity.class));
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });
    }
    void Search()
    {
        SearchView Search = findViewById(R.id.searchField);

        Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        LinearLayout layoutElement = findViewById(R.id.ReportLayout);

        layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }
    private void filter(String text) {
        RecyclerView filmReports=findViewById(R.id.FilmReports);
        MovieRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                films.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Film f = documentSnapshot.toObject(Film.class);
                    if (f.getName().toLowerCase().contains(text.toLowerCase()))
                        films.add(f);

                }

                setTotalPrice();
            }
        });

    }
    void setListRetry(List<Film> films){
        listEntries = new ArrayList<>();
        listLabels = new ArrayList<>();
        cinemas = new ArrayList<>();

        firestore.collection("Cinema").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Cinema c = documentSnapshot.toObject(Cinema.class);
                    if(selectedCinema.equals("All Cinema"))
                        cinemas.add(c);
                    else if(c.getName().equals(selectedCinema)){
                        cinemas.add(c);
                    }

                }

            }
        });

        int[] randColor = randomColor(films.size());

        ArrayList<LegendEntry> listLegend = new ArrayList<>();
        for(Film film : films){
            index = 0;

            firestore.collection("Ticket").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }
                    int id = 0;
                    total_price = 0;
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Ticket s = documentSnapshot.toObject(Ticket.class);
                        if ((s.getFilmID()).equals(film.getId())) {
                            String seat = s.getSeat();
                            int count = 1;
                            count += seat.length() - s.getSeat().replace(",", "").length();
                            Date date = s.getTime().toDate();
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            int TicketYear = calendar.get(Calendar.YEAR);
                            int TicketMonth = calendar.get(Calendar.MONTH) + 1;
                            for (int i = 0; i < cinemas.size(); i++)
                                if (cinemas.get(i).getCinemaID().equals(s.getCinemaID())) {
                                    if(date.after(BeginDate.toDate()) && date.before(EndDate.toDate())){
                                        total_price += cinemas.get(i).getPrice() * count;
                                    }
                                }
                        }
                    }
                    BarEntry barEntry = new BarEntry(index, total_price);
                    listEntries.add(barEntry);
                    BarDataSet barDataSet = new BarDataSet(listEntries, "");
                    barDataSet.setColors(randColor);
                    barDataSet.setDrawValues(false);

                    BarData barData = new BarData();
                    barData.addDataSet(barDataSet);
                    chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(listLabels));
                    chart.animateY(2000);
                    chart.setFitBars(true);
                    chart.setData(barData);
                    chart.invalidate();
                    chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            String message = "Revenue: " +Math.round(e.getY());
                            AlertDialog dialog = new AlertDialog.Builder(ReportActivity.this).setTitle("Revenue of film").setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create();
                            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                }
                            });
                            dialog.show();
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                    YAxis yAxisRight = chart.getAxisRight();
                    YAxis yAxisLeft = chart.getAxisLeft();
                    XAxis xAxis = chart.getXAxis();
                    Legend l = chart.getLegend();
                    l.setWordWrapEnabled(true);
                    yAxisLeft.setAxisMinimum(0);
                    l.setEnabled(false  );

                    if(index < films.size()){
                        LinearLayout.LayoutParams parms_left_layout = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parms_left_layout.setMargins(0, 0, 0, 20);
                        LinearLayout left_layout = new LinearLayout(ReportActivity.this);
                        left_layout.setOrientation(LinearLayout.HORIZONTAL);
                        left_layout.setGravity(Gravity.LEFT);
                        left_layout.setLayoutParams(parms_left_layout);

                        LinearLayout.LayoutParams parms_legen_layout = new LinearLayout.LayoutParams(
                                40, 40);
                        parms_legen_layout.setMargins(0, 0, 20, 0);
                        LinearLayout legend_layout = new LinearLayout(ReportActivity.this);
                        legend_layout.setLayoutParams(parms_legen_layout);
                        legend_layout.setOrientation(LinearLayout.HORIZONTAL);
                        legend_layout.setBackgroundColor(randColor[index]);
                        left_layout.addView(legend_layout);
                        TextView txt_unit = new TextView(ReportActivity.this);
                        txt_unit.setText(film.getName());
                        left_layout.addView(txt_unit);

                        legendLayout.addView(left_layout);
                    }
                    l.setCustom(listLegend);
                    yAxisRight.setEnabled(false);
                    yAxisLeft.setTextColor(Color.WHITE);
                    xAxis.setTextColor(Color.WHITE);

                    yAxisLeft.setValueFormatter(new Formater());
                    l.setTextColor(Color.WHITE);
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    l.setOrientation(Legend.LegendOrientation.VERTICAL);
                    l.setDrawInside(false);
                    index++;
                }
            });

        }


    }
    int[] randomColor(int count){
        int[] listColor = new int[count];
        int color = 0;
        for(int i = 0; i < count; i++){
            Random rnd = new Random();
            if(i == 0){
                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                listColor[i] = color;
            }
            else{
                for(int j = 0; j < listColor.length; j++){
                    do{
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    }
                    while (
                            color == listColor[j]
                    );
                }
                listColor[i] = color;
            }
        }
        return listColor;
    }
}