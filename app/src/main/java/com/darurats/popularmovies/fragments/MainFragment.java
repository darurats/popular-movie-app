package com.darurats.popularmovies.fragments;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.darurats.popularmovies.DetailActivity;
import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.MovieAdapter;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.utils.MovieDBJsonUtils;
import com.darurats.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class MainFragment extends Fragment
        implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String STATE_LIST = "MOVIES";
    private static final String STATE_POSITION = "POSITION";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Once all of our views are setup, we can load the movie data. */
        if (savedInstanceState != null) {
            ArrayList<Movie> mMovies = savedInstanceState.getParcelableArrayList(STATE_LIST);
            int scrollPosition = savedInstanceState.getInt(STATE_POSITION);

            mMovieAdapter.setMovieData(mMovies);
            mRecyclerView.scrollToPosition(scrollPosition);
        } else {
            loadMovieData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

         /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) rootView.findViewById(R.id.tv_error_message_display);

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

         /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);

        return rootView;
    }

    /**
     * This method will get the user's preferred location for movie, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();

        FetchMovieTask movieTask = new FetchMovieTask();
        String sort = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sorts_key), getString(R.string.pref_sorts_popular));

        movieTask.execute(sort);
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
        intent.putExtra(Intent.EXTRA_TEXT, movie);
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

    private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            /* If there's no sort key, there's nothing to look up. */
            if (params.length == 0) {

                return null;
            }

            String sort = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sort);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                return MovieDBJsonUtils
                        .getMovieStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        ArrayList<Movie> mMovies = mMovieAdapter.getMovies();
        int scrollPosition = ((GridLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();

        if(mMovies != null){
            savedInstanceState.putParcelableArrayList(STATE_LIST, mMovies);
            savedInstanceState.putInt(STATE_POSITION, scrollPosition);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                loadMovieData();
                break;
        }
    }
}