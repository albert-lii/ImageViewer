package indi.liyi.viewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.sipr.BaseImageLoader;
import indi.liyi.viewer.sipr.OnTransCallback;
import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.dragger.DragMode;
import indi.liyi.viewer.sipr.dragger.OnDragStatusListener;
import indi.liyi.viewer.viewpager.PreviewAdapter;
import indi.liyi.viewer.viewpager.PreviewViewPager;


public class ViewerAttacher implements ViewPager.OnPageChangeListener {
    // imageViewer 的容器
    private FrameLayout container;
    // 图片索引
    private TextView indexView;
    private PreviewViewPager viewPager;
    private PreviewAdapter mPreviewAdapter;

    // 是否执行进场动画
    private boolean doEnterAnim = true;
    // 是否执行退场动画
    private boolean doExitAnim = true;
    // 是否显示图片位置
    private boolean showIndex = true;
    // 图片是否可拖拽
    private boolean canDragged = true;
    // 是否允许更改背景透明度
    private boolean canBgAlpha = true;
    // 图片的拖拽模式
    private int mDragMode = DragMode.MODE_CLASSIC;
    // 进退场动画的执行时间
    private int mDuration = ScaleImagePager.DEF_ANIM_DURATION;
    // 图片是否可缩放
    private boolean isScaleable;
    // 最大的图片缩放等级
    private float mMaxScale;
    // 最小的图片缩放等级
    private float mMinScale;

    // 图片资源
    private List mSourceList;
    private List<ViewData> mTargetList;
    // 预览的起始位置
    private int mStartPosition;
    // 图片预览器的状态
    private int mViewStatus;

    // 图片加载器
    private BaseImageLoader mImageLoader;
    // 图片切换监听器
    private OnItemChangedListener mItemChangedListener;
    // 图片点击监听器
    private OnItemClickListener mItemClickListener;
    // 图片长按监听器
    private OnItemLongClickListener mItemLongClickListener;
    // 图片的拖拽监听器
    private OnDragStatusListener mItemDragStatusListener;
    // 预览状态监听器
    private OnPreviewStatusListener mPreviewStatusListener;


    public ViewerAttacher(@NonNull FrameLayout container, AttributeSet attrs) {
        this.container = container;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = container.getContext().obtainStyledAttributes(attrs, indi.liyi.viewer.R.styleable.ImageViewer);
            if (a != null) {
                doEnterAnim = a.getBoolean(R.styleable.ImageViewer_ivr_doEnterAnim, true);
                doExitAnim = a.getBoolean(R.styleable.ImageViewer_ivr_doExitAnim, true);
                showIndex = a.getBoolean(indi.liyi.viewer.R.styleable.ImageViewer_ivr_showIndex, true);
                canDragged = a.getBoolean(R.styleable.ImageViewer_ivr_canDragged, true);
                canBgAlpha = a.getBoolean(R.styleable.ImageViewer_ivr_canBgAlpha, true);
                mDragMode = a.getInteger(R.styleable.ImageViewer_ivr_dragMode, DragMode.MODE_CLASSIC);
                mDuration = a.getInteger(indi.liyi.viewer.R.styleable.ImageViewer_ivr_duration, ScaleImagePager.DEF_ANIM_DURATION);
                a.recycle();
            }
        }

        isScaleable = true;
        mViewStatus = ViewerStatus.STATUS_SILENCE;
        initView();
        container.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        viewPager = new PreviewViewPager(container.getContext());
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(this);
        container.addView(viewPager, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        indexView = new TextView(container.getContext());
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0,
                dp2px(container.getContext(), 5),
                0,
                0);
        textParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        indexView.setLayoutParams(textParams);
        indexView.setIncludeFontPadding(false);
        indexView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        indexView.setTextColor(Color.WHITE);
        indexView.setVisibility(View.GONE);
        container.addView(indexView);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// viewpager 页面滑动监听
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (indexView.getVisibility() == View.VISIBLE) {
            indexView.setText((position + 1) + "/" + mSourceList.size());
        }
        final ScaleImagePager imagePager = getCurrentItem();
        if (imagePager != null) {
            imagePager.setScale(1f);
            if (mItemChangedListener != null) {
                mItemChangedListener.onItemChanged(position, imagePager);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置 viewpager 是否可滑动
     */
    public void setViewPagerScrollable(boolean scrollable) {
        viewPager.setScrollable(scrollable);
    }

    /**
     * 处理图片索引的显示
     */
    private void handleImageIndex() {
        if (showIndex) {
            if (mSourceList != null && mSourceList.size() > 1) {
                indexView.setText((mStartPosition + 1) + "/" + mSourceList.size());
                indexView.setVisibility(View.VISIBLE);
            } else {
                indexView.setVisibility(View.GONE);
            }
        } else {
            indexView.setVisibility(View.GONE);
        }
    }

    /**
     * 创建 item
     */
    public ScaleImagePager createItem(final int position) {
        final ScaleImagePager item = new ScaleImagePager(container.getContext());
        item.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        return setupItemConfig(position, item);
    }

    /**
     * 初始化 item 的配置
     */
    public ScaleImagePager setupItemConfig(int position, ScaleImagePager item) {
        item.setId(position);
        item.setPosition(position);
        item.asItem(true);
        item.canBgAlpha(canBgAlpha);
        item.setScaleable(isScaleable);
        if (mMaxScale > 0) {
            item.setMaxScale(mMaxScale);
        }
        if (mMinScale > 0) {
            item.setMinScale(mMinScale);
        }
        if (mTargetList != null && mTargetList.size() > position) {
            item.setViewData(mTargetList.get(position));
        }
        item.canDragged(canDragged);
        if (canDragged) {
            item.setDragMode(mDragMode, container.getBackground(), this);
        } else {
            item.removeDragger();
        }
        if (mImageLoader != null) {
            item.setImageLoader(mImageLoader);
            item.preload(mSourceList.get(position));
        }
        final ItemGestureListener itemGestureListener = new ItemGestureListener(
                this, item,
                mItemClickListener,
                mItemLongClickListener,
                mItemDragStatusListener);
        item.setOnViewClickListener(itemGestureListener);
        item.setOnViewLongClickListener(itemGestureListener);
        item.setOnDragStatusListener(itemGestureListener);
        return item;
    }

    /**
     * 打开图片预览器
     */
    public void watch() {
        viewPager.setScrollable(true);
        ScaleImagePager item = createItem(mStartPosition);
        if (mPreviewAdapter == null) {
            mPreviewAdapter = new PreviewAdapter(this);
            mPreviewAdapter.setStartItem(item);
            mPreviewAdapter.setSource(mSourceList);
            viewPager.setAdapter(mPreviewAdapter);
        } else {
            mPreviewAdapter.setStartItem(item);
            mPreviewAdapter.setSource(mSourceList);
            mPreviewAdapter.notifyDataSetChanged();
        }
        viewPager.setCurrentItem(mStartPosition, false);
        setPreviewStatus(ViewerStatus.STATUS_READY_OPEN, item);
        container.setVisibility(View.VISIBLE);
        doEnter(item);
    }

    public void doEnter(final ScaleImagePager item) {
        if (doEnterAnim) {
            viewPager.setScrollable(false);
            item.setPosition(mStartPosition);
            item.setViewData(mTargetList.get(mStartPosition));
            item.setDuration(mDuration);
            item.setViewerBg(container.getBackground());
            item.start(container.getMeasuredWidth(), container.getMeasuredHeight(), new OnTransCallback() {

                @Override
                public void onStart() {

                }

                @Override
                public void onRunning(float progress) {
                    setPreviewStatus(ViewerStatus.STATUS_OPENING, item);
                }

                @Override
                public void onEnd() {
                    enterEnd(item);
                }
            });
        } else {
            enterEnd(item);
        }
    }

    private void enterEnd(ScaleImagePager item) {
        handleImageIndex();
        viewPager.setScrollable(true);
        setPreviewStatus(ViewerStatus.STATUS_COMPLETE_OPEN, item);
        setPreviewStatus(ViewerStatus.STATUS_WATCHING, item);
    }

    /**
     * 关闭图片预览器
     */
    public void close() {
        viewPager.setScrollable(false);
        setPreviewStatus(ViewerStatus.STATUS_READY_CLOSE, getCurrentItem());
        doExit();
    }

    public void doExit() {
        if (doExitAnim) {
            viewPager.setScrollable(false);
            indexView.setVisibility(View.GONE);
            final int position = getCurrentPosition();
            final ViewData viewData = mTargetList.get(position);
            final ScaleImagePager item = getCurrentItem();
            item.setPosition(position);
            item.setViewData(viewData);
            item.setDuration(mDuration);
            item.cancel(new OnTransCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onRunning(float progress) {
                    setPreviewStatus(ViewerStatus.STATUS_CLOSING, item);
                }

                @Override
                public void onEnd() {
                    exitEnd();
                }
            });
        } else {
            exitEnd();
        }
    }

    public void exitEnd() {
        container.setVisibility(View.GONE);
        recycle();
        setPreviewStatus(ViewerStatus.STATUS_COMPLETE_CLOSE, null);
        setPreviewStatus(ViewerStatus.STATUS_SILENCE, null);
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
        exitEnd();
        if (mSourceList != null && mSourceList.size() > 0) {
            mSourceList.clear();
        }
        if (mTargetList != null && mTargetList.size() > 0) {
            mTargetList.clear();
        }
        if (mPreviewAdapter != null) {
            mPreviewAdapter.clear();
        }
        mItemChangedListener = null;
        mItemClickListener = null;
        mItemLongClickListener = null;
        mPreviewStatusListener = null;
        mImageLoader = null;
    }

    public void setImageData(List list) {
        mSourceList = list;
    }

    public void setViewData(List<ViewData> list) {
        this.mTargetList = list;
    }

    public void setStartPosition(int position) {
        this.mStartPosition = position;
    }

    public void showIndex(boolean show) {
        this.showIndex = show;
    }

    public void doDrag(boolean isDo) {
        this.canDragged = isDo;
    }

    public void setDragType(int type) {
        this.mDragMode = type;
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

    public void setImageLoader(BaseImageLoader loader) {
        mImageLoader = loader;
    }

    public void setScaleable(boolean scaleable) {
        isScaleable = scaleable;
    }

    public boolean isScaleable() {
        return isScaleable;
    }

    public float getCurrentItemScale() {
        final ScaleImagePager scaleImageView = getCurrentItem();
        return scaleImageView != null ? scaleImageView.getScale() : 1f;
    }

    public void setMaxScale(float maxScaleLevel) {
        this.mMaxScale = maxScaleLevel;
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMinScale(float minScaleLevel) {
        this.mMinScale = minScaleLevel;
    }

    public float getMinScale() {
        return mMinScale;
    }

    public TextView getIndexView() {
        return indexView;
    }

    public int getCurrentPosition() {
        return viewPager != null ? viewPager.getCurrentItem() : 0;
    }

    public ScaleImagePager getCurrentItem() {
        return mPreviewAdapter != null ? mPreviewAdapter.getViewByPosition(getCurrentPosition()) : null;
    }

    public int getViewStatus() {
        return mViewStatus;
    }

    public boolean isImageAnimRunning() {
        ScaleImagePager item = getCurrentItem();
        if (item != null) {
            return item.isAnimRunning();
        }
        return false;
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.mItemChangedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public void setOnDragStatusListener(OnDragStatusListener listener) {
        this.mItemDragStatusListener = listener;
    }

    public void setOnPreviewStatusListener(@NonNull OnPreviewStatusListener listener) {
        this.mPreviewStatusListener = listener;
    }

    public void setPreviewStatus(int state, ScaleImagePager scaleImageView) {
        mViewStatus = state;
        if (mPreviewStatusListener != null) {
            mPreviewStatusListener.onPreviewStatus(state, scaleImageView);
        }
    }

    private int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }
}
