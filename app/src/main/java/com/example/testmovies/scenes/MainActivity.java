package com.example.testmovies.scenes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.testmovies.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbarWithSwitcher;
    private TextView textViewToolbarReviewsSelect;
    private TextView textViewToolbarCriticsSelect;
    private TextView textViewToolbarTitle;
    private NavController navController;

    private String blockNavigation = "reviews";

    private static final String REVIEWS = "reviews";
    private static final String CRITICS = "critics";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toolbar
        toolbarWithSwitcher = findViewById(R.id.toolbarWithSwitcher);
        textViewToolbarTitle = findViewById(R.id.textViewToolbarTitle);

        textViewToolbarReviewsSelect = findViewById(R.id.textViewToolbarSelectReviews);
        textViewToolbarReviewsSelect.setOnClickListener(this::setFragment);

        textViewToolbarCriticsSelect = findViewById(R.id.textViewToolbarSelectCritics);
        textViewToolbarCriticsSelect.setOnClickListener(this::setFragment);
        //Toolbar

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);

        if (savedInstanceState != null) {
            blockNavigation = savedInstanceState.getString("blockNavigation");
            if (blockNavigation.equals(REVIEWS)) setReviewsTopBar();
            if (blockNavigation.equals(CRITICS)) setCriticsTopBar();
        }

        setVisibilityToolbar();
    }


    private void setFragment(View v) {
        //Reviews
        if (v.getId() == R.id.textViewToolbarSelectReviews && !blockNavigation.equals(REVIEWS)) {
            setReviewsTopBar();
            navController.navigate(R.id.reviewsFragment);

            //Critics
        } else if (v.getId() == R.id.textViewToolbarSelectCritics && !blockNavigation.equals(CRITICS)) {
            setCriticsTopBar();
            navController.navigate(R.id.criticsFragment);
        }
    }

    //Reviews
    private void setReviewsTopBar() {
        textViewToolbarTitle.setText(R.string.reviews_label);

        textViewToolbarReviewsSelect.setTextColor(ContextCompat.getColor(this, R.color.black));
        textViewToolbarReviewsSelect.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        textViewToolbarCriticsSelect.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        textViewToolbarCriticsSelect.setTextColor(ContextCompat.getColor(this, R.color.white));

        blockNavigation = REVIEWS;
    }

    //Critics
    private void setCriticsTopBar() {
        textViewToolbarTitle.setText(R.string.critics_label);

        textViewToolbarCriticsSelect.setTextColor(ContextCompat.getColor(this, R.color.black));
        textViewToolbarCriticsSelect.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        textViewToolbarReviewsSelect.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        textViewToolbarReviewsSelect.setTextColor(ContextCompat.getColor(this, R.color.white));

        blockNavigation = CRITICS;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("blockNavigation", blockNavigation);
        super.onSaveInstanceState(outState);
    }

    private void setVisibilityToolbar() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbarWithSwitcher.setVisibility(View.VISIBLE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbarWithSwitcher.setVisibility(View.GONE);
        }
    }


}