package com.example.ticketonlineapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class Film implements Parcelable {
    private String PrimaryImage;
    private String name;
    private Timestamp movieBeginDate;
    private Timestamp movieEndDate;
    private String id;
    private  String BackGroundImage;
    private float vote;
    private String genre;
    private String description;
    private String PosterImage;
    private String durationTime;
    public Film(){}

    private List<String> trailer=new ArrayList<>();
    public List<String> getTrailer()
    {
        return  trailer;
    }public void setTrailer(List<String> trailer)
    {
        this.trailer = trailer;
    }
    public Film(String id, String PrimaryImage, String name, String BackGroundImage, String PosterImage, float vote, String genre, String description, String durationTime, Timestamp movieBeginDate, Timestamp movieEndDate, List<String> trailer) {
        this.PrimaryImage = PrimaryImage;
        this.name = name;
        this.BackGroundImage = BackGroundImage;
        this.vote = vote;
        this.genre = genre;
        this.description = description;
        this.PosterImage = PosterImage;
        this.durationTime = durationTime;
        this.id = id;
        this.movieBeginDate = movieBeginDate;
        this.trailer=trailer;
        this.movieEndDate= movieEndDate;
    }

    protected Film(Parcel in) {
        PrimaryImage = in.readString();
        name = in.readString();
        BackGroundImage = in.readString();
        vote = in.readFloat();
        genre = in.readString();
        description = in.readString();
        PosterImage=in.readString();
        durationTime=in.readString();
        id=in.readString();
        movieBeginDate = new Timestamp((Date) Objects.requireNonNull(in.readSerializable()));
        movieEndDate= new Timestamp((Date) Objects.requireNonNull(in.readSerializable()));

    }

    public Timestamp getMovieBeginDate() {
        return movieBeginDate;
    }

    public void setMovieBeginDate(Timestamp movieBeginDate) {
        this.movieBeginDate = movieBeginDate;
    }
    public Timestamp getMovieEndDate() {
        return movieEndDate;
    }

    public void setMovieEndDate(Timestamp movieEndDate) {
        this.movieEndDate = movieEndDate;
    }

    public static final Creator<Film> CREATOR = new Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };


    public  String getDurationTime(){return  durationTime;}
    public String getPrimaryImage() {
        return PrimaryImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBackGroundImage() {
        return BackGroundImage ;
    }
    public String getPosterImage() {
        return PosterImage ;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }
    public float getVote() {
        return vote;
    }

    public void setPrimaryImage(String image) {
        this.PrimaryImage = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setVote(float vote) {
        this.vote = vote;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(PrimaryImage);
        parcel.writeString(name);
        parcel.writeString(BackGroundImage);
        parcel.writeFloat(vote);
        parcel.writeString(genre);
        parcel.writeString(description);
        parcel.writeString(PosterImage);
        parcel.writeString(durationTime);
        parcel.writeString(id);
        parcel.writeSerializable(movieBeginDate.toDate());
        parcel.writeSerializable(movieEndDate.toDate());
    }
}