package indi.liyi.viewer.imgpg.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.imgpg.ViewData;


/**
 * 默认的图片拖拽处理类
 */
public class ClassicDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int ANIM_DURATION_RESTORE = 200;
    // 退出预览的动画时间
    private final int ANIM_DURATION_EXIT = 200;

    // 预览界面的宽高
    private float mPrevWidth;
    private float mPrevHeight;
    // 图片被拖拽时的背景透明度基数
    private float mAlphaBase;

    private ImagePager imagePager;
    private ImageView imageView;
    private ObjectAnimator mAnimator;

    public ClassicDragger() {

    }

    @Override
    public void init(int prevWidth, int prevHeight, View view) {
        super.init(prevWidth, prevHeight, view);
        this.imagePager = (ImagePager) view;
        this.imageView = imagePager.getImageView();
        this.mPrevWidth = prevWidth;
        this.mPrevHeight = prevHeight;
        this.mAlphaBase = getMaxMovableDisOnY() * 2;
    }

    @Override
    public void onDown(float downX, float downY) {
        super.onDown(downX, downY);
    }

    @Override
    public void onDrag(float lastX, float lastY, MotionEvent ev) {
        super.onDrag(lastX, lastY, ev);
        // 计算 view 的 Y 轴坐标
        final float disY = ev.getY() - lastY;
        final float newViewY = imageView.getY() + disY;
        imageView.setY(newViewY);
        // 计算背景透明度
        final float value = Math.abs(newViewY) / mAlphaBase;
        changeBackgroundAlpha((int) ((value < 0.8f ? 1 - value : 0.2f) * NO_BACKGROUND_ALPHA));
    }

    @Override
    public void onUp() {
        super.onUp();
        final float disOnY = Math.abs(imageView.getY());
        if (disOnY <= getMaxMovableDisOnY()) {
            setDragStatus(DragStatus.STATUS_BEGIN_RESTORE);
            doRestoreAnim();
        } else {
            setDragStatus(DragStatus.STATUS_BEGIN_EXIT);
            doExitAnim();
        }
    }

    @Override
    public void clear() {
        imagePager = null;
        imageView = null;
    }

    /**
     * 执行图片恢复原样动画
     */
    private void doRestoreAnim() {
        executeCommonAnim(0, ANIM_DURATION_RESTORE,
                new AnimatorListenerAdapter() {

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

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float progress = animation.getAnimatedFraction();
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
        // 图片的原始宽高
        float originalImageWidth, originalImageHeight;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            originalImageWidth = drawable.getIntrinsicWidth();
            originalImageHeight = drawable.getIntrinsicHeight();
        } else {
            originalImageWidth = viewData.getImageWidth();
            originalImageHeight = viewData.getImageHeight();
        }
        if (originalImageWidth == 0 || originalImageHeight == 0) {
            originalImageWidth = mPrevWidth;
            originalImageHeight = mPrevHeight;
        }
        final float scale = Math.min(mPrevWidth / originalImageWidth, mPrevHeight / originalImageHeight);
        // 图片的缩放等级为 1f 时的图片高度
        final float adjustImageHeight = originalImageHeight * scale;
        // 图片的缩放等级为 1f 且居中时时，在预览界面中的 y 轴坐标
        final float adjustImageY = (mPrevHeight - adjustImageHeight) / 2;
        // imageView 当前的 Y 轴坐标
        final float fromY = imageView.getY();
        // 图片在预览界面中的当前 Y 轴坐标
        float currentImageY = fromY + adjustImageY;
        // 图片是否已经滑出预览界面
        final boolean isOutOfInterface;
        if (
            // 图片已经从顶部滑出预览界面
                (currentImageY + adjustImageHeight) <= 0
                        // 图片已经从底部滑出预览界面
                        || currentImageY >= mPrevHeight) {
            isOutOfInterface = true;
        } else {
            isOutOfInterface = false;
        }
        final float toY;
        if (isOutOfInterface) {
            toY = fromY;
        } else {
            // 此处加 20 ,是为了减少误差，防止影响动画美观
            toY = currentImageY > adjustImageY ?
                    // 向下滑动
                    fromY + (mPrevHeight - currentImageY) + 20
                    // 向上滑动
                    : fromY - (currentImageY + adjustImageHeight) - 20;
        }
        executeCommonAnim(toY, ANIM_DURATION_EXIT,
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setDragStatus(DragStatus.STATUS_END_RESTORE);
                        if (checkAttacherNotNull()) {
                            getWrapper().exitEnd();
                        }
                    }
                }, new ValueAnimator.AnimatorUpdateListener() {
                    final FloatEvaluator evaluator = new FloatEvaluator();
                    final int oldBgAlpha = getBackgroundAlpha();

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float progress = animation.getAnimatedFraction();
                        final float alpha = evaluator.evaluate(progress, oldBgAlpha, 0);
                        changeBackgroundAlpha((int) alpha);
                        setDragStatus(DragStatus.STATUS_EXITTING);
                    }
                });
    }

    private void executeCommonAnim(float toY, int duration, AnimatorListenerAdapter listener, ValueAnimator.AnimatorUpdateListener updater) {
        mAnimator = ObjectAnimator.ofFloat(imageView, "y", toY);
        mAnimator.setDuration(duration);
        mAnimator.addListener(listener);
        mAnimator.addUpdateListener(updater);
        mAnimator.start();
    }
}
