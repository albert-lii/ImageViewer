package indi.liyi.example.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import indi.liyi.example.R;
import indi.liyi.example.utils.GlideUtil;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.viewer.scip.OnTransCallback;
import indi.liyi.viewer.scip.ScaleImagePager;
import indi.liyi.viewer.scip.ViewData;

/**
 * 单独使用 ScaleImagePager
 */
public class SinglePrevActivity extends BaseActivity {
    private ImageView imageView;
    private ScaleImagePager imagePager;

    private String mUrl;
    private ViewData mViewData = new ViewData();


    @Override
    public int getLayoutId() {
        return R.layout.activity_single_prev;
    }

    @Override
    public void initView() {
        imageView = findViewById(R.id.imageView);
        imagePager = findViewById(R.id.imagePager);

        mUrl = mSourceList.get(0);
        imagePager.setImageLoader(new PhotoLoader());
        imagePager.setViewData(mViewData);

        GlideUtil.loadImage(this, mUrl, imageView, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                mViewData.setImageWidth(resource.getIntrinsicWidth());
                mViewData.setImageHeight(resource.getIntrinsicHeight());
                return false;
            }
        });
        imagePager.preload(mUrl);
    }

    @Override
    public void addListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePager.getViewData().setTargetX(imageView.getX());
                // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
                imagePager.getViewData().setTargetY(imageView.getY());
                imagePager.getViewData().setTargetWidth(imageView.getWidth());
                imagePager.getViewData().setTargetHeight(imageView.getHeight());
                imagePager.start(new OnTransCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onRunning(float progress) {

                    }

                    @Override
                    public void onEnd() {
                        setTransparentStatusBar(R.color.colorBlack);
                    }
                });
            }
        });
        imagePager.setOnViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePager.cancel(new OnTransCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onRunning(float progress) {

                    }

                    @Override
                    public void onEnd() {
                        setTransparentStatusBar(R.color.colorPrimaryDark);
                    }
                });
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
        boolean b = imagePager.onKeyDown(keyCode, event);
        if (b) {
            return b;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (imagePager != null) {
            imagePager.recycle();
        }
        super.finish();
    }
}
