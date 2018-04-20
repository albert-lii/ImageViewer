package com.liyi.viewer.view;


import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.liyi.viewer.ImageViewer;
import com.liyi.viewer.R;
import com.liyi.viewer.Utils;
import com.liyi.viewer.data.PreviewData;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnImageLoadListener;

import java.util.ArrayList;
import java.util.List;


public class ImagePreviewActivity extends Activity implements IImagePreview {
    private View view_background;
    private ViewPager viewPager;
    private TextView tv_index;
    // 执行过渡动画的 view
    private ImageView iv_transition;

    private LayoutInflater mInflater;
    private Point mScreenSize;

    // 存储所有的图片视图
    private List<View> mViews;
    // 所有的预览数据
    private PreviewData mPreviewData;
    // 过渡 view 的位置
    private int mClickPosition;
    // 图片资源列表
    private List<Object> mImageList;
    // view 的数据列表
    private List<ViewData> mViewDataList;
    // 图片加载监听
    private OnImageLoadListener mImageLoadListener;
    // 图片切换监听
    private OnImageChangedListener mImageChangedListener;
    // 是否执行启动动画
    private boolean doEnterAnim;
    // 是否执行关闭动画
    private boolean doExitAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer_aty_image_preview);
        initView();
        handlePreviewInfo();
    }

    private void initView() {
        view_background = findViewById(R.id.view_preview_bg);
        viewPager = (ViewPager) findViewById(R.id.vp_preview);
        tv_index = (TextView) findViewById(R.id.tv_preview_index);

        view_background.setAlpha(0f);
        mInflater = LayoutInflater.from(this);
        mViews = new ArrayList<>();
        mScreenSize = Utils.getScreenSize(this);
    }

    @Override
    public void handlePreviewInfo() {
        mPreviewData = ImageViewer.getInstance().getPreviewData();
        if (mPreviewData == null) return;
        mClickPosition = mPreviewData.getClickPosition();
        mImageList = mPreviewData.getImageList();
        mViewDataList = mPreviewData.getViewDataList();
        mImageLoadListener = mPreviewData.getImageLoadListener();
        mImageChangedListener = mPreviewData.getImageChangedListener();
        doEnterAnim = mPreviewData.isDoEnterAnim();
        doExitAnim = mPreviewData.isDoExitAnim();

        // 创建 item view
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            View view = mInflater.inflate(R.layout.imageviewer_viewpager_item_display, null);
            final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView_image);
            // 自定义图片加载方式
            if (mImageLoadListener != null) {
                mImageLoadListener.displayImage(i, mImageList.get(i), photoView);
            }
            // 单击图片，退出浏览
            photoView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (doExitAnim) {
                        excuteExitAnim();
                    } else {
                        finish();
                    }
                }
            });
            if (doEnterAnim && (i == mClickPosition)) {
                iv_transition = photoView;
                // 设置过渡 view
                final ViewData viewData = mViewDataList.get(i);
                iv_transition.setX(viewData.getX());
                iv_transition.setY(viewData.getY());
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv_transition.getLayoutParams();
                layoutParams.width = (int) viewData.getWidth();
                layoutParams.height = (int) viewData.getHeight();
                iv_transition.setLayoutParams(layoutParams);
            }
            mViews.add(view);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final PhotoView photoView = (PhotoView) mViews.get(position).findViewById(R.id.photoView_image);
                photoView.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        excuteExitAnim();
                    }
                });
                if (mImageChangedListener != null) {
                    mImageChangedListener.onImageSelected(position, photoView);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ImageAdapter(mViews));
        viewPager.setCurrentItem(mClickPosition);

        // 判断是否执行过渡动画
        if (doEnterAnim) {
            excuteEnterAnim();
        }
    }

    @Override
    public void excuteEnterAnim() {
        // 获取 view 的数据
        final ViewData viewData = mViewDataList.get(mClickPosition);
        // 缩放前的过渡 view 的宽度
        final float beforeScale_width = viewData.getWidth();
        // 缩放前的过渡 view 的高度
        final float beforeScale_height = viewData.getHeight();
        // 缩放后的过渡 view 的宽度
        final float afterScale_width;
        // 缩放后的过渡 view 的高度
        final float afterScale_heigt;
        // 过渡 view 缩放前的 x 轴，y 轴的坐标
        final float from_x = viewData.getX();
        final float from_y = viewData.getY();
        // 是否定义了图片尺寸
        final boolean hasImageSize;
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            afterScale_width = mScreenSize.x;
            afterScale_heigt = mScreenSize.y;
            hasImageSize = false;
        } else {
            float scale = Math.min(mScreenSize.x / viewData.getImageWidth(), mScreenSize.y / viewData.getImageHeight());
            afterScale_width = viewData.getImageWidth() * scale;
            afterScale_heigt = viewData.getImageHeight() * scale;
            iv_transition.setScaleType(ImageView.ScaleType.CENTER_CROP);
            hasImageSize = true;
        }
        // 过渡 view 缩放后的 x 轴，y 轴的坐标
        final float to_x = (mScreenSize.x - afterScale_width) / 2;
        final float to_y = (mScreenSize.y - afterScale_heigt) / 2;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv_transition.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float fraction = currentValue / 100f;
                final float x = evaluator.evaluate(fraction, from_x, to_x);
                final float y = evaluator.evaluate(fraction, from_y, to_y);
                final float width = evaluator.evaluate(fraction, beforeScale_width, afterScale_width);
                final float height = evaluator.evaluate(fraction, beforeScale_height, afterScale_heigt);

                iv_transition.setX(x);
                iv_transition.setY(y);
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                iv_transition.setLayoutParams(layoutParams);
                view_background.setAlpha(fraction);
                if (fraction == 1) {
                    if (hasImageSize) {
                        iv_transition.setX(0);
                        iv_transition.setX(0);
                        layoutParams.width = mScreenSize.x;
                        layoutParams.height = mScreenSize.y;
                        iv_transition.setLayoutParams(layoutParams);
                        iv_transition.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                }
            }
        });
        animator.setDuration(240);
        animator.start();
    }

    @Override
    public void excuteExitAnim() {
        final int position = viewPager.getCurrentItem();
        final ViewData viewData = mViewDataList.get(position);
        final PhotoView photoView = (PhotoView) mViews.get(position).findViewById(R.id.photoView_image);
        iv_transition = photoView;
        final Drawable drawable = iv_transition.getDrawable();

        float curImg_width = 0, curImg_height = 0;
        if (drawable != null) {
            // 图片原始的宽度和高度
            int oriImg_width = drawable.getIntrinsicWidth();
            int oriImg_height = drawable.getIntrinsicHeight();
            float scale = Math.min(mScreenSize.x / oriImg_width, mScreenSize.y / oriImg_height);
            // 图片当前的宽度与高度
            curImg_width = oriImg_width * scale;
            curImg_height = oriImg_height * scale;
        }
        // 缩放前的过渡 view 的宽度和高度
        final float beforeScale_width1 = iv_transition.getWidth();
        final float beforeScale_height1 = iv_transition.getHeight();
        // 经历第一步缩放后的 view 的宽度和高度
        final float afterScale_width1 = curImg_width;
        final float afterScale_heigt1 = curImg_height;

        // 第二步缩放前的 view 的宽度和高度
        final float beforeScale_width2 = afterScale_width1;
        final float beforeScale_height2 = afterScale_heigt1;
        // 经历第二步缩放后的 view 的宽度和高度
        final float afterScale_width2 = viewData.getWidth();
        final float afterScale_heigt2 = viewData.getHeight();
        // 第二步缩放前的 view 的 x 轴，y 轴的坐标
        final float from_x = (mScreenSize.x - beforeScale_width2) / 2;
        final float from_y = (mScreenSize.y - beforeScale_height2) / 2;
        // 经历第二步缩放后的 view 的 x 轴，y 轴的坐标
        final float to_x = viewData.getX();
        final float to_y = viewData.getY();
        // 图片处于自适应模式时的动画
        ValueAnimator animator1 = ValueAnimator.ofFloat(0, 100);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv_transition.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float fraction = currentValue / 100f;
                final float width = evaluator.evaluate(fraction, beforeScale_width1, afterScale_width1);
                final float height = evaluator.evaluate(fraction, beforeScale_height1, afterScale_heigt1);
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                iv_transition.setLayoutParams(layoutParams);
                if (fraction == 1) {
                    iv_transition.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        });
        animator1.setDuration(0);
        animator1.start();
        // 图片由自适应转为 CENTER_CROP 时的动画
        ValueAnimator animator2 = ValueAnimator.ofFloat(0, 100);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv_transition.getLayoutParams();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float currentValue = (float) animation.getAnimatedValue();
                final float fraction = currentValue / 100f;
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);
                float width = evaluator.evaluate(fraction, beforeScale_width2, afterScale_width2);
                float height = evaluator.evaluate(fraction, beforeScale_height2, afterScale_heigt2);

                iv_transition.setX(x);
                iv_transition.setY(y);
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                iv_transition.setLayoutParams(layoutParams);
                view_background.setAlpha(1 - fraction);
                if (fraction == 1) {
                    finish();
                }
            }
        });
        animator2.setDuration(240);
        animator2.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (doExitAnim) {
                excuteExitAnim();
            }
        }
        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
