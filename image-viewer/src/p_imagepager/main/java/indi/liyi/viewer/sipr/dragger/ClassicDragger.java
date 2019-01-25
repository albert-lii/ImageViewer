package indi.liyi.viewer.sipr.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import indi.liyi.viewer.ImageViewerStatus;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.ScaleImagePager;


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
        setPreviewStatus(ImageViewerStatus.STATUS_DRAGGING, imagePager);
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
        final float viewY = imageView.getY();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(REBACK_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float y = evaluator.evaluate(currentValue, viewY, 0);
                final float alpha = evaluator.evaluate(currentValue, getBackgroundAlpha(), NO_BACKGROUND_ALPHA);
                imageView.setY(y);
                changeBackgroundAlpha((int) alpha);
                setDragStatus(DragStatus.STATUS_REBACKING);
                setPreviewStatus(ImageViewerStatus.STATUS_REBACKING, imagePager);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setDragStatus(DragStatus.STATUS_END_REBACK);
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
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
        // imageView 当前的 Y 轴坐标
        final float viewY = imageView.getY();
        // 图片的原始宽高
        float origImageWidth = 0, origImageHeight = 0;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            origImageWidth = drawable.getIntrinsicWidth();
            origImageHeight = drawable.getIntrinsicHeight();
        } else if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
            origImageWidth = viewData.getImageWidth();
            origImageHeight = viewData.getImageHeight();
        }
        final float scale = Math.min(mPrevWidth / origImageWidth, mPrevHeight / origImageHeight);
        // 图片的缩放等级为 1f 时的图片高度
        final float adjustHeight = origImageHeight * scale;
        // 图片的缩放等级为 1f 且居中时时，在预览界面中的 Y 轴坐标
        final float adjustImgY = (mPrevHeight - adjustHeight) / 2;
        // 图片在预览界面中的当前 Y 轴坐标
        float currentImgY = viewY + adjustImgY;
        // 此处加 20 ,是为了减少误差，防止影响动画美观
        final float toY = currentImgY > adjustImgY ? viewY + (mPrevHeight - currentImgY + 20) : viewY - (currentImgY + adjustHeight + 20);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(EXIT_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float y = evaluator.evaluate(progress, viewY, toY);
                final float alpha = evaluator.evaluate(progress, getBackgroundAlpha(), 0);
                imageView.setY(y);
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
                setDragStatus(DragStatus.STATUS_END_EXIT);
                imageView.setY(0);
                changeBackgroundAlpha(NO_BACKGROUND_ALPHA);
                setPreviewStatus(ImageViewerStatus.STATUS_COMPLETE_CLOSE, imagePager);
                setPreviewStatus(ImageViewerStatus.STATUS_SILENCE, null);
            }
        });
        animator.start();
    }
}
