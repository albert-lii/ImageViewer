package com.liyi.viewer.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.liyi.viewer.ImageViewerUtil;
import com.liyi.viewer.TransitionCallback;
import com.liyi.viewer.ViewData;
import com.liyi.viewer.dragger.DefaultImageDragger;
import com.liyi.viewer.dragger.ImageDragger;
import com.liyi.viewer.dragger.ImageDraggerState;
import com.liyi.viewer.dragger.ImageDraggerStateListener;
import com.liyi.viewer.dragger.ImageDraggerType;
import com.liyi.viewer.dragger.WxImageDragger;
import com.liyi.viewer.widget.progressbar.CircleProgressBar;

/**
 * 可缩放图片的自定义 View（即 viewPager 的 item）
 */
public class ScaleImageView extends FrameLayout {
    // 图片的位置
    private int mPosition;
    // view 的相关数据
    private ViewData mViewData;
    // 动画执行时间
    private int mDuration;
    // 默认的预览界面的宽高
    private float mDefWidth, mDefHeight;
    // 图片拖拽处理类
    private ImageDragger mImageDragger;
    // 图片拖拽模式
    private int mDragType;
    // 是否执行背景透明度渐变
    private boolean doBackgroundAlpha;
    // 加载进度 view
    private View progressView;

    // 可缩放的 imageView
    private PhotoView imageView;
    private FrameLayout.LayoutParams mImageParams;
    // 过渡背景
    private Drawable mBackground;
    // 手指按下时的坐标
    private float mDownX, mDownY;
    // imageView 是否正在执行动画
    private boolean isImageAnimRunning;
    // imageView 是否正在正在被拖拽
    private boolean isImageDragging;
    // 是否定义了图片尺寸
    private boolean hasImageSize;
    // 图片拖拽状态监听
    private ImageDraggerStateListener mStateListener;


    public ScaleImageView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ScaleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScaleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mDuration = ImageViewerAttacher.DEF_DURATION;
        isImageAnimRunning = false;
        isImageDragging = false;
        hasImageSize = false;
        doBackgroundAlpha = true;

        imageView = new PhotoView(context);
        imageView.setX(0);
        imageView.setY(0);
        mImageParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(mImageParams);
        addView(imageView);
        initDragStateMonitor();
    }

    /**
     * 初始化图片拖拽状态监测
     */
    private void initDragStateMonitor() {
        mStateListener = new ImageDraggerStateListener() {
            @Override
            public void onImageDraggerState(int state) {
                switch (state) {
                    case ImageDraggerState.DRAG_STATE_READY:
                        isImageDragging = true;
                        break;
                    case ImageDraggerState.DRAG_STATE_DRAGGING:
                        isImageDragging = true;
                        break;
                    case ImageDraggerState.DRAG_STATE_BEGIN_REBACK:
                        isImageAnimRunning = true;
                        isImageDragging = false;
                        break;
                    case ImageDraggerState.DRAG_STATE_REBACKING:
                        break;
                    case ImageDraggerState.DRAG_STATE_END_REBACK:
                        isImageAnimRunning = false;
                        break;
                    case ImageDraggerState.DRAG_STATE_BEGIN_EXIT:
                        isImageAnimRunning = true;
                        isImageDragging = false;
                        break;
                    case ImageDraggerState.DRAG_STATE_EXITTING:
                        break;
                    case ImageDraggerState.DRAG_STATE_END_EXIT:
                        isImageAnimRunning = false;
                        setVisibility(View.GONE);
                        break;
                }
                if (isImageDragging || isImageAnimRunning) {
                    setScaleable(false);
                } else {
                    setScaleable(true);
                }
            }
        };
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 是否拦截触摸事件，若拦截，则 ImagePager 自己处理触摸事件；
        // 若不拦截，则 imageView 处理触摸事件
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                /**
                 * 拖拽触发条件：
                 * 1、仅有一个触摸点
                 * 2、图片的缩放等级为 1f
                 * 3、拖拽处理类不为空
                 */
                if (ev.getPointerCount() == 1 && getScale() <= 1f && mImageDragger != null) {
                    float diffX = ev.getX() - mDownX;
                    float diffY = ev.getY() - mDownY;
                    // 上下滑动手势
                    if (Math.abs(diffX) < Math.abs(diffY)) {
                        if ((mDragType == ImageDraggerType.DRAG_TYPE_DEFAULT) || (mDragType == ImageDraggerType.DRAG_TYPE_WX && diffY > 0)) {
                            mImageDragger.bindScaleImageView(this);
                            mImageDragger.onReady(getWidth() != 0 ? getWidth() : mDefWidth, getHeight() != 0 ? getHeight() : mDefHeight);
                            isIntercept = true;
                        }
                    }
                }
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;

            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionDown(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();
    }

    private void onActionMove(MotionEvent event) {
        // 拖拽图片，只有一个触摸点时触发
        if (event.getPointerCount() == 1 && getScale() <= 1f && isImageDragging && mImageDragger != null) {
            mImageDragger.onDragging(mDownX, mDownY, event.getX(), event.getY());
        }
        mDownX = event.getX();
        mDownY = event.getY();
    }

    private void onActionUp(MotionEvent event) {
        // 释放图片
        if (getScale() <= 1f && isImageDragging && mImageDragger != null) {
            mImageDragger.onRelease();
        }
        mDownX = 0;
        mDownY = 0;
    }

    public void setDefSize(float width, float height) {
        this.mDefWidth = width;
        this.mDefHeight = height;
    }

    public void start() {
        start(null);
    }

    public void cancel() {
        cancel(null);
    }

    /**
     * 开启预览
     */
    public void start(TransitionCallback callback) {
        performEnterAnim(callback);
    }

    /**
     * 关闭预览
     */
    public void cancel(TransitionCallback callback) {
        performExitAnim(callback);
    }

    public void setPosition(final int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setViewData(ViewData data) {
        this.mViewData = data;
    }

    public ViewData getViewData() {
        return mViewData;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setImageDraggerType(@ImageDraggerType int type) {
        setImageDraggerType(type, null, getBackground());
    }

    public void setImageDraggerType(@ImageDraggerType int type, ImageViewerAttacher attacher, Drawable background) {
        mDragType = type;
        if (mDragType == ImageDraggerType.DRAG_TYPE_DEFAULT) {
            mImageDragger = new DefaultImageDragger();
        } else if (mDragType == ImageDraggerType.DRAG_TYPE_WX) {
            mImageDragger = new WxImageDragger();
        }
        if (mImageDragger != null) {
            mImageDragger.setBackground(background);
            if (attacher != null) mImageDragger.bindImageViewerAttacher(attacher);
            mImageDragger.setImageDraggerStateListener(mStateListener);
        }
    }

    public void clearImageDragger() {
        if (mImageDragger != null) {
            mImageDragger = null;
        }
    }

    /**
     * 是否正在拖拽图片
     */
    public boolean isImageDragging() {
        return isImageDragging;
    }

    /**
     * imageView 是否正在执行动画
     */
    public boolean isImageAnimRunning() {
        return isImageAnimRunning;
    }

    public void setScaleable(boolean scaleable) {
        imageView.setZoomable(scaleable);
    }

    public boolean isScaleable() {
        return imageView.isZoomable();
    }

    public void setScale(float scale) {
        imageView.setScale(scale);
    }

    public float getScale() {
        return imageView.getScale();
    }

    public void setMaxScale(float maxScale) {
        imageView.setMaximumScale(maxScale);
    }

    public float getMaxScale() {
        return imageView.getMaximumScale();
    }

    public void setMinScale(float minScale) {
        imageView.setMinimumScale(minScale);
    }

    public float getMinScale() {
        return imageView.getMinimumScale();
    }

    public void setDoBackgroundAlpha(boolean isDo) {
        this.doBackgroundAlpha = isDo;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void showProgess() {
        if (progressView == null) {
            CircleProgressBar progressBar = new CircleProgressBar(getContext(), 0x77FAFAfA);
            progressBar.setInnerRadius(ImageViewerUtil.dp2px(getContext(), 19));
            final int size = ImageViewerUtil.dp2px(getContext(), 60);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.width = size;
            lp.height = size;
            lp.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(lp);
            setProgressView(progressBar);
        }
        progressView.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        if (progressView != null) {
            progressView.setVisibility(GONE);
        }
    }

    /**
     * 设置进度条
     *
     * @param view
     */
    public void setProgressView(View view) {
        if (progressView == null || progressView.getParent() == null) {
            progressView = view;
            progressView.setVisibility(GONE);
            addView(progressView);
        }
    }

    /**
     * 移除进度条
     */
    public void removeProgressView() {
        if (progressView != null && progressView.getParent() != null) {
            removeView(progressView);
            progressView = null;
        }
    }

    /**
     * 回收图片内存
     */
    public void recycle() {
        if (imageView != null) {
            ImageViewerUtil.recycleImage(imageView);
        }
        clearImageDragger();
    }

    public void setOnViewClickListener(final OnClickListener listener) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isImageAnimRunning && !isImageDragging && listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    public void setOnViewLongClickListener(final OnLongClickListener listener) {
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isImageAnimRunning && !isImageDragging && listener != null) {
                    return listener.onLongClick(v);
                }
                return false;
            }
        });
    }

    private void setBackgroundAlpha(int alpha) {
        if (mBackground != null) {
            mBackground.setAlpha(alpha);
        }
    }

    /**
     * 执行进场动画
     */
    private void performEnterAnim(final TransitionCallback callback) {
        isImageAnimRunning = true;
        // 图片预览界面的宽高
        final float previewW = getWidth() != 0 ? getWidth() : mDefWidth;
        final float previewH = getHeight() != 0 ? getHeight() : mDefHeight;
        final float old_width = mViewData.getTargetWidth();
        final float old_height = mViewData.getTargetHeight();
        final float new_width, new_height;
        // 如果定义了图片的原始宽高
        if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            // 获取 imageView 的缩放比例
            final float scale = Math.min(previewW / mViewData.getImageWidth(), previewH / mViewData.getImageHeight());
            // 缩放后的 imageView 的宽度和高度
            new_width = mViewData.getImageWidth() * scale;
            new_height = mViewData.getImageHeight() * scale;
            // 为了使动画看起来更流畅
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            hasImageSize = true;
        } else {
            new_width = previewW;
            new_height = previewH;
            hasImageSize = false;
        }
        final float from_x = mViewData.getTargetX();
        final float from_y = mViewData.getTargetY();
        final float to_x = (previewW - new_width) / 2;
        final float to_y = (previewH - new_height) / 2;
        if (doBackgroundAlpha && mBackground == null && getBackground() != null) {
            mBackground = getBackground().mutate();
        }
        setVisibility(VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mDuration);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (callback != null) {
                    callback.onTransitionStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (hasImageSize) {
                    // 在动画完成后，将 imageView 充满整个预览界面
                    imageView.setX(0);
                    imageView.setY(0);
                    mImageParams.width = (int) previewW;
                    mImageParams.height = (int) previewH;
                    imageView.setLayoutParams(mImageParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                isImageAnimRunning = false;
                if (callback != null) {
                    callback.onTransitionEnd();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);
                float width = evaluator.evaluate(fraction, old_width, new_width);
                float height = evaluator.evaluate(fraction, old_height, new_height);

                imageView.setX(x);
                imageView.setY(y);
                mImageParams.width = (int) width;
                mImageParams.height = (int) height;
                imageView.setLayoutParams(mImageParams);
                if (doBackgroundAlpha) {
                    setBackgroundAlpha((int) (fraction * 255));
                }
                if (callback != null) {
                    callback.onTransitionRunning(fraction);
                }
            }
        });
        animator.start();
    }

    /**
     * 执行退场动画
     */
    private void performExitAnim(final TransitionCallback callback) {
        isImageAnimRunning = true;
        // 图片预览界面的宽高
        final float previewW = getWidth() != 0 ? getWidth() : mDefWidth;
        final float previewH = getHeight() != 0 ? getHeight() : mDefHeight;
        // 如果图片处于被放大状态，先将图片恢复原样，动画会看起来更流畅
        if (imageView != null && imageView.getScale() > 1f) {
            imageView.setScale(1f);
        }
        // 图片的原始宽高
        float oriImg_width = 0, oriImg_height = 0;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            oriImg_width = drawable.getIntrinsicWidth();
            oriImg_height = drawable.getIntrinsicHeight();
        } else if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            oriImg_width = mViewData.getImageWidth();
            oriImg_height = mViewData.getImageHeight();
        }
        final float scale = Math.min(previewW / oriImg_width, previewH / oriImg_height);
        // 图片的缩放等级为 1f 时的宽高
        final float adjustImg_width = oriImg_width * scale;
        final float adjustImg_height = oriImg_height * scale;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 计算动画中用到的参数
        final float old_width = adjustImg_width != 0 ? adjustImg_width : imageView.getWidth();
        final float old_height = adjustImg_height != 0 ? adjustImg_height : imageView.getHeight();
        final float new_width = mViewData.getTargetWidth();
        final float new_height = mViewData.getTargetHeight();
        final float from_x = (previewW - old_width) / 2;
        final float from_y = (previewH - old_height) / 2;
        final float to_x = mViewData.getTargetX();
        final float to_y = mViewData.getTargetY();
        if (doBackgroundAlpha && mBackground == null && getBackground() != null) {
            mBackground = getBackground().mutate();
        }
        imageView.setX(from_x);
        imageView.setY(from_y);
        mImageParams.width = (int) old_width;
        mImageParams.height = (int) old_height;
        imageView.setLayoutParams(mImageParams);
        if (progressView != null && progressView.getVisibility() == VISIBLE) {
            removeProgressView();
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (callback != null) {
                    callback.onTransitionStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
                isImageAnimRunning = false;
                if (callback != null) {
                    callback.onTransitionEnd();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);
                float width = evaluator.evaluate(fraction, old_width, new_width);
                float height = evaluator.evaluate(fraction, old_height, new_height);
                imageView.setX(x);
                imageView.setY(y);
                mImageParams.width = (int) width;
                mImageParams.height = (int) height;
                imageView.setLayoutParams(mImageParams);
                if (doBackgroundAlpha) {
                    setBackgroundAlpha((int) ((1 - fraction) * 255));
                }
                if (callback != null) {
                    callback.onTransitionRunning(fraction);
                }
            }
        });
        animator.start();
    }
}
