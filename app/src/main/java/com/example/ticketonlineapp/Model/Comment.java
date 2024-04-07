package com.example.ticketonlineapp.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Comment {
    private String profileUrl;
    private int rating;
    private String userId;
    private String reviewText;

    private int like;

    private int dislike;

    private Timestamp timeStamp;
    private List<String> listReact;
    private String ID;

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Comment(){}
    public Comment(String profileUrl, String reviewText, int like, int dislike, Timestamp timeStamp, int rating, List<String> listReact, String ID, String userId) {
        this.profileUrl = profileUrl;

        this.reviewText = reviewText;
        this.like = like;
        this.dislike = dislike;
        this.timeStamp = timeStamp;
        this.rating = rating;
        this.listReact = listReact;
        this.ID = ID;
        this.userId = userId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<String> getListReact() {
        return listReact;
    }

    public void setListReact(List<String> listReact) {
        this.listReact = listReact;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }



    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}