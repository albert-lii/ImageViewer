package indi.liyi.viewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 图片进退场动画处理类
 */
public class ImageTransfer {
    // 没有需要执行的动画
    public static final int ACTION_IDEL = -1;
    // 执行进场动画
    public static final int ACTION_ENTER = 0;
    // 执行退场动画
    public static final int ACTION_EXIT = 1;
    // 拖拽后，执行复原动画
    public static final int ACTION_DRAG_RESTORE = 2;
    // 在灵巧模式下，拖拽后，执行退场动画
    public static final int ACTION_DRAG_EXIT_AGILE = 3;
    // 在简单模式下，拖拽后，执行退场动画
    public static final int ACTION_DRAG_EXIT_SIMPLE = 4;


    private int interfaceWidth, interfaceHeight;
    private int startWidth, startHeight;
    private int endWidth, endHeight;
    private float startX, startY;
    private float endX, endY;
    private long duration;
    private Drawable background;
    private int startAlpha, endAlpha;

    private int animAction;
    // 是否设置了图片的原始尺寸
    private boolean hasSetImageOriginalSize;
    private ImageView imageView;
    private OnTransCallback mTransCallback;


    public ImageTransfer(int interfaceWidth, int interfaceHeight) {
        this.interfaceWidth = interfaceWidth;
        this.interfaceHeight = interfaceHeight;
    }

    public ImageTransfer with(@NonNull ImageView target) {
        this.imageView = target;
        return this;
    }

    /**
     * 加载进场动画数据
     */
    public ImageTransfer loadEnterData(@NonNull ViewData viewData) {
        startX = viewData.getTargetX();
        startY = viewData.getTargetY();
        startWidth = viewData.getTargetWidth();
        startHeight = viewData.getTargetHeight();
        int imageWidth, imageHeight;
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            imageWidth = interfaceWidth;
            imageHeight = interfaceHeight;
            hasSetImageOriginalSize = false;
        } else {
            imageWidth = viewData.getImageWidth();
            imageHeight = viewData.getImageHeight();
            hasSetImageOriginalSize = true;
        }
        float scale = Math.min(interfaceWidth * 1f / imageWidth, interfaceHeight * 1f / imageHeight);
        endWidth = (int) (imageWidth * scale);
        endHeight = (int) (imageHeight * scale);
        endX = (interfaceWidth - endWidth) * 1f / 2;
        endY = (interfaceHeight - endHeight) * 1f / 2;
        startAlpha = 0;
        endAlpha = 255;
        animAction = ACTION_ENTER;
        return this;
    }

    /**
     * 加载退场动画数据
     */
    public ImageTransfer loadExitData(@NonNull ViewData viewData) {
        endX = viewData.getTargetX();
        endY = viewData.getTargetY();
        endWidth = viewData.getTargetWidth();
        endHeight = viewData.getTargetHeight();
        int imageWidth, imageHeight;
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            Drawable thumb = imageView.getDrawable();
            if (thumb != null) {
                imageWidth = thumb.getIntrinsicWidth();
                imageHeight = thumb.getIntrinsicHeight();
                hasSetImageOriginalSize = true;
            } else {
                imageWidth = interfaceWidth;
                imageHeight = interfaceHeight;
                hasSetImageOriginalSize = false;
            }
        } else {
            imageWidth = viewData.getImageWidth();
            imageHeight = viewData.getImageHeight();
            hasSetImageOriginalSize = true;
        }
        float scale = Math.min(interfaceWidth * 1f / imageWidth, interfaceHeight * 1f / imageHeight);
        startWidth = (int) (imageWidth * scale);
        startHeight = (int) (imageHeight * scale);
        startX = (interfaceWidth - startWidth) * 1f / 2;
        startY = (interfaceHeight - startHeight) * 1f / 2;
        startAlpha = background.getAlpha();
        endAlpha = 0;
        animAction = ACTION_EXIT;
        return this;
    }

    /**
     * 加载拖拽复原动画数据
     */
    public ImageTransfer loadDragRestoreData() {
        startX = imageView.getTranslationX();
        startY = imageView.getTranslationY();
        startWidth = imageView.getWidth();
        startHeight = imageView.getHeight();
        endX = 0;
        endY = 0;
        endWidth = interfaceWidth;
        endHeight = interfaceHeight;
        startAlpha = background.getAlpha();
        endAlpha = 255;
        animAction = ACTION_DRAG_RESTORE;
        return this;
    }


    /**
     * 灵巧拖拽模式下，加载拖拽退场动画数据
     */
    public ImageTransfer loadDragExitDataInAgile(@NonNull ViewData viewData) {
        int imageWidth, imageHeight;
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            Drawable thumb = imageView.getDrawable();
            if (thumb != null) {
                imageWidth = thumb.getIntrinsicWidth();
                imageHeight = thumb.getIntrinsicHeight();
                hasSetImageOriginalSize = true;
            } else {
                imageWidth = interfaceWidth;
                imageHeight = interfaceHeight;
                hasSetImageOriginalSize = false;
            }
        } else {
            imageWidth = viewData.getImageWidth();
            imageHeight = viewData.getImageHeight();
            hasSetImageOriginalSize = true;
        }
        float adjustScale = Math.min(interfaceWidth * 1f / imageWidth, interfaceHeight * 1f / imageHeight);
        float dragScale = imageView.getWidth() * 1f / interfaceWidth;
        startWidth = (int) (imageWidth * adjustScale * dragScale);
        startHeight = (int) (imageHeight * adjustScale * dragScale);
        startX = imageView.getTranslationX() + (imageView.getWidth() - startWidth) * 1f / 2;
        startY = imageView.getTranslationY() + (imageView.getHeight() - startHeight) * 1f / 2;
        // 图片是否已经滑出预览界面
        if (
            // imageView 已经从左边滑出预览界面
                (startX + startWidth) <= 0
                        // imageView 已经从右边滑出预览界面
                        || startX >= interfaceWidth
                        // imageView 已经从底部滑出图片预览界面
                        || startY >= interfaceHeight) {
            endX = startX;
            endY = startY;
            endWidth = startWidth;
            endHeight = startHeight;
        } else {
            endX = viewData.getTargetX();
            endY = viewData.getTargetY();
            endWidth = viewData.getTargetWidth();
            endHeight = viewData.getTargetHeight();
        }
        startAlpha = background.getAlpha();
        endAlpha = 0;
        animAction = ACTION_DRAG_EXIT_AGILE;
        return this;
    }

    /**
     * 简单拖拽模式下，加载拖拽退场动画数据
     */
    public ImageTransfer loadDragExitDataInSimple(@NonNull ViewData viewData) {
        startX = endX = 0;
        startY = imageView.getTranslationY();
        startWidth = endWidth = imageView.getWidth();
        startHeight = endHeight = imageView.getHeight();

        // 计算 endY
        int imageWidth, imageHeight;
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            Drawable thumb = imageView.getDrawable();
            if (thumb != null) {
                imageWidth = thumb.getIntrinsicWidth();
                imageHeight = thumb.getIntrinsicHeight();
                hasSetImageOriginalSize = true;
            } else {
                imageWidth = interfaceWidth;
                imageHeight = interfaceHeight;
                hasSetImageOriginalSize = false;
            }
        } else {
            imageWidth = viewData.getImageWidth();
            imageHeight = viewData.getImageHeight();
            hasSetImageOriginalSize = true;
        }
        float scale = Math.min(interfaceWidth * 1f / imageWidth, interfaceHeight * 1f / imageHeight);
        // 图片的缩放等级为 1f 时，图片的高度
        int adjustImageHeight = (int) (imageHeight * scale);
        // 图片的缩放等级为 1f 且居中时时，在预览界面中的距离容器顶部的距离
        float disOnYFromTop = (interfaceHeight - adjustImageHeight) / 2;
        // 图片在预览界面中的当前 Y 轴坐标
        float currentImageY = startY + disOnYFromTop;
        // 图片是否已经滑出浏览界面
        if (
            // 图片已经从顶部滑出预览界面
                (currentImageY + adjustImageHeight) <= 0
                        // 图片已经从底部滑出预览界面
                        || currentImageY >= interfaceHeight) {
            endY = startY;
        } else {
            // 此处加 20 ,是为了减少误差，防止影响动画美观
            endY = currentImageY > disOnYFromTop ?
                    // 向下滑动
                    startY + (interfaceHeight - currentImageY) + 20
                    // 向上滑动
                    : startY - (currentImageY + adjustImageHeight) - 20;
        }

        startAlpha = background.getAlpha();
        endAlpha = 0;
        animAction = ACTION_DRAG_EXIT_SIMPLE;
        return this;
    }

    public ImageTransfer background(Drawable background) {
        this.background = background;
        return this;
    }

    public ImageTransfer duration(long duration) {
        this.duration = duration;
        return this;
    }

    public ImageTransfer callback(OnTransCallback callback) {
        this.mTransCallback = callback;
        return this;
    }

    public void play() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                if (startX != endX) {
                    imageView.setTranslationX(calculateByProgress(progress, startX, endX));
                }
                if (startY != endY) {
                    imageView.setTranslationY(calculateByProgress(progress, startY, endY));
                }
                if (startWidth != endWidth || startHeight != endHeight) {
                    imageView.getLayoutParams().width = (int) calculateByProgress(progress, startWidth, endWidth);
                    imageView.getLayoutParams().height = (int) calculateByProgress(progress, startHeight, endHeight);
                    imageView.requestLayout();
                }
                if (startAlpha != endAlpha) {
                    background.setAlpha((int) calculateByProgress(progress, startAlpha, endAlpha));
                }
                if (mTransCallback != null) {
                    mTransCallback.onRunning(progress);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (animAction == ACTION_ENTER
                        || animAction == ACTION_EXIT
                        || animAction == ACTION_DRAG_EXIT_AGILE) {
                    if (hasSetImageOriginalSize) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
                if (mTransCallback != null) {
                    mTransCallback.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animAction == ACTION_ENTER && hasSetImageOriginalSize) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setTranslationX(0);
                    imageView.setTranslationY(0);
                    imageView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
                    imageView.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                    imageView.requestLayout();
                }
                imageView = null;
                background = null;
                animAction = ACTION_IDEL;
                if (mTransCallback != null) {
                    mTransCallback.onEnd();
                }
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 根据进度，计算过渡值
     */
    private float calculateByProgress(float progress, float startVal, float endVal) {
        return startVal + progress * (endVal - startVal);
    }

    public interface OnTransCallback {
        void onStart();

        void onRunning(float progress);

        void onEnd();
    }
}
