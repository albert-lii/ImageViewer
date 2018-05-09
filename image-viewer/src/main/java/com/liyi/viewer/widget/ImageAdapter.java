package com.liyi.viewer.widget;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.liyi.viewer.Utils;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends PagerAdapter {
    // 图片资源
    private List<Object> mImageList;
    // 活跃的 Item 视图，（此处会重复使用，节省内存开销，提高性能）
    private List<View> mActiveViews;
    // 当前显示的视图
    private View currentView;
    // 图片浏览器
    private ImageViewer imageViewer;
    // 图片起始位置
    private int mStartPosition;

    public ImageAdapter(ImageViewer imageViewer) {
        this.imageViewer = imageViewer;
        mActiveViews = new ArrayList<>();
    }

    public void setStartPosition(int position) {
        mStartPosition = position;
        // 提前创建好起始视图，用于开启动画
        View itemView = imageViewer.createItemView(position);
        imageViewer.initItemViewConfig(position, itemView);
        currentView = itemView;
    }

    public void setImageRes(List<Object> list) {
        this.mImageList = list;
    }

    @Override
    public int getCount() {
        return mImageList != null ? mImageList.size() : 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View itemView = null;
        if (mStartPosition == position) {
            itemView = currentView;
            mActiveViews.add(itemView);
            // 将 mStartPosition 无效化，防止滑动页面的时候，重新进入此方法
            mStartPosition = -1;
        } else if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                View view = mActiveViews.get(i);
                if (view.getParent() == null) {
                    itemView = view;
                    imageViewer.initItemViewConfig(position, itemView);
                    break;
                }
            }
        }
        if (itemView == null) {
            itemView = imageViewer.createItemView(position);
            imageViewer.initItemViewConfig(position, itemView);
            mActiveViews.add(itemView);
        }
        itemView.setId(position);
        // 加载页面
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 回收图片，释放内存
        if (object != null) {
            final PhotoView photoView = (PhotoView) ((FrameLayout) object).getChildAt(0);
            Utils.recycleImage(photoView);
        }
        // 移除页面
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        if (imageViewer.getCurrentPosition() == position) {
//            currentView = (View) object;
//        }
//    }

    /**
     * 根据 position 获取 Item
     *
     * @param position
     * @return
     */
    public View getViewByPosition(int position) {
        for (int i = 0, len = mActiveViews.size(); i < len; i++) {
            if (mActiveViews.get(i).getId() == position) {
                currentView = mActiveViews.get(i);
                break;
            }
        }
        return currentView;
    }

    /**
     * 根据 position 获取 PhotoView
     * <p>第一次开启浏览获取起始图片时，会先调用此方法，此时 nstantiateItem(ViewGroup container, final int position) 方法还未执行</p>
     *
     * @param position
     * @return
     */
    public PhotoView getPhotoViewByPosition(int position) {
        getViewByPosition(position);
        if (currentView != null) {
            return (PhotoView) ((FrameLayout) currentView).getChildAt(0);
        }
        return null;
    }

    /**
     * 清除图片数据
     */
    public void clear() {
        if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                View itemView = mActiveViews.get(i);
                final PhotoView photoView = (PhotoView) ((FrameLayout) itemView).getChildAt(0);
                Utils.recycleImage(photoView);
                itemView = null;
            }
            mActiveViews.clear();
        }
        if (currentView != null) {
            final PhotoView photoView = (PhotoView) ((FrameLayout) currentView).getChildAt(0);
            Utils.recycleImage(photoView);
            currentView = null;
        }
    }
}
