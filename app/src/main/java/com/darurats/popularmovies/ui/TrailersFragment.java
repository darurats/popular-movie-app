package com.darurats.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.darurats.popularmovies.App;
import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.TrailerAdapter;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.models.Trailer;
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
public class TrailersFragment extends Fragment
        implements TrailerAdapter.TrailerAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Trailer>> {

    private static final int POPULAR_TRAILERS_LOADER_ID = 3;

    private static final String VIDEO_TYPE = "YouTube";

    private Movie mMovie;

    private TrailerAdapter mTrailerAdapter;

    @BindView(R.id.rv_trailers)
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

        loadTrailerData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);

        unBinder = ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The TrailerAdapter is responsible for linking our trailer data with the Views that
         * will end up displaying our trailer data.
         */
        mTrailerAdapter = new TrailerAdapter(this);

        // Set CustomTrailerAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mTrailerAdapter);

        return rootView;
    }

    /**
     * This method will get the user's preferred location for trailer, and then tell some
     * background method to get the trailer data in the background.
     */
    private void loadTrailerData() {
        showTrailerDataView();

        getActivity().getSupportLoaderManager().initLoader(POPULAR_TRAILERS_LOADER_ID, null, this);
    }

    /**
     * This method will make the View for the trailer data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showTrailerDataView() {
        /* Then, make sure the trailer data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the trailer
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
    public Loader<ArrayList<Trailer>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Trailer>>(getActivity()) {

            ArrayList<Trailer> mTrailersJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mTrailersJson != null) {
                    deliverResult(mTrailersJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Trailer> loadInBackground() {
                Call<MovieAPI.Trailers> call = App.getMovieClient().getMovieAPI().loadTrailers(mMovie.getId(), "ce4303a68e9aed6532239f50db805da8");

                try {
                    Response<MovieAPI.Trailers> response = call.execute();
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
            public void deliverResult(ArrayList<Trailer> data) {
                mTrailersJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
        if (data != null) {
            showTrailerDataView();
            mTrailerAdapter.setTrailerData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

    @Override
    public void onClick(Trailer trailer) {
        if (trailer.getSite().equals(VIDEO_TYPE)) {
            Uri fileUri = Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey());

            // view the video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(), "Unsupported video format", Toast.LENGTH_LONG).show();
        }
    }
}