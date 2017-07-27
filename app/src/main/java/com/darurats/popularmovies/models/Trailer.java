package com.darurats.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Trailer implements Parcelable {

    @Expose
    private final String id;

    @Expose @SerializedName("iso_639_1")
    private String iso;
    private String key = "";
    private String name = "";
    private String site = "";
    private String size = "";
    private String type = "";

    public Trailer(String id, String iso, String key, String name, String site, String size, String type) {
        this.id = id;
        this.iso = iso;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getIso() {
        return iso;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public static final class Response {

        @Expose
        public long id;

        @Expose @SerializedName("results")
        public ArrayList<Trailer> trailers;

    }

    private Trailer(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = data[0];
        this.iso = data[1];
        this.key = data[2];
        this.name = data[3];
        this.site = data[4];
        this.size = data[5];
        this.type = data[6];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.id,
                this.iso,
                this.key,
                this.name,
                this.site,
                this.size,
                this.type,
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
