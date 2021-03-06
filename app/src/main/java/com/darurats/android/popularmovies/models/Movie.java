package com.darurats.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Movie implements Parcelable {

    @Expose
    private final String id;
    private String title = "";

    @Expose @SerializedName("poster_path")
    private String posterPath = "";
    private String overview = "";

    @Expose @SerializedName("release_date")
    private String releaseDate = "";

    @Expose @SerializedName("backdrop_path")
    private String backdropPath = "";

    @Expose @SerializedName("vote_average")
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

    public String getId(){
        return id;
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

    public static final class Response {

        @Expose
        public int page;

        @Expose @SerializedName("total_pages")
        public int totalPages;

        @Expose @SerializedName("total_results")
        public int totalMovies;

        @Expose @SerializedName("results")
        public ArrayList<Movie> movies = new ArrayList<>();
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
