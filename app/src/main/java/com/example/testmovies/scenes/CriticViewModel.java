package com.example.testmovies.scenes;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testmovies.adapters.items.FooterNext;
import com.example.testmovies.adapters.items.ItemType;
import com.example.testmovies.api.ApiFactory;
import com.example.testmovies.api.ApiService;
import com.example.testmovies.pojo.critic.Critic;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CriticViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<ItemType>> itemsLiveData = new MutableLiveData<>();
    private List<ItemType> items = new ArrayList<>();
    private Critic critic = new Critic();

    public MutableLiveData<List<ItemType>> getItemsLiveData() {
        return itemsLiveData;
    }

    //Critic
    public void loadDataCritic(String reviewer, int offset) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        Disposable disposable = apiService.getCritics(reviewer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(criticResponse -> {
                    if (offset == 0) {
                        items.clear();
                        itemsLiveData.setValue(items);
                    }
                    if (criticResponse.getCritics() != null && offset == 0) {
                        critic = (criticResponse.getCritics().get(0));
                        items.add(critic);
                        itemsLiveData.setValue(items);
                    }
                }, throwable -> Log.i("errorLoadDataCritic", throwable.getMessage()));

        compositeDisposable.add(disposable);
    }

    //ReviewsByCritic
    public void loadDataReviewsByCritic(int offset, String reviewer) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        Disposable disposable = apiService.getReviewsByCritic(offset, reviewer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewResponse -> {
                    if (reviewResponse.getReviews() != null) {
                        if (items.size() >= 20) {
                            items.remove(items.size() - 1);
                        }
                        items.addAll(reviewResponse.getReviews());
                        if (items.size() >= 20) {
                            items.add(new FooterNext());
                        }
                        itemsLiveData.setValue(items);
                    }
                    if (reviewResponse.getReviews() == null && items.size() == 1) {
                        itemsLiveData.setValue(items);
                    }
                    if (reviewResponse.getReviews() == null && items.size() == 0) {
                        itemsLiveData.setValue(items);
                    }
                    if (reviewResponse.getReviews() == null && items.get(items.size() - 1).getItemType() == ItemType.TYPE_FOOTER) {
                        items.remove(items.size() - 1);
                        itemsLiveData.setValue(items);
                    }

//                    Log.i("error", " offset " +  offset);
//                    Log.i("error", " item type " +  (items.get(items.size() - 1).getItemType()));
//                    Log.i("error", " response " + (reviewResponse.getReviews().size()));
//                    Log.i("error", " size " + (items.size()));

                }, throwable -> Log.i("errorLoadDataReviewByCr", throwable.getMessage()));

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        if (compositeDisposable != null)
            compositeDisposable.dispose();
        super.onCleared();
    }
}
