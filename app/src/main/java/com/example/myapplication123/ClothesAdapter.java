package com.example.myapplication123;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {
    private List<ClothesItem> itemList;

    public ClothesAdapter(List<ClothesItem> items) {
        this.itemList = items;
    }

    public void updateList(List<ClothesItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothesItem item = itemList.get(position);
        // Glide 라이브러리로 이미지 로딩
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUrl())
                //.centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}