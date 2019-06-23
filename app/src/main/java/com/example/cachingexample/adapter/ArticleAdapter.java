package com.example.cachingexample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cachingexample.R;
import com.example.cachingexample.data.model.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.Holder> {


    private List<Article> articleList;
    private Context context;

    public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_artikel)
        ImageView imgArtikel;
        @BindView(R.id.tv_judul_artikel)
        TextView tvJudulArtikel;
        @BindView(R.id.tv_tanggal)
        TextView tvTanggal;
        @BindView(R.id.openEdukasi)
        RelativeLayout openEdukasi;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            Glide.with(context)
                    .load(articleList.get(position).getUrlToImage())
                    .into(imgArtikel);
            tvJudulArtikel.setText(articleList.get(position).getTitle());
            tvTanggal.setText(articleList.get(position).getPublishedAt());
            openEdukasi.setOnClickListener(v -> {
                Toast.makeText(context, "clicked : " + tvJudulArtikel.getText().toString(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
