package indi.liyi.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;

import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.utils.glide.GlideUtil;

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
//        final List<ViewData> viewDataList = new ArrayList<>();
//        for (int i = 0; i < mSourceList.get(position).size(); i++) {
//            ViewData viewData = new ViewData();
//            viewDataList.add(viewData);
//        }

        SimpleAutoGridAdapter adapter = new SimpleAutoGridAdapter();
        adapter.setSource(mSourceList.get(position));
        adapter.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, ImageView view, int viewType) {
                GlideUtil.loadImage(itemHolder.gridView.getContext(), source, view);
            }
        });
        final int fp = position;
        itemHolder.gridView.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {
                if (mCallback != null) {
//                    for (int i = 0; i < viewDataList.size(); i++) {
//                        View child = itemHolder.gridView.getChildAt(i);
//                        int[] location = new int[2];
//                        child.getLocationOnScreen(location);
//                        viewDataList.get(position).setTargetX(location[0]);
//                        viewDataList.get(position).setTargetY(location[0] - Utils.getStatusBarHeight(itemHolder.gridView.getContext()));
//                    }
                    mCallback.onItemClick(i, mSourceList.get(fp), itemHolder.gridView);
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
        void onItemClick(int position, List<String> list, ViewGroup viewGroup);
    }
}
