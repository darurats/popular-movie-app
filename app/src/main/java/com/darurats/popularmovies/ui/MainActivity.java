package com.darurats.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.darurats.popularmovies.R;
import com.darurats.popularmovies.models.Movie;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectedListener{

    private static final int RESULT_SETTINGS = 100;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movies_linear_layout) != null){
            mTwoPane = true;

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                MainFragment mainFragment = new MainFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.main_container, mainFragment)
                        .commit();

                DetailFragment detailFragment = new DetailFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.detail_container, detailFragment)
                        .commit();
            }
        }else{
            mTwoPane = false;

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                MainFragment mainFragment = new MainFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.main_container, mainFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RESULT_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Fragment fragment =  getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if(!mTwoPane){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            DetailFragment detailFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable("movie", movie);
            detailFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}