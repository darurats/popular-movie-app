package com.darurats.popularmovies.fragments;

import android.content.Intent;
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

public class DetailFragment extends Fragment {

    private Movie mMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView mTitleTextView = (TextView) rootView.findViewById(R.id.tv_title);
        ImageView mPosterImageView = (ImageView) rootView.findViewById(R.id.iv_poster);
        ImageView mBackdropImageView = (ImageView) rootView.findViewById(R.id.iv_backdrop);
        TextView mOverviewTextView = (TextView) rootView.findViewById(R.id.tv_overview);
        TextView mRating = (TextView) rootView.findViewById(R.id.tv_rating);
        TextView mReleaseDate = (TextView) rootView.findViewById(R.id.tv_release_date);

        if (savedInstanceState == null) {
            //collect our intent
            Bundle data = getActivity().getIntent().getExtras();
            mMovie = data.getParcelable(Intent.EXTRA_TEXT);
        }else{
            mMovie = savedInstanceState.getParcelable(Intent.EXTRA_TEXT);
        }

        if(mMovie != null) {
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
        savedInstanceState.putParcelable(Intent.EXTRA_TEXT, mMovie);
    }
}
