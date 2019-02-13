package indi.liyi.viewer.scip.dragger;

import android.graphics.drawable.Drawable;

import indi.liyi.viewer.ViewerWrapper;

/**
 * 图片拖拽处理类的基类
 */
public abstract class BaseDragger implements DragHandler {
    protected final int NO_BACKGROUND_ALPHA = 255;

    private OnDragStatusListener mStatusListener;
    private ViewerWrapper mWrapper;

    private boolean canChangeBgAlpha = true;
    private Drawable mBackground;
    // 在不退出浏览的情况下， 图片在 Y 轴上的最大可移动距离
    private float mMaxMovableDisOnY;
    // 图片被拖拽时的背景透明度基数
    private float mAlphaBase;


    public BaseDragger() {

    }

    @Override
    public void injectViewerWrapper(ViewerWrapper wrapper) {
        this.mWrapper = wrapper;
    }

    @Override
    public void canChangeBgAlpha(boolean isCan) {
        this.canChangeBgAlpha = isCan;
    }

    @Override
    public void setBackground(Drawable background) {
        if (background != null) {
            mBackground = background;
        } else {
            mBackground = null;
        }
    }

    @Override
    public void changeBackgroundAlpha(int alpha) {
        if (canChangeBgAlpha
                && mBackground != null
                && mBackground.getAlpha() != alpha) {
            mBackground.setAlpha(alpha);
        }
    }

    @Override
    public void addDragStatusListener(OnDragStatusListener listener) {
        this.mStatusListener = listener;
    }

    @Override
    public void onDown(float prevWidth, float prevHeight) {
        mMaxMovableDisOnY = prevHeight / 5f;
        mAlphaBase = mMaxMovableDisOnY * 2;
        if (checkAttacherNotNull()) {
            mWrapper.setViewPagerScrollable(false);
        }
        setDragStatus(DragStatus.STATUS_READY);
    }

    @Override
    public void onDrag(final float downX, final float downY, final float curX, final float curY) {
        setDragStatus(DragStatus.STATUS_DRAGGING);
    }

    @Override
    public void onUp() {

    }

    @Override
    public void clear() {
        if (mStatusListener != null) {
            mStatusListener = null;
        }
    }

    public void setDragStatus(int status) {
        if (mStatusListener != null) {
            mStatusListener.onDragStatusChanged(status);
        }
    }

    public ViewerWrapper getWrapper() {
        return mWrapper;
    }

    public int getBackgroundAlpha() {
        if (mBackground != null) {
            return mBackground.getAlpha();
        } else {
            return NO_BACKGROUND_ALPHA;
        }
    }

    public float getMaxMovableDisOnY() {
        return mMaxMovableDisOnY;
    }

    public float getAlphaBase() {
        return mAlphaBase;
    }

    /**
     * 判断 ViewerWrapper 是否为空
     *
     * @return
     */
    public boolean checkAttacherNotNull() {
        if (mWrapper != null) {
            return true;
        }
        return false;
    }
}
