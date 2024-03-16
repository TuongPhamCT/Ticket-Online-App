package com.example.ticketonlineapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;


public class Ticket implements Parcelable {
    protected Timestamp time;
    protected String cinemaID;
    protected String seat;
    protected String paid;
    protected String filmID;
    protected String idorder;
    protected String voucherID;
    protected String userID;
    protected String ID;
    
    public Ticket( Timestamp time, String cinemaID, String seat, String paid, String idorder, String filmID, String userID, String voucherID, String ID)
    {
        this.filmID = filmID;
        this.time = time;
        this.cinemaID = cinemaID;
        this.seat = seat;
        this.paid = paid;
        this.idorder = idorder;
        this.userID = userID;
        this.voucherID = voucherID;
        this.ID = ID;
    }

    public Ticket(Parcel in) {
        time = in.readParcelable(Timestamp.class.getClassLoader());
        cinemaID = in.readString();
        seat = in.readString();
        paid = in.readString();
        filmID = in.readString();
        idorder = in.readString();
        voucherID = in.readString();
        userID = in.readString();
        ID = in.readString();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    public void setCinemaID(String cinemaID) {

        this.cinemaID = cinemaID;
    }

    public String getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(String voucherID) {
        this.voucherID = voucherID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Ticket(){}

    public Timestamp getTime() {
        return time;
    }

    public String getCinemaID() {
        return cinemaID;
    }

    public String getSeat() {return  seat;}
    public String getPaid() {return paid;}
    public String getIdorder() {return idorder;}

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getFilmID() {
        return filmID;
    }

    public void setFilmID(String filmID) {
        this.filmID = filmID;
    }

    public void setCinema(String cinema) {
        this.cinemaID = cinema;
    }

    public void setSeat(String seat) {this.seat =seat;}
    public void setPaid(String paid) {this.paid = paid;}
    public void setIdorder(String idorder) {this.idorder = idorder;}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(time, i);
        parcel.writeString(cinemaID);
        parcel.writeString(seat);
        parcel.writeString(paid);
        parcel.writeString(filmID);
        parcel.writeString(idorder);
        parcel.writeString(voucherID);
        parcel.writeString(userID);
        parcel.writeString(ID);
    }
}
