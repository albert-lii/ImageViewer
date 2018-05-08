package com.liyi.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.liyi.example.R;

import java.util.List;

/**
 * Created by albertlii on 2018/5/2.
 */

public class RecyclerAdp extends RecyclerView.Adapter {
    private List<Object> mImgList;
    private RequestOptions mOptions;
    private OnItemClickCallback mCallback;

    public RecyclerAdp() {
        mOptions = new RequestOptions()
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_placeholder);
    }

    public void setData(List<Object> list) {
        this.mImgList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_preview, parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        Glide.with(itemHolder.iv_preview.getContext())
                .load(mImgList.get(position))
                .apply(mOptions)
                .into(itemHolder.iv_preview);
        itemHolder.iv_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClick(position, itemHolder.iv_preview);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImgList != null ? mImgList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_preview;

        public ItemHolder(View itemView) {
            super(itemView);
            iv_preview = itemView.findViewById(R.id.iv_recycler);
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback clickCallback) {
        this.mCallback = clickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position,ImageView view);
    }
}
