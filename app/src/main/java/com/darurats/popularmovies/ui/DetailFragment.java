package com.darurats.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailFragment extends Fragment {

    private Movie mMovie;

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

        return rootView;
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
