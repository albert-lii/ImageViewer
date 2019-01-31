package indi.liyi.viewer.sipr;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import indi.liyi.viewer.sipr.ScaleImagePager;

public abstract class BaseImageLoader {
    // 是否图片加载结束
    private boolean isLoadFinish = true;

    /**
     * 加载图片
     */
    public abstract void displayImage(int position, Object src, ScaleImagePager imagePager);

    /**
     * 开始加载
     */
    public void onStart(Object placeholder, ScaleImagePager imagePager) {
        isLoadFinish = false;
        loadSource(placeholder, imagePager);
    }

    /**
     * 更新加载进度
     */
    public void onProgress(float progress, ScaleImagePager imagePager) {
        imagePager.updateProgress(progress);
    }

    /**
     * 加载成功
     */
    public void onSuccess(Object source, ScaleImagePager imagePager) {
        isLoadFinish = true;
        imagePager.hideProgressBar();
        loadSource(source, imagePager);
    }

    /**
     * 加载失败
     */
    public void onFailure(Object error, ScaleImagePager imagePager) {
        isLoadFinish = true;
        imagePager.hideProgressBar();
        loadSource(error, imagePager);
    }

    /**
     * 加载图片资源
     */
    private void loadSource(Object obj, ScaleImagePager imagePager) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Drawable) {
            imagePager.getImageView().setImageDrawable((Drawable) obj);
        } else if (obj instanceof Bitmap) {
            imagePager.getImageView().setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Integer) {
            imagePager.getImageView().setImageResource((Integer) obj);
        } else {
            Log.e("BaseImageLoader", "Unable to identify picture resources ");
        }
    }

    public boolean isLoadFinish() {
        return isLoadFinish;
    }
}
