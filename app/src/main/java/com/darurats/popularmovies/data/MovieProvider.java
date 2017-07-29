package com.darurats.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * This class serves as the ContentProvider for all of Popular Movie's data. This class allows us to
 * bulkInsert data, query data, and delete data.
 * <p>
 * Although ContentProvider implementation requires the implementation of additional methods to
 * perform single inserts, updates, and the ability to get the type of the data from a URI.
 * However, here, they are not implemented for the sake of brevity and simplicity. If you would
 * like, you may implement them on your own. However, we are not going to be teaching how to do
 * so in this course.
 */
public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_WEATHER and
     * CODE_WEATHER_WITH_DATE constants defined above.
     * <p>
     * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
     * expressions instead? After all, we really just need to match some patterns, and we can
     * use regular expressions to do that right?" Because you're not crazy, that's why.
     * <p>
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automagically. Remember, the best programmers try
     * to never reinvent the wheel. If there is a solution for a problem that exists and has
     * been tested and proven, you should almost always use it unless there is a compelling
     * reason not to.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_WEATHER and CODE_WEATHER_WITH_DATE
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     * <p>
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     * <p>
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Handles query requests from clients. We will use this method in Popular Movies to query for all
     * of our weather data as well as to query for the weather on a particular day.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case CODE_MOVIE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                String mSelection = "id=?";
                String[] mSelectionArgs = new String[]{id};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    /**
     * Insert to handle requests to insert a single new row of data
     *
     * @param uri    The URI of the insertion request. This must not be null.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be null
     * @return nothing in Popular Movies, but normally the URI for the newly inserted item.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case CODE_MOVIE:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    /**
     * In Popular Movies, we aren't going to do anything with this method. However, we are required to
     * override it as MovieProvider extends ContentProvider and getType is an abstract method in
     * ContentProvider. Normally, this method handles requests for the MIME type of the data at the
     * given URI. For example, if your app provided images at a particular URI, then you would
     * return an image URI from this method.
     *
     * @param uri the URI to query.
     * @return nothing in Popular Movies, but normally a MIME type string, or null if there is no type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Popular Movies.");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Popular Movies");
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}