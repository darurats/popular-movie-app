package com.darurats.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.utils.MovieConstants;

public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Bundle arguments = new Bundle();
        mMovie = getIntent().getExtras().getParcelable(MovieConstants.MOVIE_TAG);
        arguments.putParcelable(MovieConstants.MOVIE_TAG, mMovie);

        setTitle(mMovie.getTitle());

        if (savedInstanceState == null) {
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment)
                    .commit();
        }

    }
}
