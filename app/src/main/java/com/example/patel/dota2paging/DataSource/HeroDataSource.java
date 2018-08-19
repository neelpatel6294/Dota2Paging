package com.example.patel.dota2paging.DataSource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.patel.dota2paging.Model.Heroes;
import com.example.patel.dota2paging.Network.GetDataService;
import com.example.patel.dota2paging.Network.NetworkState;
import com.example.patel.dota2paging.Network.RetroClientInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeroDataSource extends ItemKeyedDataSource<Long, Heroes> {

    private static final String TAG = "HeroesDataSource";

    private GetDataService mService;
    LoadInitialParams<Long> initialParams;
    LoadParams<Long> afterParams;
    private MutableLiveData<NetworkState> networkState;
    private MutableLiveData<NetworkState> initialLoading;
    private Executor retryExecutor;


    public HeroDataSource(Executor retryExecutor) {

        mService = RetroClientInstance.getRetrofitInstance();
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
        this.retryExecutor = retryExecutor;
    }


    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }
    

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Heroes> callback) {
        final List<Heroes> heroesList = new ArrayList<>();
        initialParams = params;
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        mService.getAllData(1, params.requestedLoadSize).enqueue(new Callback<List<Heroes>>() {
            @Override
            public void onResponse(Call<List<Heroes>> call, Response<List<Heroes>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    heroesList.addAll(response.body());
                    callback.onResult(heroesList);
                    initialLoading.postValue(NetworkState.LOADED);
                    networkState.postValue(NetworkState.LOADED);
                    initialParams = null;
                } else {
                    Log.e("API CALL", response.message());
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Heroes>> call, Throwable t) {
                String errorMessage;
                errorMessage = t.getMessage();
                if (t == null) {
                    errorMessage = "unknown error";
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

            }
        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull final LoadCallback<Heroes> callback) {
        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);
        final List<Heroes> heroesList = new ArrayList();
        afterParams = params;

        networkState.postValue(NetworkState.LOADING);
        mService.getAllData(params.key, params.requestedLoadSize).enqueue(new Callback<List<Heroes>>() {
            @Override
            public void onResponse(Call<List<Heroes>> call, Response<List<Heroes>> response) {
                if (response.isSuccessful()) {
                    heroesList.addAll(response.body());
                    callback.onResult(heroesList);
                    networkState.postValue(NetworkState.LOADED);
                    afterParams = null;
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    Log.e("API CALL", response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Heroes>> call, Throwable t) {

            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Heroes> callback) {

    }

    @NonNull
    @Override
    public Long getKey(@NonNull Heroes item) {
        return item.getHeroId();
    }
}
