package com.example.ticketonlineapp.Model;

import java.util.ArrayList;
import java.util.List;

public class ScheduledFilm {
    private static ScheduledFilm instance;
    public String dateBooked;
    public String cinemaName;
    public boolean isDateSelected;
    public boolean isCitySelected;
    public List<ShowTime> listShowTime = new ArrayList<ShowTime>();

    public static ScheduledFilm getInstance() {
        if (instance == null) {
            instance = new ScheduledFilm();
        }
        return instance;
    }
}
