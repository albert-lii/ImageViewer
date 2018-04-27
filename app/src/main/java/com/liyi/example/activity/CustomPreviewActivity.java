package com.liyi.example.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.example.DataUtil;
import com.liyi.example.R;
import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.factory.ImageLoader;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnViewClickListener;
import com.liyi.viewer.listener.OnWatchStatusListener;
import com.liyi.viewer.widget.ImageViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义图片预览
 */
public class CustomPreviewActivity extends Activity {
    private View coverView;
    private TextView tv_cover_back, tv_cover_index;
    private ImageViewer imageViewer;
    private AutoGridView autoGridView;
    private SimpleAutoGridAdapter mImageAdp;

    private RequestOptions mOptions;
    private List<Object> mImageList;
    private List<ViewData> mViewDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_preview);
        initView();
        addListener();
    }

    private void initView() {
        coverView = findViewById(R.id.icd_cover);
        tv_cover_back = findViewById(R.id.tv_cover_back);
        tv_cover_index = findViewById(R.id.tv_cover_index);
        imageViewer = findViewById(R.id.imagePreivew);
        autoGridView = findViewById(R.id.autoGridView);

        tv_cover_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出浏览
                imageViewer.close();
            }
        });

        mOptions = new RequestOptions()
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_placeholder);
        mImageList = DataUtil.getImageData();
        mViewDatas = new ArrayList<>();
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            mViewDatas.add(viewData);
        }

        mImageAdp = new SimpleAutoGridAdapter();
        mImageAdp.setSource(mImageList);
        mImageAdp.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, final ImageView view, int viewType) {
                Glide.with(CustomPreviewActivity.this)
                        .load(source)
                        .apply(mOptions)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                                view.setImageDrawable(placeholder);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                view.setImageDrawable(errorDrawable);
                            }

                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                view.setImageDrawable(resource);
                                mImageList.set(position, resource);
                                mViewDatas.get(position).setImageWidth(resource.getIntrinsicWidth());
                                mViewDatas.get(position).setImageHeight(resource.getIntrinsicHeight());
                            }
                        });
            }
        });
    }

    private void addListener() {
        autoGridView.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (mViewDatas.get(position).getWidth() == 0) {
                    for (int i = 0; i < autoGridView.getChildCount(); i++) {
                        int[] location = new int[2];
                        // 获取在整个屏幕内的绝对坐标
                        autoGridView.getChildAt(i).getLocationOnScreen(location);
                        ViewData viewData = mViewDatas.get(i);
                        viewData.setX(location[0]);
                        // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
                        viewData.setY(location[1]);
                        viewData.setWidth(autoGridView.getChildAt(i).getMeasuredWidth());
                        viewData.setHeight(autoGridView.getChildAt(i).getMeasuredHeight());
                        mViewDatas.set(i, viewData);
                    }
                }
                // 设置图片浏览的起始位置
                imageViewer.setStartPosition(position);
                // 设置图片资源
                imageViewer.setImageData(mImageList);
                // 设置外部 View 的位置以及大小信息
                imageViewer.setViewData(mViewDatas);
                // 设置图片加载方式
                imageViewer.setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(final int position, Object src, final ImageView view) {
                        Glide.with(CustomPreviewActivity.this)
                                .load(src)
                                .into(new SimpleTarget<Drawable>() {

                                    @Override
                                    public void onLoadStarted(@Nullable Drawable placeholder) {
                                        super.onLoadStarted(placeholder);
                                        view.setImageDrawable(placeholder);
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        view.setImageDrawable(errorDrawable);
                                    }

                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        view.setImageDrawable(resource);
                                        mImageList.set(position, resource);
                                        mViewDatas.get(position).setImageWidth(resource.getIntrinsicWidth());
                                        mViewDatas.get(position).setImageHeight(resource.getIntrinsicHeight());
                                    }
                                });
                    }
                });
                // 设置背景色
                imageViewer.setImageBackgroundColor(Color.DKGRAY);
                // 设置点击事件
                imageViewer.setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public boolean onViewClick(int position, View view, float x, float y) {
                        if (coverView.getVisibility() == View.VISIBLE) {
                            coverView.setVisibility(View.GONE);
                        } else {
                            coverView.setVisibility(View.VISIBLE);
                        }
                        // 如果返回 true ，则内部点击事件不会执行，返回 false 则内部点击事件继续执行，退出浏览
                        return true;
                    }
                });
                // 设置图片的切换监听
                imageViewer.setOnImageChangedListener(new OnImageChangedListener() {
                    @Override
                    public void onImageSelected(int position, ImageView view) {
                        tv_cover_index.setText("我是图片" + (position + 1) + "号");
                    }
                });
                // 设置图片浏览器的状态监听
                imageViewer.setOnWatchStatusListener(new OnWatchStatusListener() {
                    @Override
                    public void onWatchStart(int state, int position, ImageView view) {
                        if (state == State.STATE_START_AFTER) {
                            coverView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onWatchDragging(ImageView view) {
                        coverView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onWatchReset(int state, ImageView view) {

                    }

                    @Override
                    public void onWatchEnd(int state) {
                        coverView.setVisibility(View.GONE);
                    }
                });
                // 设置不可拖拽
                imageViewer.doDragAction(true);
                // 设置有启动动画，默认为 true
                imageViewer.doEnterAnim(true);
                // 设置有关闭动画，默认为 true
                imageViewer.doEnterAnim(true);
                // 设置动画时间，默认 200ms
                imageViewer.setAnimDuration(240);
                // 开始浏览
                imageViewer.watch();
            }
        });
        autoGridView.setAdapter(mImageAdp);
    }
}
