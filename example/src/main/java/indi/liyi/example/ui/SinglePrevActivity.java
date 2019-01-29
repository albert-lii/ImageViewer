package indi.liyi.example.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import indi.liyi.example.R;
import indi.liyi.example.utils.GlideUtil;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.dragger.DragMode;

/**
 * 单独使用 ScaleImagePager
 */
public class SinglePrevActivity extends BaseActivity {
    private ImageView imageView;
    private ScaleImagePager imagePager;

    private String mUrl;
    private ViewData mViewData = new ViewData();
    private boolean isCancelByBack;


    @Override
    public int getLayoutId() {
        return R.layout.activity_single_prev;
    }

    @Override
    public void initView() {
        imageView = findViewById(R.id.imageView);
        imagePager = findViewById(R.id.imagePager);

        mUrl = mSourceList.get(0);
        imagePager.setDragMode(DragMode.MODE_CLASSIC);
        imagePager.setImageLoader(new PhotoLoader());
        imagePager.setViewData(mViewData);
        imagePager.preload(mUrl);
        GlideUtil.loadImage(this, mUrl, new SimpleTarget<Drawable>() {

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
                mViewData.setImageWidth(resource.getIntrinsicWidth());
                mViewData.setImageHeight(resource.getIntrinsicHeight());
                imagePager.setViewData(mViewData);
            }
        });
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
