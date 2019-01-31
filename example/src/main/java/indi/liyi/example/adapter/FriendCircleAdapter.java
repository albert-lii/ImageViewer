package indi.liyi.example.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;

import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.utils.GlideUtil;

public class FriendCircleAdapter extends RecyclerView.Adapter {
    private List<List<String>> mSourceList;
    private OnItemClickCallback mCallback;

    public FriendCircleAdapter() {

    }

    public void setData(List<List<String>> list) {
        this.mSourceList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_friend_cirlce, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        SimpleAutoGridAdapter adapter = new SimpleAutoGridAdapter();
        adapter.setSource(mSourceList.get(position));
        adapter.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(int position, Object source, ImageView view, int viewType) {
                GlideUtil.loadImage(itemHolder.gridView.getContext(), source, view, new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });
            }
        });
        itemHolder.gridView.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (mCallback != null) {
                    mCallback.onItemClick(mSourceList.get(position));
                }
            }
        });
        itemHolder.gridView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mSourceList != null ? mSourceList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private AutoGridView gridView;

        public ItemHolder(View itemView) {
            super(itemView);
            gridView = itemView.findViewById(R.id.autoGrid);
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback clickCallback) {
        this.mCallback = clickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(List<String> list);
    }
}
