package com.darurats.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.darurats.popularmovies.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", getIntent().getExtras().getParcelable("movie"));
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragment)
                    .commit();
        }

    }


}
