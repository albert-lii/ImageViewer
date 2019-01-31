package indi.liyi.example.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;

import indi.liyi.example.R;
import indi.liyi.example.utils.GlideUtil;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.scip.ScaleImagePager;
import indi.liyi.viewer.scip.ViewData;
import indi.liyi.viewer.ImageViewer;

/**
 * 简单的预览界面
 */
public class SimplePrevActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private AutoGridView autoGv;
    private SimpleAutoGridAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_simple_prev;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imagePreivew);
        autoGv = findViewById(R.id.autoGridView);

        mAdapter = new SimpleAutoGridAdapter();
        mAdapter.setSource(mSourceList);
        mAdapter.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, final ImageView imageView, int viewType) {
                GlideUtil.loadImage(SimplePrevActivity.this, source, new SimpleTarget<Drawable>() {

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
                        imageViewer.setViewData(mViewList);
                    }
                });
            }
        });
        imageViewer.setImageData(mSourceList);
        imageViewer.setImageLoader(new PhotoLoader());
    }

    @Override
    public void addListener() {
        autoGv.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (mViewList.get(position).getTargetWidth() == 0) {
                    for (int i = 0; i < autoGv.getChildCount(); i++) {
                        ViewData viewData = mViewList.get(i);
                        viewData.setTargetX(autoGv.getChildAt(i).getX());
                        // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
                        viewData.setTargetY(autoGv.getChildAt(i).getY());
                        viewData.setTargetWidth(autoGv.getChildAt(i).getMeasuredWidth());
                        viewData.setTargetHeight(autoGv.getChildAt(i).getMeasuredHeight());
                        mViewList.set(i, viewData);
                    }
                }
                imageViewer.setStartPosition(position);
                imageViewer.setViewData(mViewList);
                imageViewer.watch();
            }
        });
        autoGv.setAdapter(mAdapter);

        imageViewer.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int status, ScaleImagePager imagePager) {
                if (status == ViewerStatus.STATUS_COMPLETE_OPEN) {
                    setTransparentStatusBar(R.color.colorBlack);
                }else if(status == ViewerStatus.STATUS_COMPLETE_CLOSE){
                    setTransparentStatusBar(R.color.colorPrimaryDark);
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
        boolean b = imageViewer.onKeyDown(keyCode, event);
        if (b) {
            return b;
        }
        return super.onKeyDown(keyCode, event);
    }
}
