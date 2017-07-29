package com.darurats.popularmovies.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.adapters.SectionsPagerAdapter;
import com.darurats.popularmovies.data.MovieContract;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.utils.ImageUtils;
import com.darurats.popularmovies.utils.MovieConstants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailFragment extends Fragment {

    private Movie mMovie;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    @BindView(R.id.tv_title)
    TextView mTitleTextView;
    @BindView(R.id.iv_poster)
    ImageView mPosterImageView;
    @BindView(R.id.tv_overview)
    TextView mOverviewTextView;
    @BindView(R.id.tv_rating)
    TextView mRating;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.btn_favorite)
    Button mFavoriteButton;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    private Unbinder unBinder;

    private Boolean mFavorite;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle data = this.getArguments();

            if (data != null) {
                mMovie = data.getParcelable(MovieConstants.MOVIE_TAG);
            }
        } else {
            mMovie = savedInstanceState.getParcelable(MovieConstants.MOVIE_TAG);
        }

        if (mMovie != null) {
            mTitleTextView.setText(mMovie.getTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mRating.setText(mMovie.getRating());
            mReleaseDate.setText(mMovie.getReleaseDate());

            Cursor mCursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?",
                    new String[]{mMovie.getId()},
                    null);

            if (mCursor.getCount() > 0) {
                mFavorite = true;
                mFavoriteButton.setBackground(getResources().getDrawable(R.drawable.fav_add));
            } else {
                mFavorite = false;
                mFavoriteButton.setBackground(getResources().getDrawable(R.drawable.fav_remove));
            }

            mCursor.close();

            Picasso.with(getActivity())
                    .load(ImageUtils.buildImageUrl(mMovie.getPosterPath(), 185))
                    .placeholder(R.drawable.placeholder)
                    .into(mPosterImageView);
        }

        mFavoriteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (mFavorite) {
                    int rowDeleted = getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?",
                            new String[]{mMovie.getId()}
                    );

                    if (rowDeleted > 0) {
                        mFavoriteButton.setBackground(getResources().getDrawable(R.drawable.fav_remove));
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

                    Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        mFavoriteButton.setBackground(getResources().getDrawable(R.drawable.fav_add));
                    }
                }
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getFragmentManager(), mMovie);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        unBinder = ButterKnife.bind(this, rootView);

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(MovieConstants.MOVIE_TAG, mMovie);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }
}
