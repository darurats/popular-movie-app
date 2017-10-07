package com.darurats.android.popularmovies.ui;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darurats.android.popularmovies.R;
import com.darurats.android.popularmovies.adapters.MovieAdapter;
import com.darurats.android.popularmovies.models.Movie;
import com.darurats.android.popularmovies.services.MovieService;
import com.darurats.android.popularmovies.services.MovieClient;
import com.darurats.android.popularmovies.utils.MovieConstants;
import com.darurats.android.popularmovies.views.StatefulRecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class MoviesFragment extends Fragment
        implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final int POPULAR_MOVIES_LOADER_ID = 1;

    private MovieClient movieClient;

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.rv_movies)
    StatefulRecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private Unbinder unBinder;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Once all of our views are setup, we can load the movie data. */
        if (savedInstanceState != null) {
            ArrayList<Movie> mMovies = savedInstanceState.getParcelableArrayList(MovieConstants.MOVIES_TAG);
            mMovieAdapter.setMovieData(mMovies);
        } else {
            loadMovieData();
        }
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
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        mMovieAdapter = new MovieAdapter(this);

        // Set CustomMovieAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mMovieAdapter);

        movieClient = MovieService.createService(MovieClient.class);

        return rootView;
    }

    /**
     * This method will get the user's preferred location for movie, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();

        getActivity().getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_ID, null, this);
    }

    /**
     * This method is overridden by our MoviesActivity class in order to handle RecyclerView item
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
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getActivity()) {

            ArrayList<Movie> mMoviesJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mMoviesJson != null) {
                    deliverResult(mMoviesJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                String sort = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.pref_sorts_key), getString(R.string.pref_sorts_popular));

                Call<Movie.Response> call = movieClient.loadMovies(sort);

                try {
                    Response<Movie.Response> response = call.execute();
                    return response.body().movies;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Movie> data) {
                mMoviesJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (data != null) {
            showMovieDataView();
            mMovieAdapter.setMovieData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        ArrayList<Movie> mMovies = mMovieAdapter.getMovies();

        if(mMovies != null){
            savedInstanceState.putParcelableArrayList(MovieConstants.MOVIES_TAG, mMovies);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }
}