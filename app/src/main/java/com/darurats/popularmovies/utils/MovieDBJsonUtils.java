package com.darurats.popularmovies.utils;

import com.darurats.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Utility functions to handle TheMovieDB JSON data.
 */
public final class MovieDBJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movie.
     * <p/>
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<Movie> getMovieStringsFromJson(String movieJsonStr)
            throws JSONException {

        final ArrayList<Movie> movieList = new ArrayList<>();

        final String TMD_BASE_URL = "http://image.tmdb.org/t/p";
        final String TMD_POSTER_SIZE = "w185";
        final String TMD_BACKDROP_SIZE = "w780";
        final String TMD_RESULTS = "results";
        final String TMD_MESSAGE_CODE = "cod";
        final String TMD_MOVIE_ID = "id";
        final String TMD_MOVIE_TITLE = "original_title";
        final String TMD_MOVIE_POSTER = "poster_path";
        final String TMD_MOVIE_BACKDROP = "backdrop_path";
        final String TMD_MOVIE_OVERVIEW = "overview";
        final String TMD_MOVIE_RELEASE_DATE = "release_date";
        final String TMD_MOVIE_RATING = "vote_average";
        final String TMD_MOVIE_POSTER_PATH = TMD_BASE_URL + '/' + TMD_POSTER_SIZE + '/';
        final String TMD_MOVIE_BACKDROP_PATH = TMD_BASE_URL + '/' + TMD_BACKDROP_SIZE;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(TMD_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(TMD_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            String id;
            String title;
            String posterPath;
            String overview;
            String releaseDate;
            String backdropPath;
            String rating;

            /* Get the JSON object representing the day */
            JSONObject movie = movieArray.getJSONObject(i);

            id = movie.getString(TMD_MOVIE_ID);
            title = movie.getString(TMD_MOVIE_TITLE);
            posterPath = TMD_MOVIE_POSTER_PATH + movie.getString(TMD_MOVIE_POSTER);
            overview = movie.getString(TMD_MOVIE_OVERVIEW);
            releaseDate = movie.getString(TMD_MOVIE_RELEASE_DATE);
            rating = movie.getString(TMD_MOVIE_RATING);
            backdropPath = TMD_MOVIE_BACKDROP_PATH  + movie.getString(TMD_MOVIE_BACKDROP);

            Movie mMovie = new Movie(id, title, posterPath, overview, releaseDate, backdropPath, rating);
            movieList.add(mMovie);
        }

        return movieList;
    }
}