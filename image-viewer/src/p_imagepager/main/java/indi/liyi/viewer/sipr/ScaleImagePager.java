package indi.liyi.viewer.sipr;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.text.DecimalFormat;

import indi.liyi.viewer.R;
import indi.liyi.viewer.Utils;
import indi.liyi.viewer.sipr.dragger.AgileDragger;
import indi.liyi.viewer.sipr.dragger.ClassicDragger;
import indi.liyi.viewer.sipr.dragger.DragHandler;
import indi.liyi.viewer.sipr.dragger.DragMode;
import indi.liyi.viewer.sipr.dragger.DragStatus;
import indi.liyi.viewer.sipr.dragger.OnDragStatusListener;
import indi.liyi.viewer.imgv.PhotoView;
import indi.liyi.viewer.pgbr.ProgressWheel;
import indi.liyi.viewer.ImageViewerAttacher;

/**
 * 可缩放图片的自定义 View（即 viewPager 的 item）
 */
public class ScaleImagePager extends FrameLayout {
    public static final int DEF_ANIM_DURATION = 300;
    // 当前的位置
    private int mPosition;
    // view 的相关数据
    private ViewData mViewData;


    // 是否进场动画
    private boolean doEnterAnim = true;
    // 是否执行退场动画
    private boolean doExitAnim = true;
    // 是否可拖拽
    private boolean canDragged = true;
    // 是否执行背景透明度渐变
    private boolean canBgAlpha = true;
    // 图片拖拽模式
    private int mDragMode = DragMode.MODE_CLASSIC;
    // 动画执行时间
    private int mDuration = DEF_ANIM_DURATION;
    // 是否显示加载进度
    private boolean showProgress = true;


    // 手指按下时的坐标
    private float mDownX;
    private float mDownY;
    // imageView 是否正在执行动画
    private boolean isAnimRunning;
    // imageView 是否正在被拖拽
    private boolean isDragged;
    // 是否定义了图片尺寸
    private boolean hasImageSize;
    // 是否当作一个 item
    private boolean isAsItem = false;
    // 背景
    private Drawable mBackground;


    private ProgressWheel progressBar;
    private PhotoView imageView;
    private FrameLayout.LayoutParams mImageViewParams;
    // 图片拖拽处理类
    private DragHandler mDragHandler;
    // 图片拖拽状态监听
    private OnDragStatusListener mStatusListener;


    public ScaleImagePager(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ScaleImagePager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleImagePager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleImagePager);
            if (a != null) {
                doEnterAnim = a.getBoolean(R.styleable.ScaleImagePager_sip_doEnterAnim, true);
                doExitAnim = a.getBoolean(R.styleable.ScaleImagePager_sip_doExitAnim, true);
                canDragged = a.getBoolean(R.styleable.ScaleImagePager_sip_canDragged, true);
                canBgAlpha = a.getBoolean(R.styleable.ScaleImagePager_sip_canBgAlpha, true);
                mDragMode = a.getInteger(R.styleable.ScaleImagePager_sip_dragMode, DragMode.MODE_CLASSIC);
                mDuration = a.getInteger(R.styleable.ScaleImagePager_sip_duration, DEF_ANIM_DURATION);
                showProgress = a.getBoolean(R.styleable.ScaleImagePager_sip_showProgress, true);
                a.recycle();
            }
            isAsItem = false;
        }

        isAnimRunning = false;
        isDragged = false;
        hasImageSize = false;
        initView(context);
        initDragStatusMonitor();
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
        progressBar = new ProgressWheel(getContext());
        final int size = dp2px(getContext(), 60);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(size, size);
        barLp.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(barLp);
        final int barWidth = dp2px(getContext(), 3);
        progressBar.setBarColor(Color.parseColor("#CCFFFFFF"));
        progressBar.setBarWidth(barWidth);
        progressBar.setBarLength(dp2px(getContext(), 100));
        progressBar.setRimColor(Color.parseColor("#11FFFFFF"));
        progressBar.setRimWidth(barWidth);
        progressBar.setContourColor(Color.parseColor("#10000000"));
        progressBar.setSpinSpeed(3.5f);
        progressBar.setText("");
        progressBar.setTextColor(Color.parseColor("#CCFFFFFF"));
        progressBar.setTextSize(dp2px(getContext(), 12));
        progressBar.setVisibility(GONE);
        addView(progressBar);

        // 如果不是作为 item，而是单独使用，则设置显示为 INVISIBLE
        if (!isAsItem) {
            setVisibility(INVISIBLE);
        }
    }

    /**
     * 初始化图片拖拽状态监测
     */
    private void initDragStatusMonitor() {
        mStatusListener = new OnDragStatusListener() {
            @Override
            public void onDragStatusChanged(int status) {
                switch (status) {
                    case DragStatus.STATUS_READY:
                        isDragged = true;
                        break;
                    case DragStatus.STATUS_DRAGGING:
                        break;
                    case DragStatus.STATUS_BEGIN_REBACK:
                        isAnimRunning = true;
                        isDragged = false;
                        break;
                    case DragStatus.STATUS_REBACKING:
                        break;
                    case DragStatus.STATUS_END_REBACK:
                        isAnimRunning = false;
                        break;
                    case DragStatus.STATUS_BEGIN_EXIT:
                        isDragged = false;
                        isAnimRunning = true;
                        break;
                    case DragStatus.STATUS_EXITTING:
                        break;
                    case DragStatus.STATUS_END_EXIT:
                        isAnimRunning = false;
                        setVisibility(View.GONE);
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 是否拦截触摸事件？
        // 若拦截，则 ImagePager 自己处理触摸事件；
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
                 * 1.允许拖拽
                 * 2.仅有一个触摸点
                 * 3.图片的缩放等级 = 1f
                 * 4.拖拽处理类不为空
                 * 5.手势为上下滑动手势，左右滑动不触发
                 */
                if (canDragged && ev.getPointerCount() == 1 && getScale() <= 1f && mDragHandler != null) {
                    float diffX = ev.getX() - mDownX;
                    float diffY = ev.getY() - mDownY;
                    // 上下滑动手势
                    if (Math.abs(diffX) < Math.abs(diffY)) {
                        if ((mDragMode == DragMode.MODE_CLASSIC) || (mDragMode == DragMode.MODE_AGLIE && diffY > 0)) {
                            mDragHandler.injectImagePager(this);
                            mDragHandler.canChangeBgAlpha(canBgAlpha);
                            mDragHandler.onDown(getWidth(), getHeight());
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
        if (canDragged
                && event.getPointerCount() == 1
                && getScale() <= 1f
                && isDragged
                && mDragHandler != null) {
            mDragHandler.onDrag(mDownX, mDownY, event.getX(), event.getY());
        }
        mDownX = event.getX();
        mDownY = event.getY();
    }

    private void onActionUp(MotionEvent event) {
        // 释放图片
        if (getScale() <= 1f && isDragged && mDragHandler != null) {
            mDragHandler.onUp();
        }
        mDownX = 0;
        mDownY = 0;
    }

    /**
     * 开启预览
     */
    public void start() {
        start(0, 0, null);
    }

    /**
     * 开启预览
     */
    public void start(OnTransCallback callback) {
        start(0, 0, callback);
    }

    /**
     * 开启预览
     */
    public void start(float readyWidth, float readyHeight, OnTransCallback callback) {
        if (doEnterAnim) {
            doEnterAnim(readyWidth, readyHeight, callback);
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
            doExitAnim(callback);
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
    //// 进度条相关
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ProgressWheel getProgressBar() {
        return progressBar;
    }

    /**
     * 显示进度条
     */
    public void showProgessBar() {
        if (showProgress) {
            progressBar.setVisibility(VISIBLE);
            progressBar.startSpinning();
        }
    }

    /**
     * 更新进度
     */
    public void updateProgress(float progress) {
        if (showProgress && progressBar.getVisibility() == VISIBLE) {
            progressBar.setProgress((int) (progress * 360));
            DecimalFormat df = new DecimalFormat("#%");
            progressBar.setText(df.format(progress));
            if (progress == 1f) {
                hideProgressBar();
            }
        }
    }

    /**
     * 隐藏进度条
     */
    public void hideProgressBar() {
        progressBar.startSpinning();
        progressBar.setVisibility(GONE);
    }

    /**
     * 进度条是否正在显示
     */
    public boolean isProgressBarShowing() {
        return progressBar.getVisibility() == VISIBLE ? true : false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// 动效处理
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 执行进场动画
     */
    private void doEnterAnim(float readyWidth, float readyHeight, final OnTransCallback callback) {
        isAnimRunning = true;
        // 图片预览界面的宽高
        final float prevWidth = getWidth() != 0 ? getWidth() : readyWidth;
        final float prevHeight = getHeight() != 0 ? getHeight() : readyHeight;
        // 未执行动画前的 imageView 的宽高
        final float oldWidth = mViewData.getTargetWidth();
        final float oldHeight = mViewData.getTargetHeight();
        // 执行完动画后的 imageView 的宽高
        final float newWidth, newHeight;
        // 如果定义了图片的原始宽高
        if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            // 获取 imageView 的缩放比例
            final float scale = Math.min(prevWidth / mViewData.getImageWidth(), prevHeight / mViewData.getImageHeight());
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
        final float from_x = mViewData.getTargetX();
        final float from_y = mViewData.getTargetY();
        final float to_x = (prevWidth - newWidth) / 2;
        final float to_y = (prevHeight - newHeight) / 2;
        setVisibility(VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
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
                    mImageViewParams.width = (int) prevWidth;
                    mImageViewParams.height = (int) prevHeight;
                    imageView.setLayoutParams(mImageViewParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
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
                final float fraction = animation.getAnimatedFraction();
                final float x = evaluator.evaluate(fraction, from_x, to_x);
                final float y = evaluator.evaluate(fraction, from_y, to_y);
                final float width = evaluator.evaluate(fraction, oldWidth, newWidth);
                final float height = evaluator.evaluate(fraction, oldHeight, newHeight);

                imageView.setX(x);
                imageView.setY(y);
                mImageViewParams.width = (int) width;
                mImageViewParams.height = (int) height;
                imageView.setLayoutParams(mImageViewParams);
                changeBackgroundAlpha((int) (fraction * 255));
                if (callback != null) {
                    callback.onRunning(fraction);
                }
            }
        });
        animator.start();
    }

    /**
     * 执行退场动画
     */
    private void doExitAnim(final OnTransCallback callback) {
        isAnimRunning = true;
        // 如果图片处于被放大状态，先将图片恢复原样，动画会看起来更流畅
        if (imageView != null && imageView.getScale() > 1f) {
            imageView.setScale(1f);
        }
        // 图片预览界面的宽高
        final float prevWidth = getWidth();
        final float prevHeight = getHeight();
        // 图片的原始宽高
        float origImageWidth = 0, origImageHeight = 0;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            origImageWidth = drawable.getIntrinsicWidth();
            origImageHeight = drawable.getIntrinsicHeight();
        } else if (mViewData.getImageWidth() != 0 && mViewData.getImageHeight() != 0) {
            origImageWidth = mViewData.getImageWidth();
            origImageHeight = mViewData.getImageHeight();
        }
        final float scale = Math.min(prevWidth / origImageWidth, prevHeight / origImageHeight);
        // 图片的缩放等级为 1f 时的宽高
        final float adjustImageWidth = origImageWidth * scale;
        final float adjustImageHeight = origImageHeight * scale;
        // 计算动画中用到的参数
        final float oldWidth = adjustImageWidth != 0 ? adjustImageWidth : imageView.getWidth();
        final float oldHeight = adjustImageHeight != 0 ? adjustImageHeight : imageView.getHeight();
        final float newWidth = mViewData.getTargetWidth();
        final float newHeight = mViewData.getTargetHeight();
        final float from_x = (prevWidth - oldWidth) / 2;
        final float from_y = (prevHeight - oldHeight) / 2;
        final float to_x = mViewData.getTargetX();
        final float to_y = mViewData.getTargetY();
        imageView.setX(from_x);
        imageView.setY(from_y);
        mImageViewParams.width = (int) oldWidth;
        mImageViewParams.height = (int) oldHeight;
        imageView.setLayoutParams(mImageViewParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
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
                float fraction = animation.getAnimatedFraction();
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);
                float width = evaluator.evaluate(fraction, oldWidth, newWidth);
                float height = evaluator.evaluate(fraction, oldHeight, newHeight);
                imageView.setX(x);
                imageView.setY(y);
                mImageViewParams.width = (int) width;
                mImageViewParams.height = (int) height;
                imageView.setLayoutParams(mImageViewParams);
                changeBackgroundAlpha((int) ((1 - fraction) * 255));
                if (callback != null) {
                    callback.onRunning(fraction);
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
    //// 状态获取
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// 属性设置
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

    public void setDragMode(int mode, Drawable background, ImageViewerAttacher attacher) {
        mDragMode = mode;
        if (mDragMode == DragMode.MODE_CLASSIC) {
            mDragHandler = new ClassicDragger();
        } else if (mDragMode == DragMode.MODE_AGLIE) {
            mDragHandler = new AgileDragger();
        }
        if (mDragHandler != null) {
            mDragHandler.setBackground(background);
            mDragHandler.addDragStatusListener(mStatusListener);
            if (attacher != null) {
                mDragHandler.injectImageViewerAttacher(attacher);
            }
        }
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

    /**
     * dp 转 px
     *
     * @param dpVal
     * @return
     */
    private int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
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
