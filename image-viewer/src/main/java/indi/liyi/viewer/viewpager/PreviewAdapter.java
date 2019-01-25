package indi.liyi.viewer.viewpager;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.ImageViewerAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * 预览适配器
 */
public class PreviewAdapter extends PagerAdapter {
    // 无效值
    private final int INVALID_VALUE = -1;
    // 预览的起始位置
    private int mStartPosition;
    // 第一个展示的 View
    private ScaleImagePager mStartItem;
    // 图片资源
    private List mSourceList;
    // item 集合（在 item 被移除后，会被重复使用）
    private List<ScaleImagePager> mActiveViews;
    private ImageViewerAttacher mAttacher;

    public PreviewAdapter(ImageViewerAttacher attacher) {
        this.mAttacher = attacher;
        mActiveViews = new ArrayList<>();
    }

    public void setStartItem(ScaleImagePager item) {
        mStartPosition = item.getPosition();
        // 提前创建 item，用作执行图片浏览器的开启动画
        mStartItem = item;
    }

    /**
     * 设置图片资源
     */
    public void setSource(List list) {
        this.mSourceList = list;
    }

    @Override
    public int getCount() {
        return mSourceList != null ? mSourceList.size() : 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        ScaleImagePager item = null;
        if (mStartPosition == position) {
            item = mStartItem;
            mActiveViews.add(item);
            // 无效化起始位置值，防止重复执行此方法
            mStartPosition = INVALID_VALUE;
        } else if (mActiveViews != null && mActiveViews.size() > 0) {
            for (int i = 0, len = mActiveViews.size(); i < len; i++) {
                ScaleImagePager tempItem = mActiveViews.get(i);
                if (tempItem.getParent() == null) {
                    item = mAttacher.setupItemConfig(position, tempItem);
                    break;
                }
            }
        }
        if (item == null) {
            item = mAttacher.createItem(position);
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
     * 根据 position 获取 item
     */
    public ScaleImagePager getViewByPosition(int position) {
        ScaleImagePager item = null;
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
                ScaleImagePager item = mActiveViews.get(i);
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
