package com.darurats.android.popularmovies.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.darurats.android.popularmovies.R;
import com.darurats.android.popularmovies.data.MovieContract;
import com.darurats.android.popularmovies.models.Movie;
import com.darurats.android.popularmovies.utils.ImageUtils;
import com.darurats.android.popularmovies.utils.MovieConstants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    Movie mMovie;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.detail_toolbar)
    Toolbar mDetailToolbar;

    @BindView(R.id.iv_backdrop)
    ImageView mBackDropImageView;

    @BindView(R.id.fab)
    FloatingActionButton mFloatingFavoriteButton;

    private Boolean mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        ButterKnife.bind(this);

        setSupportActionBar(mDetailToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle arguments = new Bundle();
        mMovie = getIntent().getExtras().getParcelable(MovieConstants.MOVIE_TAG);
        arguments.putParcelable(MovieConstants.MOVIE_TAG, mMovie);

        if (savedInstanceState == null) {
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment)
                    .commit();
        }

        if (mMovie != null) {
            mCollapsingToolbarLayout.setTitle(mMovie.getTitle());

            Picasso.with(this)
                    .load(ImageUtils.buildImageUrl(mMovie.getBackdropPath(), 780))
                    .placeholder(R.drawable.placeholder)
                    .into(mBackDropImageView);

            Cursor mCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?",
                    new String[]{mMovie.getId()},
                    null);

            if (mCursor.getCount() > 0) {
                mFavorite = true;
                mFloatingFavoriteButton.setImageResource(R.drawable.fav_add);
            } else {
                mFavorite = false;
                mFloatingFavoriteButton.setImageResource(R.drawable.fav_remove);
            }

            mCursor.close();
        }

        mFloatingFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavorite) {
                    int rowDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?",
                            new String[]{mMovie.getId()}
                    );

                    if (rowDeleted > 0) {
                        mFloatingFavoriteButton.setImageResource(R.drawable.fav_remove);
                    }
                } else {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdropPath());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getRating());

                    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        mFloatingFavoriteButton.setImageResource(R.drawable.fav_add);
                    }
                }
            }
        });
    }
}
