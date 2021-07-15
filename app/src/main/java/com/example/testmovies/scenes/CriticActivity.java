package com.example.testmovies.scenes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testmovies.R;
import com.example.testmovies.adapters.HeaderAdapter;

public class CriticActivity extends AppCompatActivity {
    private ProgressBar progressBarLoadingData;
    private ProgressBar progressBarLoadingDataCritic;
    private Toolbar toolbarWithCriticName;
    private CriticViewModel viewModel;
    private RecyclerView recyclerView;
    private HeaderAdapter adapter;
    private TextView textViewToolbarCriticName;

    private String displayName = "";
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critic);


        //getIntent
        displayName = getIntent().getStringExtra("displayName");

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HeaderAdapter();
        recyclerView.setAdapter(adapter);

        //Toolbar
        toolbarWithCriticName = findViewById(R.id.toolbarWithCriticName);
        textViewToolbarCriticName = findViewById(R.id.textViewToolbarCriticName);
        textViewToolbarCriticName.setText(displayName);
        //Toolbar

        //FooterClickListener
        adapter.setOnFooterClickListener(position -> {
            offset += 20;
            loadData();
        });

        //ProgressBar
        progressBarLoadingData = findViewById(R.id.progressBarLoadingDataInCriticActivity);
        progressBarLoadingDataCritic = findViewById(R.id.progressBarLoadingDataCriticInCriticActivity);


        //Initial load data
        viewModel = new ViewModelProvider(this).get(CriticViewModel.class);
        loadDataCritic();

        //live data loading
        viewModel.getItemsLiveData().observe(this, itemTypes -> {
            adapter.setItems(itemTypes);
            if (itemTypes.size() > 0) {
                if (itemTypes.size() == 1) {
                    progressBarLoadingDataCritic.setVisibility(View.GONE);
                    loadData();
                }
                if (itemTypes.size() > offset + 1) {
                    progressBarLoadingData.setVisibility(View.GONE);
                }

            }
        });
    }

    private void loadData() {
        viewModel.loadDataReviewsByCritic(offset, displayName);
        progressBarLoadingData.setVisibility(View.VISIBLE);
    }

    private void loadDataCritic() {
        viewModel.loadDataCritic(displayName, offset);
        progressBarLoadingDataCritic.setVisibility(View.VISIBLE);
    }

}