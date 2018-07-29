package com.liyi.viewer.dragger;

import android.graphics.drawable.Drawable;

import com.liyi.viewer.ImageViewerState;
import com.liyi.viewer.widget.ScaleImageView;
import com.liyi.viewer.widget.ImageViewerAttacher;

/**
 * 图片拖拽处理类的基类
 */
public class ImageDragger {
    // 默认的背景透明度
    protected final int DEF_BACKGROUND_ALPHA = 255;

    // 在不退出浏览的情况下， Y 轴上的最大可移动距离
    protected float mMaxDisOnY;
    // 图片被拖拽时的背景透明度基数
    protected float mAlphaBase;
    // 预览背景
    protected Drawable mBackground;
    // 背景透明度
    protected float mBackgroundAlpha;
    // 预览界面的宽高
    protected float mPreviewWidth, mPreviewHeight;
    // 图片拖拽状态监听
    protected ImageDraggerStateListener mStateListener;
    protected ScaleImageView scaleImageView;
    protected ImageViewerAttacher mAttacher;


    public ImageDragger() {

    }

    public void bindScaleImageView(ScaleImageView scaleImageView) {
        this.scaleImageView = scaleImageView;
    }

    public void bindImageViewerAttacher(ImageViewerAttacher attacher) {
        this.mAttacher = attacher;
    }

    public void setBackground(Drawable drawable) {
        if (drawable != null) {
            mBackground = drawable.mutate();
        } else {
            mBackground = null;
        }
    }

    /**
     * 计算相关值
     *
     * @param height
     */
    public void calculateValue(float height) {
        mMaxDisOnY = height / 5f;
        mAlphaBase = mMaxDisOnY * 2;
    }

    /**
     * 准备拖拽
     *
     * @param width
     * @param height
     */
    public void onReady(float width, float height) {
        mBackgroundAlpha = DEF_BACKGROUND_ALPHA;
        mPreviewWidth = width;
        mPreviewHeight = height;
        calculateValue(mPreviewHeight);
        if (checkAttacherNotNull()) mAttacher.setViewPagerScrollable(false);
        setImageDraggerState(ImageDraggerState.DRAG_STATE_READY);
    }

    /**
     * 拖拽图片中
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void onDragging(final float x1, final float y1, final float x2, final float y2) {
        setImageDraggerState(ImageDraggerState.DRAG_STATE_DRAGGING);
        setPreviewStatus(ImageViewerState.STATE_DRAGGING, scaleImageView);
    }

    /**
     * 释放图片
     */
    public void onRelease() {

    }

    /**
     * 设置图片拖拽状态监听
     *
     * @param listener
     */
    public void setImageDraggerStateListener(ImageDraggerStateListener listener) {
        this.mStateListener = listener;
    }

    /**
     * 设置图片拖拽状态
     *
     * @param state {@link ImageDraggerState}
     */
    public void setImageDraggerState(int state) {
        if (mStateListener != null) {
            mStateListener.onImageDraggerState(state);
        }
    }

    /**
     * 设置背景透明度
     *
     * @param alpha
     */
    public void setBackgroundAlpha(int alpha) {
        if (mBackground != null) {
            mBackground.setAlpha(alpha);
        }
    }

    /**
     * 设置预览状态
     *
     * @param state      {@link ImageViewerState}
     * @param imagePager
     */
    public void setPreviewStatus(@ImageViewerState int state, ScaleImageView imagePager) {
        if (checkAttacherNotNull()) {
            mAttacher.setPreviewStatus(state, imagePager);
        }
    }

    /**
     * 判断 ImageViewerAttacher 是否为空
     *
     * @return
     */
    public boolean checkAttacherNotNull() {
        if (mAttacher != null) return true;
        return false;
    }
}
