package com.darurats.popularmovies.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darurats.popularmovies.App;
import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.ReviewAdapter;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.models.Review;
import com.darurats.popularmovies.utils.MovieAPI;

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
public class ReviewsFragment extends Fragment
        implements ReviewAdapter.ReviewAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Review>> {

    private static final int POPULAR_REVIEWS_LOADER_ID = 2;

    private Movie mMovie;

    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.rv_reviews)
    RecyclerView mRecyclerView;

    private Unbinder unBinder;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle data = this.getArguments();

            if (data != null) {
                mMovie = data.getParcelable("movie");
            }
        } else {
            mMovie = savedInstanceState.getParcelable("movie");
        }

        loadReviewData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        unBinder = ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The ReviewAdapter is responsible for linking our review data with the Views that
         * will end up displaying our review data.
         */
        mReviewAdapter = new ReviewAdapter(this);

        // Set CustomReviewAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mReviewAdapter);

        return rootView;
    }

    /**
     * This method will get the user's preferred location for review, and then tell some
     * background method to get the review data in the background.
     */
    private void loadReviewData() {
        showReviewDataView();

        getActivity().getSupportLoaderManager().initLoader(POPULAR_REVIEWS_LOADER_ID, null, this);
    }

    /**
     * This method will make the View for the review data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showReviewDataView() {
        /* Then, make sure the review data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the review
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Review>>(getActivity()) {

            ArrayList<Review> mReviewsJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mReviewsJson != null) {
                    deliverResult(mReviewsJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Review> loadInBackground() {
                Call<MovieAPI.Reviews> call = App.getMovieClient().getMovieAPI().loadReviews(mMovie.getId(), "ce4303a68e9aed6532239f50db805da8");

                try {
                    Response<MovieAPI.Reviews> response = call.execute();
                    Log.v(getClass().getSimpleName(), "Testing " + response);
                    return response.body().results;
                } catch (IOException e ){
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Review> data) {
                mReviewsJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
        if (data != null) {
            showReviewDataView();
            Log.v(getClass().getSimpleName(), "Testing " + data);
            mReviewAdapter.setReviewData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Review>> loader) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

    @Override
    public void onClick(Review review) {

    }
}