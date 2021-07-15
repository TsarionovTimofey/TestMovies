package com.example.testmovies.scenes;

import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testmovies.adapters.items.FooterNext;
import com.example.testmovies.adapters.items.ItemType;
import com.example.testmovies.api.ApiFactory;
import com.example.testmovies.api.ApiService;
import com.example.testmovies.pojo.critic.Critic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SharedViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<ItemType>> reviews = new MutableLiveData<>();
    private MutableLiveData<List<ItemType>> critics = new MutableLiveData<>();


    private List<ItemType> criticsRaw = new ArrayList<>();
    private List<ItemType> reviewsRaw = new ArrayList<>();


    public MutableLiveData<List<ItemType>> getReviews() {
        return reviews;
    }

    public MutableLiveData<List<ItemType>> getCritics() {
        return critics;
    }

    //Reviews
    private HashMap<String, String> publicationDateQueryStateReviews;
    private HashMap<String, Boolean> blockInitialLoadDataStateReviews;
    private HashMap<String, Integer> offsetStateReviews;

    Parcelable recyclerViewReviewsState;

    //Critics
    private HashMap<String, String> queryStateCritics;
    private HashMap<String, Boolean> blockInitialLoadDataStateCritics;
    private HashMap<String, Integer> offsetStateCritics;

    Parcelable recyclerViewCriticsState;


    //Reviews
    public Parcelable getRecyclerViewReviewsState() {
        return recyclerViewReviewsState;
    }

    public void setRecyclerViewReviewsState(Parcelable recyclerViewReviewsState) {
        this.recyclerViewReviewsState = recyclerViewReviewsState;
    }

    public HashMap<String, String> getPublicationDateQueryStateReviews() {
        return publicationDateQueryStateReviews;
    }

    public void setPublicationDateQueryStateReviews(HashMap<String, String> publicationDateQueryStateReviews) {
        this.publicationDateQueryStateReviews = publicationDateQueryStateReviews;
    }

    public HashMap<String, Boolean> getBlockInitialLoadDataStateReviews() {
        return blockInitialLoadDataStateReviews;
    }

    public void setBlockInitialLoadDataStateReviews(HashMap<String, Boolean> blockInitialLoadDataStateReviews) {
        this.blockInitialLoadDataStateReviews = blockInitialLoadDataStateReviews;
    }

    public HashMap<String, Integer> getOffsetStateReviews() {
        return offsetStateReviews;
    }

    public void setOffsetStateReviews(HashMap<String, Integer> offsetStateReviews) {
        this.offsetStateReviews = offsetStateReviews;
    }

    //Critics
    public Parcelable getRecyclerViewCriticsState() {
        return recyclerViewCriticsState;
    }

    public void setRecyclerViewCriticsState(Parcelable recyclerViewCriticsState) {
        this.recyclerViewCriticsState = recyclerViewCriticsState;
    }

    public HashMap<String, Integer> getOffsetStateCritics() {
        return offsetStateCritics;
    }

    public void setOffsetStateCritics(HashMap<String, Integer> offsetStateCritics) {
        this.offsetStateCritics = offsetStateCritics;
    }

    public HashMap<String, String> getQueryStateCritics() {
        return queryStateCritics;
    }

    public void setQueryStateCritics(HashMap<String, String> queryStateCritics) {
        this.queryStateCritics = queryStateCritics;
    }

    public HashMap<String, Boolean> getBlockInitialLoadDataStateCritics() {
        return blockInitialLoadDataStateCritics;
    }

    public void setBlockInitialLoadDataStateCritics(HashMap<String, Boolean> blockInitialLoadDataStateCritics) {
        this.blockInitialLoadDataStateCritics = blockInitialLoadDataStateCritics;
    }

    //Reviews
    public void loadDataReviews(int offset, String publicationDate, String query) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        Disposable disposable = apiService.getReviews(offset, publicationDate, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewResponse -> {
                    if (offset == 0) {
                        reviewsRaw.clear();
                        reviews.setValue(reviewsRaw);
                    }
                    if (reviewResponse.getReviews() != null) {
                        if (reviewsRaw.size() >= 19) {
                            reviewsRaw.remove(reviewsRaw.size() - 1);
                        }
                        reviewsRaw.addAll(reviewResponse.getReviews());
                        if (reviewsRaw.size() >= 19) {
                            reviewsRaw.add(new FooterNext());
                        }
                        reviews.setValue(reviewsRaw);
                    }
                    if (reviewResponse.getReviews() == null && reviewsRaw.isEmpty()) {
                        reviews.setValue(new ArrayList<>());
                    }
                }, throwable -> Log.i("errorLoadDataReviews", throwable.getMessage()));

        compositeDisposable.add(disposable);
    }

    //Critics
    public void loadDataCritics(String reviewer, int offset) {

        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        Disposable disposable = apiService.getCritics(reviewer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(criticResponse -> {

                    if (offset == 0) {
                        criticsRaw.clear();
                        critics.setValue(criticsRaw);
                    }
                    if (criticResponse.getCritics() != null) {
                        List<Critic> criticsByStorage = downloadByParts(reviewer, offset, criticResponse.getCritics());

                        if (criticsRaw.size() >= 19) {
                            criticsRaw.remove(criticsRaw.size() - 1);
                        }
                        criticsRaw.addAll(criticsByStorage);
                        if (criticsRaw.size() >= 19) {
                            criticsRaw.add(new FooterNext());
                        }
                        critics.setValue(criticsRaw);

                        if (criticResponse.getCritics() == null && criticsRaw.isEmpty()) {
                            critics.setValue(new ArrayList<>());
                        }
                    }

                }, throwable -> Log.i("errorLoadDataCritics", throwable.getMessage()));

        compositeDisposable.add(disposable);
    }


    //From the api we get a JSON that contains 82 critics
    // and there are no parameters for getting data in parts,
    // so this method emulates getting data in parts from api

    private List<Critic> downloadByParts(String reviewer, int offset, List<Critic> criticsFromApi) {
        List<Critic> raw = criticsFromApi;
        List<Critic> criticsStorage = new ArrayList<>();

        if (offset == 0) {
            if (raw.size() > 19) {
                for (int i = 0; i < 20; i++) criticsStorage.add(raw.get(i));
            } else {
                criticsStorage.addAll(raw);
            }
        } else {
            if (raw.size() < offset + 20) {
                for (int i = offset; i < raw.size(); i++) criticsStorage.add(raw.get(i));
            } else {
                for (int i = offset; i < offset + 20; i++) criticsStorage.add(raw.get(i));
            }

        }

        return criticsStorage;
    }


}
