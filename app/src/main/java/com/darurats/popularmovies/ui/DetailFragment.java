package com.darurats.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darurats.popularmovies.App;
import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.ReviewAdapter;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.models.Review;
import com.darurats.popularmovies.models.Trailer;
import com.darurats.popularmovies.utils.MovieAPI;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

public class DetailFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler{

    private static final String TAG = DetailFragment.class.getSimpleName();

    private static final int POPULAR_REVIEWS_LOADER_ID = 2;
    private static final int POPULAR_TRAILERS_LOADER_ID = 3;

    private Movie mMovie;

    private ReviewAdapter mReviewAdapter;

    final ArrayList<Review> reviewList = new ArrayList<>();

    @BindView(R.id.tv_title)
    TextView mTitleTextView;
    @BindView(R.id.iv_poster)
    ImageView mPosterImageView;
    @BindView(R.id.iv_backdrop)
    ImageView mBackdropImageView;
    @BindView(R.id.tv_overview)
    TextView mOverviewTextView;
    @BindView(R.id.tv_rating)
    TextView mRating;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.rv_reviews)
    RecyclerView mReviewRecyclerView;

    private Unbinder unBinder;

    private class Callback1 implements LoaderManager.LoaderCallbacks<ArrayList<Review>> {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
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
                mReviewAdapter.setReviewData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(POPULAR_REVIEWS_LOADER_ID, null, new Callback1());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        unBinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {
            Bundle data = this.getArguments();

            if (data != null) {
                mMovie = data.getParcelable("movie");
            }
        } else {
            mMovie = savedInstanceState.getParcelable("movie");
        }

        if (mMovie != null) {
            mTitleTextView.setText(mMovie.getTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mRating.setText(mMovie.getRating());
            mReleaseDate.setText(mMovie.getReleaseDate());

            Picasso.with(getActivity()).load(mMovie.getPosterPath()).into(mPosterImageView);
            Picasso.with(getActivity()).load(mMovie.getBackdropPath()).into(mBackdropImageView);
        }

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);

        mReviewAdapter = new ReviewAdapter(this);

        mReviewRecyclerView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onClick(Review review) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("movie", mMovie);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }
}
