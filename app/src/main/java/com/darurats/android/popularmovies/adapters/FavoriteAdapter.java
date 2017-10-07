package com.darurats.android.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.darurats.android.popularmovies.R;
import com.darurats.android.popularmovies.data.MovieContract;
import com.darurats.android.popularmovies.models.Movie;
import com.darurats.android.popularmovies.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link FavoriteAdapter} exposes a list of movies to a
 * {@link RecyclerView}
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteAdapterViewHolder> {

    private Cursor mCursor;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final FavoriteAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface FavoriteAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * Creates a FavoriteAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public FavoriteAdapter(FavoriteAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @BindView(R.id.iv_movie_data) ImageView mMovieImageView;

        public FavoriteAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            int ratingIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
            int backdropPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);

            mCursor.moveToPosition(adapterPosition); // get to the right location in the cursor

            String id = mCursor.getString(idIndex);
            String title = mCursor.getString(titleIndex);
            String posterPath = mCursor.getString(posterPathIndex);
            String overview = mCursor.getString(overviewIndex);
            String releaseDate = mCursor.getString(releaseDateIndex);
            String rating = mCursor.getString(ratingIndex);
            String backdropPath = mCursor.getString(backdropPathIndex);

            Movie mMovie = new Movie(id, title, posterPath, overview, releaseDate, backdropPath, rating);

            mClickHandler.onClick(mMovie);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new FavoriteAdapterViewHolder that holds the View for each list item
     */
    @Override
    public FavoriteAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FavoriteAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param favoriteAdapterViewHolder The ViewHolder which should be updated to represent the
     *                               contents of the item at the given position in the data set.
     * @param position               The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(FavoriteAdapterViewHolder favoriteAdapterViewHolder, int position) {
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String posterPath = ImageUtils.buildImageUrl(mCursor.getString(posterPathIndex), 185);

        Context context = favoriteAdapterViewHolder.mMovieImageView.getContext();

        //Set values
        favoriteAdapterViewHolder.itemView.setTag(id);
        Picasso.with(context).load(posterPath)
                .placeholder(R.drawable.placeholder)
                .into(favoriteAdapterViewHolder.mMovieImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our movie
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}