package indi.liyi.viewer.imgpg.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.imgpg.ViewData;

/**
 * 灵巧的图片拖拽处理类
 * <p>
 * PS: 仿微信朋友圈拖拽效果
 */
public class AgileDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int ANIM_DURATION_RESTORE = 200;
    // 退出预览的动画时间
    private final int ANIM_DURATION_EXIT = 280;
    // 默认的最小缩放比例
    private final float MIN_SCALE = 0.2f;

    // 预览界面的宽高
    private int mPrevWidth;
    private int mPrevHeight;
    // 图片的原始宽高
    private int mOriginalImageWidth;
    private int mOriginalImageHeight;
    // 图片自适应预览界面时的宽高
    private int mAdjustImageWidth;
    private int mAdjustImageHeight;
    // imageView 的当前缩放比例
    private float mCurScale;

    private ImagePager imagePager;
    private ImageView imageView;
    private FrameLayout.LayoutParams mImageViewLps;

    public AgileDragger() {

    }

    @Override
    public void init(int prevWidth, int prevHeight, View view) {
        super.init(prevWidth, prevHeight, view);
        this.mPrevWidth = prevWidth;
        this.mPrevHeight = prevHeight;
        this.imagePager = (ImagePager) view;
        this.imageView = imagePager.getImageView();
        mImageViewLps = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        final ViewData viewData = imagePager.getViewData();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            mOriginalImageWidth = drawable.getIntrinsicWidth();
            mOriginalImageHeight = drawable.getIntrinsicHeight();
        } else {
            mOriginalImageWidth = viewData.getImageWidth();
            mOriginalImageHeight = viewData.getImageHeight();
        }
        if (mOriginalImageWidth == 0 || mOriginalImageHeight == 0) {
            mAdjustImageWidth = prevWidth;
            mAdjustImageHeight = prevHeight;
        } else {
            // 图片自适应预览界面时的缩放比例
            final float adjustImageScale = Math.min(prevWidth * 1f / mOriginalImageWidth, prevHeight * 1f / mOriginalImageHeight);
            mAdjustImageWidth = (int) (mOriginalImageWidth * adjustImageScale);
            mAdjustImageHeight = (int) (mOriginalImageHeight * adjustImageScale);
        }
    }

    @Override
    public void onDown(float downX, float downY) {
        super.onDown(downX, downY);
    }

    @Override
    public void onDrag(float lastX, float lastY, MotionEvent ev) {
        super.onDrag(lastX, lastY, ev);
        imageView = imagePager.getImageView();
        // 手指的移动距离
        final float disX = ev.getX() - lastX;
        final float disY = ev.getY() - lastY;
        // imageView 被拖拽后的坐标
        final float nvX = imageView.getX() + disX;
        final float nvY = imageView.getY() + disY;
        // 向上移动
        if (nvY <= 0) {
            if (mCurScale < 1f) {
                mCurScale = 1f;
                mImageViewLps.width = mPrevWidth;
                mImageViewLps.height = mPrevHeight;
                imageView.setLayoutParams(mImageViewLps);
            }
            if (getBackgroundAlpha() < NO_BACKGROUND_ALPHA) {
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
            }
        }
        // 向下移动
        else {
            // 计算缩放比例
            mCurScale = Math.min(Math.max(1f - nvY / mPrevHeight, MIN_SCALE), 1f);
            mImageViewLps.width = (int) (mPrevWidth * mCurScale);
            mImageViewLps.height = (int) (mPrevHeight * mCurScale);
            imageView.setLayoutParams(mImageViewLps);
            // 计算背景透明度
            final int backgroundAlpha = (int) (mCurScale * NO_BACKGROUND_ALPHA);
            changeBackgroundAlpha(backgroundAlpha);
        }
        imageView.setX(nvX);
        imageView.setY(nvY);
    }

    @Override
    public void onUp() {
        super.onUp();
        final float viewY = imageView.getY();
        if (viewY <= getMaxMovableDisOnY()) {
            setDragStatus(DragStatus.STATUS_BEGIN_RESTORE);
            doRestoreAnim();
        } else {
            setDragStatus(DragStatus.STATUS_BEGIN_EXIT);
            doExitAnim();
        }
    }

    @Override
    public void clear() {
        mImageViewLps = null;
        imageView = null;
        imagePager = null;
    }

    /**
     * 执行图片恢复原样动画
     */
    private void doRestoreAnim() {
        final int oldWidth = mImageViewLps.width;
        final int oldHeight = mImageViewLps.height;
        final int newWidth = mPrevWidth;
        final int newHeight = mPrevHeight;
        final float fromX = imageView.getX();
        final float fromY = imageView.getY();
        final float toX = 0;
        final float toY = 0;
        executeCommonAnim(ANIM_DURATION_RESTORE, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setDragStatus(DragStatus.STATUS_END_RESTORE);
                if (checkAttacherNotNull()) {
                    getWrapper().setViewPagerScrollable(true);
                }
            }
        }, new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = animation.getAnimatedFraction();
                changeImageViewParams(fromX, fromY, toX, toY,
                        oldWidth, oldHeight, newWidth, newHeight,
                        progress, evaluator);
                final float alpha = evaluator.evaluate(progress, oldBgAlpha, NO_BACKGROUND_ALPHA);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_RESTORING);
            }
        });
    }

    /**
     * 执行退出预览动画
     */
    private void doExitAnim() {
        final ViewData viewData = imagePager.getViewData();
        // 计算当前图片的宽高，然后将 imageView 的宽高设置为图片的宽高
        final int oldWidth = (int) (mAdjustImageWidth * mCurScale);
        final int oldHeight = (int) (mAdjustImageHeight * mCurScale);
        // 目标 view 的宽高
        final int newWidth = viewData.getTargetWidth();
        final int newHeight = viewData.getTargetHeight();
        // 图片在预览界面中的当前坐标
        final float fromX = imageView.getX() + (mPrevWidth - mAdjustImageWidth) / 2 * mCurScale;
        final float fromY = imageView.getY() + (mPrevHeight - mAdjustImageHeight) / 2 * mCurScale;
        // 如果没有设置外部目标 view 的 targetX 与 targetY，则将 toX 与 toY 设为当前 imageView 的 x 与 y 轴坐标，
        // 直接在原地进行缩放，不做位移动画
        final float toX = (viewData.getTargetX() != ViewData.INVALID_VAL) ? viewData.getTargetX() : fromX;
        final float toY = (viewData.getTargetY() != ViewData.INVALID_VAL) ? viewData.getTargetY() : fromY;
        // 图片是否已经滑出预览界面
        final boolean isOutOfInterface;
        if (
            // imageView 已经从左边滑出预览界面
                (fromX + oldWidth) <= 0
                        // imageView 已经从右边滑出预览界面
                        || fromX >= mPrevWidth
                        // imageView 已经从底部滑出图片预览界面
                        || fromY >= mPrevHeight) {
            isOutOfInterface = true;
        } else {
            isOutOfInterface = false;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        executeCommonAnim(ANIM_DURATION_EXIT, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setDragStatus(DragStatus.STATUS_END_EXIT);
                if (checkAttacherNotNull()) {
                    getWrapper().exitEnd();
                }
            }
        }, new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = animation.getAnimatedFraction();
                // 如果图片已经滑出预览界面，则直接更改背景透明度即可
                // 否则需要不断更新 imageView 的坐标与大小
                if (!isOutOfInterface) {
                    changeImageViewParams(fromX, fromY, toX, toY,
                            oldWidth, oldHeight, newWidth, newHeight,
                            progress, evaluator);
                }
                final float alpha = evaluator.evaluate(progress, oldBgAlpha, 0);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_EXITTING);
            }
        });
    }

    private void executeCommonAnim(int duration, AnimatorListenerAdapter listener, ValueAnimator.AnimatorUpdateListener updater) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 100f);
        animator.setDuration(duration);
        animator.addListener(listener);
        animator.addUpdateListener(updater);
        animator.start();
    }

    private void changeImageViewParams(float fromX, float fromY, float toX, float toY,
                                       int oldWidth, int oldHeight, int newWidth, int newHeight,
                                       float progress, FloatEvaluator evaluator) {
        if (oldWidth != newWidth
                || oldHeight != newHeight
                || newWidth != mImageViewLps.width
                || newHeight != mImageViewLps.height) {
            final float width = evaluator.evaluate(progress, oldWidth, newWidth);
            final float height = evaluator.evaluate(progress, oldHeight, newHeight);
            mImageViewLps.width = (int) width;
            mImageViewLps.height = (int) height;
            imageView.setLayoutParams(mImageViewLps);
        }
        if (fromX != toX || toX != imageView.getX()) {
            final float x = evaluator.evaluate(progress, fromX, toX);
            imageView.setX(x);
        }
        if (fromY != toY || toY != imageView.getY()) {
            final float y = evaluator.evaluate(progress, fromY, toY);
            imageView.setY(y);
        }
    }
}
