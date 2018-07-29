package com.liyi.example.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liyi.example.R;
import com.liyi.example.glide.GlideUtil;
import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;
import com.liyi.viewer.ImageLoader;
import com.liyi.viewer.ImageViewerState;
import com.liyi.viewer.ViewData;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnItemClickListener;
import com.liyi.viewer.listener.OnPreviewStatusListener;
import com.liyi.viewer.widget.ScaleImageView;
import com.liyi.viewer.widget.ImageViewer;

/**
 * 自定义图片预览
 */
public class CustomPreviewAty extends BaseActivity {
    private AutoGridView autoGridView;
    private ImageViewer imagePreview;
    private View coverView;
    private TextView tv_cover_back, tv_cover_index;
    private SimpleAutoGridAdapter mGridAdp;

    @Override
    int onBindLayoutResID() {
        return R.layout.aty_custom_preview;
    }

    @Override
    void onInit(Bundle savedInstanceState) {
        initView();
        addListener();
    }

    private void initView() {
        imagePreview = findViewById(R.id.imageViewer);
        autoGridView = findViewById(R.id.autoGridView);
        coverView = findViewById(R.id.icd_cover);
        tv_cover_back = findViewById(R.id.tv_cover_back);
        tv_cover_index = findViewById(R.id.tv_cover_index);

        mGridAdp = new SimpleAutoGridAdapter();
        mGridAdp.setSource(mImageList);
        mGridAdp.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, final ImageView view, int viewType) {
                GlideUtil.loadImage(CustomPreviewAty.this, source, view);
            }
        });
        // 设置图片资源
        imagePreview.setImageData(mImageList);
        // 设置图片加载方式
        imagePreview.setImageLoader(new ImageLoader<String>() {

            @Override
            public void displayImage(final int position, String src, final ImageView imageView) {
                GlideUtil.loadImage(CustomPreviewAty.this, src, imageView);
            }
        });
    }

    private void addListener() {
        tv_cover_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出浏览
                imagePreview.close();
            }
        });
        // 设置图片的切换监听
        imagePreview.setOnImageChangedListener(new OnImageChangedListener() {
            @Override
            public void onImageSelected(int position, ScaleImageView view) {
                tv_cover_index.setText("我是图片" + (position + 1) + "号");
            }
        });
        // 图片预览器 item 的单击事件
        imagePreview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public boolean onItemClick(int position, View view) {
                if (coverView.getVisibility() == View.VISIBLE) {
                    coverView.setVisibility(View.GONE);
                } else {
                    coverView.setVisibility(View.VISIBLE);
                }
                // 如果返回 true ，则内部点击事件不会执行，返回 false 则内部点击事件继续执行，退出浏览
                return true;
            }
        });
        // 设置图片预览器的状态监听
        imagePreview.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int state, ScaleImageView imagePager) {
                if (state == ImageViewerState.STATE_COMPLETE_OPEN) {
                    coverView.setVisibility(View.VISIBLE);
                } else if (state == ImageViewerState.STATE_COMPLETE_CLOSE) {
                    coverView.setVisibility(View.GONE);
                }
            }
        });
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
                tv_cover_index.setText("我是图片" + (position + 1) + "号");
                // 设置图片浏览的起始位置
                imagePreview.setStartPosition(position);
                // 设置外部 View 的位置以及大小信息
                imagePreview.setViewData(mViewList);
                // 开始浏览
                imagePreview.watch();
            }
        });
        autoGridView.setAdapter(mGridAdp);
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
