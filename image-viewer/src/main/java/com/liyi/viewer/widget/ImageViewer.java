package com.liyi.viewer.widget;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.liyi.viewer.R;
import com.liyi.viewer.Utils;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.factory.ImageDragger;
import com.liyi.viewer.factory.ImageLoader;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnViewClickListener;
import com.liyi.viewer.listener.OnWatchStatusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片浏览器
 */
public class ImageViewer extends FrameLayout implements IImageViewer {
    private final FrameLayout.LayoutParams FRAME_LAYOUT_PARAMS_MATCH = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
    private final int DEF_ANIM_DURATION = 240;

    // 背景 View
    private View view_background;
    // 自定义 ViewPager
    private ImagePager viewPager;
    // 图片序号
    private TextView tv_index;
    // 当前的 PhotoView
    private PhotoView photoView_current;
    // ViewPager 的适配器
    private ImageAdapter mImageAdapter;


    /**
     * 外部传递进来的数据
     */
    // 当前的 View 的位置
    private int mStartPosition;
    // 图片资源列表
    private List<Object> mImageList;
    // View 的数据列表
    private List<ViewData> mViewDataList;
    // 图片加载监听
    private ImageLoader mImageLoader;
    // 图片切换监听
    private OnImageChangedListener mImageChangedListener;
    // 图片 View 的点击监听
    private OnViewClickListener mViewClickListener;
    // 图片浏览状态监听
    private OnWatchStatusListener mWatchStatusListener;
    // 是否显示图片序号
    private boolean showIndex;
    // 是否允许图片被拖拽
    private boolean doDragAction;
    // 是否执行启动动画
    private boolean doEnterAnim;
    // 是否执行关闭动画
    private boolean doExitAnim;
    // 动画执行时间
    private int mAnimDuration;


    /**
     * 内部数据
     */
    // 图片拖拽处理类
    private ImageDragHandler mImageDragHandler;
    // 屏幕的尺寸
    private Point mScreenSize;
    // 存储所有的图片视图
    private List<View> mViews;
    // 记录上次的图片资源数据
    private List<Object> mOldImageList;
    // 判断图片是否可以点击
    private boolean isPhotoClickalbe;
    // 判断图片的动画是否正在执行
    private boolean isPhotoAnimRunning;
    // 判断图片资源数据是否需要更新
    private boolean isNeedUpdate;
    // 判断图片是否可缩放
    private boolean isImageZoomable;
    // 当前图片的位置
    private int mCurrentPosition;

    public ImageViewer(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ImageViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ImageViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initAttr(attrs);
        initView();
    }

    private void initAttr(AttributeSet attrs) {
        showIndex = true;
        doDragAction = true;
        doEnterAnim = true;
        doExitAnim = true;
        mAnimDuration = DEF_ANIM_DURATION;
        mScreenSize = Utils.getScreenSize(getContext());
        mViews = new ArrayList<>();
        isPhotoClickalbe = true;
        isPhotoAnimRunning = false;
        isNeedUpdate = true;
        isImageZoomable = true;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageViewer);
            if (a != null) {
                showIndex = a.getBoolean(R.styleable.ImageViewer_ivr_show_index, true);
                doDragAction = a.getBoolean(R.styleable.ImageViewer_ivr_drag_enable, true);
                doEnterAnim = a.getBoolean(R.styleable.ImageViewer_ivr_enter_anim, true);
                doExitAnim = a.getBoolean(R.styleable.ImageViewer_ivr_exit_anim, true);
                mAnimDuration = a.getInteger(R.styleable.ImageViewer_ivr_anim_duration, DEF_ANIM_DURATION);
                a.recycle();
            }
        }
    }

    private void initView() {
        // 添加背景 View 和 ViewPager
        view_background = new View(getContext());
        view_background.setBackgroundColor(Color.BLACK);
        view_background.setAlpha(0f);
        viewPager = new ImagePager(getContext());
        viewPager.setVisibility(GONE);
        addView(view_background, FRAME_LAYOUT_PARAMS_MATCH);
        addView(viewPager, FRAME_LAYOUT_PARAMS_MATCH);

        // 添加图片序号
        tv_index = new TextView(getContext());
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, Utils.dp2px(getContext(), 8), 0, 0);
        textParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        tv_index.setLayoutParams(textParams);
        tv_index.setIncludeFontPadding(false);
        tv_index.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv_index.setTextColor(Color.WHITE);
        tv_index.setVisibility(GONE);
        addView(tv_index);

        // 配置 ViewPager
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                mCurrentPosition = position;
                if (tv_index.getVisibility() == VISIBLE) {
                    tv_index.setText((position + 1) + "/" + mImageList.size());
                }
                final PhotoView photoView = (PhotoView) ((FrameLayout) mViews.get(position)).getChildAt(0);
                photoView.setScale(1f, true);
                photoView.setZoomable(isImageZoomable);
                photoView.setOnViewTapListener(new ViewTabListener(position));
                photoView_current = photoView;
                if (mImageChangedListener != null) {
                    mImageChangedListener.onImageSelected(position, photoView);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setImageBackground(Drawable drawable) {
        view_background.setBackground(drawable);
    }

    @Override
    public void setImageBackgroundResource(@DrawableRes int resid) {
        view_background.setBackgroundResource(resid);
    }

    @Override
    public void setImageBackgroundColor(@ColorInt int color) {
        view_background.setBackgroundColor(color);
    }

    @Override
    public void setStartPosition(int position) {
        this.mStartPosition = position;
        this.mCurrentPosition = position;
    }

    @Override
    public void setImageData(List<Object> list) {
        if (mOldImageList == null) {
            mImageList = mOldImageList = list;
            isNeedUpdate = true;
        } else {
            // 如果传入的数据与上次传入的数据一致，则不更新 View
            if (mOldImageList.equals(list)) {
                isNeedUpdate = false;
            } else {
                isNeedUpdate = true;
            }
            mImageList = mOldImageList = list;
        }
    }

    @Override
    public void setViewData(List<ViewData> list) {
        this.mViewDataList = list;
    }

    @Override
    public void setImageLoader(ImageLoader loader) {
        mImageLoader = loader;
    }

    @Override
    public void setOnImageChangedListener(OnImageChangedListener listener) {
        this.mImageChangedListener = listener;
    }

    @Override
    public void setOnViewClickListener(OnViewClickListener listener) {
        this.mViewClickListener = listener;
    }

    @Override
    public void setOnWatchStatusListener(OnWatchStatusListener listener) {
        this.mWatchStatusListener = listener;
    }

    @Override
    public void showIndex(boolean show) {
        this.showIndex = show;
    }

    @Override
    public void doDragAction(boolean isDo) {
        this.doDragAction = isDo;
    }

    @Override
    public void doEnterAnim(boolean isDo) {
        this.doEnterAnim = isDo;
    }

    @Override
    public void doExitAnim(boolean isDo) {
        this.doExitAnim = isDo;
    }

    @Override
    public void setAnimDuration(int duration) {
        this.mAnimDuration = duration;
    }

    @Override
    public void excuteEnterAnim() {
        isPhotoClickalbe = false;
        isPhotoAnimRunning = true;
        // 获取当前 View 的数据
        final ViewData viewData = mViewDataList.get(mStartPosition);
        // 缩放前的 View 的宽度
        final float beforeScale_width = viewData.getWidth();
        // 缩放前的 View 的高度
        final float beforeScale_height = viewData.getHeight();
        // 缩放后的 View 的宽度
        final float afterScale_width;
        // 缩放后的 View 的高度
        final float afterScale_heigt;
        // 缩放前的 View  的 X 轴，Y 轴的坐标
        final float from_x = viewData.getX();
        final float from_y = viewData.getY();
        // 是否定义了图片尺寸
        final boolean hasImageSize;
        // ImageViewer 的宽度和高度
        float parentW = getWidth(), parentH = getHeight();
        // 如果 ImageViewer 的宽度和高度为 0，即还未测量完成，则取屏幕宽高
        float previewW = parentW != 0 ? parentW : mScreenSize.x;
        float previewH = parentH != 0 ? parentH : mScreenSize.y;
        // 如果自定义了图片的尺寸，则使用图片的尺寸
        if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
            final float scale = Math.min(previewW / viewData.getImageWidth(), previewH / viewData.getImageHeight());
            afterScale_width = viewData.getImageWidth() * scale;
            afterScale_heigt = viewData.getImageHeight() * scale;
            photoView_current.setScaleType(ImageView.ScaleType.CENTER_CROP);
            hasImageSize = true;
        } else {
            afterScale_width = previewW;
            afterScale_heigt = previewH;
            hasImageSize = false;
        }
        // View 缩放后的 X 轴，Y 轴的坐标
        final float to_x = (previewW - afterScale_width) / 2;
        final float to_y = (previewH - afterScale_heigt) / 2;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) photoView_current.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float x = evaluator.evaluate(currentValue, from_x, to_x);
                final float y = evaluator.evaluate(currentValue, from_y, to_y);
                final float width = evaluator.evaluate(currentValue, beforeScale_width, afterScale_width);
                final float height = evaluator.evaluate(currentValue, beforeScale_height, afterScale_heigt);

                photoView_current.setX(x);
                photoView_current.setY(y);
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                photoView_current.setLayoutParams(layoutParams);
                view_background.setAlpha(currentValue);
                if (currentValue == 1) {
                    handleImageIndex();
                    isPhotoClickalbe = true;
                    isPhotoAnimRunning = false;
                    isNeedUpdate = true;
                    if (hasImageSize) {
                        photoView_current.setX(0);
                        photoView_current.setY(0);
                        layoutParams.width = mScreenSize.x;
                        layoutParams.height = mScreenSize.y;
                        photoView_current.setLayoutParams(layoutParams);
                        photoView_current.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                    if (mWatchStatusListener != null) {
                        mWatchStatusListener.onWatchStart(OnWatchStatusListener.State.STATE_START_AFTER, mStartPosition, photoView_current);
                    }
                }
            }
        });
        animator.setDuration(mAnimDuration);
        animator.start();
    }

    @Override
    public void excuteExitAnim() {
        isPhotoClickalbe = false;
        isPhotoAnimRunning = true;
        final int position = viewPager.getCurrentItem();
        final ViewData viewData = mViewDataList.get(position);
        final PhotoView photoView = (PhotoView) ((FrameLayout) mViews.get(position)).getChildAt(0);
        photoView_current = photoView;

        // 获取 PhotoView 的图片
        final Drawable drawable = photoView_current.getDrawable();
        // 图片原始的宽度和高度
        float oriImg_width = 0, oriImg_height = 0;
        // 图片当前的宽度和高度
        float curImg_width = 0, curImg_height = 0;
        if (drawable != null) {
            oriImg_width = drawable.getIntrinsicWidth();
            oriImg_height = drawable.getIntrinsicHeight();
        } else if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
            oriImg_width = viewData.getImageWidth();
            oriImg_height = viewData.getImageHeight();
        }
        // ImageViewer 的宽度和高度
        float parentW = getWidth(), parentH = getHeight();
        // 如果图片原始的宽度和高度不等于 0，则先将 PhotoView 的宽高设置为图片当前的宽高，使动画看起来更流畅
        if (oriImg_width != 0 && oriImg_height != 0) {
            final float scale = Math.min(parentW / oriImg_width, parentH / oriImg_height);
            // 图片当前的宽度与高度
            curImg_width = oriImg_width * scale;
            curImg_height = oriImg_height * scale;
            // PhotoView 改变宽高后的坐标
            final float x = (parentW - curImg_width) / 2;
            final float y = (parentH - curImg_height) / 2;

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) photoView_current.getLayoutParams();
            lp.width = (int) curImg_width;
            lp.height = (int) curImg_height;
            photoView_current.setLayoutParams(lp);
            photoView_current.setX(x);
            photoView_current.setY(y);
            photoView_current.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // 缩放前的 View 的宽度和高度
        final float beforeScale_width = curImg_width != 0 ? curImg_width : photoView_current.getWidth();
        final float beforeScale_height = curImg_height != 0 ? curImg_height : photoView_current.getHeight();
        // 缩放后的 View 的宽度和高度
        final float afterScale_width = viewData.getWidth();
        final float afterScale_heigt = viewData.getHeight();
        // 缩放前的 View 的 X 轴，Y 轴的坐标
        final float from_x = (parentW - beforeScale_width) / 2;
        final float from_y = (parentH - beforeScale_height) / 2;
        // 缩放后的 View 的 X 轴，Y 轴的坐标
        final float to_x = viewData.getX();
        final float to_y = viewData.getY();

        tv_index.setVisibility(GONE);
        // 图片由自适应转为 CENTER_CROP 时的动画
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) photoView_current.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                float x = evaluator.evaluate(currentValue, from_x, to_x);
                float y = evaluator.evaluate(currentValue, from_y, to_y);
                float width = evaluator.evaluate(currentValue, beforeScale_width, afterScale_width);
                float height = evaluator.evaluate(currentValue, beforeScale_height, afterScale_heigt);

                photoView_current.setX(x);
                photoView_current.setY(y);
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                photoView_current.setLayoutParams(layoutParams);
                view_background.setAlpha(1 - currentValue);
                if (currentValue == 1) {
                    // 动画完成后，将 View 恢复原样
                    viewPager.setVisibility(GONE);
                    photoView_current.setX(from_x);
                    photoView_current.setY(from_y);
                    layoutParams.width = (int) beforeScale_width;
                    layoutParams.height = (int) beforeScale_height;
                    photoView_current.setLayoutParams(layoutParams);
                    photoView_current.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    isPhotoClickalbe = true;
                    isPhotoAnimRunning = false;
                    if (mWatchStatusListener != null) {
                        mWatchStatusListener.onWatchEnd(OnWatchStatusListener.State.STATE_END_AFTER);
                    }
                }
            }
        });
        animator.setDuration(mAnimDuration);
        animator.start();
    }

    /**
     * 添加图片的 View
     */
    private void addImageViews() {
        if (mImageList == null) {
            throw new NullPointerException(" ImageList cannot be empty. ");
        }
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            final FrameLayout container = new FrameLayout(getContext());
            final PhotoView photoView = new PhotoView(getContext());
            // 此处不要共用 LayoutParams，否则改变一个  View 的 LayoutParams，其余的 View 都会受到影响
            photoView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            photoView.setZoomable(isImageZoomable);
            photoView.setOnViewTapListener(new ViewTabListener(i));
            container.addView(photoView);
            // 自定义图片加载方式
            if (mImageLoader != null) {
                mImageLoader.displayImage(i, mImageList.get(i), photoView);
            }
            if (i == mStartPosition) {
                photoView_current = photoView;
            }
            mViews.add(container);
        }
    }

    /**
     * 图片序号显示处理
     */
    private void handleImageIndex() {
        if (showIndex) {
            if (mImageList != null && mImageList.size() > 1) {
                tv_index.setText((mStartPosition + 1) + "/" + mImageList.size());
                tv_index.setVisibility(View.VISIBLE);
            } else {
                tv_index.setVisibility(View.GONE);
            }
        } else {
            tv_index.setVisibility(View.GONE);
        }
    }

    @Override
    public void watch() {
        // 是否已经更新 View
        boolean hasUpdate = false;
        // 如果数据更新，则重新添加图片
        if (isNeedUpdate || mViews == null || mViews.size() == 0) {
            hasUpdate = true;
            if (mViews == null) mViews = new ArrayList<>();
            if (mViews.size() > 0) mViews.clear();
            addImageViews();
            viewPager.setMaxTranslateY(getHeight() != 0 ? getHeight() : mScreenSize.y);
        }
        if (mImageAdapter == null) {
            mImageAdapter = new ImageAdapter();
            mImageAdapter.setData(mViews);
            viewPager.setAdapter(mImageAdapter);
        } else {
            if (hasUpdate) {
                mImageAdapter.setData(mViews);
                mImageAdapter.notifyDataSetChanged();
            }
        }
        if (doDragAction) {
            if (mImageDragHandler == null) mImageDragHandler = new ImageDragHandler();
            viewPager.setImageDragger(mImageDragHandler);
        } else {
            viewPager.setImageDragger(null);
        }
        viewPager.setCurrentItem(mStartPosition, false);
        if (mWatchStatusListener != null) {
            mWatchStatusListener.onWatchStart(OnWatchStatusListener.State.STATE_START_BEFORE, mStartPosition, photoView_current);
        }
        viewPager.setVisibility(VISIBLE);
        if (doEnterAnim) {
            excuteEnterAnim();
        } else {
            view_background.setAlpha(1f);
            handleImageIndex();
            isNeedUpdate = true;
            if (mWatchStatusListener != null) {
                mWatchStatusListener.onWatchStart(OnWatchStatusListener.State.STATE_START_AFTER, mStartPosition, photoView_current);
            }
        }
    }

    @Override
    public void close() {
        if (mWatchStatusListener != null) {
            mWatchStatusListener.onWatchEnd(OnWatchStatusListener.State.STATE_END_BEFORE);
        }
        if (doExitAnim) {
            excuteExitAnim();
        } else {
            view_background.setAlpha(0);
            viewPager.setVisibility(GONE);
            tv_index.setVisibility(GONE);
            if (mWatchStatusListener != null) {
                mWatchStatusListener.onWatchEnd(OnWatchStatusListener.State.STATE_END_AFTER);
            }
        }
    }

    @Override
    public void clear() {
        if (mViews != null && mViews.size() > 0) {
            mViews.clear();
        }
        if (mViewDataList != null && mViewDataList.size() > 0) {
            mViewDataList.clear();
        }
        if (mImageList != null && mImageList.size() > 0) {
            mImageList.clear();
        }
        mImageDragHandler = null;
        mImageLoader = null;
        mImageChangedListener = null;
        mViewClickListener = null;
        mWatchStatusListener = null;
    }

    @Override
    public void setImageZoomable(boolean zoomable) {
        isImageZoomable = zoomable;
    }

    @Override
    public boolean isImageZoomable() {
        return isImageZoomable;
    }

    @Override
    public void setCurrentImageZoomable(boolean zoomable) {
        if (photoView_current != null) {
            photoView_current.setScale(1f, true);
            photoView_current.setZoomable(zoomable);
        }
    }

    @Override
    public boolean isCurrentImageZoomable() {
        if (photoView_current != null) {
            return photoView_current.isZoomable();
        }
        return false;
    }

    @Override
    public float getImageScale() {
        return photoView_current != null ? photoView_current.getScale() : 1;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
    }

    /**
     * =============================================================================================
     * ==== PhotoView 的点击事件
     * =============================================================================================
     */
    private class ViewTabListener implements OnViewTapListener {
        private int position;

        public ViewTabListener(int position) {
            this.position = position;
            mCurrentPosition = position;
        }

        @Override
        public void onViewTap(View view, float x, float y) {
            if (isPhotoClickalbe) {
                if (mViewClickListener != null) {
                    final boolean b = mViewClickListener.onViewClick(position, view, x, y);
                    // 判断是否消费点击事件，若消费，则后续方法不执行
                    if (b) {
                        return;
                    }
                }
                close();
            }
        }
    }

    /**
     * =============================================================================================
     * ==== 图片拖动处理类
     * =============================================================================================
     */
    public class ImageDragHandler implements ImageDragger {
        @Override
        public float getImageScale() {
            return ImageViewer.this.getImageScale();
        }

        @Override
        public void dragImage(final float y1, final float y2, final float alphaBase) {
            if (photoView_current != null && !isPhotoAnimRunning) {
                isPhotoClickalbe = false;
                // 计算当前的 PhotoView 的 Y 轴坐标
                final float diff = y2 - y1;
                final float curY = photoView_current.getY() + diff;
                // 计算当前背景透明度
                float value = Math.abs(curY) / alphaBase;
                float alpha = value < 1 ? 1 - value : 0;
                photoView_current.setY(curY);
                view_background.setAlpha(alpha);
                if (mWatchStatusListener != null) {
                    mWatchStatusListener.onWatchDragging(photoView_current);
                }
            }
        }

        @Override
        public void releaseImage(final float maxTranslateY) {
            if (photoView_current != null && !isPhotoAnimRunning) {
                if (mWatchStatusListener != null) {
                    mWatchStatusListener.onWatchReset(OnWatchStatusListener.State.STATE_RESET_BEFORE, photoView_current);
                }
                isPhotoClickalbe = false;
                isPhotoAnimRunning = true;
                final float curY = photoView_current.getY();
                if (Math.abs(curY) <= maxTranslateY) {
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                    animator.setDuration(150);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        FloatEvaluator evaluator = new FloatEvaluator();

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            final float currentValue = (float) animation.getAnimatedValue();
                            final float y = evaluator.evaluate(currentValue, curY, 0);
                            final float alpha = evaluator.evaluate(currentValue, view_background.getAlpha(), 1);
                            photoView_current.setY(y);
                            view_background.setAlpha(alpha);
                            if (currentValue == 1) {
                                isPhotoClickalbe = true;
                                isPhotoAnimRunning = false;
                                if (mWatchStatusListener != null) {
                                    mWatchStatusListener.onWatchReset(OnWatchStatusListener.State.STATE_RESET_AFTER, photoView_current);
                                }
                            }
                        }
                    });
                    animator.start();
                } else {
                    if (mWatchStatusListener != null) {
                        mWatchStatusListener.onWatchEnd(OnWatchStatusListener.State.STATE_END_BEFORE);
                    }
                    final int position = viewPager.getCurrentItem();
                    final ViewData viewData = mViewDataList.get(position);
                    final Drawable drawable = photoView_current.getDrawable();
                    float curImg_height = 0;
                    float oriImg_width = 0, oriImg_height = 0;
                    // 图片在 PhotoView 中的 Y 轴坐标
                    float imgY = 0;
                    if (drawable != null) {
                        // 图片原始的宽度和高度
                        oriImg_width = drawable.getIntrinsicWidth();
                        oriImg_height = drawable.getIntrinsicHeight();
                    } else if (viewData.getImageWidth() != 0 && viewData.getImageHeight() != 0) {
                        oriImg_width = viewData.getImageWidth();
                        oriImg_height = viewData.getImageHeight();
                    }
                    if (oriImg_width != 0 && oriImg_height != 0) {
                        final float scale = Math.min(mScreenSize.x / oriImg_width, mScreenSize.y / oriImg_height);
                        // 图片当前的高度
                        curImg_height = oriImg_height * scale;
                        // 图片在 PhotoView 中的 Y 轴坐标
                        imgY = (mScreenSize.y - curImg_height) / 2;
                    }
                    // 图片当前在屏幕中的 Y 轴坐标
                    float cutImgY = curY + imgY;
                    // 此处加 10 ,是为了减少误差，影响动画美观
                    final float toY = cutImgY > imgY ? curY + (mScreenSize.y - cutImgY + 10) : curY - (cutImgY + curImg_height + 10);
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                    animator.setDuration(200);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        FloatEvaluator evaluator = new FloatEvaluator();

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            final float currentValue = (float) animation.getAnimatedValue();
                            final float y = evaluator.evaluate(currentValue, curY, toY);
                            final float alpha = evaluator.evaluate(currentValue, view_background.getAlpha(), 0);
                            photoView_current.setY(y);
                            view_background.setAlpha(alpha);
                            if (currentValue == 1) {
                                isPhotoClickalbe = true;
                                isPhotoAnimRunning = false;
                                viewPager.setVisibility(GONE);
                                photoView_current.setY(0);
                                if (mWatchStatusListener != null) {
                                    mWatchStatusListener.onWatchEnd(OnWatchStatusListener.State.STATE_END_AFTER);
                                }
                            }
                        }
                    });
                    animator.start();
                }
            }
        }
    }
}
