package com.example.cachingexample.feature;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cachingexample.R;
import com.example.cachingexample.adapter.ArticleAdapter;
import com.example.cachingexample.data.model.ArticleResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainPresenter.View {

    @BindView(R.id.rvArticle)
    RecyclerView rvArticle;
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainPresenter presenter;
    private ArticleAdapter adapter;
    private String API_KEY = "efd67bbc1d024b32a17469d00124ec15";
    private String source = "techcrunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainPresenter(this);
        presenter.getArticle(source, API_KEY);
    }

    @Override
    public void onSuccess(ArticleResponse articleResponse) {
        adapter = new ArticleAdapter(articleResponse.getArticles(), this);
        rvArticle.setLayoutManager(new LinearLayoutManager(this));
        rvArticle.setHasFixedSize(true);
        rvArticle.setAdapter(adapter);
    }

    @Override
    public void onError(String errorMessage) {
        Log.e(TAG, errorMessage);
    }
}
