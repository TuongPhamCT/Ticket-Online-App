package com.example.ticketonlineapp.Activity.Ticket;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ticketonlineapp.Database.FirebaseRequests;
import com.example.ticketonlineapp.Model.Discount;
import com.example.ticketonlineapp.Model.Film;
import com.example.ticketonlineapp.Model.Service;
import com.example.ticketonlineapp.Model.ServiceInTicket;
import com.example.ticketonlineapp.Model.Ticket;
import com.example.ticketonlineapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {
    private ImageView qrCode;
    private TextView filmName ;
    private RatingBar filmRating;
    private TextView filmRatingText;
    private TextView filmKind ;
    private TextView filmDuration;
    private TextView cinema;
    private TextView bookDay;
    private TextView seatPositon;
    private TextView paidBill;
    private TextView idOder;
    private TextView voucher;
    RelativeLayout voucherLayout;
    LinearLayout inforTicketLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        RoundedImageView filmPoster = (RoundedImageView) findViewById(R.id.ivPoster);
        filmName = (TextView) findViewById(R.id.tvName);
        filmRating = (RatingBar) findViewById(R.id.rating);
        filmRatingText = (TextView) findViewById(R.id.vote);
        filmKind = (TextView) findViewById(R.id.tvKind);
        filmDuration = (TextView) findViewById(R.id.tvDuration);
        cinema = (TextView) findViewById(R.id.cinema);
        bookDay = (TextView) findViewById(R.id.DateAndTime);
        seatPositon = (TextView) findViewById(R.id.SeatNumber);
        paidBill = (TextView) findViewById(R.id.Paid);
        idOder = (TextView) findViewById(R.id.IDOrder);
        voucher = findViewById(R.id.voucher);
        voucherLayout = findViewById(R.id.voucherLayout);
        inforTicketLayout = findViewById(R.id.inforTicket);
        Button backButton = (Button) findViewById(R.id.backbutton);
        qrCode = findViewById(R.id.QRCode);
        Ticket ticket = getIntent().getParcelableExtra("ticket");
        if(ticket == null) {
            ticket = new Ticket(Timestamp.now() , "1" , "1" , "1" , "1" , "1" , "1" , "1" , "1");
        }
        FirebaseRequests.database.collection("Movies").document(ticket.getFilmID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Film film = documentSnapshot.toObject(Film.class);
                if(film != null) {
                    Picasso.get().load(film.getPosterImage()).into(filmPoster);
                    filmName.setText(film.getName());
                    filmRating.setRating(film.getVote());
                    DecimalFormat df = new DecimalFormat("0.0");
                    filmRatingText.setText("(" + df.format(film.getVote())  + ")");
                    filmKind.setText(film.getGenre());
                    filmDuration.setText(film.getDurationTime());
                }
            }
        });

        String cinemaID = ticket.getCinemaID();

        FirebaseRequests.database.collection("Cinema").document(cinemaID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("Name") != null) {
                    cinema.setText(documentSnapshot.get("Name").toString());
                }

            }
        });
        Timestamp time = ticket.getTime();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm, E MMM dd", Locale.ENGLISH);
        String timeBooked = dateFormat.format(time.toDate());
        bookDay.setText(timeBooked);
        seatPositon.setText(ticket.getSeat());
        paidBill.setText(ticket.getPaid());
        idOder.setText(ticket.getIdorder());
        String voucherID = ticket.getVoucherID();
        if(voucherID.isEmpty()){
            voucherLayout.setVisibility(View.GONE);
        }
        else {
            FirebaseRequests.database.collection("Discounts").document(voucherID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Discount discount = documentSnapshot.toObject(Discount.class);
                    if(discount != null) {
                        voucher.setText(discount.getName() + " " + discount.getDiscountRate() + "%");
                    }

                }
            });
        }
        generateQrcode(idOder.getText().toString());
        addServiceToLayout(ticket.getID());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    void generateQrcode(String idOrder){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode("id order: " + idOrder, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);

        }
        catch(WriterException e){
            throw new RuntimeException(e);
        }
    }
    void addServiceToLayout(String id){
        FirebaseRequests.database.collection("Ticket").document(id).collection("ServiceInTicket").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<ServiceInTicket> listService = new ArrayList<>();
                for(DocumentSnapshot doc : queryDocumentSnapshots){
                    ServiceInTicket service = doc.toObject(ServiceInTicket.class);
                    listService.add(service);
                }
                for(ServiceInTicket serviceInTicket : listService){
                    View con = LayoutInflater.from(TicketDetailActivity.this).inflate(R.layout.service_ticket_item, null);
                    TextView name = con.findViewById(R.id.name);
                    TextView value = con.findViewById(R.id.value);
                    FirebaseRequests.database.collection("Service").document(serviceInTicket.getServiceID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Service service = documentSnapshot.toObject(Service.class);
                            name.setText(service.getDetail());
                            value.setText(service.getPrice()  + " x " + String.valueOf(serviceInTicket.getCount()));

                        }
                    });
                    inforTicketLayout.addView(con);
                }

            }
        });

    }

}