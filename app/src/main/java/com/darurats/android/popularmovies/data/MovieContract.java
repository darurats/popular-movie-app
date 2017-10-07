package com.darurats.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.darurats.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        public static final String COLUMN_RATING = "rating";
    }
}