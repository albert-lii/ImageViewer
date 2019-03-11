package indi.liyi.viewer;

import android.widget.ImageView;

public abstract class ImageLoader {
    private static final String TAG = "ImageLoader";

    /**
     * 加载图片
     */
    public abstract void displayImage(Object src, ImageView imageView, LoadCallback callback);

    public static class LoadCallback {
        private ImageDrawee drawee;

        public LoadCallback(ImageDrawee drawee) {
            this.drawee = drawee;
        }

        public void onLoadStarted(Object placeholder) {
            loadSource(placeholder, drawee);
        }

        public void onLoading(float progress) {

        }

        public void onLoadSucceed(Object source) {
            loadSource(source, drawee);
        }

        public void onLoadFailed(Object error) {
            loadSource(error, drawee);
        }
    }

    /**
     * 加载图片资源
     */
    private static void loadSource(Object obj, ImageDrawee drawee) {
        drawee.setImage(obj);
    }
}
