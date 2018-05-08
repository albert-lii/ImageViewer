package com.liyi.example.activity;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnViewClickListener;
import com.liyi.viewer.widget.ImageViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 横向列表页面
 */
public class LandListAty extends Activity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private RecyclerAdp mAdapter;
    private LinearLayoutManager mLinearManager;

    private List<Object> mImageList = new ArrayList<>();
    private List<ViewData> mViewDatas = new ArrayList<>();
    // 当前的图片位置
    private int mCurrentPos;
    private Point mScreenSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_list);
        initView();
        addListener();
    }

    private void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        mLinearManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // 从底部开始显示
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);


        mAdapter = new RecyclerAdp();
        mImageList = DataUtil.getImageData();
        mAdapter.setData(mImageList);

        mScreenSize = Utils.getScreenSize(this);
        initViewData();
    }

    private void initViewData() {
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setWidth(mScreenSize.x);
            viewData.setHeight(Utils.dp2px(this, 200));
            viewData.setX(0);
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
                viewData.setX(location[0]);
                mViewDatas.set(position, viewData);

                imageViewer.setStartPosition(position);
                imageViewer.setImageData(mImageList);
                imageViewer.setViewData(mViewDatas);
                imageViewer.setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(final int position, Object src, final ImageView view) {
                        Glide.with(LandListAty.this)
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
        imageViewer.setOnImageChangedListener(new OnImageChangedListener() {
            @Override
            public void onImageSelected(int position, ImageView view) {
                // 获取当前的坐标
                mCurrentPos = position;
            }
        });
        imageViewer.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public boolean onViewClick(int position, View view, float x, float y) {
                // 每次退出浏览时，都将图片显示在中间位置
                ViewData viewData = mViewDatas.get(position);
                viewData.setX(0);
                mViewDatas.set(position, viewData);
                imageViewer.setViewData(mViewDatas);
                mLinearManager.scrollToPositionWithOffset(mCurrentPos, (int) (viewData.getX() / 2));
                return false;
            }
        });
    }
}
