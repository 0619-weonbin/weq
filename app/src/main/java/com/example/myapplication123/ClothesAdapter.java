package com.example.myapplication123;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {
    // ★★★ ClothesItem 대신 CloTag 리스트로 타입 변경 ★★★
    private List<CloTag> itemList;

    // ★★★ 생성자 파라미터 타입 변경 ★★★
    public ClothesAdapter(List<CloTag> items) {
        this.itemList = items;
    }

    // ★★★ updateList 메서드 파라미터 타입 변경 ★★★
    public void updateList(List<CloTag> newList) {
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
        // ★★★ ClothesItem 대신 CloTag 사용 ★★★
        CloTag item = itemList.get(position);
        // Glide 라이브러리로 이미지 로딩
        Glide.with(holder.imageView.getContext())
                .load(item.CloUrl) // ★★★ CloTag의 이미지 URL 필드인 CloUrl 사용 ★★★
                //.centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}