package com.example.ticketonlineapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Notification implements Parcelable {
    String ID;
    Timestamp PostTime;
    String Heading;
    String Description;
    String PostAuthor;
    public  static String CollectionName="Notifications";
    public Notification(){}
    public Notification(String ID, String Heading, String Description, String PostAuthor, Timestamp PostTime){
        this.Heading=Heading;
        this.ID=ID;
        this.Description=Description;
        this.PostAuthor=PostAuthor;
        this.PostTime=PostTime;
    }

    protected Notification(Parcel in) {
        ID = in.readString();
        PostTime = in.readParcelable(Timestamp.class.getClassLoader());
        Heading = in.readString();
        Description = in.readString();
        PostAuthor = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public  void setDescription(String Description)
    {
        this.Description = Description;
    }
    public String getDescription(){return Description;}
    public  void setHeading(String Heading)
    {
        this.ID = Heading;
    }
    public String getHeading(){return Heading;}
    public  void setPostAuthor(String PostAuthor)
    {
        this.PostAuthor = PostAuthor;
    }
    public String getPostAuthor(){return PostAuthor;}
    public  void setPostTime(Timestamp PostTime)
    {
        this.PostTime = PostTime;
    }
    public Timestamp getPostTime(){return PostTime;}
    public  void setID(String ID)
    {
        this.ID = ID;
    }
    public String getID(){return ID;}
    public Map<String, Object> toJson()
    {
        Map<String, Object> json = new HashMap<>();
        json.put("ID", ID);
        json.put("Heading", Heading);
        json.put("Description", Description);
        json.put("PostTime", PostTime);
        json.put("PostAuthor", PostAuthor);
        return  json;
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeParcelable(PostTime, i);
        parcel.writeString(Heading);
        parcel.writeString(Description);
        parcel.writeString(PostAuthor);
    }
}