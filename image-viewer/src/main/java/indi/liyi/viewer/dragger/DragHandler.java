package indi.liyi.viewer.dragger;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import indi.liyi.viewer.ImageTransfer;
import indi.liyi.viewer.ViewData;

public class DragHandler {
    // 完全不透明的透明度值
    private final int FULL_ALPHA = 255;
    // 动画执行时间
    private final int ANIM_DURATION = 300;
    // 最小缩放比例
    private final float MIN_SCALE = 0.2f;
    // 在不退出浏览的情况下， 图片在 Y 轴上的最大可移动距离
    private final float MAX_MOVABLE_DIS_ON_Y;
    // 背景透明度的变换基数
    private final float ALPHA_BASE;

    // 拖拽模式
    private int mMode;
    // 浏览界面的宽高
    private int mIfWidth, mIfHeight;
    private Drawable mBackground;
    private ImageTransfer mTransfer;
    private int mAction;

    public DragHandler(int ifWidth, int ifHeight) {
        this.mIfWidth = ifWidth;
        this.mIfHeight = ifHeight;
        this.MAX_MOVABLE_DIS_ON_Y = ifHeight / 5f;
        this.ALPHA_BASE = MAX_MOVABLE_DIS_ON_Y * 2;
        this.mTransfer = new ImageTransfer(ifWidth, ifHeight);
    }

    /**
     * 准备拖拽
     */
    public void onReay(int mode, Drawable bg) {
        this.mMode = mode;
        this.mBackground = bg;
        mAction = ImageTransfer.ACTION_IDEL;
    }

    /**
     * 拖拽中
     *
     * @param lastX     上一次触摸点的 x 轴坐标
     * @param lastY     上一次触摸点的 y 轴坐标
     * @param event     本次触摸点的手势事件
     * @param imageView
     */
    public void onDrag(float lastX, float lastY, MotionEvent event, ImageView imageView) {
        if (mMode == DragMode.MODE_SIMPLE) {
            dragBySimple(lastY, event, imageView);
        } else if (mMode == DragMode.MODE_AGILE) {
            dragByAgile(lastX, lastY, event, imageView);
        }
    }

    /**
     * 手指抬起
     */
    public void onUp(ImageView imageView, ViewData viewData, ImageTransfer.OnTransCallback callback) {
        final float transY = imageView.getTranslationY();
        mTransfer.with(imageView)
                .background(mBackground)
                .duration(ANIM_DURATION);
        // 执行复原动画
        if (Math.abs(transY) <= MAX_MOVABLE_DIS_ON_Y) {
            mAction = ImageTransfer.ACTION_DRAG_RESTORE;
            mTransfer.loadDragRestoreData()
                    .callback(callback);
        }
        // 执行退场动画
        else {
            if (mMode == DragMode.MODE_AGILE) {
                if (transY < 0) {
                    mAction = ImageTransfer.ACTION_DRAG_RESTORE;
                    mTransfer.loadDragRestoreData()
                            .callback(callback);
                } else {
                    mAction = ImageTransfer.ACTION_DRAG_EXIT_AGILE;
                    mTransfer.loadDragExitDataInAgile(viewData)
                            .callback(callback);
                }
            } else {
                mAction = ImageTransfer.ACTION_DRAG_EXIT_SIMPLE;
                mTransfer.loadDragExitDataInSimple(viewData)
                        .callback(callback);
            }
        }
        mTransfer.play();
    }

    public int getAction() {
        return mAction;
    }

    public void clear() {
        if (mTransfer != null) {
            mTransfer = null;
        }
    }

    /**
     * 简单模式拖拽（类似今日头条）
     */
    private void dragBySimple(float lastY, MotionEvent event, ImageView imageView) {
        final float disY = event.getY() - lastY;
        final float nextY = imageView.getTranslationY() + disY;
        imageView.setTranslationY(nextY);
        // 计算背景透明度
        final float value = Math.abs(nextY) / ALPHA_BASE;
        transformBgAlpha((int) ((value < 0.8f ? 1 - value : 0.2f) * FULL_ALPHA));
    }

    /**
     * 灵巧模式拖拽（类似微信朋友圈）
     */
    private void dragByAgile(float lastX, float lastY, MotionEvent event, ImageView imageView) {
        // 手指的移动距离
        final float disX = event.getX() - lastX;
        final float disY = event.getY() - lastY;
        // imageView 被拖拽后的坐标
        final float nextX = imageView.getTranslationX() + disX;
        final float nextY = imageView.getTranslationY() + disY;
        // 向上移动
        if (nextY <= 0) {
            if (imageView.getLayoutParams().width != mIfWidth ||
                    imageView.getLayoutParams().height != mIfHeight) {
                imageView.getLayoutParams().width = mIfWidth;
                imageView.getLayoutParams().height = mIfHeight;
                imageView.requestLayout();
            }
            transformBgAlpha(FULL_ALPHA);
        }
        // 向下移动
        else {
            // 计算缩放比例
            final float scale = Math.min(Math.max(1f - nextY / mIfHeight, MIN_SCALE), 1f);
            imageView.getLayoutParams().width = (int) (mIfWidth * scale);
            imageView.getLayoutParams().height = (int) (mIfHeight * scale);
            imageView.requestLayout();
            // 变换背景透明度
            transformBgAlpha((int) (scale * FULL_ALPHA));
        }
        imageView.setTranslationX(nextX);
        imageView.setTranslationY(nextY);
    }

    /**
     * 变换背景透明度
     */
    private void transformBgAlpha(int alpha) {
        if (mBackground != null && mBackground.getAlpha() != alpha) {
            mBackground.setAlpha(alpha);
        }
    }
}
