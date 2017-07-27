package com.darurats.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darurats.popularmovies.App;
import com.darurats.popularmovies.R;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.utils.MovieAPI;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private Movie mMovie;

    private static final String TAG = DetailFragment.class.getSimpleName();

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

        fetchReviews();
        fetchTrailers();

        return rootView;
    }

    private void fetchReviews() {
        Call<MovieAPI.Reviews> reviewCall = App.getMovieClient().getMovieAPI().loadReviews(mMovie.getId(), "ce4303a68e9aed6532239f50db805da8");
        reviewCall.enqueue(new Callback<MovieAPI.Reviews>() {
            @Override
            public void onResponse(Call<MovieAPI.Reviews> call, Response<MovieAPI.Reviews> response) {
                Log.v(TAG, "Testing Reviews " + response);
            }

            @Override
            public void onFailure(Call<MovieAPI.Reviews> call, Throwable t) {

            }
        });
    }

    private void fetchTrailers() {
        Call<MovieAPI.Trailers> trailerCall = App.getMovieClient().getMovieAPI().loadTrailers(mMovie.getId(), "ce4303a68e9aed6532239f50db805da8");
        trailerCall.enqueue(new Callback<MovieAPI.Trailers>() {
            @Override
            public void onResponse(Call<MovieAPI.Trailers> call, Response<MovieAPI.Trailers> response) {
                Log.v(TAG, "Testing Trailers " + response);
            }

            @Override
            public void onFailure(Call<MovieAPI.Trailers> call, Throwable t) {

            }
        });
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
