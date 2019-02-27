package indi.liyi.viewer.imgpg;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;


/**
 * 图片加载器基类
 */
public abstract class BaseImageLoader {
    // 是否图片加载结束
    private boolean isLoadFinish = true;

    /**
     * 加载图片
     */
    public abstract void displayImage(int position, Object src, ImagePager imagePager);

    /**
     * 开始加载
     */
    public void onStart(Object placeholder, ImagePager imagePager) {
        isLoadFinish = false;
        loadSource(placeholder, imagePager);
    }

    /**
     * 更新加载进度
     */
    public void onProgress(float progress, ImagePager imagePager) {
        imagePager.updateProgress(progress);
    }

    /**
     * 加载成功
     */
    public void onSuccess(Object source, ImagePager imagePager) {
        isLoadFinish = true;
        imagePager.hideProgress();
        loadSource(source, imagePager);
        updateViewData(imagePager);
    }

    /**
     * 加载失败
     */
    public void onFailure(Object error, ImagePager imagePager) {
        isLoadFinish = true;
        imagePager.hideProgress();
        loadSource(error, imagePager);
        updateViewData(imagePager);
    }

    /**
     * 加载图片资源
     */
    private void loadSource(Object obj, ImagePager imagePager) {
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

    private void updateViewData(ImagePager imagePager) {
        ViewData viewData = imagePager.getViewData();
        if (viewData.getImageWidth() == 0 || viewData.getImageHeight() == 0) {
            Drawable drawable = imagePager.getImageView().getDrawable();
            if (drawable != null) {
                viewData.setImageWidth(drawable.getIntrinsicWidth());
                viewData.setImageHeight(drawable.getIntrinsicHeight());
                imagePager.setViewData(viewData);
            }
        }
    }

    public boolean isLoadFinish() {
        return isLoadFinish;
    }
}
