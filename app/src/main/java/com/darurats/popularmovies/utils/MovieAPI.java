package com.darurats.popularmovies.utils;

import com.darurats.popularmovies.models.Movie;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {

    @GET("/3/movie/{sort}")
    Call<Movies> loadMovies(@Path("sort") String sort, @Query("api_key") String api_key);

    class MovieClient
    {
        private MovieAPI movieAPI;


        public MovieClient()
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            movieAPI = retrofit.create(MovieAPI.class);
        }

        public MovieAPI getMovieAPI()
        {
            return movieAPI;
        }
    }

    class Movies {
        public ArrayList<Movie> results;
    }
}
