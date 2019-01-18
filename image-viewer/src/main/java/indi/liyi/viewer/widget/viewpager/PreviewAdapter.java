package indi.liyi.viewer.widget.viewpager;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import indi.liyi.viewer.widget.ScaleImagePager;
import indi.liyi.viewer.widget.ImageViewerAttacher;

import java.util.ArrayList;
import java.util.List;


public class PreviewAdapter extends PagerAdapter {
    // 无效值
    private final int INVALID_VALUE = -1;
    // 预览的起始位置
    private int mStartPosition;
    // 第一个展示的 View
    private ScaleImagePager mStartView;
    // 图片资源
    private List mImageDataList;
    // itemView 集合（在 itemView 被移除后，会被重复使用）
    private List<ScaleImagePager> mActiveViews;
    private ImageViewerAttacher mAttacher;

    public PreviewAdapter(ImageViewerAttacher attacher) {
        this.mAttacher = attacher;
        mActiveViews = new ArrayList<>();
    }

    public void setStartView(ScaleImagePager itemView) {
        mStartPosition = itemView.getPosition();
        // 提前创建 itemView，用作执行图片浏览器的开启动画
        mStartView = itemView;
    }

    /**
     * 设置图片资源
     *
     * @param list
     */
    public void setImageData(List list) {
        this.mImageDataList = list;
    }

    @Override
    public int getCount() {
        return mImageDataList != null ? mImageDataList.size() : 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        ScaleImagePager itemView = null;
        if (mStartPosition == position) {
            itemView = mStartView;
            mActiveViews.add(itemView);
            // 无效化起始位置值，防止重复执行此方法
            mStartPosition = INVALID_VALUE;
        } else if (mActiveViews != null && mActiveViews.size() > 0) {
            if (mActiveViews != null && mActiveViews.size() > 0) {
                for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                    ScaleImagePager scaleImageView = mActiveViews.get(i);
                    if (scaleImageView.getParent() == null) {
                        itemView = mAttacher.setupItemViewConfig(position, scaleImageView);
                        break;
                    }
                }
            }
        }
        if (itemView == null) {
            itemView = mAttacher.createItemView(position);
            mActiveViews.add(itemView);
        }
        // 加载页面
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 回收图片，释放内存
        if (object != null) {
            ((ScaleImagePager) object).recycle();
        }
        // 移除页面
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法，此方法会刷新所有的 item
        return POSITION_NONE;
    }

    /**
     * 根据 position 获取 itemView
     *
     * @param position
     * @return
     */
    public ScaleImagePager getViewByPosition(int position) {
        ScaleImagePager itemView = null;
        if (mStartPosition == INVALID_VALUE) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                if (mActiveViews.get(i).getId() == position) {
                    itemView = mActiveViews.get(i);
                    break;
                }
            }
        } else if (mStartPosition == position) {
            itemView = mStartView;
        }
        return itemView;
    }

    public void clear() {
        if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                ScaleImagePager itemView = mActiveViews.get(i);
                itemView.recycle();
                itemView = null;
            }
            mActiveViews.clear();
        }
        if (mStartView != null) {
            mStartView.recycle();
            mStartView = null;
        }
        mStartPosition = INVALID_VALUE;
    }
}
