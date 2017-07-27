package com.darurats.popularmovies;


import android.app.Application;
import android.widget.Toast;

import com.darurats.popularmovies.utils.MovieAPI;

public class App extends Application {
    private static MovieAPI.MovieClient movieClient;

    @Override
    public void onCreate() {
        super.onCreate();
        movieClient = new MovieAPI.MovieClient();
    }

    public static MovieAPI.MovieClient getMovieClient() {
        return movieClient;
    }
}
