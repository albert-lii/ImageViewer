package com.liyi.example.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.example.R;
import com.liyi.example.glide.GlideUtil;
import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;
import com.liyi.viewer.ImageLoader;
import com.liyi.viewer.ViewData;
import com.liyi.viewer.dragger.ImageDraggerType;
import com.liyi.viewer.listener.OnItemLongClickListener;
import com.liyi.viewer.widget.ImageViewer;
import com.liyi.viewer.widget.ScaleImageView;

/**
 * 简单的图片预览
 */
public class SimplePreivewAty extends BaseActivity {
    private ImageViewer imagePreview;
    private AutoGridView autoGridView;
    private SimpleAutoGridAdapter mGridAdp;


    @Override
    int onBindLayoutResID() {
        return R.layout.aty_simple_preview;
    }

    @Override
    void onInit(Bundle savedInstanceState) {
        initView();
        addListener();
    }

    private void initView() {
        imagePreview = findViewById(R.id.imagePreivew);
        autoGridView = findViewById(R.id.autoGridView);

        mGridAdp = new SimpleAutoGridAdapter();
        mGridAdp.setSource(mImageList);
        mGridAdp.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, final ImageView imageView, int viewType) {
                GlideUtil.loadImage(SimplePreivewAty.this, source, new SimpleTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        mViewList.get(position).setImageWidth(resource.getIntrinsicWidth());
                        mViewList.get(position).setImageHeight(resource.getIntrinsicHeight());
                        imagePreview.setViewData(mViewList);
                    }
                });
            }
        });
        imagePreview.doDrag(true);
        imagePreview.setDragType(ImageDraggerType.DRAG_TYPE_WX);
        imagePreview.setImageData(mImageList);
        imagePreview.setImageLoader(new ImageLoader<String>() {

            @Override
            public void displayImage(final int position, String src, final ImageView imageView) {
                final ScaleImageView scaleImageView= (ScaleImageView) imageView.getParent();
                GlideUtil.loadImage(SimplePreivewAty.this, src, new SimpleTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        scaleImageView.showProgess();
                        imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        scaleImageView.removeProgressView();
                        imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        scaleImageView.removeProgressView();
                        imageView.setImageDrawable(resource);
                    }
                });
            }
        });
    }

    private void addListener() {
        autoGridView.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (mViewList.get(position).getTargetWidth() == 0) {
                    for (int i = 0; i < autoGridView.getChildCount(); i++) {
                        int[] location = new int[2];
                        // 获取在整个屏幕内的绝对坐标
                        autoGridView.getChildAt(i).getLocationOnScreen(location);
                        ViewData viewData = mViewList.get(i);
                        viewData.setTargetX(location[0]);
                        // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
                        viewData.setTargetY(location[1]);
                        viewData.setTargetWidth(autoGridView.getChildAt(i).getMeasuredWidth());
                        viewData.setTargetHeight(autoGridView.getChildAt(i).getMeasuredHeight());
                        mViewList.set(i, viewData);
                    }
                }
                imagePreview.setStartPosition(position);

                imagePreview.setViewData(mViewList);
                imagePreview.watch();
            }
        });
        autoGridView.setAdapter(mGridAdp);
        imagePreview.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                Toast.makeText(SimplePreivewAty.this, position + "号被长按", Toast.LENGTH_SHORT).show();
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
