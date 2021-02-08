package com.example.libnetwork;

import java.net.PortUnreachableException;

public abstract class JsonCallBack<T> {



    public void onSuccess(ApiResponse<T> response){

    }

    public void onError(ApiResponse<T> response){

    }

    public void onCacheSuccess(ApiResponse<T> response){

    }
}
