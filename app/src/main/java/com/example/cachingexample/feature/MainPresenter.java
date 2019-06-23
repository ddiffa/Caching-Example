package com.example.cachingexample.feature;

import android.annotation.SuppressLint;

import com.example.cachingexample.data.model.ArticleResponse;
import com.example.cachingexample.data.remote.NewsAPI;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter {
    private View view;

    public MainPresenter(View view) {
        this.view = view;
    }

    @SuppressLint("CheckResult")
    public void getArticle(String sources, String apiKey) {
        NewsAPI.INSTANCE.getArticles(sources, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articleResponse -> view.onSuccess(articleResponse),
                        throwable -> view.onError(throwable.getMessage()));
    }


    interface View {
        void onSuccess(ArticleResponse articleResponse);

        void onError(String errorMessage);
    }
}
