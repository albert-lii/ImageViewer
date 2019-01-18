package indi.liyi.viewer.dragger;

import android.graphics.drawable.Drawable;

import indi.liyi.viewer.ImageViewerState;
import indi.liyi.viewer.widget.ImageViewerAttacher;
import indi.liyi.viewer.widget.ScaleImagePager;

/**
 * 图片拖拽处理类的基类
 */
public abstract class BaseDragger implements DragHandler {
    protected final int NO_BACKGROUND_ALPHA = 255;

    private ScaleImagePager scaleImagePager;
    private ImageViewerAttacher mAttacher;
    private OnDragStatusListener mStatusListener;

    private Drawable mBackground;
    private int mBackgroundAlpha;

    private float mPreiWidth;
    private float mPreiHeight;

    // 在不退出浏览的情况下， 图片在 Y 轴上的最大可移动距离
    private float mMaxMovableDisOnY;
    // 图片被拖拽时的背景透明度基数
    private float mAlphaBase;


    public BaseDragger() {

    }

    @Override
    public void injectImagePager(ScaleImagePager pager) {
        this.scaleImagePager = pager;
    }

    @Override
    public void injectImageViewerAttacher(ImageViewerAttacher attacher) {
        this.mAttacher = attacher;
    }

    @Override
    public void changeBackground(Drawable background) {
        if (background != null) {
            mBackground = background.mutate();
        } else {
            mBackground = null;
        }
    }

    @Override
    public void changeBackgroundAlpha(int alpha) {
        if (mBackground != null) {
            mBackground.setAlpha(alpha);
            mBackgroundAlpha = alpha;
        }
    }

    @Override
    public void addDragStatusListener(OnDragStatusListener listener) {
        this.mStatusListener = listener;
    }

    @Override
    public void onDown(float preiWidth, float preiHeight) {
        mPreiWidth = preiWidth;
        mPreiHeight = preiHeight;
        mBackgroundAlpha = NO_BACKGROUND_ALPHA;
        mMaxMovableDisOnY = mPreiHeight / 5f;
        mAlphaBase = mMaxMovableDisOnY * 2;
        if (checkAttacherNotNull()) {
            mAttacher.setViewPagerScrollable(false);
        }
        setDragStatus(DragStatus.STATUS_READY);
    }

    @Override
    public void onDrag(final float downX, final float downY, final float curX, final float curY) {
        setDragStatus(DragStatus.STATUS_DRAGGING);
        setPreviewStatus(ImageViewerState.STATE_DRAGGING, scaleImagePager);
    }

    @Override
    public void onUp() {

    }

    public void setDragStatus(int status) {
        if (mStatusListener != null) {
            mStatusListener.onDragStatusChanged(status);
        }
    }

//    public ScaleImagePager getScaleImagePager() {
//        return scaleImagePager;
//    }

    public ImageViewerAttacher getAttacher() {
        return mAttacher;
    }

    public int getBackgroundAlpha() {
        return mBackgroundAlpha;
    }

    public void setBackgroundAlpha(int alpha) {
        this.mBackgroundAlpha = alpha;
    }

//    public float getPreiWidth() {
//        return mPreiWidth;
//    }
//
//    public float getPreiHeight() {
//        return mPreiHeight;
//    }

    public float getMaxMovableDisOnY() {
        return mMaxMovableDisOnY;
    }

    public float getAlphaBase() {
        return mAlphaBase;
    }

    /**
     * 设置预览状态
     *
     * @param state      {@link ImageViewerState}
     * @param imagePager
     */
    public void setPreviewStatus(@ImageViewerState int state, ScaleImagePager imagePager) {
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
