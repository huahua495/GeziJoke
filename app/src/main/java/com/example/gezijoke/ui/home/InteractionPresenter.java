package com.example.gezijoke.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.example.gezijoke.model.Feed;
import com.example.gezijoke.model.User;
import com.example.gezijoke.ui.login.UserManager;
import com.example.libcommon.AppGlobals;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallBack;

public class InteractionPresenter {

    private static final String URL_TOGGLE_FEED_LIKE = "/ugc/toggleFeedLike";
    private static final String URL_TOGGLE_FEED_DISS = "/ugc/hasDissFeed";

    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getsApplication());
            userLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (null != user) {
                        toggleFeedLikeInternal(feed);
                    }
                    userLiveData.removeObserver(this);
                }
            });
            return;
        }

        toggleFeedLikeInternal(feed);
    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIKE)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            Boolean hasLiked = response.body.getBoolean("hasLiked")
                                    .booleanValue();
                            feed.getUgc().setHasLiked(hasLiked);
                        }
                    }
                });
    }

    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getsApplication());
            userLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (null != user) {
                        toggleFeedDissInternal(feed);
                    }
                    userLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedDissInternal(feed);
    }

    private static void toggleFeedDissInternal(Feed feed) {

        ApiService.get(URL_TOGGLE_FEED_DISS)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            Boolean hasDiss = response.body.getBoolean("hasdiss")
                                    .booleanValue();

                            feed.getUgc().setHasdiss(hasDiss);
                        }
                    }
                });
    }
}
