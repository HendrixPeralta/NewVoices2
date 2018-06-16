package com.hdx.newvoices;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

//    private Context mContext;
    private ArrayList<NewsModel> mNewsModels;

    public NewsAdapter(ArrayList<NewsModel> mNewsModels) {
        this.mNewsModels = mNewsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NewsModel news = mNewsModels.get(position);

        holder.itemTitle.setText(news.getPostTitle());
        holder.itemAddress.setText(news.getPostAddress());
        holder.itemDate.setText(news.getPostDate());
        holder.itemCategory.setText(news.getPostCategory());
        holder.itemUserName.setText(news.getPostUserName());
        holder.itemDescription.setText(news.getPostDescription());
//        holder.itemImage.setImageResource(R.drawable.ic_launcher_background);
        Glide.with(holder.itemImage.getContext())
                .load(news.getPostImage())
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return mNewsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private TextView itemAddress;
        private TextView itemDate;
        private TextView itemCategory;
        private TextView itemUserName;
        private TextView itemDescription;
        private ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);

            itemTitle = itemView.findViewById(R.id.post_title);
            itemAddress = itemView.findViewById(R.id.post_address);
            itemDate = itemView.findViewById(R.id.post_date);
            itemCategory = itemView.findViewById(R.id.post_category);
            itemUserName = itemView.findViewById(R.id.post_user_name);
            itemDescription = itemView.findViewById(R.id.post_description);
            itemImage = itemView.findViewById(R.id.post_image);
        }
    }
}
