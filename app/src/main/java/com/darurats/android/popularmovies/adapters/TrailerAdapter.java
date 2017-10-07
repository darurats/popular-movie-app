package com.darurats.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darurats.android.popularmovies.R;
import com.darurats.android.popularmovies.models.Trailer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link TrailerAdapter} exposes a list of trailers to a
 * {@link RecyclerView}
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> mTrailerData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final TrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    /**
     * Creates a TrailerAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @BindView(R.id.tv_trailer_name) TextView mTrailerTextView;

        public TrailerAdapterViewHolder(View view) {
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

            Trailer trailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(trailer);
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
     * @return A new TrailerAdapterViewHolder that holds the View for each list item
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the trailer
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                               contents of the item at the given position in the data set.
     * @param position               The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        Trailer mTrailer = mTrailerData.get(position);

        trailerAdapterViewHolder.mTrailerTextView.setText(mTrailer.getName());
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our trailer
     */
    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    /**
     * This method is used to set the trailer on a TrailerAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new TrailerAdapter to display it.
     *
     * @param trailerData The new trailer data to be displayed.
     */
    public void setTrailerData(ArrayList<Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }
}