package com.darurats.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Although these tests aren't a complete set of tests one should run on a ContentProvider
 * implementation, they do test that the basic functionality of Sunshine's ContentProvider is
 * working properly.
 * <p>
 * In this test suite, we have the following tests:
 * <p>
 *   1) A test to ensure that your ContentProvider has been properly registered in the
 *    AndroidManifest
 * <p>
 *   2) A test to determine if you've implemented the query functionality for your
 *    ContentProvider properly
 * <p>
 *   3) A test to determine if you've implemented the bulkInsert functionality of your
 *    ContentProvider properly
 * <p>
 *   4) A test to determine if you've implemented the delete functionality of your
 *    ContentProvider properly.
 * <p>
 * If any of these tests fail, you should see useful error messages in the testing console's
 * output window.
 * <p>
 * Finally, we have a method annotated with the @Before annotation, which tells the test runner
 * that the {@link #setUp()} method should be called before every method annotated with a @Test
 * annotation. In our setUp method, all we do is delete all records from the database to start our
 * tests with a clean slate each time.
 */
@RunWith(AndroidJUnit4.class)
public class TestMovieProvider {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the movie table to do so.
     */
    @Before
    public void setUp() {
        deleteAllRecordsFromMovieTable();
    }

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) Your MovieProvider was registered with the incorrect authority
     * <p>
     *   2) Your MovieProvider was not registered at all
     */
    @Test
    public void testProviderRegistry() {

        /*
         * A ComponentName is an identifier for a specific application component, such as an
         * Activity, ContentProvider, BroadcastReceiver, or a Service.
         *
         * Two pieces of information are required to identify a component: the package (a String)
         * it exists in, and the class (a String) name inside of that package.
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String movieProviderClassName = MovieProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, movieProviderClassName);

        try {

            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = MovieContract.CONTENT_AUTHORITY;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: MovieProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: MovieProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }

    /**
     * This test uses the database directly to insert a row of test data and then uses the
     * ContentProvider to read out the data. We access the database directly to insert the data
     * because we are testing our ContentProvider's query functionality. If we wanted to use the
     * ContentProvider's insert method, we would have to assume that that insert method was
     * working, which defeats the point of testing.
     * <p>
     * If this test fails, you should check the logic in your
     * {@link MovieProvider#insert(Uri, ContentValues)} and make sure it matches up with our
     * solution code.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) There was a problem inserting data into the database directly via SQLite
     * <p>
     *   2) The values contained in the cursor did not match the values we inserted via SQLite
     */
    @Test
    public void testBasicMovieQuery() {

        /* Use MovieDbHelper to get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Obtain movie values from TestUtilities */
        ContentValues testMovieValues = TestUtilities.createTestMovieContentValues();

        /* Insert ContentValues into database and get a row ID back */
        long movieRowId = database.insert(
                /* Table to insert values into */
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testMovieValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, movieRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /*
         * Perform our ContentProvider query. We expect the cursor that is returned will contain
         * the exact same data that is in testMovieValues and we will validate that in the next
         * step.
         */
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        /* This method will ensure that we  */
        TestUtilities.validateThenCloseCursor("testBasicMovieQuery",
                movieCursor,
                testMovieValues);
    }

    /**
     * This method will clear all rows from the movie table in our database.
     * <p>
     * Please note:
     * <p>
     * - This does NOT delete the table itself. We call this method from our @Before annotated
     * method to clear all records from the database before each test on the ContentProvider.
     * <p>
     * - We don't use the ContentProvider's delete functionality to perform this row deletion
     * because in this class, we are attempting to test the ContentProvider. We can't assume
     * that our ContentProvider's delete method works in our ContentProvider's test class.
     */
    private void deleteAllRecordsFromMovieTable() {
        /* Access writable database through MovieDbHelper */
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* The delete method deletes all of the desired rows from the table, not the table itself */
        database.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        /* Always close the database when you're through with it */
        database.close();
    }
}