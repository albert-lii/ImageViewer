package indi.liyi.viewer.sipr.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import indi.liyi.viewer.ImageViewerStatus;
import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.sipr.ViewData;

public class AgileDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int REBACK_ANIM_DURATION = 200;
    // 退出预览的动画时间
    private final int EXIT_ANIM_DURATION = 280;
    // 默认的最小缩放比例
    private final float MIN_SCALE_RATIO = 0.25f;

    // 预览界面的宽高
    private float mPrevWidth;
    private float mPrevHeight;
    // 图片的原始宽高
    private float mOrigImageWidth;
    private float mOrigImageHeight;
    // 图片自适应预览界面时的宽高
    private float mAdjustImageWidth;
    private float mAdjustImageHeight;
    // imageView 的当前缩放比例
    private float mCurScale;

    private FrameLayout.LayoutParams mImageViewParams;
    private ScaleImagePager imagePager;

    public AgileDragger() {

    }

    @Override
    public void injectImagePager(ScaleImagePager imagePager) {
        this.imagePager = imagePager;
        final ViewData viewData = imagePager.getViewData();
        final ImageView imageView = imagePager.getImageView();
        mImageViewParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            mOrigImageWidth = drawable.getIntrinsicWidth();
            mOrigImageHeight = drawable.getIntrinsicHeight();
        } else {
            mOrigImageWidth = viewData.getImageWidth();
            mOrigImageHeight = viewData.getImageHeight();
        }
    }

    @Override
    public void onDown(float prevWidth, float prevHeight) {
        super.onDown(prevWidth, prevHeight);
        this.mPrevWidth = prevWidth;
        this.mPrevHeight = prevHeight;
        if (mOrigImageWidth == 0 || mOrigImageHeight == 0) {
            mAdjustImageWidth = prevWidth;
            mAdjustImageHeight = prevHeight;
        } else {
            // 图片自适应预览界面时的缩放比例
            final float adjustImageScale = Math.min(prevWidth / mOrigImageWidth, prevHeight / mOrigImageHeight);
            mAdjustImageWidth = mOrigImageWidth * adjustImageScale;
            mAdjustImageHeight = mOrigImageHeight * adjustImageScale;
        }
    }

    @Override
    public void onDrag(float downX, float downY, float curX, float curY) {
        super.onDrag(downX, downY, curX, curY);
        setPreviewStatus(ImageViewerStatus.STATUS_DRAGGING, imagePager);
        View imageView = imagePager.getImageView();
        // 计算 view 的坐标
        final float diffX = curX - downX;
        final float diffY = curY - downY;
        final float oldViewX = imageView.getX();
        final float oldViewY = imageView.getY();
        final float newViewX = oldViewX + diffX;
        final float newViewY = oldViewY + diffY;
        // 向上移动
        if (newViewY <= 0) {
            if (mCurScale < 1f) {
                mImageViewParams.width = (int) (mPrevWidth * mCurScale);
                mImageViewParams.height = (int) (mPrevHeight * mCurScale);
                imageView.setLayoutParams(mImageViewParams);
                mCurScale = 1f;
            }
            if (getBackgroundAlpha() < NO_BACKGROUND_ALPHA) {
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
            }
        }
        // 向下移动
        else {
            // 计算缩放比例
            mCurScale = Math.min(Math.max(1f - newViewY / mPrevHeight, MIN_SCALE_RATIO), 1f);
            mImageViewParams.width = (int) (mPrevWidth * mCurScale);
            mImageViewParams.height = (int) (mPrevHeight * mCurScale);
            imageView.setLayoutParams(mImageViewParams);
            // 计算背景透明度
            final float value = newViewY / getAlphaBase();
            final int backgroundAlpha = (int) ((value <= 0.8f ? 1 - value : 0.2f) * NO_BACKGROUND_ALPHA);
            changeBackgroundAlpha(backgroundAlpha);
        }
        imageView.setX(newViewX);
        imageView.setY(newViewY);
    }

    @Override
    public void onUp() {
        super.onUp();
        if (!imagePager.isAnimRunning()) {
            View imageView = imagePager.getImageView();
            final float viewY = imageView.getY();
            if (viewY <= getMaxMovableDisOnY()) {
                reback();
            } else {
                exit();
            }
        }
    }

    @Override
    public void clear() {
        if (imagePager != null) {
            imagePager = null;
        }
    }

    /**
     * 图片恢复原样
     */
    private void reback() {
        setDragStatus(DragStatus.STATUS_BEGIN_REBACK);
        setPreviewStatus(ImageViewerStatus.STATUS_READY_REBACK, imagePager);
        final View imageView = imagePager.getImageView();
        final float fromX = imageView.getX();
        final float fromY = imageView.getY();
        final float toX = 0;
        final float toY = 0;
        final float oldWidth = imageView.getWidth();
        final float oldHeight = imageView.getHeight();
        final float newWidth = mPrevWidth;
        final float newHeight = mPrevHeight;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(REBACK_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                if (fromX != toX) {
                    final float x = evaluator.evaluate(currentValue, fromX, toX);
                    imageView.setX(x);
                }
                if (fromY != toY) {
                    final float y = evaluator.evaluate(currentValue, fromY, toY);
                    imageView.setY(y);
                }
                if (oldWidth != newWidth) {
                    final float width = evaluator.evaluate(currentValue, oldWidth, newWidth);
                    mImageViewParams.width = (int) width;
                }
                if (oldHeight != newHeight) {
                    final float height = evaluator.evaluate(currentValue, oldHeight, newHeight);
                    mImageViewParams.height = (int) height;
                }
                if (oldWidth != newWidth || oldHeight != newHeight) {
                    imageView.setLayoutParams(mImageViewParams);
                }
                if (oldBgAlpha != NO_BACKGROUND_ALPHA) {
                    final float alpha = evaluator.evaluate(currentValue, oldBgAlpha, NO_BACKGROUND_ALPHA);
                    changeBackgroundAlpha((int) alpha);
                }
                setDragStatus(DragStatus.STATUS_REBACKING);
                setPreviewStatus(ImageViewerStatus.STATUS_REBACKING, imagePager);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setDragStatus(DragStatus.STATUS_END_REBACK);
                if (checkAttacherNotNull()) {
                    getAttacher().setViewPagerScrollable(true);
                    setPreviewStatus(ImageViewerStatus.STATUS_COMPLETE_REBACK, imagePager);
                    setPreviewStatus(ImageViewerStatus.STATUS_WATCHING, imagePager);
                }
            }
        });
        animator.start();
    }

    /**
     * 退出预览
     */
    private void exit() {
        setDragStatus(DragStatus.STATUS_BEGIN_EXIT);
        setPreviewStatus(ImageViewerStatus.STATUS_READY_CLOSE, imagePager);
        final ImageView imageView = imagePager.getImageView();
        final ViewData viewData = imagePager.getViewData();
        // 图片在预览界面中的当前坐标
        final float fromX = imageView.getX() + (mPrevWidth - mAdjustImageWidth) / 2 * mCurScale;
        final float fromY = imageView.getY() + (mPrevHeight - mAdjustImageHeight) / 2 * mCurScale;
        // 如果没有设置 view 的 targetX 与 targetY，则将 toX 与 toY 设为当前 view 的 x 与 y 轴坐标，
        // 直接在原地进行缩放，不做位移动画
        final float toX = viewData.getTargetX();
        final float toY = viewData.getTargetY();
        // 将 imageView 的宽高设置为图片的宽高
        final float oldWidth = mAdjustImageWidth * mCurScale;
        final float oldHeight = mAdjustImageHeight * mCurScale;
        final float newWidth = viewData.getTargetWidth();
        final float newHeight = viewData.getTargetHeight();
        // 图片是否已经滑出预览界面
        final boolean isOutOfPreview;
        if (
            // imageView 已经从左边滑出预览界面
                (fromX + oldWidth) <= 0
                        // imageView 已经从右边滑出预览界面
                        || fromX >= mPrevWidth
                        // imageView 已经从底部滑出图片预览界面
                        || fromY >= mPrevHeight) {
            isOutOfPreview = true;
        } else {
            isOutOfPreview = false;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(EXIT_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                // 如果图片已经滑出预览界面，则直接更改背景透明度即可
                // 否则需要不断更新 imageView 的坐标与大小
                if (!isOutOfPreview) {
                    if (fromX != toX) {
                        final float x = evaluator.evaluate(currentValue, fromX, toX);
                        imageView.setX(x);
                    }
                    if (fromY != toY) {
                        final float y = evaluator.evaluate(currentValue, fromY, toY);
                        imageView.setY(y);
                    }
                    if (oldWidth != newWidth) {
                        final float width = evaluator.evaluate(currentValue, oldWidth, newWidth);
                        mImageViewParams.width = (int) width;
                    }
                    if (oldHeight != newHeight) {
                        final float height = evaluator.evaluate(currentValue, oldHeight, newHeight);
                        mImageViewParams.height = (int) height;
                    }
                    if (oldWidth != newWidth || oldHeight != newHeight) {
                        imageView.setLayoutParams(mImageViewParams);
                    }
                }
                final float alpha = evaluator.evaluate(currentValue, oldBgAlpha, 0);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_EXITTING);
                setPreviewStatus(ImageViewerStatus.STATUS_CLOSING, imagePager);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (checkAttacherNotNull()) {
                    getAttacher().exitEnd();
                }
                imageView.setX(0);
                imageView.setY(0);
                mImageViewParams.width = (int) mPrevWidth;
                mImageViewParams.height = (int) mPrevHeight;
                imageView.setLayoutParams(mImageViewParams);
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setDragStatus(DragStatus.STATUS_END_EXIT);
                setPreviewStatus(ImageViewerStatus.STATUS_COMPLETE_CLOSE, imagePager);
                setPreviewStatus(ImageViewerStatus.STATUS_SILENCE, null);
            }
        });
        animator.start();
    }
}
