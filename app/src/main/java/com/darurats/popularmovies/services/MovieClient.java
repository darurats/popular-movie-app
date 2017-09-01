package com.darurats.popularmovies.services;

import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.models.Review;
import com.darurats.popularmovies.models.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieClient {

    @GET("/3/movie/{sort}")
    Call<Movie.Response> loadMovies(@Path("sort") String sort);

    @GET("3/movie/{id}/reviews?")
    Call<Review.Response> loadReviews(@Path("id") String id);

    @GET("3/movie/{id}/videos?")
    Call<Trailer.Response> loadTrailers(@Path("id") String id);
}
