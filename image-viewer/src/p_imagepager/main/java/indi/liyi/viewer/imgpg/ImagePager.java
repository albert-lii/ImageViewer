package indi.liyi.viewer.imgpg;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.text.DecimalFormat;

import indi.liyi.viewer.R;
import indi.liyi.viewer.ViewerWrapper;
import indi.liyi.viewer.imgpg.dragger.AgileDragger;
import indi.liyi.viewer.imgpg.dragger.ClassicDragger;
import indi.liyi.viewer.imgpg.dragger.DragHandler;
import indi.liyi.viewer.imgpg.dragger.DragMode;
import indi.liyi.viewer.imgpg.dragger.DragStatus;
import indi.liyi.viewer.imgpg.dragger.OnDragStatusListener;
import indi.liyi.viewer.progrv.ProgressWheel;
import indi.liyi.viewer.scimgv.PhotoView;

/**
 * 可缩放图片的自定义 View（即 viewPager 的 item）
 */
public class ImagePager extends FrameLayout {
    public static final int DEF_ANIM_DURATION = 300;

    // 是否执行进场动画
    private boolean doEnterAnim = true;
    // 是否执行退场动画
    private boolean doExitAnim = true;
    // 进退场动画执行时间
    private int mDuration = DEF_ANIM_DURATION;
    // 是否可拖拽
    private boolean canDragged = true;
    // 图片拖拽模式
    private int mDragMode = DragMode.MODE_CLASSIC;
    // 是否执行背景透明度渐变
    private boolean canBgAlpha = true;
    // 是否显示加载进度
    private boolean showProgress = true;

    // 手指最近一次的操作坐标
    private float mLastX, mLastY;
    // imageView 是否正在执行动画
    private boolean isAnimRunning = false;
    // imageView 是否正在被拖拽
    private boolean isDragged = false;

    // 是否当作一个 item
    private boolean isAsItem = true;
    // 当前的位置
    private int mPosition;
    // 背景
    private Drawable mBackground;
    // view 的相关数据
    private ViewData mViewData;
    private ProgressWheel progressView;
    private PhotoView imageView;
    private FrameLayout.LayoutParams mImageViewParams;
    // 图片拖拽处理类
    private DragHandler mDragHandler;
    // 内部设置的图片拖拽状态监听
    private OnDragStatusListener mInternalDragStatusListener;
    // 外部部设置的图片拖拽状态监听
    private OnDragStatusListener mExternalDragStatusListener;
    // 图片加载器
    private BaseImageLoader mImageLoader;


    public ImagePager(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ImagePager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImagePager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ivr_ImagePager);
            if (a != null) {
                doEnterAnim = a.getBoolean(R.styleable.ivr_ImagePager_imgpg_doEnterAnim, true);
                doExitAnim = a.getBoolean(R.styleable.ivr_ImagePager_imgpg_doExitAnim, true);
                canDragged = a.getBoolean(R.styleable.ivr_ImagePager_imgpg_canDragged, true);
                canBgAlpha = a.getBoolean(R.styleable.ivr_ImagePager_imgpg_canBgAlpha, true);
                mDragMode = a.getInteger(R.styleable.ivr_ImagePager_imgpg_dragMode, DragMode.MODE_CLASSIC);
                mDuration = a.getInteger(R.styleable.ivr_ImagePager_imgpg_duration, DEF_ANIM_DURATION);
                showProgress = a.getBoolean(R.styleable.ivr_ImagePager_imgpg_showProgress, true);
                a.recycle();
            }
            isAsItem = false;
        }
        isAnimRunning = false;
        isDragged = false;
        initView(context);
    }

    private void initView(Context context) {
        // 添加 imageView
        imageView = new PhotoView(context);
        imageView.setX(0);
        imageView.setY(0);
        mImageViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(mImageViewParams);
        addView(imageView);

        // 添加进度条
        progressView = new ProgressWheel(getContext());
        final int size = Utils.dp2px(getContext(), 60);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(size, size);
        barLp.gravity = Gravity.CENTER;
        progressView.setLayoutParams(barLp);
        final int barWidth = Utils.dp2px(getContext(), 3);
        progressView.setBarColor(Color.parseColor("#CCFFFFFF"));
        progressView.setBarWidth(barWidth);
        progressView.setBarLength(Utils.dp2px(getContext(), 50));
        progressView.setRimColor(Color.parseColor("#22FFFFFF"));
        progressView.setRimWidth(barWidth);
        progressView.setContourColor(Color.parseColor("#10000000"));
        progressView.setSpinSpeed(3.5f);
        progressView.setText("");
        progressView.setTextColor(Color.parseColor("#CCFFFFFF"));
        progressView.setTextSize(Utils.dp2px(getContext(), 14));
        progressView.setVisibility(GONE);
        addView(progressView);

        if (canDragged) {
            setDragMode(mDragMode);
        }
        // 如果不是作为 item，而是单独使用，则设置显示为 INVISIBLE
        if (!isAsItem) {
            setVisibility(INVISIBLE);
        }
    }

    private void setDragStatus(int status) {
        if (mExternalDragStatusListener != null) {
            mExternalDragStatusListener.onDragStatusChanged(status);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 是否拦截触摸事件？
        // 若拦截，则 ImagePager 自己处理触摸事件；
        // 若不拦截，则 ImageView 处理触摸事件
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                /**
                 * 拖拽触发条件：
                 * 1.允许拖拽
                 * 2.仅有一个触摸点
                 * 3.图片的缩放等级 = 1f
                 * 4.拖拽处理类不为空
                 * 5.手势为上下滑动手势，左右滑动不触发
                 */
                if (canDragged && ev.getPointerCount() == 1 && getScale() <= 1f && mDragHandler != null) {
                    float disX = ev.getX() - mLastX;
                    float disY = ev.getY() - mLastY;
                    // 上下滑动手势
                    if (Math.abs(disX) < Math.abs(disY)) {
                        if ((mDragMode == DragMode.MODE_CLASSIC) || (mDragMode == DragMode.MODE_AGLIE && disY > 0)) {
                            mDragHandler.init(getWidth(), getHeight(), this);
                            mDragHandler.canChangeBgAlpha(canBgAlpha);
                            mDragHandler.onDown(mLastX, mLastY);
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
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;

            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionMove(MotionEvent event) {
        // 拖拽图片，只有一个触摸点时触发
        if (canDragged
                && event.getPointerCount() == 1
                && getScale() <= 1f
                && isDragged
                && mDragHandler != null) {
            mDragHandler.onDrag(mLastX, mLastY, event);
        }
        mLastX = event.getX();
        mLastY = event.getY();
    }

    private void onActionUp() {
        // 释放图片
        if (getScale() <= 1f && isDragged && mDragHandler != null) {
            mDragHandler.onUp();
        }
        mLastX = 0;
        mLastY = 0;
    }

    public void setImageLoader(@NonNull BaseImageLoader loader) {
        this.mImageLoader = loader;
    }

    /**
     * 预加载图片
     */
    public void preload() {
        if (mImageLoader != null) {
            mImageLoader.displayImage(mPosition, mViewData.getImageSrc(), this);
        }
    }

    /**
     * 预加载图片
     *
     * @param src 图片资源
     */
    public void preload(Object src) {
        if (mViewData == null) {
            mViewData = new ViewData();
        }
        mViewData.setImageSrc(src);
        if (mImageLoader != null) {
            mImageLoader.displayImage(mPosition, mViewData.getImageSrc(), this);
        }
    }

    /**
     * 绑定要预览的 view，获取 view 的位置信息
     *
     * @param view
     * @param overlayStatusBar 预览界面是否占据了状态栏的空间
     */
    public void bindView(@NonNull View view, boolean overlayStatusBar) {
        if (mViewData == null) {
            mViewData = new ViewData();
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mViewData.setTargetX(location[0]);
        // location[1] 是 view 在整个屏幕中的 y 轴坐标，包含了状态栏的高度
        // 如果 imagePager 没有占据状态栏的空间，则需要减去状态栏的高度，否则进退场动画有误差
        mViewData.setTargetY(overlayStatusBar ? location[1] : location[1] - Utils.getStatusBarHeight(getContext()));
        mViewData.setTargetWidth(view.getWidth());
        mViewData.setTargetHeight(view.getHeight());
    }

    /**
     * 开启预览
     */
    public void watch() {
        watch(0, 0, null);
    }

    /**
     * 开启预览
     */
    public void watch(OnTransCallback callback) {
        watch(0, 0, callback);
    }

    /**
     * 开启预览
     */
    public void watch(int readyWidth, int readyHeight, OnTransCallback callback) {
        if (doEnterAnim) {
            executeEnterAnim(readyWidth, readyHeight, callback);
        } else {
            if (showProgress && mImageLoader != null && !mImageLoader.isLoadFinish()) {
                showProgess();
            }
            setVisibility(VISIBLE);
        }
    }

    /**
     * 取消预览
     */
    public void cancel() {
        cancel(null);
    }

    /**
     * 取消预览
     */
    public void cancel(OnTransCallback callback) {
        if (doExitAnim) {
            executeExitAnim(callback);
        } else {
            if (isProgressShowing()) {
                hideProgress();
            }
            setVisibility(INVISIBLE);
        }
    }

    /**
     * imageView 的点击事件
     */
    public void setOnViewClickListener(final OnClickListener listener) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnimRunning && !isDragged && listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    /**
     * imageView 的长按点击事件
     */
    public void setOnViewLongClickListener(final OnLongClickListener listener) {
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isAnimRunning && !isDragged && listener != null) {
                    return listener.onLongClick(v);
                }
                return false;
            }
        });
    }

    /**
     * 设置图片拖拽监听
     */
    public void setOnDragStatusListener(final OnDragStatusListener listener) {
        this.mExternalDragStatusListener = listener;
    }

    /**
     * 移除拖拽效果
     */
    public void removeDragger() {
        if (mDragHandler != null) {
            mDragHandler.clear();
            mDragHandler = null;
        }
    }

    /**
     * 回收图片内存
     */
    public void recycle() {
        recycleImage(imageView);
        removeDragger();
    }

    public ImageView getImageView() {
        return imageView;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  进度条相关
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ProgressWheel getProgressView() {
        return progressView;
    }

    /**
     * 显示进度条
     */
    public void showProgess() {
        if (showProgress) {
            progressView.setVisibility(VISIBLE);
            progressView.startSpinning();
        }
    }

    /**
     * 更新进度
     */
    public void updateProgress(float progress) {
        if (showProgress && progressView.getVisibility() == VISIBLE) {
            DecimalFormat df = new DecimalFormat("#%");
            progressView.setText(df.format(progress));
            progressView.setProgress((int) (progress * 360));
            if (progress == 1f) {
                hideProgress();
            }
        }
    }

    /**
     * 隐藏进度条
     */
    public void hideProgress() {
        progressView.stopSpinning();
        progressView.setVisibility(GONE);
    }

    /**
     * 进度条是否正在显示
     */
    public boolean isProgressShowing() {
        return progressView.getVisibility() == VISIBLE ? true : false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  动效处理
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 执行进场动画
     */
    private void executeEnterAnim(int readyWidth, int readyHeight, final OnTransCallback callback) {
        isAnimRunning = true;
        // 图片预览界面的宽高
        final int prevWidth = getWidth() != 0 ? getWidth() : readyWidth;
        final int prevHeight = getHeight() != 0 ? getHeight() : readyHeight;
        // 未执行动画前的 imageView 的宽高
        final int oldWidth = mViewData.getTargetWidth();
        final int oldHeight = mViewData.getTargetHeight();
        // 执行完动画后的 imageView 的宽高
        final float newWidth, newHeight;
        if (mViewData.getImageWidth() == 0 || mViewData.getImageHeight() == 0) {
            if (imageView.getDrawable() != null) {
                mViewData.setImageWidth(imageView.getDrawable().getIntrinsicWidth());
                mViewData.setImageHeight(imageView.getDrawable().getIntrinsicHeight());
            }
        }
        // 是否设置了图片的原始宽高
        final boolean hasImageSize;
        // 如果定义了图片的原始宽高
        if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            // 获取 imageView 的缩放比例
            final float scale = Math.min(prevWidth * 1f / mViewData.getImageWidth(), prevHeight * 1f / mViewData.getImageHeight());
            // 缩放后的 imageView 的宽度和高度
            newWidth = mViewData.getImageWidth() * scale;
            newHeight = mViewData.getImageHeight() * scale;
            // 为了使动画看起来更流畅
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            hasImageSize = true;
        } else {
            newWidth = prevWidth;
            newHeight = prevHeight;
            hasImageSize = false;
        }
        final float fromX = mViewData.getTargetX() != ViewData.INVALID_VAL ? mViewData.getTargetX() : 0;
        final float fromY = mViewData.getTargetY() != ViewData.INVALID_VAL ? mViewData.getTargetY() : 0;
        final float toX = (prevWidth - newWidth) / 2;
        final float toY = (prevHeight - newHeight) / 2;
        if (showProgress && isProgressShowing()) {
            hideProgress();
        }
        setVisibility(VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 100f);
        animator.setDuration(mDuration);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (hasImageSize) {
                    // 在动画完成后，imageView 将充满整个预览界面
                    imageView.setX(0);
                    imageView.setY(0);
                    mImageViewParams.width = prevWidth;
                    mImageViewParams.height = prevHeight;
                    imageView.setLayoutParams(mImageViewParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                isAnimRunning = false;
                if (showProgress && mImageLoader != null && !mImageLoader.isLoadFinish()) {
                    showProgess();
                }
                if (callback != null) {
                    callback.onEnd();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = animation.getAnimatedFraction();
                final float x = evaluator.evaluate(progress, fromX, toX);
                final float y = evaluator.evaluate(progress, fromY, toY);
                final float width = evaluator.evaluate(progress, oldWidth, newWidth);
                final float height = evaluator.evaluate(progress, oldHeight, newHeight);

                imageView.setX(x);
                imageView.setY(y);
                mImageViewParams.width = (int) width;
                mImageViewParams.height = (int) height;
                imageView.setLayoutParams(mImageViewParams);
                changeBackgroundAlpha((int) (progress * 255));
                if (callback != null) {
                    callback.onRunning(progress);
                }
            }
        });
        animator.start();
    }

    /**
     * 执行退场动画
     */
    private void executeExitAnim(final OnTransCallback callback) {
        isAnimRunning = true;
        // 如果图片处于被放大状态，先将图片恢复原样，动画会看起来更流畅
        if (imageView != null && imageView.getScale() > 1f) {
            imageView.setScale(1f);
        }
        // 图片预览界面的宽高
        final float prevWidth = getWidth();
        final float prevHeight = getHeight();
        // 图片的原始宽高
        float originalImageWidth = 0, originalImageHeight = 0;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            originalImageWidth = drawable.getIntrinsicWidth();
            originalImageHeight = drawable.getIntrinsicHeight();
        } else if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            originalImageWidth = mViewData.getImageWidth();
            originalImageHeight = mViewData.getImageHeight();
        }
        final float scale = Math.min(prevWidth / originalImageWidth, prevHeight / originalImageHeight);
        // 图片的缩放等级为 1f 时的宽高
        final float adjustImageWidth = originalImageWidth * scale;
        final float adjustImageHeight = originalImageHeight * scale;
        // 计算动画中用到的参数
        final float oldWidth = adjustImageWidth != 0 ? adjustImageWidth : imageView.getWidth();
        final float oldHeight = adjustImageHeight != 0 ? adjustImageHeight : imageView.getHeight();
        final float newWidth = mViewData.getTargetWidth();
        final float newHeight = mViewData.getTargetHeight();
        final float fromX = (prevWidth - oldWidth) / 2;
        final float fromY = (prevHeight - oldHeight) / 2;
        final float toX = mViewData.getTargetX() != ViewData.INVALID_VAL ? mViewData.getTargetX() : 0;
        final float toY = mViewData.getTargetY() != ViewData.INVALID_VAL ? mViewData.getTargetY() : 0;
        if (isProgressShowing()) {
            hideProgress();
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 100f);
        animator.setDuration(mDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
                isAnimRunning = false;
                if (callback != null) {
                    callback.onEnd();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = animation.getAnimatedFraction();
                float x = evaluator.evaluate(progress, fromX, toX);
                float y = evaluator.evaluate(progress, fromY, toY);
                float width = evaluator.evaluate(progress, oldWidth, newWidth);
                float height = evaluator.evaluate(progress, oldHeight, newHeight);

                imageView.setX(x);
                imageView.setY(y);
                mImageViewParams.width = (int) width;
                mImageViewParams.height = (int) height;
                imageView.setLayoutParams(mImageViewParams);
                changeBackgroundAlpha((int) ((1 - progress) * 255));
                if (callback != null) {
                    callback.onRunning(progress);
                }
            }
        });
        animator.start();
    }

    /**
     * 设置 ImageViewer 的背景
     */
    public void setViewerBg(Drawable bg) {
        this.mBackground = bg;
    }

    /**
     * 更改背景透明度
     */
    private void changeBackgroundAlpha(@IntRange(from = 0, to = 255) int alpha) {
        if (canBgAlpha) {
            // 如果不是作为 item，而是单独使用，则 Background 为 ImagePager 的背景
            if (!isAsItem && mBackground == null) {
                mBackground = getBackground();
            }
            if (mBackground != null) {
                mBackground.setAlpha(alpha);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  状态获取
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * imageView 是否正在被拖拽
     */
    public boolean isDragged() {
        return isDragged;
    }

    /**
     * imageView 是否正在执行动画
     */
    public boolean isAnimRunning() {
        return isAnimRunning;
    }

    /**
     * 如果本方法未执行，则是因为 view 未获取到焦点，可在外部手动获取焦点
     * 建议在外部手动调动本方法
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果不是作为 item 并且没有动画在运行
            if (!isAsItem && isShowing() && !isAnimRunning) {
                cancel(null);
                // 消费返回键点击事件，不传递出去
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycle();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  属性设置
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void asItem(boolean isAs) {
        this.isAsItem = isAs;
        if (isAs) {
            // 如果作为 item，则一开始就处于 VISIBLE 状态
            setVisibility(VISIBLE);
        } else {
            // 如果作单独使用时，则一开始就处于 INVISIBLE 状态
            // 主要是为了在 doEnterAnim() 时获取的宽高不为0，
            // 如果一开始状态是 GONE ，则 doEnterAnim() 时获取的宽高为0
            setVisibility(INVISIBLE);
        }
    }

    public void setPosition(int position) {
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

    public boolean isDoEnterAnim() {
        return doEnterAnim;
    }

    public void doEnterAnim(boolean doEnterAnim) {
        this.doEnterAnim = doEnterAnim;
    }

    public boolean isDoExitAnim() {
        return doExitAnim;
    }

    public void doExitAnim(boolean doExitAnim) {
        this.doExitAnim = doExitAnim;
    }

    public boolean isCanDragged() {
        return canDragged;
    }

    public void canDragged(boolean canDragged) {
        this.canDragged = canDragged;
    }

    public boolean isCanBgAlpha() {
        return canBgAlpha;
    }

    public void canBgAlpha(boolean canBgAlpha) {
        this.canBgAlpha = canBgAlpha;
    }

    public void setDragMode(int mode) {
        setDragMode(mode, getBackground(), null);
    }

    public void setDragMode(int mode, Drawable background, ViewerWrapper wrapper) {
        mDragMode = mode;
        if (mDragMode == DragMode.MODE_CLASSIC) {
            mDragHandler = new ClassicDragger();
        } else if (mDragMode == DragMode.MODE_AGLIE) {
            mDragHandler = new AgileDragger();
        }
        if (mDragHandler != null) {
            initDragStatusMonitor();
            mDragHandler.setBackground(background);
            mDragHandler.addDragStatusListener(mInternalDragStatusListener);
            if (wrapper != null) {
                mDragHandler.injectViewerWrapper(wrapper);
            }
        }
    }

    /**
     * 初始化图片拖拽状态监测
     */
    private void initDragStatusMonitor() {
        mInternalDragStatusListener = new OnDragStatusListener() {
            @Override
            public void onDragStatusChanged(int status) {
                switch (status) {
                    case DragStatus.STATUS_READY:
                        isDragged = true;
                        setDragStatus(DragStatus.STATUS_READY);
                        break;
                    case DragStatus.STATUS_DRAGGING:
                        setDragStatus(DragStatus.STATUS_DRAGGING);
                        break;
                    case DragStatus.STATUS_BEGIN_RESTORE:
                        isAnimRunning = true;
                        isDragged = false;
                        setDragStatus(DragStatus.STATUS_BEGIN_RESTORE);
                        break;
                    case DragStatus.STATUS_RESTORING:
                        setDragStatus(DragStatus.STATUS_RESTORING);
                        break;
                    case DragStatus.STATUS_END_RESTORE:
                        isAnimRunning = false;
                        setDragStatus(DragStatus.STATUS_END_RESTORE);
                        break;
                    case DragStatus.STATUS_BEGIN_EXIT:
                        isDragged = false;
                        isAnimRunning = true;
                        setDragStatus(DragStatus.STATUS_BEGIN_EXIT);
                        break;
                    case DragStatus.STATUS_EXITTING:
                        setDragStatus(DragStatus.STATUS_EXITTING);
                        break;
                    case DragStatus.STATUS_END_EXIT:
                        isAnimRunning = false;
                        setVisibility(View.GONE);
                        setDragStatus(DragStatus.STATUS_END_EXIT);
                        break;
                }
                if (isDragged || isAnimRunning) {
                    setScaleable(false);
                } else {
                    setScaleable(true);
                }
            }
        };
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void showProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isScaleable() {
        return imageView.isZoomable();
    }

    public void setScaleable(boolean scaleable) {
        imageView.setZoomable(scaleable);
    }

    public float getScale() {
        return imageView.getScale();
    }

    public void setScale(float scale) {
        imageView.setScale(scale);
    }

    public float getMaxScale() {
        return imageView.getMaximumScale();
    }

    public void setMaxScale(float maxScale) {
        imageView.setMaximumScale(maxScale);
    }

    public float getMinScale() {
        return imageView.getMinimumScale();
    }

    public void setMinScale(float minScale) {
        imageView.setMinimumScale(minScale);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    /**
     * 释放 imageView 占据的内存
     * <p>
     * Bitmap 的存储分为两部分，一部分是 Bitmap 的数据，一部分是 Bitmap 的引用。
     * 在 Android2.3 时代，Bitmap 的引用是放在堆中的，而 Bitmap 的数据部分是放在栈中的，需要用户调用 recycle 方法手动进行内存回收；
     * 在 Android2.3 之后，整个 Bitmap（包括数据和引用）都放在了堆中，整个 Bitmap 的回收就全部交给GC了，不用在手动调用 recycle 方法回收内存。
     *
     * @param imageView
     */
    private void recycleImage(ImageView imageView) {
//        Drawable drawable = imageView.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                /**
//                 * 当 bitmap 已经被回收，但是 canvas 在 draw 时，继续使用被回收的 bitmap，会抛出异常：
//                 * a BitmapDrawable: Canvas: trying to use a recycled bitmap.
//                 * 故此处不使用 bitmap.recycle() 方法。
//                 */
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
        // 调用 setImageDrawable(null) 方法,然后 GC 会完成图片的回收
        imageView.setImageDrawable(null);
        // 手动调用 GC（但是 GC 并不一定是马上执行的，只能说是加速 GC 回收）
        System.gc();
    }
}
