package com.darurats.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.models.Movie;
import com.darurats.popularmovies.ui.ReviewsFragment;
import com.darurats.popularmovies.ui.TrailersFragment;
import com.darurats.popularmovies.utils.MovieConstants;

import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Movie mMovie;
    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Movie movie) {
        super(fm);
        mContext = context;
        mMovie = movie;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle args = new Bundle();
        args.putParcelable(MovieConstants.MOVIE_TAG, mMovie);

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                TrailersFragment trailersFragment = new TrailersFragment();
                trailersFragment.setArguments(args);
                return trailersFragment;
            case 1:
                ReviewsFragment reviewsFragment = new ReviewsFragment();
                reviewsFragment.setArguments(args);
                return reviewsFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}