package com.example.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.example.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class Request<T, R extends Request> implements Cloneable{
    private static final String TAG = Request.class.getSimpleName();
    protected String mUrl;

    private HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();
    //仅访问本地缓存，即便本地不存在，也不访问网络
    public static final int CACHE_ONLY = 1;
    //先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅只访问服务器，不做任何存储
    public static final int NET_ONLY = 3;

    //优先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;
    private String cacheKey;
    private Type mType;
    private Class mClaz;
    private int mCacheStrategy;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_CACHE, NET_ONLY})
    public @interface CacheStrategy {

    }

    public Request(String url) {
        this.mUrl = url;
    }


    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        if (value == null) {
            return (R) this;
        }
        //int byte char short long double float boolean 和他们的包装类型，但是除了 String.class 所以要额外判断
        try {
            if (value.getClass() == String.class) {
                params.put(key, value);
            } else {
                Field field = value.getClass().getField("TYPE");
                Class claz = (Class) field.get(null);
                if (claz.isPrimitive()) {
                    params.put(key, value);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    public R responseType(Class claz) {
        mClaz = claz;
        return (R) this;
    }


    protected Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeader(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);


    private void addHeader(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

    }


    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallBack<T> callBack) {
        if (mCacheStrategy != NET_ONLY) {

            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (null != callBack) {
                        callBack.onCacheSuccess(response);
                    }
                }
            });
        }
        if (mCacheStrategy != CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> response = new ApiResponse<>();
                    response.message = e.getMessage();
                    callBack.onError(response);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> result = parseResponse(response, callBack);
                    if (result.success) {
                        callBack.onSuccess(result);
                    } else {
                        callBack.onError(result);
                    }
                }
            });
        }


    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.body = (T) cache;
        result.success = true;
        return result;
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallBack<T> callBack) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();

        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().toString();
            if (success) {
                if (null != callBack) {
                    ParameterizedType type = (ParameterizedType) callBack.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else if (mClaz != null) {
                    result.body = (T) convert.convert(content, mClaz);
                } else {
                    Log.e(TAG, "parseResponse: response parse failed");
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
        }

        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy != NET_ONLY &&
                result.success && null != result.body && result.body instanceof Serializable) {

            saveCache(result.body);
        }

        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ?
                generateCacheKey() : cacheKey;
        CacheManager.save(key, body);

    }

    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }

    public ApiResponse<T> execute() {
        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }
        try {
            Response response = getCall().execute();
            ApiResponse<T> result = parseResponse(response, null);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request<T,R>) super.clone();

    }
}

