package com.darurats.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.darurats.popularmovies.data.TestUtilities.getStaticIntegerField;
import static com.darurats.popularmovies.data.TestUtilities.studentReadableNoSuchField;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    private static final String movieCodeVariableName = "CODE_MOVIE";
    private static int REFLECTED_MOVIE_CODE;

    private UriMatcher testMatcher;

    @Before
    public void before() {
        try {

            Method buildUriMatcher = MovieProvider.class.getDeclaredMethod("buildUriMatcher");
            testMatcher = (UriMatcher) buildUriMatcher.invoke(MovieProvider.class);

            REFLECTED_MOVIE_CODE = getStaticIntegerField(
                    MovieProvider.class,
                    movieCodeVariableName);

        } catch (NoSuchFieldException e) {
            fail(studentReadableNoSuchField(e));
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            String noBuildUriMatcherMethodFound =
                    "It doesn't appear that you have created a method called buildUriMatcher in " +
                            "the MovieProvider class.";
            fail(noBuildUriMatcherMethodFound);
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Students: This function tests that your UriMatcher returns the correct integer value for
     * each of the Uri types that our ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        /* Test that the code returned from our matcher matches the expected movie code */
        String movieUriDoesNotMatch = "Error: The CODE_MOVIE URI was matched incorrectly.";
        int actualMovieCode = testMatcher.match(TEST_MOVIE_DIR);
        int expectedMovieCode = REFLECTED_MOVIE_CODE;
        assertEquals(movieUriDoesNotMatch,
                expectedMovieCode,
                actualMovieCode);
    }
}
