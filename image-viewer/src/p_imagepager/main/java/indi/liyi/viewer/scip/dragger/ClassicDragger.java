package indi.liyi.viewer.scip.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import indi.liyi.viewer.scip.ScaleImagePager;
import indi.liyi.viewer.scip.ViewData;


/**
 * 默认的图片拖拽处理类
 */
public class ClassicDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int REBACK_ANIM_DURATION = 200;
    // 退出预览的动画时间
    private final int EXIT_ANIM_DURATION = 200;

    // 预览界面的宽高
    private float mPrevWidth;
    private float mPrevHeight;

    private ScaleImagePager imagePager;

    public ClassicDragger() {

    }

    @Override
    public void injectImagePager(ScaleImagePager imagePager) {
        this.imagePager = imagePager;
    }

    @Override
    public void onDown(float prevWidth, float prevHeight) {
        super.onDown(prevWidth, prevHeight);
        this.mPrevWidth = prevWidth;
        this.mPrevHeight = prevHeight;
    }

    @Override
    public void onDrag(float downX, float downY, float curX, float curY) {
        super.onDrag(downX, downY, curX, curY);
        View imageView = imagePager.getImageView();
        // 计算 view 的 Y 轴坐标
        final float diffY = curY - downY;
        final float oldViewY = imageView.getY();
        final float newViewY = oldViewY + diffY;
        imageView.setY(newViewY);
        // 计算背景透明度
        final float value = Math.abs(newViewY) / getAlphaBase();
        changeBackgroundAlpha((int) ((value < 0.8f ? 1 - value : 0.2f) * NO_BACKGROUND_ALPHA));
    }

    @Override
    public void onUp() {
        super.onUp();
        if (!imagePager.isAnimRunning()) {
            View imageView = imagePager.getImageView();
            final float disOnY = Math.abs(imageView.getY());
            if (disOnY <= getMaxMovableDisOnY()) {
                setDragStatus(DragStatus.STATUS_BEGIN_REBACK);
                reback();
            } else {
                setDragStatus(DragStatus.STATUS_BEGIN_EXIT);
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
        final View imageView = imagePager.getImageView();
        final float fromY = imageView.getY();
        final float toY = 0;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(REBACK_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                if (fromY != toY) {
                    final float y = evaluator.evaluate(currentValue, fromY, toY);
                    imageView.setY(y);
                }
                if (oldBgAlpha != NO_BACKGROUND_ALPHA) {
                    final float alpha = evaluator.evaluate(currentValue, oldBgAlpha, NO_BACKGROUND_ALPHA);
                    changeBackgroundAlpha((int) alpha);
                }
                setDragStatus(DragStatus.STATUS_REBACKING);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setDragStatus(DragStatus.STATUS_END_REBACK);
                if (checkAttacherNotNull()) {
                    getWrapper().setViewPagerScrollable(true);
                }
            }
        });
        animator.start();
    }

    /**
     * 退出预览
     */
    private void exit() {
        final ImageView imageView = imagePager.getImageView();
        final ViewData viewData = imagePager.getViewData();
        // imageView 当前的 Y 轴坐标
        final float fromY = imageView.getY();
        // 图片的原始宽高
        float origImageWidth, origImageHeight;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            origImageWidth = drawable.getIntrinsicWidth();
            origImageHeight = drawable.getIntrinsicHeight();
        } else {
            origImageWidth = viewData.getImageWidth();
            origImageHeight = viewData.getImageHeight();
        }
        final float scale = Math.min(mPrevWidth / origImageWidth, mPrevHeight / origImageHeight);
        // 图片的缩放等级为 1f 时的图片高度
        final float adjustImageHeight = origImageHeight * scale;
        // 图片的缩放等级为 1f 且居中时时，在预览界面中的 Y 轴坐标
        final float adjustImageY = (mPrevHeight - adjustImageHeight) / 2;
        // 图片在预览界面中的当前 Y 轴坐标
        float currentImageY = fromY + adjustImageY;
        final boolean isOutOfPreview;
        if (
            // 图片已经从顶部滑出预览界面
                (currentImageY + adjustImageHeight) <= 0
                        // 图片已经从底部滑出预览界面
                        || currentImageY >= mPrevHeight) {
            isOutOfPreview = true;
        } else {
            isOutOfPreview = false;
        }
        // 此处加 20 ,是为了减少误差，防止影响动画美观
        final float toY = currentImageY > adjustImageY ?
                // 向下滑动
                fromY + (mPrevHeight - currentImageY) + 20
                // 向上滑动
                : fromY - (currentImageY + adjustImageHeight) - 20;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(EXIT_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final FloatEvaluator evaluator = new FloatEvaluator();
            final int oldBgAlpha = getBackgroundAlpha();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                if (!isOutOfPreview) {
                    final float y = evaluator.evaluate(currentValue, fromY, toY);
                    imageView.setY(y);
                }
                final float alpha = evaluator.evaluate(currentValue, oldBgAlpha, 0);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_EXITTING);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setDragStatus(DragStatus.STATUS_END_EXIT);
                if (checkAttacherNotNull()) {
                    getWrapper().exitEnd();
                }
            }
        });
        animator.start();
    }
}
