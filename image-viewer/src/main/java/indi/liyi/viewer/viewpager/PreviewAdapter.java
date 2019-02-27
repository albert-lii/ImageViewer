package indi.liyi.viewer.viewpager;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import indi.liyi.viewer.ViewerWrapper;
import indi.liyi.viewer.imgpg.ImagePager;

/**
 * 预览适配器
 */
public class PreviewAdapter extends PagerAdapter {
    // 无效值
    private final int INVALID_VALUE = -100;

    private int mStartPosition;
    private int mItemCount;
    // 第一个展示的 View
    private ImagePager mStartItem;
    // item 集合（在 item 被移除后，会被重复使用）
    private List<ImagePager> mActiveViews;
    private ViewerWrapper mWrapper;

    public PreviewAdapter(ViewerWrapper wrapper) {
        this.mWrapper = wrapper;
        mActiveViews = new ArrayList<>();
    }

    public void setStartItem(ImagePager item) {
        mStartPosition = item.getPosition();
        // 提前创建 item，用作执行图片浏览器的开启动画
        mStartItem = item;
    }

    public void setItemCount(int count) {
        this.mItemCount = count;
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        ImagePager item = null;
        if (mStartPosition == position) {
            item = mStartItem;
            mActiveViews.add(item);
            // 无效化起始位置值，防止重复执行此方法
            mStartPosition = INVALID_VALUE;
        } else if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                ImagePager tempItem = mActiveViews.get(i);
                if (tempItem.getParent() == null) {
                    item = mWrapper.setupItemConfig(position, tempItem);
                    break;
                }
            }
        }
        if (item == null) {
            item = mWrapper.createItem(position);
            mActiveViews.add(item);
        }
        // 加载页面
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 回收图片，释放内存
        if (object != null) {
            ((ImagePager) object).recycle();
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
     * 根据 position 获取 item
     */
    public ImagePager getViewByPosition(int position) {
        ImagePager item = null;
        if (mStartPosition == INVALID_VALUE) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                if (mActiveViews.get(i).getId() == position) {
                    item = mActiveViews.get(i);
                    break;
                }
            }
        } else if (mStartPosition == position) {
            item = mStartItem;
        }
        return item;
    }

    public void clear() {
        if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                ImagePager item = mActiveViews.get(i);
                item.recycle();
            }
            mActiveViews.clear();
        }
        if (mStartItem != null) {
            mStartItem.recycle();
            mStartItem = null;
        }
        mStartPosition = INVALID_VALUE;
    }
}
