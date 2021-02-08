package com.example.gezijoke.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.example.gezijoke.AbsViewModel;
import com.example.gezijoke.model.Feed;
import com.example.gezijoke.ui.MutableDataSource;
import com.example.gezijoke.ui.login.UserManager;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallBack;
import com.example.libnetwork.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {
    private volatile boolean witchCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();

    private AtomicBoolean loadAfter = new AtomicBoolean(false);

    @Override
    public DataSource createDataSource() {
        return mDataSource;
    }


    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    ItemKeyedDataSource<Integer, Feed> mDataSource = new ItemKeyedDataSource<Integer, Feed>() {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据

            loadData(0, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //加载分页数据的
            loadData(params.key, callback);
            witchCache = false;

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //能够向前加载数据的
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    };

    private void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key>0){
            loadAfter.set(true);
        }


        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", null)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());


        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallBack<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e("onCacheSuccess", "onCacheSuccess: " + response.body.size());
                    List<Feed> body = response.body;
                    MutableDataSource dataSource = new MutableDataSource<Integer, Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPageList(config);
                    cacheLiveData.postValue(pagedList);
                }
            });
        }
        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;

            callback.onResult(data);
            if (key > 0) {
                //通过liveData发送数据，告诉UI城市是否应该主动关闭上拉加载的分页动画
                getBoundaryPageData().postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> feedLoadCallback) {

        if (loadAfter.get()) {
            feedLoadCallback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, feedLoadCallback);
            }
        });
    }
}