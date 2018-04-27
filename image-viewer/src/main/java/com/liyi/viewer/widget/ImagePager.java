package com.liyi.viewer.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.liyi.viewer.factory.ImageDragger;


public class ImagePager extends ViewPager {
    // 图片在不关闭浏览的情况下，在 Y 轴上的最大可移动距离
    private float mMaxTranslateY;
    // 拖拽图片时的背景透明度的基数
    private float mAlphaBase;
    private float mTouchSlop;
    // 手指按下时的 X 轴坐标
    private float mDownX;
    // 手指按下时的 Y 轴坐标
    private float mDownY;

    // 图片拖拽处理类
    private ImageDragger mDragger;
    // 当前是否处于图片拖拽状态
    private boolean isDoDragging;

    public ImagePager(Context context) {
        super(context);
        init();
    }

    public ImagePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.getTouchSlop();
        isDoDragging = false;
    }

    /**
     * 设置在 Y 轴上的最大可移动距离
     *
     * @param height
     */
    public void setMaxTranslateY(final float height) {
        this.mMaxTranslateY = height / 5f;
        mAlphaBase = mMaxTranslateY * 2;
    }

    /**
     * 设置图片拖动处理类
     *
     * @param dragger
     */
    public void setImageDragger(ImageDragger dragger) {
        this.mDragger = dragger;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 是否拦截触摸事件，如果拦截，则 viewPager 的子 view 获取不到事件；不拦截则将触摸事件传递给子 view
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 当屏幕上只有一个触摸点时执行
                if (ev.getPointerCount() <= 1 && mDragger != null && mDragger.getImageScale() <= 1) {
                    float deltaX = Math.abs(ev.getX() - mDownX);
                    float deltaY = Math.abs(ev.getY() - mDownY);
                    // 如果 deltaX < deltaY ，则处于拖拽图片状态，否则处于滑动翻页状态
                    if (deltaX < deltaY) {
                        isIntercept = deltaY > mTouchSlop;
                        isDoDragging = true;
                    } else {
                        isDoDragging = false;
                    }
                }
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 此处注意，如果在 onInterceptTouchEvent(MotionEvent ev) 的 MotionEvent.ACTION_MOVE 状态以及后续状态拦截事件，
                // 则此处 MotionEvent.ACTION_DOWN 不被执行
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() == 1
                        && mDragger != null && mDragger.getImageScale() <= 1
                        && isDoDragging) {
                    mDragger.dragImage(mDownY, ev.getY(), mAlphaBase);
                }
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (mDragger != null && mDragger.getImageScale() <= 1 && isDoDragging) {
                    mDragger.releaseImage(mMaxTranslateY);
                }
                isDoDragging = false;
                mDownY = 0;
                break;
        }
        return super.onTouchEvent(ev);
    }
}
