package com.darurats.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.FavoriteAdapter;
import com.darurats.popularmovies.data.MovieContract;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.utils.MovieConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class FavoritesFragment extends Fragment
        implements FavoriteAdapter.FavoriteAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int POPULAR_FAVORITES_LOADER_ID = 4;

    private FavoriteAdapter mFavoriteAdapter;

    @BindView(R.id.rv_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private Unbinder unBinder;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadFavoriteData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        unBinder = ButterKnife.bind(this, rootView);

        int spanCount = getContext().getResources().getInteger(R.integer.grid_span_count);

        GridLayoutManager layoutManager
                = new GridLayoutManager(getActivity(), spanCount);

        mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The FavoriteAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        mFavoriteAdapter = new FavoriteAdapter(this);

        // Set CustomFavoriteAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mFavoriteAdapter);

        return rootView;
    }

    /**
     * This method will get the user's preferred location for movie, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadFavoriteData() {
        showMovieDataView();

        getActivity().getSupportLoaderManager().restartLoader(POPULAR_FAVORITES_LOADER_ID, null, this);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movie The movie for the day that was clicked
     */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(MovieConstants.MOVIE_TAG, movie);
        startActivity(intent);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mLoadingIndicator.setVisibility(View.VISIBLE);

                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data != null) {
            // Update the data that the adapter uses to create ViewHolders
            mFavoriteAdapter.swapCursor(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteAdapter.swapCursor(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }
}