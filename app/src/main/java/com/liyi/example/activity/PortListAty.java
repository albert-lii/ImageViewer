package com.liyi.example.activity;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.example.DataUtil;
import com.liyi.example.R;
import com.liyi.example.adapter.RecyclerAdp;
import com.liyi.viewer.Utils;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.factory.ImageLoader;
import com.liyi.viewer.listener.OnWatchStatusListener;
import com.liyi.viewer.widget.ImageViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 竖向列表页面
 */
public class PortListAty extends Activity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private RecyclerAdp mAdapter;
    private LinearLayoutManager mLinearManager;

    private List<Object> mImageList = new ArrayList<>();
    private List<ViewData> mViewDatas = new ArrayList<>();
    private Point mScreenSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_list_port);
        initView();
        addListener();
    }

    private void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        mLinearManager = new LinearLayoutManager(this);
        // 从底部开始显示
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);


        mAdapter = new RecyclerAdp(1);
        mImageList = DataUtil.getImageData();
        mAdapter.setData(mImageList);

        mScreenSize = Utils.getScreenSize(this);
        initViewData();
    }

    private void initViewData() {
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setWidth(mScreenSize.x - Utils.dp2px(this, 20));
            viewData.setHeight(Utils.dp2px(this, 200));
            viewData.setX(Utils.dp2px(this, 10));
            viewData.setY(0);
            mViewDatas.add(viewData);
        }
    }

    private void addListener() {
        mAdapter.setOnItemClickCallback(new RecyclerAdp.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                ViewData viewData = mViewDatas.get(position);
                // 去掉状态栏的高度
                viewData.setY(location[1]);
                mViewDatas.set(position, viewData);

                imageViewer.setStartPosition(position);
                imageViewer.setImageData(mImageList);
                imageViewer.setViewData(mViewDatas);
                imageViewer.setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(final int position, Object src, final ImageView view) {
                        Glide.with(PortListAty.this)
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
                                        // 此处最好是后台返回给你所有图片的宽高
                                        mViewDatas.get(position).setImageWidth(resource.getIntrinsicWidth());
                                        mViewDatas.get(position).setImageHeight(resource.getIntrinsicHeight());
                                    }
                                });
                    }
                });
                imageViewer.watch();
            }
        });
        recyclerView.setAdapter(mAdapter);
        mLinearManager.scrollToPositionWithOffset(0, 0);
        imageViewer.setOnWatchStatusListener(new OnWatchStatusListener() {
            @Override
            public void onWatchStart(int state, int position, ImageView view) {

            }

            @Override
            public void onWatchDragging(ImageView view) {

            }

            @Override
            public void onWatchReset(int state, ImageView view) {

            }

            @Override
            public void onWatchEnd(int state) {
                if (state == State.STATE_END_BEFORE) {
                    int top = getTop(imageViewer.getCurrentPosition());
                    ViewData viewData = mViewDatas.get(imageViewer.getCurrentPosition());
                    viewData.setY(top);
                    mViewDatas.set(imageViewer.getCurrentPosition(), viewData);
                    imageViewer.setViewData(mViewDatas);
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });
    }

    private int getTop(int position) {
        int top = 0;
        // 当前图片的高度
        float imgH = Float.valueOf(mViewDatas.get(position).getHeight());
        // 图片距离 imageViewer 的上下边距
        int dis = (int) ((imageViewer.getHeight() - imgH) / 2);
        // 如果图片高度大于等于 imageViewer 的高度
        if (dis <= 0) {
            return top + dis;
        } else {
            float th1 = 0;
            float th2 = 0;
            // 计算当前图片上方所有 Item 的总高度
            for (int i = 0; i < position; i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th1 += Utils.dp2px(this, 210);
            }
            // 计算当前图片下方所有 Item 的总高度
            for (int i = position + 1; i < mImageList.size(); i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th2 += Utils.dp2px(this, 210);
            }
            if (th1 >= dis && th2 >= dis) {
                return top + dis;
            } else if (th1 < dis) {
                return (int) (top + th1);
            } else if (th2 < dis) {
                return (int) (recyclerView.getHeight() - imgH);
            }
        }
        return 0;
    }
}
