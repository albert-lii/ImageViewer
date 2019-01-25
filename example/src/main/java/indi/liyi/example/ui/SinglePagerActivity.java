package indi.liyi.example.ui;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import indi.liyi.example.R;
import indi.liyi.example.utils.glide.GlideUtil;
import indi.liyi.viewer.Utils;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.dragger.AgileDragger;
import indi.liyi.viewer.sipr.dragger.DragMode;
import indi.liyi.viewer.sipr.ScaleImagePager;

/**
 * 单独使用 ScaleImagePager
 */
public class SinglePagerActivity extends BaseActivity {
    private ImageView imageView;
    private ScaleImagePager imagePager;

    private AgileDragger mDragger;
    private ViewData mViewData;
    private Point mScreenSize;
    private boolean isCancelByBack;


    @Override
    public int getLayoutId() {
        return R.layout.activity_single_pager;
    }

    @Override
    public void initView() {
        imageView = findViewById(R.id.imageView);
        imagePager = findViewById(R.id.imagePager);

        imagePager.setDragMode(DragMode.MODE_AGLIE);
        mDragger = new AgileDragger();
        mDragger.setBackground(imagePager.getBackground());

        mViewData = new ViewData();
        mScreenSize = Utils.getScreenSize(this);
        loadImage();
//        Glide.with(this).load(mSourceList.get(5)).into(imageView);
//        GlideApp.with(this).load(mSourceList.get(0)).into(imageView);
    }

    @Override
    public void addListener() {
        imagePager.setOnViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePager.cancel();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewData.setTargetX(imageView.getX());
                // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
                mViewData.setTargetY(imageView.getY());
                mViewData.setTargetWidth(imageView.getWidth());
                mViewData.setTargetHeight(imageView.getHeight());
                imagePager.setViewData(mViewData);
                imagePager.start();
            }
        });
    }

    private void loadImage() {
        GlideUtil.loadImage(this, R.drawable.img_placeholder, new SimpleTarget<Drawable>() {
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                super.onLoadCleared(placeholder);
                imageView.setImageDrawable(placeholder);
                imagePager.getImageView().setImageDrawable(placeholder);
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
//                imagePager.showProgess();
                imagePager.getImageView().setImageDrawable(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
//                imagePager.hideProgress();
                imagePager.getImageView().setImageDrawable(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (resource != null) {
//                    imagePager.hideProgress();
                    imageView.setImageDrawable(resource);
                    imagePager.getImageView().setImageDrawable(resource);
                    mViewData.setImageWidth(resource.getIntrinsicWidth());
                    mViewData.setImageHeight(resource.getIntrinsicHeight());
                }
            }
        });
    }

    @Override
    public void finish() {
        if (imagePager != null) {
            imagePager.recycle();
        }
        super.finish();
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
        if (imagePager.getVisibility() == View.VISIBLE) {
            if (!isCancelByBack) {
                imagePager.cancel();
                isCancelByBack = true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
