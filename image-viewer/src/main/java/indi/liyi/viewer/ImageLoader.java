package indi.liyi.viewer;

import android.widget.ImageView;

public abstract class ImageLoader {
    /**
     * 加载图片
     */
    public abstract void displayImage(Object src, ImageView imageView, LoadCallback callback);

    public interface LoadCallback {
        void onLoadStarted(Object placeholder);

        void onLoading(float progress);

        void onLoadSucceed(Object source);

        void onLoadFailed(Object error);
    }
}
