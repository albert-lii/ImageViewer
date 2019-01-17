package indi.liyi.viewer.widget;


import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import indi.liyi.viewer.ImageLoader;
import indi.liyi.viewer.ImageViewerState;
import indi.liyi.viewer.ImageViewerUtil;
import indi.liyi.viewer.TransitionCallback;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.dragger.ImageDraggerType;
import indi.liyi.viewer.imgv.PhotoView;
import indi.liyi.viewer.listener.OnImageChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.widget.viewpager.PreviewAdapter;
import indi.liyi.viewer.widget.viewpager.PreviewPager;

import java.util.List;

import indi.liyi.viewer.listener.OnImageChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.widget.viewpager.PreviewAdapter;
import indi.liyi.viewer.widget.viewpager.PreviewPager;


public class ImageViewerAttacher implements ViewPager.OnPageChangeListener {
    // 默认的动画执行时间
    public static final int DEF_DURATION = 300;

    // imageViewer 的容器
    private FrameLayout container;
    // 图片序号
    private TextView indexView;
    private PreviewPager viewPager;
    private PreviewAdapter mPreviewAdapter;

    // 屏幕尺寸
    private Point mScreenSize;
    // 图片资源
    private List mImageDataList;
    private List<ViewData> mViewDataList;
    // 预览的起始位置
    private int mStartPosition;
    // 是否显示图片位置
    private boolean showIndex;
    // 图片是否可拖拽
    private boolean doDrag;
    // 图片的拖拽模式
    private int mDragType;
    // 是否执行进场动画
    private boolean doEnterAnim;
    // 是否执行退场动画
    private boolean doExitAnim;
    // 进退场动画的执行时间
    private int mDuration;
    // 图片是否可缩放
    private boolean isImageScaleable;
    // 最大的图片缩放等级
    private float mImageMaxScale;
    // 最小的图片缩放等级
    private float mImageMinScale;
    // 图片预览器的状态
    private int mViewState;
    // 图片加载器
    private ImageLoader mImageLoader;
    // 图片切换监听器
    private OnImageChangedListener mImageChangedListener;
    // 图片点击监听器
    private OnItemClickListener mItemClickListener;
    // 图片长按监听器
    private OnItemLongClickListener mItemLongClickListener;
    // 预览状态监听器
    private OnPreviewStatusListener mPreviewStatusListener;


    public ImageViewerAttacher(FrameLayout frameLayout, AttributeSet attrs) {
        this.container = frameLayout;
        initData();
        initAttr(attrs);
        initView();
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = container.getContext().obtainStyledAttributes(attrs, indi.liyi.viewer.R.styleable.ImageViewer);
            if (a != null) {
                showIndex = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_show_index, true);
                doDrag = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_do_drag, true);
                mDragType = a.getInteger(indi.liyi.viewer.R.styleable.ImageViewer_ivr_drag_type, ImageDraggerType.DRAG_TYPE_DEFAULT);
                doEnterAnim = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_do_enter, true);
                doExitAnim = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_do_exit, true);
                mDuration = a.getInteger(indi.liyi.viewer.R.styleable.ImageViewer_ivr_duration, DEF_DURATION);
                isImageScaleable = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_scaleable, true);
                a.recycle();
            }
        }
    }

    private void initData() {
        showIndex = true;
        doDrag = true;
        mDragType = ImageDraggerType.DRAG_TYPE_DEFAULT;
        doEnterAnim = true;
        doExitAnim = true;
        mDuration = DEF_DURATION;
        isImageScaleable = true;
        mScreenSize = ImageViewerUtil.getScreenSize(container.getContext());
        mViewState = ImageViewerState.STATE_SILENCE;
    }

    private void initView() {
        viewPager = new PreviewPager(container.getContext());
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(this);
        container.addView(viewPager, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        indexView = new TextView(container.getContext());
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, ImageViewerUtil.getStatusBarHeight(container.getContext()) + ImageViewerUtil.dp2px(container.getContext(), 5), 0, 0);
        textParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        indexView.setLayoutParams(textParams);
        indexView.setIncludeFontPadding(false);
        indexView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        indexView.setTextColor(Color.WHITE);
        indexView.setVisibility(View.GONE);
        container.addView(indexView);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (indexView.getVisibility() == View.VISIBLE) {
            indexView.setText((position + 1) + "/" + mImageDataList.size());
        }
        final ScaleImageView scaleImageView = getCurrentView();
        if (scaleImageView != null) {
            scaleImageView.setScale(1f);
            if (mImageChangedListener != null) {
                mImageChangedListener.onImageSelected(position, scaleImageView);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setBackgroundAlpha(int alpha) {
        if (container != null && container.getBackground() != null) {
            container.getBackground().mutate().setAlpha(alpha);
        }
    }

    public void setViewPagerScrollable(boolean scrollable) {
        viewPager.setScrollable(scrollable);
    }

    /**
     * 处理图片索引的显示
     */
    private void handleImageIndex() {
        if (showIndex) {
            if (mImageDataList != null && mImageDataList.size() > 1) {
                indexView.setText((mStartPosition + 1) + "/" + mImageDataList.size());
                indexView.setVisibility(View.VISIBLE);
            } else {
                indexView.setVisibility(View.GONE);
            }
        } else {
            indexView.setVisibility(View.GONE);
        }
    }

    /**
     * 创建 itemView
     *
     * @return
     */
    public ScaleImageView createItemView(final int position) {
        final ScaleImageView itemView = new ScaleImageView(container.getContext());
        return setupItemViewConfig(position, itemView);
    }

    /**
     * 设置 item 视图的配置
     *
     * @param position
     * @param itemView
     */
    public ScaleImageView setupItemViewConfig(int position, ScaleImageView itemView) {
        itemView.setId(position);
        itemView.setPosition(position);
        itemView.setScaleable(isImageScaleable);
        itemView.setDefSize(mScreenSize.x, mScreenSize.y);
        if (mImageMaxScale > 0) itemView.setMaxScale(mImageMaxScale);
        if (mImageMinScale > 0) itemView.setMinScale(mImageMinScale);
        if (mViewDataList != null && mViewDataList.size() > position) {
            itemView.setViewData(mViewDataList.get(position));
        }
        final PhotoView imageView = (PhotoView) itemView.getImageView();
        imageView.setX(0);
        imageView.setY(0);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setScale(1f);
        final ImageGestureListener imageGestureListener = new ImageGestureListener(itemView);
        itemView.setOnViewClickListener(imageGestureListener);
        itemView.setOnViewLongClickListener(imageGestureListener);
        if (doDrag) {
            itemView.setImageDraggerType(mDragType, this, container.getBackground());
        } else {
            itemView.clearImageDragger();
        }
        if (mImageLoader != null) {
            mImageLoader.displayImage(position, mImageDataList.get(position), imageView);
        }
        itemView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return itemView;
    }

    /**
     * 打开图片预览器
     */
    public void watch() {
        viewPager.setScrollable(true);
        ScaleImageView scaleImageView = createItemView(mStartPosition);
        if (mPreviewAdapter == null) {
            mPreviewAdapter = new PreviewAdapter(this);
            mPreviewAdapter.setStartView(scaleImageView);
            mPreviewAdapter.setImageData(mImageDataList);
            viewPager.setAdapter(mPreviewAdapter);
        } else {
            mPreviewAdapter.setStartView(scaleImageView);
            mPreviewAdapter.setImageData(mImageDataList);
            mPreviewAdapter.notifyDataSetChanged();
        }
        viewPager.setCurrentItem(mStartPosition, false);
        setPreviewStatus(ImageViewerState.STATE_READY_OPEN, scaleImageView);
        container.setVisibility(View.VISIBLE);
        if (doEnterAnim) {
            enterWithAnim(scaleImageView);
        } else {
            enter(scaleImageView);
        }
    }

    public void enterWithAnim(final ScaleImageView scaleImageView) {
        viewPager.setScrollable(false);
        scaleImageView.setPosition(mStartPosition);
        scaleImageView.setViewData(mViewDataList.get(mStartPosition));
        scaleImageView.setDuration(mDuration);
        scaleImageView.setDoBackgroundAlpha(false);
        scaleImageView.start(new TransitionCallback() {

            @Override
            public void onTransitionRunning(float progress) {
                super.onTransitionRunning(progress);
                setBackgroundAlpha((int) (progress * 255));
                setPreviewStatus(ImageViewerState.STATE_OPENING, scaleImageView);
            }

            @Override
            public void onTransitionEnd() {
                super.onTransitionEnd();
                enter(scaleImageView);
            }
        });
    }

    private void enter(ScaleImageView scaleImageView) {
        setBackgroundAlpha(255);
        handleImageIndex();
        viewPager.setScrollable(true);
        setPreviewStatus(ImageViewerState.STATE_COMPLETE_OPEN, scaleImageView);
        setPreviewStatus(ImageViewerState.STATE_WATCHING, scaleImageView);
    }

    /**
     * 关闭图片预览器
     */
    public void close() {
        viewPager.setScrollable(false);
        setPreviewStatus(ImageViewerState.STATE_READY_CLOSE, getCurrentView());
        if (doExitAnim) {
            exitWithAnim();
        } else {
            exit();
        }
    }

    public void exitWithAnim() {
        viewPager.setScrollable(false);
        indexView.setVisibility(View.GONE);
        final int position = getCurrentPosition();
        final ViewData viewData = mViewDataList.get(position);
        final ScaleImageView scaleImageView = getCurrentView();
        scaleImageView.setPosition(position);
        scaleImageView.setViewData(viewData);
        scaleImageView.setDuration(mDuration);
        scaleImageView.setDoBackgroundAlpha(false);
        scaleImageView.cancel(new TransitionCallback() {
            @Override
            public void onTransitionRunning(float progress) {
                super.onTransitionRunning(progress);
                setBackgroundAlpha((int) ((1 - progress) * 255));
                setPreviewStatus(ImageViewerState.STATE_CLOSING, scaleImageView);
            }

            @Override
            public void onTransitionEnd() {
                super.onTransitionEnd();
                exit();
            }
        });
    }

    public void exit() {
        container.setVisibility(View.GONE);
        recycle();
        setPreviewStatus(ImageViewerState.STATE_COMPLETE_CLOSE, null);
        setPreviewStatus(ImageViewerState.STATE_SILENCE, null);
    }

    private void recycle() {
        if (mPreviewAdapter != null) {
            mPreviewAdapter.clear();
        }
    }

    /**
     * 清除所有的数据
     */
    public void clear() {
        exit();
        if (mImageDataList != null && mImageDataList.size() > 0) mImageDataList.clear();
        if (mViewDataList != null && mViewDataList.size() > 0) mViewDataList.clear();
        if (mPreviewAdapter != null) mPreviewAdapter.clear();
        mImageChangedListener = null;
        mItemClickListener = null;
        mItemLongClickListener = null;
        mPreviewStatusListener = null;
        mImageLoader = null;
        initData();
    }

    public void setImageData(List list) {
        mImageDataList = list;
    }

    public void setViewData(List<ViewData> list) {
        this.mViewDataList = list;
    }

    public void setStartPosition(int position) {
        this.mStartPosition = position;
    }

    public void showIndex(boolean show) {
        this.showIndex = show;
    }

    public void doDrag(boolean isDo) {
        this.doDrag = isDo;
    }

    public void setDragType(@ImageDraggerType int type) {
        this.mDragType = type;
    }

    public void doEnterAnim(boolean isDo) {
        this.doEnterAnim = isDo;
    }

    public void doExitAnim(boolean isDo) {
        this.doExitAnim = isDo;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setImageLoader(ImageLoader loader) {
        mImageLoader = loader;
    }

    public void setImageScaleable(boolean scaleable) {
        isImageScaleable = scaleable;
    }

    public boolean isImageScaleable() {
        return isImageScaleable;
    }

    public float getImageScale() {
        final ScaleImageView scaleImageView = getCurrentView();
        return scaleImageView != null ? scaleImageView.getScale() : 1f;
    }

    public void setImageMaxScale(float maxScaleLevel) {
        this.mImageMaxScale = maxScaleLevel;
    }

    public float getImageMaxScale() {
        return mImageMaxScale;
    }

    public void setImageMinScale(float minScaleLevel) {
        this.mImageMinScale = minScaleLevel;
    }

    public float getImageMinScale() {
        return mImageMinScale;
    }

    public TextView getIndexView() {
        return indexView;
    }

    public int getCurrentPosition() {
        return viewPager != null ? viewPager.getCurrentItem() : 0;
    }

    public ScaleImageView getCurrentView() {
        return mPreviewAdapter != null ? mPreviewAdapter.getViewByPosition(getCurrentPosition()) : null;
    }

    @ImageViewerState
    public int getViewState() {
        return mViewState;
    }

    public boolean isImageAnimRunning() {
        ScaleImageView scaleImageView = getCurrentView();
        if (scaleImageView != null) {
            return scaleImageView.isImageAnimRunning();
        }
        return false;
    }

    public void setOnImageChangedListener(OnImageChangedListener listener) {
        this.mImageChangedListener = listener;
    }

    public void setOnViewClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public void setOnPreviewStatusListener(@NonNull OnPreviewStatusListener listener) {
        this.mPreviewStatusListener = listener;
    }

    public void setPreviewStatus(int state, ScaleImageView scaleImageView) {
        mViewState = state;
        if (mPreviewStatusListener != null) {
            mPreviewStatusListener.onPreviewStatus(state, scaleImageView);
        }
    }

    /**
     * 图片手势类事件监听类
     */
    private class ImageGestureListener implements View.OnClickListener, View.OnLongClickListener {
        private ScaleImageView scaleImageView;

        public ImageGestureListener(ScaleImageView scaleImageView) {
            this.scaleImageView = scaleImageView;
        }

        /**
         * 单击事件
         */
        @Override
        public void onClick(View v) {
            if (!scaleImageView.isImageAnimRunning() && !scaleImageView.isImageDragging()) {
                if (mItemClickListener != null) {
                    final boolean result = mItemClickListener.onItemClick(scaleImageView.getPosition(), scaleImageView.getImageView());
                    // 判断是否消费了单击事件，如果消费了，则单击事件的后续方法不执行
                    if (result) return;
                }
                close();
            }
        }

        /**
         * 长按事件
         */
        @Override
        public boolean onLongClick(View v) {
            if (!scaleImageView.isImageAnimRunning() && !scaleImageView.isImageDragging() && mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(scaleImageView.getPosition(), scaleImageView.getImageView());
                return true;
            }
            return false;
        }
    }
}
