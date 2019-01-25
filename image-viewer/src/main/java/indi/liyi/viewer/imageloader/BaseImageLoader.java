package indi.liyi.viewer.imageloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import indi.liyi.viewer.sipr.ScaleImagePager;

public abstract class BaseImageLoader {
    private ScaleImagePager imagePager;

    public void injectImageView(ScaleImagePager imagePager) {
        this.imagePager = imagePager;
    }

    /**
     * 开始加载
     */
    public void onStart(Object placeholder) {
        loadSource(placeholder);
    }

    /**
     * 更新加载进度
     */
    public void onUpdateProgress(float progress) {
        if (!imagePager.isProgressBarShowing()) {
            imagePager.showProgessBar();
        }
        imagePager.updateProgress(progress);
    }

    /**
     * 加载成功
     */
    public void onSuccess(Object source) {
        imagePager.hideProgressBar();
        loadSource(source);
    }

    /**
     * 加载失败
     */
    public void onFailure(Object error) {
        imagePager.hideProgressBar();
        loadSource(error);
    }

    /**
     * 加载图片资源
     */
    private void loadSource(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Drawable) {
            imagePager.getImageView().setImageDrawable((Drawable) obj);
        } else if (obj instanceof Bitmap) {
            imagePager.getImageView().setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Integer) {
            imagePager.getImageView().setImageResource((Integer) obj);
        }
    }
}
