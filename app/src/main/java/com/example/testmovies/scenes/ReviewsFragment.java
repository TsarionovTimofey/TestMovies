package com.example.testmovies.scenes;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.testmovies.adapters.ReviewsAdapter;
import com.example.testmovies.adapters.items.ItemType;
import com.example.testmovies.datamask.DateInputMask;

import java.util.HashMap;

public class ReviewsFragment extends Fragment {

    private ProgressBar progressBarLoading;
    private SwipeRefreshLayout pullToRefresh;
    private EditText editTextEnterQuery;
    private EditText editTextEnterDate;
    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter reviewsAdapter;
    private SharedViewModel viewModel;

    View includeQueryEditTextReviews;
    View includeDataEditTextReviews;

    private Boolean blockInitialLoadData = false;
    private Boolean blockChangeOffset = false;

    private Parcelable recyclerViewStateReviews;

    private int offset = 0;
    private String publicationDate = "";
    private String query = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //SaveState
        if (viewModel.getOffsetStateReviews() != null) {
            offset = viewModel.getOffsetStateReviews().get("offset");
        }
        if (viewModel.getBlockInitialLoadDataStateReviews() != null) {
            blockChangeOffset = viewModel.getBlockInitialLoadDataStateReviews().get("blockChangeOffset");
        }

        //RecyclerView
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviewsFragment);
        reviewsAdapter = new ReviewsAdapter();
        recyclerViewReviews.setAdapter(reviewsAdapter);
        changeOrientationRecycler(view);

        //SaveState Recycler
        if (viewModel.getRecyclerViewReviewsState() != null) {
            recyclerViewStateReviews = viewModel.getRecyclerViewReviewsState();
            recyclerViewReviews.getLayoutManager().onRestoreInstanceState(recyclerViewStateReviews);
        }

        //FooterClickListener
        reviewsAdapter.setOnFooterClickListener(position -> {
            offset += 20;
            blockInitialLoadData = true;
            blockChangeOffset = false;
            loadData();
        });

        //ProgressBarLoading
        progressBarLoading = view.findViewById(R.id.progressBarLoadingReviewsFragment);

        //Initial load data
        if (viewModel.getBlockInitialLoadDataStateReviews() != null) {
            blockInitialLoadData = viewModel.getBlockInitialLoadDataStateReviews().get("blockInitialLoadData");
        }
        if (!blockInitialLoadData) {
            loadData();
            blockInitialLoadData = true;
        }

        //Live data loading
        viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews.isEmpty()) {
                offset = 0;
            }
            reviewsAdapter.setReviews(reviews);
            progressBarLoading.setVisibility(View.GONE);
            if (pullToRefresh != null) {
                pullToRefresh.setRefreshing(false);
            }
        });

        //Query
        includeQueryEditTextReviews = view.findViewById(R.id.includeQueryEditTextReviewsFragment);
        editTextEnterQuery = includeQueryEditTextReviews.findViewById(R.id.editTextEnterQuery);
        if (viewModel.getPublicationDateQueryStateReviews() != null) {
            query = viewModel.getPublicationDateQueryStateReviews().get("query");
            editTextEnterQuery.setText(query);
        }

        //Sort Date
        includeDataEditTextReviews = view.findViewById(R.id.includeDataEditTextReviewsFragment);
        editTextEnterDate = includeDataEditTextReviews.findViewById(R.id.editTextEnterDate);
        if (viewModel.getPublicationDateQueryStateReviews() != null) {
            publicationDate = viewModel.getPublicationDateQueryStateReviews().get("publicationDate");
            editTextEnterDate.setText(viewModel.getPublicationDateQueryStateReviews().get("publicationDateMask"));
        }
        DateInputMask dateInputMask = new DateInputMask(editTextEnterDate);
        dateInputMask.listen();
        LiveData<String> liveData = dateInputMask.getLiveData();
        liveData.observe(getViewLifecycleOwner(), this::sortDate);

        //PullToRefresh
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            offset = 0;
            blockChangeOffset = false;
            query = editTextEnterQuery.getText().toString();
            reviewsAdapter.clear();
            loadData();
        });

        setVisibilityEditText(view);

        return view;
    }

    private void sortDate(String s) {
        if (s.equals("")) {
            Log.i("aaaar", "пусто");
            publicationDate = "";
        }

        if (s.length() == 14) {
            s = s.replaceAll(" ", "");
            s = s.replaceAll("/", "-");
            publicationDate = s + ":" + s;
        }
    }

    private void loadData() {
        viewModel.loadDataReviews(offset, publicationDate, query);
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        if (!blockChangeOffset) {
            offset += 20;
        }
        blockChangeOffset = true;
        saveState();
        Log.i("stop", "onStop: ");
        super.onStop();
    }


    public void saveState() {
        HashMap<String, Integer> offsetState = new HashMap<>();
        HashMap<String, String> publicationDateQueryState = new HashMap<>();
        HashMap<String, Boolean> blockInitialLoadDataState = new HashMap<>();

        offsetState.put("offset", offset);

        publicationDateQueryState.put("publicationDate", publicationDate);
        publicationDateQueryState.put("publicationDateMask", editTextEnterDate.getText().toString());
        publicationDateQueryState.put("query", query);

        blockInitialLoadDataState.put("blockInitialLoadData", blockInitialLoadData);
        blockInitialLoadDataState.put("blockChangeOffset", blockChangeOffset);

        viewModel.setOffsetStateReviews(offsetState);
        viewModel.setPublicationDateQueryStateReviews(publicationDateQueryState);
        viewModel.setBlockInitialLoadDataStateReviews(blockInitialLoadDataState);

        recyclerViewStateReviews = recyclerViewReviews.getLayoutManager().onSaveInstanceState();
        viewModel.setRecyclerViewReviewsState(recyclerViewStateReviews);
    }

    private void setVisibilityEditText(View view) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            includeDataEditTextReviews.setVisibility(View.VISIBLE);
            includeQueryEditTextReviews.setVisibility(View.VISIBLE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            includeDataEditTextReviews.setVisibility(View.GONE);
            includeQueryEditTextReviews.setVisibility(View.GONE);
        }
    }

    private void changeOrientationRecycler(View view) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewReviews.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return reviewsAdapter.getItemViewType(position) == ItemType.TYPE_FOOTER ? 2 : 1;
                }
            });
            recyclerViewReviews.setLayoutManager(gridLayoutManager);
        }
    }

}