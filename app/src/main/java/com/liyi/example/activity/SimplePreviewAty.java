package com.liyi.example.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.liyi.viewer.widget.ImageViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的图片预览
 */
public class SimplePreviewAty extends Activity {
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
        setContentView(R.layout.aty_simple_preview);
        initView();
        addListener();
    }

    private void initView() {
        imageViewer = findViewById(R.id.imagePreivew);
        autoGridView = findViewById(R.id.autoGridView);

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
                Glide.with(SimplePreviewAty.this)
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
                imageViewer.setStartPosition(position);
                imageViewer.setImageData(mImageList);
                imageViewer.setViewData(mViewDatas);
                imageViewer.setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(final int position, Object src, final ImageView view) {
                        Glide.with(SimplePreviewAty.this)
                                .load(src)
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
                                        mViewDatas.get(position).setImageWidth(resource.getIntrinsicWidth());
                                        mViewDatas.get(position).setImageHeight(resource.getIntrinsicHeight());
                                    }
                                });
                    }
                });
                imageViewer.watch();
            }
        });
        autoGridView.setAdapter(mImageAdp);
    }
}
