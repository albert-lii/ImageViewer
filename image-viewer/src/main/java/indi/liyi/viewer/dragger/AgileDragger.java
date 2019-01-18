package indi.liyi.viewer.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import indi.liyi.viewer.ImageViewerState;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.widget.ScaleImagePager;

public class AgileDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int REBACK_ANIM_DURATION = 200;
    // 退出预览的动画时间
    private final int EXIT_ANIM_DURATION = 280;
    // 默认的最小缩放比例
    private final float MIN_SCALE_RATIO = 0.25f;

    // 预览界面的宽高
    private float mPreiWidth;
    private float mPreiHeight;
    // 图片的原始宽高
    private float mOrigImageWidth = 0;
    private float mOrigImageHeight = 0;
    private float mAdjustScale;
    private float mAdjustImageWidth;
    private float mAdjustImageHeight;
    // imageView 的当前缩放比例
    private float mCurScale;
    private float mCurImageX;
    private float mCurImageY;

    private FrameLayout.LayoutParams mImageParams;
    private ScaleImagePager imagePager;

    public AgileDragger() {

    }

    @Override
    public void injectImagePager(ScaleImagePager imagePager) {
        super.injectImagePager(imagePager);
        this.imagePager = imagePager;
        final ViewData viewData = imagePager.getViewData();
        final ImageView imageView = imagePager.getImageView();
        mImageParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            mOrigImageWidth = drawable.getIntrinsicWidth();
            mOrigImageHeight = drawable.getIntrinsicHeight();
        } else if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
            mOrigImageWidth = viewData.getImageWidth();
            mOrigImageHeight = viewData.getImageHeight();
        }
    }

    @Override
    public void onDown(float preiWidth, float preiHeight) {
        super.onDown(preiWidth, preiHeight);
        this.mPreiWidth = preiWidth;
        this.mPreiHeight = preiHeight;
        mAdjustScale = Math.min(preiWidth / mOrigImageWidth, preiHeight / mOrigImageHeight);
        mAdjustImageWidth = mOrigImageWidth * mAdjustScale;
        mAdjustImageHeight = mOrigImageHeight * mAdjustScale;
    }

    @Override
    public void onDrag(float downX, float downY, float curX, float curY) {
        super.onDrag(downX, downY, curX, curY);
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
                mImageParams.width = (int) (mPreiWidth * mCurScale);
                mImageParams.height = (int) (mPreiHeight * mCurScale);
                imageView.setLayoutParams(mImageParams);
                mCurScale = 1f;
            }
            if (getBackgroundAlpha() < NO_BACKGROUND_ALPHA) {
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
            }
        }
        // 向下移动
        else {
            // 计算缩放比例
            mCurScale = Math.min(Math.max(newViewY < 0 ? 1f : (1f - Math.abs(newViewY) / mPreiHeight), MIN_SCALE_RATIO), 1f);
            mImageParams.width = (int) (mPreiWidth * mCurScale);
            mImageParams.height = (int) (mPreiHeight * mCurScale);
            imageView.setLayoutParams(mImageParams);
            // 计算背景透明度
            final float value = Math.abs(newViewY) / getAlphaBase();
            final int backgroundAlpha = (int) ((value <= 0.8f ? 1 - value : 0.2f) * NO_BACKGROUND_ALPHA);
            changeBackgroundAlpha(backgroundAlpha);
        }
        imageView.setX(newViewX);
        imageView.setY(newViewY);
    }

    @Override
    public void onUp() {
        super.onUp();
        if (!imagePager.isImageAnimRunning()) {
            View imageView = imagePager.getImageView();
            final float viewX = imageView.getX();
            final float viewY = imageView.getY();
            // 图片在预览界面中的当前坐标
            mCurImageX = viewX + (mPreiWidth - mAdjustImageWidth) / 2 * mCurScale;
            mCurImageY = viewY + (mPreiHeight - mAdjustImageHeight) / 2 * mCurScale;
            if (viewY <= getMaxMovableDisOnY()) {
                reback();
            } else {
                exit();
            }
        }
    }

    /**
     * 图片恢复原样
     */
    private void reback() {
        setDragStatus(DragStatus.STATUS_BEGIN_REBACK);
        setPreviewStatus(ImageViewerState.STATE_READY_REBACK, imagePager);
        final View imageView = imagePager.getImageView();
        final float from_x = imageView.getX();
        final float from_y = imageView.getY();
        final float to_x = 0;
        final float to_y = 0;
        final float old_width = imageView.getWidth();
        final float old_height = imageView.getHeight();
        final float new_width = mPreiWidth;
        final float new_height = mPreiHeight;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(REBACK_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float x = evaluator.evaluate(currentValue, from_x, to_x);
                final float y = evaluator.evaluate(currentValue, from_y, to_y);
                final float width = evaluator.evaluate(currentValue, old_width, new_width);
                final float height = evaluator.evaluate(currentValue, old_height, new_height);
                final float alpha = evaluator.evaluate(currentValue, getBackgroundAlpha(), NO_BACKGROUND_ALPHA);
                imageView.setX(x);
                imageView.setY(y);
                mImageParams.width = (int) width;
                mImageParams.height = (int) height;
                imageView.setLayoutParams(mImageParams);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_REBACKING);
                setPreviewStatus(ImageViewerState.STATE_REBACKING, imagePager);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setDragStatus(DragStatus.STATUS_END_REBACK);
                if (checkAttacherNotNull()) {
                    getAttacher().setViewPagerScrollable(true);
                    setPreviewStatus(ImageViewerState.STATE_COMPLETE_REBACK, imagePager);
                    setPreviewStatus(ImageViewerState.STATE_WATCHING, imagePager);
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
        setPreviewStatus(ImageViewerState.STATE_READY_CLOSE, imagePager);
        final ImageView imageView = imagePager.getImageView();
        final ViewData viewData = imagePager.getViewData();
        final float from_x = mCurImageX;
        final float from_y = mCurImageY;
        final float toX = viewData.getTargetX();
        final float toY = viewData.getTargetY();
        final float old_width = mAdjustImageWidth * mCurScale;
        final float old_height = mAdjustImageHeight * mCurScale;
        final float new_width = viewData.getTargetWidth();
        final float new_height = viewData.getTargetHeight();
        // 是否需要改变 imageView 的尺寸
        final boolean needChangeImageSize;
        if ((mCurImageX + mAdjustImageWidth * mCurScale) <= 0 || mCurImageX >= mPreiWidth || mCurImageY >= mPreiHeight) {
            needChangeImageSize = false;
        } else {
            needChangeImageSize = true;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(EXIT_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                if (needChangeImageSize) {
                    final float x = evaluator.evaluate(progress, from_x, toX);
                    final float y = evaluator.evaluate(progress, from_y, toY);
                    final float width = evaluator.evaluate(progress, old_width, new_width);
                    final float height = evaluator.evaluate(progress, old_height, new_height);
                    imageView.setX(x);
                    imageView.setY(y);
                    layoutParams.width = (int) width;
                    layoutParams.height = (int) height;
                    imageView.setLayoutParams(layoutParams);
                }
                final float alpha = evaluator.evaluate(progress, getBackgroundAlpha(), 0);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_EXITTING);
                setPreviewStatus(ImageViewerState.STATE_CLOSING, imagePager);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (checkAttacherNotNull()) getAttacher().exit();
                setBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setDragStatus(DragStatus.STATUS_END_EXIT);
                imageView.setX(0);
                imageView.setY(0);
                mImageParams.width = (int) mPreiWidth;
                mImageParams.height = (int) mPreiHeight;
                imageView.setLayoutParams(mImageParams);
                setPreviewStatus(ImageViewerState.STATE_COMPLETE_CLOSE, imagePager);
                setPreviewStatus(ImageViewerState.STATE_SILENCE, null);
            }
        });
        animator.start();
    }
}
