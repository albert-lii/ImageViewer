package indi.liyi.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.utils.glide.GlideUtil;


public class ImageAdapter extends RecyclerView.Adapter {
    // 0: 水平方向  1: 垂直方向
    private int mOrientation;
    private List<String> mImgList;
    private OnItemClickCallback mCallback;

    public ImageAdapter(int orientation) {
        mOrientation = orientation;
    }

    public void setData(List<String> list) {
        this.mImgList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mOrientation == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_horizontal, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_vertical, parent, false);
        }
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        GlideUtil.loadImage(itemHolder.iv_pic.getContext(), mImgList.get(position), itemHolder.iv_pic);
        final int fp = position;
        itemHolder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClick(fp, itemHolder.iv_pic);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImgList != null ? mImgList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_pic;

        public ItemHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.iv_pic);
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback clickCallback) {
        this.mCallback = clickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position, ImageView view);
    }
}
