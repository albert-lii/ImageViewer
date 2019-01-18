package indi.liyi.viewer.dragger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import indi.liyi.viewer.ImageViewerState;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.widget.ScaleImagePager;


/**
 * 默认的图片拖拽处理类
 */
public class ClassicDragger extends BaseDragger {
    // 恢复原样的动画时间
    private final int REBACK_ANIM_DURATION = 200;
    // 退出预览的动画时间
    private final int EXIT_ANIM_DURATION = 200;

    // 预览界面的宽高
    private float mPreiWidth;
    private float mPreiHeight;

    private ScaleImagePager imagePager;

    public ClassicDragger() {

    }

    @Override
    public void injectImagePager(ScaleImagePager imagePager) {
        super.injectImagePager(imagePager);
        this.imagePager = imagePager;
    }

    @Override
    public void onDown(float preiWidth, float preiHeight) {
        super.onDown(preiWidth, preiHeight);
        this.mPreiWidth = preiWidth;
        this.mPreiHeight = preiHeight;
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
        if (!imagePager.isImageAnimRunning()) {
            View imageView = imagePager.getImageView();
            final float disOnY = Math.abs(imageView.getY());
            if (disOnY <= getMaxMovableDisOnY()) {
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
        final float imageViewY = imageView.getY();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(REBACK_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float y = evaluator.evaluate(currentValue, imageViewY, 0);
                final float alpha = evaluator.evaluate(currentValue, getBackgroundAlpha(), NO_BACKGROUND_ALPHA);
                imageView.setY(y);
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
        // imageView 当前的 Y 轴坐标
        final float imageViewY = imageView.getY();
        // 图片的原始宽高
        float oriImg_width = 0, oriImg_height = 0;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            oriImg_width = drawable.getIntrinsicWidth();
            oriImg_height = drawable.getIntrinsicHeight();
        } else if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
            oriImg_width = viewData.getImageWidth();
            oriImg_height = viewData.getImageHeight();
        }
        final float scale = Math.min(mPreiWidth / oriImg_width, mPreiHeight / oriImg_height);
        // 图片的缩放等级为 1f 时的图片高度
        final float adjustHeight = oriImg_height * scale;
        // 图片的缩放等级为 1f 且居中时时，在预览界面中的 Y 轴坐标
        final float adjustImgY = (mPreiHeight - adjustHeight) / 2;
        // 图片在预览界面中的当前 Y 轴坐标
        float currentImgY = imageViewY + adjustImgY;
        // 此处加 20 ,是为了减少误差，防止影响动画美观
        final float toY = currentImgY > adjustImgY ? imageViewY + (mPreiHeight - currentImgY + 20) : imageViewY - (currentImgY + adjustHeight + 20);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(EXIT_ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float y = evaluator.evaluate(progress, imageViewY, toY);
                final float alpha = evaluator.evaluate(progress, getBackgroundAlpha(), 0);
                imageView.setY(y);
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
                imageView.setY(0);
                changeBackgroundAlpha((int) getBackgroundAlpha());
                setPreviewStatus(ImageViewerState.STATE_COMPLETE_CLOSE, imagePager);
                setPreviewStatus(ImageViewerState.STATE_SILENCE, null);
            }
        });
        animator.start();
    }
}
