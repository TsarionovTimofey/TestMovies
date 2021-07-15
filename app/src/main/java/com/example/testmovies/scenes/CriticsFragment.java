package com.example.testmovies.scenes;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.testmovies.R;
import com.example.testmovies.adapters.CriticsAdapter;
import com.example.testmovies.adapters.items.ItemType;
import com.example.testmovies.pojo.critic.Critic;

import java.util.HashMap;

public class CriticsFragment extends Fragment {

    private ProgressBar progressBarLoading;
    private SwipeRefreshLayout pullToRefresh;
    private EditText editTextEnterQuery;
    private RecyclerView recyclerViewCritics;
    private CriticsAdapter criticsAdapter;
    private SharedViewModel viewModel;

    View includeQueryEditTextCritics;

    private Bundle bundleCriticActivity = new Bundle();

    private Boolean blockInitialLoadData = false;
    private Boolean blockChangeOffset = false;

    private Parcelable recyclerViewStateCritics;

    private int offset = 0;
    private String query = "";

    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_critics, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //SaveState
        if (viewModel.getOffsetStateCritics() != null) {
            offset = viewModel.getOffsetStateCritics().get("offset");
        }
        if (viewModel.getBlockInitialLoadDataStateCritics() != null) {
            blockChangeOffset = viewModel.getBlockInitialLoadDataStateCritics().get("blockChangeOffset");
        }

        navController = NavHostFragment.findNavController(this);

        //RecyclerView
        recyclerViewCritics = view.findViewById(R.id.recyclerViewCriticsFragment);
        criticsAdapter = new CriticsAdapter();
        recyclerViewCritics.setAdapter(criticsAdapter);
        changeOrientationRecycler(view);

        //SaveState Recycler
        if (viewModel.getRecyclerViewCriticsState() != null) {
            recyclerViewStateCritics = viewModel.getRecyclerViewCriticsState();
            recyclerViewCritics.getLayoutManager().onRestoreInstanceState(recyclerViewStateCritics);
        }

        //FooterClickListener
        criticsAdapter.setOnFooterClickListener(position -> {
            offset += 20;
            blockInitialLoadData = true;
            blockChangeOffset = false;
            loadData();
        });

        //PosterClickListener
        criticsAdapter.setOnCriticClickListener(critic -> {
            if (critic != null) {
                Critic critic1 = (Critic) critic;
                bundleCriticActivity.putString("displayName", critic1.getDisplayName());
                navController.navigate(R.id.criticActivity, bundleCriticActivity);
            }
        });


        //ProgressBarLoading
        progressBarLoading = view.findViewById(R.id.progressBarLoadingCriticsFragment);


        //Initial load data
        if (viewModel.getBlockInitialLoadDataStateCritics() != null) {
            blockInitialLoadData = viewModel.getBlockInitialLoadDataStateCritics().get("blockInitialLoadData");
        }
        if (!blockInitialLoadData) {
            loadData();
            blockInitialLoadData = true;
        }

        //live data loading
        viewModel.getCritics().observe(getViewLifecycleOwner(), critics -> {
            criticsAdapter.setCritics(critics);
            progressBarLoading.setVisibility(View.GONE);

            if (pullToRefresh != null) {
                pullToRefresh.setRefreshing(false);
            }
        });

        //Query
        includeQueryEditTextCritics = view.findViewById(R.id.includeQueryEditTextCriticsFragment);
        editTextEnterQuery = includeQueryEditTextCritics.findViewById(R.id.editTextEnterQuery);
        if (viewModel.getQueryStateCritics() != null) {
            query = viewModel.getQueryStateCritics().get("query");
            editTextEnterQuery.setText(query);
        }

        //PullToRefresh
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            offset = 0;
            blockChangeOffset = false;
            query = editTextEnterQuery.getText().toString();
            criticsAdapter.clear();
            loadData();
        });

        setVisibilityEditText();

        return view;
    }

    private void loadData() {
        viewModel.loadDataCritics(query, offset);
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        if (!blockChangeOffset) {
            offset += 20;
        }
        blockChangeOffset = true;
        saveState();
        super.onStop();
    }


    public void saveState() {
        HashMap<String, Integer> offsetState = new HashMap<>();
        HashMap<String, String> queryState = new HashMap<>();
        HashMap<String, Boolean> blockInitialLoadDataState = new HashMap<>();

        offsetState.put("offset", offset);
        queryState.put("query", query);

        blockInitialLoadDataState.put("blockInitialLoadData", blockInitialLoadData);
        blockInitialLoadDataState.put("blockChangeOffset", blockChangeOffset);

        viewModel.setOffsetStateCritics(offsetState);
        viewModel.setQueryStateCritics(queryState);
        viewModel.setBlockInitialLoadDataStateCritics(blockInitialLoadDataState);

        recyclerViewStateCritics = recyclerViewCritics.getLayoutManager().onSaveInstanceState();
        viewModel.setRecyclerViewCriticsState(recyclerViewStateCritics);
    }

    private void setVisibilityEditText() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            includeQueryEditTextCritics.setVisibility(View.VISIBLE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            includeQueryEditTextCritics.setVisibility(View.GONE);
        }
    }

    private void changeOrientationRecycler(View view) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return criticsAdapter.getItemViewType(position) == ItemType.TYPE_FOOTER ? 2 : 1;
                }
            });
            recyclerViewCritics.setLayoutManager(gridLayoutManager);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return criticsAdapter.getItemViewType(position) == ItemType.TYPE_FOOTER ? 3 : 1;
                }
            });
            recyclerViewCritics.setLayoutManager(gridLayoutManager);
        }
    }

}