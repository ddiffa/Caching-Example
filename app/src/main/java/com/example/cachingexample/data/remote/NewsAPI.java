package com.example.cachingexample.data.remote;

import android.util.Log;

import com.example.cachingexample.MyApplication;
import com.example.cachingexample.data.model.ArticleResponse;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public enum NewsAPI {
    INSTANCE;
    private Api api;
    private static final String TAG = "ServerGenerator";
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final long cacheSize = 5 * 1024 * 1024; // 5 MB

    private static final String BASE_URL = "https:/newsapi.org/";

    NewsAPI() {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(makeLoggingInterceptor(true))
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offlineInterceptor())
                .build();

        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Api.class);
    }

    private Cache cache() {
        return new Cache(new File(MyApplication.getInstance().getCacheDir(), "someIdentifier"), cacheSize);
    }

    private HttpLoggingInterceptor makeLoggingInterceptor(boolean isDebug) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(isDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

    private Interceptor offlineInterceptor() {
        return chain -> {
            Log.d(TAG, "offline interceptor: called");
            Request mRequest = chain.request();

            if (!MyApplication.hasNetwork()) {
                CacheControl mCacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                mRequest = mRequest.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(mCacheControl)
                        .build();
            }
            return chain.proceed(mRequest);
        };
    }


    private static Interceptor networkInterceptor() {
        return chain -> {
            Log.d(TAG, "network interceptor: called.");

            Response mResponse = chain.proceed(chain.request());

            CacheControl mCacheControl = new CacheControl.Builder()
                    .maxAge(5, TimeUnit.SECONDS)
                    .build();

            return mResponse.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, mCacheControl.toString())
                    .build();
        };
    }

    public Observable<ArticleResponse> getArticles(String sources, String apiKey) {
        return api.getArticle(sources, apiKey);
    }

    private interface Api {

        @GET("v2/top-headlines")
        Observable<ArticleResponse> getArticle(@Query("sources") String country,
                                               @Query("apiKey") String apiKey);
    }
}

