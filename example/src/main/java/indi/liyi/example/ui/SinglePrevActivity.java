package indi.liyi.example.ui;

import android.graphics.Matrix;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import indi.liyi.example.R;
import indi.liyi.example.utils.ImageLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.example.utils.glide.GlideUtil;
import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.imgpg.OnTransCallback;
import indi.liyi.viewer.scimgv.PhotoView;

/**
 * 单独使用 ScaleImagePager
 */
public class SinglePrevActivity extends BaseActivity {
    private PhotoView imageView;
    private ImagePager imagePager;

    private String mUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_single_prev;
    }

    @Override
    public void initView() {
        imageView = findViewById(R.id.imageView);
        imagePager = findViewById(R.id.imagePager);

        mUrl = SourceUtil.getImageList().get(0);
        GlideUtil.loadImage(this, mUrl, imageView);
        imagePager.setImageLoader(new ImageLoader());
        imagePager.preload(mUrl);
        imagePager.setDuration(300);
    }

    @Override
    public void addListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("?????? >>>>  "+imageView.getDrawable().getIntrinsicWidth()+"  "+imageView.getDrawable().getIntrinsicHeight());
                imagePager.bindView(imageView, false);
                imagePager.watch(new OnTransCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onRunning(float progress) {

                    }

                    @Override
                    public void onEnd() {
                        changeStatusBarColor(R.color.colorBlack);
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
                        changeStatusBarColor(R.color.colorPrimaryDark);
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
