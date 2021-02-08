package com.example.gezijoke.ui.login;

import android.content.Context;
import android.content.Intent;

import androidx.constraintlayout.motion.widget.KeyCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gezijoke.model.User;
import com.example.libcommon.PixUtils;
import com.example.libnetwork.PostRequest;
import com.example.libnetwork.cache.CacheManager;

public class UserManager {

    public static final String KEY_CACHE_USER = "cache_user";

    private static UserManager userManager = new UserManager();

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private User mUser;

    public static UserManager get(){
        return userManager;
    }


    private UserManager() {
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (null!=cache&&cache.expires_time<System.currentTimeMillis()){
            mUser=cache;
        }
    }

    public void save(User user) {
        mUser = user;
        CacheManager.save(KEY_CACHE_USER, user);
        if (userLiveData.hasObservers()) {
            userLiveData.postValue(user);
        }
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActiivty.class);
        context.startActivity(intent);
        return userLiveData;
    }

    public boolean isLogin() {
        return mUser == null ? false : mUser.expires_time < System.currentTimeMillis();
    }

    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public long getUserId() {
        return isLogin() ? mUser.userId : 0;
    }
}
