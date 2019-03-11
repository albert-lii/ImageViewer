package indi.liyi.viewer.viewpager;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * 预览器 ViewPager
 */
public class ImageViewPager extends ViewPager {
    // 是否可滑动
    private boolean isScrollable = true;
    // 上一次的触摸点坐标
    private float mLastX, mLastY;

    public ImageViewPager(Context context) {
        super(context);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isScrollable) {
            super.scrollTo(x, y);
        }
    }

    /**
     * 设置是否可滑动
     *
     * @param isScrollable
     */
    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }
}
