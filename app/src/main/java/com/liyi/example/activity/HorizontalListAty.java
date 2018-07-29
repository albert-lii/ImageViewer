package com.liyi.example.activity;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.example.R;
import com.liyi.example.adapter.RecyclerAdp;
import com.liyi.example.glide.GlideUtil;
import com.liyi.viewer.ImageLoader;
import com.liyi.viewer.ImageViewerUtil;
import com.liyi.viewer.ViewData;
import com.liyi.viewer.listener.OnPreviewStatusListener;
import com.liyi.viewer.widget.ScaleImageView;
import com.liyi.viewer.widget.ImageViewer;

/**
 * 横向列表页面
 */
public class HorizontalListAty extends BaseActivity {
    private ImageViewer imagePreview;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearManager;
    private RecyclerAdp mAdapter;

    private Point mScreenSize;

    @Override
    int onBindLayoutResID() {
        return R.layout.aty_horizontal_list;
    }

    @Override
    void onInit(Bundle savedInstanceState) {
        initView();
        addListener();
    }

    private void initView() {
        imagePreview = findViewById(R.id.imagePreview);
        recyclerView = findViewById(R.id.recyclerview);

        mLinearManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);

        mAdapter = new RecyclerAdp(0);
        mAdapter.setData(mImageList);
        initData();
        imagePreview.setImageData(mImageList);
        imagePreview.setImageLoader(new ImageLoader<String>() {
            @Override
            public void displayImage(final int position, String src, final ImageView imageView) {
                GlideUtil.loadImage(HorizontalListAty.this, src, new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        mViewList.get(position).setImageWidth(resource.getIntrinsicWidth());
                        mViewList.get(position).setImageHeight(resource.getIntrinsicHeight());
                    }
                });
            }
        });
    }

    private void initData() {
        mScreenSize = ImageViewerUtil.getScreenSize(this);
        for (int i = 0, len = mViewList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setTargetX(0);
            viewData.setTargetY(0);
            viewData.setTargetWidth(mScreenSize.x);
            viewData.setTargetHeight(ImageViewerUtil.dp2px(this, 200));
            mViewList.set(i, viewData);
        }
    }

    private void addListener() {
        mAdapter.setOnItemClickCallback(new RecyclerAdp.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                ViewData viewData = mViewList.get(position);
                viewData.setTargetX(location[0]);
                mViewList.set(position, viewData);
                imagePreview.setStartPosition(position);
                imagePreview.setViewData(mViewList);
                imagePreview.watch();
            }
        });
        recyclerView.setAdapter(mAdapter);
        mLinearManager.scrollToPositionWithOffset(0, 0);

        imagePreview.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int state, ScaleImageView imagePager) {
                if (state == com.liyi.viewer.ImageViewerState.STATE_READY_CLOSE) {
                    // 每次退出浏览时，都将图片显示在中间位置
                    ViewData viewData = mViewList.get(imagePreview.getCurrentPosition());
                    viewData.setTargetX(0);
                    mViewList.set(imagePreview.getCurrentPosition(), viewData);
                    imagePreview.setViewData(mViewList);
                    mLinearManager.scrollToPositionWithOffset(imagePreview.getCurrentPosition(), (int) (viewData.getTargetX() / 2));
                }
            }
        });
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean b = imagePreview.onKeyDown(keyCode, event);
        if (b) {
            return b;
        }
        return super.onKeyDown(keyCode, event);
    }
}
