package com.darurats.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private final String id;
    private String title = "";
    private String posterPath = "";
    private String overview = "";
    private String releaseDate = "";
    private String backdropPath = "";
    private String rating = "";

    public Movie(String id, String title, String posterPath, String overview, String releaseDate, String backdropPath, String rating) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.rating = rating;
    }

    public String getTitle(){
        return title;
    }

    public String getPosterPath(){
        return posterPath;
    }

    public String getOverview(){
        return overview;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public String getBackdropPath(){
        return backdropPath;
    }

    public String getRating(){
        return rating;
    }

    private Movie(Parcel in){
        String[] data = new String[7];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = data[0];
        this.title = data[1];
        this.posterPath = data[2];
        this.overview = data[3];
        this.releaseDate = data[4];
        this.backdropPath = data[5];
        this.rating = data[6];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.id,
                this.title,
                this.posterPath,
                this.overview,
                this.releaseDate,
                this.backdropPath,
                this.rating,
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
