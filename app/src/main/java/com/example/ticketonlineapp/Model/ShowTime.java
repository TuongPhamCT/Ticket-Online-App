package com.example.ticketonlineapp.Model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ShowTime {
    private String cinemaID;
    private String filmID;

    private List<String> bookedSeat;
    private Timestamp timeBooked;

    public ShowTime(String cinemaID, String filmID, List<String> bookedSeat, Timestamp timeBooked) {
        this.cinemaID = cinemaID;
        this.filmID = filmID;

        this.bookedSeat = bookedSeat;
        this.timeBooked = timeBooked;
    }
    public ShowTime(){}

    public String getCinemaID() {
        return cinemaID;
    }

    public void setCinemaID(String cinemaID) {
        this.cinemaID = cinemaID;
    }

    public String getFilmID() {
        return filmID;
    }

    public void setFilmID(String filmID) {
        this.filmID = filmID;
    }


    public List<String> getBookedSeat() {
        return bookedSeat;
    }

    public void setBookedSeat(List<String> bookedSeat) {
        this.bookedSeat = bookedSeat;
    }

    public Timestamp getTimeBooked() {
        return timeBooked;
    }

    public void setTimeBooked(Timestamp timeBooked) {
        this.timeBooked = timeBooked;
    }
}
