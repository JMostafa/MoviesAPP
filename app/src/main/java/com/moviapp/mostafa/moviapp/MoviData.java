package com.moviapp.mostafa.moviapp;

/**
 * Created by mostafa on 8/15/2016.
 */
public class MoviData {
    private int id;
    private float rate;
    private String posterPath;
    private String backdropPath;
    private String title;
    private String date;
    private String overView;
    private String language;

    public MoviData()
    {
        id = -1;
        rate =0.0f;
        posterPath ="";
        backdropPath ="";
        title ="";
        date ="";
        overView ="";
        language ="";
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getLanguage() {return language;}

    public void setLanguage(String language) {this.language = language;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }
}
