package indi.liyi.viewer.widget.viewpager;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PreviewPager extends ViewPager {
    // 是否可滑动
    private boolean isScrollable;

    public PreviewPager(Context context) {
        super(context);
    }

    public PreviewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
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
