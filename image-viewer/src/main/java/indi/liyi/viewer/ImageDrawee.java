package indi.liyi.viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import indi.liyi.viewer.otherui.ProgressUI;
import indi.liyi.viewer.scimgv.PhotoView;


public class ImageDrawee extends FrameLayout {
    private PhotoView imageView;
    private ProgressUI progressUI;

    public ImageDrawee(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        // 添加 imageView
        imageView = new PhotoView(getContext());
        addView(imageView);
    }

    public void setProgressUI(ProgressUI progressUI) {
        this.progressUI = progressUI;
        if (progressUI != null) {
            progressUI.init(this);
        }
    }

    /**
     * 更新加载进度
     */
    public void handleProgress(float progress) {
        if (progressUI != null && progressUI.getProgressView() != null) {
            if (progress == 0) {
                progressUI.start();
            } else if (progress == 1f) {
                progressUI.stop();
            }
            progressUI.handleProgress(progress);
        }
    }

    public void hideProgressUI() {
        if (progressUI != null && progressUI.getProgressView() != null
                && progressUI.getProgressView().getVisibility() == VISIBLE) {
            progressUI.stop();
        }
    }

    /**
     * 释放 imageView 占据的内存
     * <p>
     * Bitmap 的存储分为两部分，一部分是 Bitmap 的数据，一部分是 Bitmap 的引用。
     * 在 Android2.3 时代，Bitmap 的引用是放在堆中的，而 Bitmap 的数据部分是放在栈中的，需要用户调用 recycle 方法手动进行内存回收；
     * 在 Android2.3 之后，整个 Bitmap（包括数据和引用）都放在了堆中，整个 Bitmap 的回收就全部交给GC了，不用在手动调用 recycle 方法回收内存。
     */
    public void recycle() {
//        Drawable drawable = imageView.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                /**
//                 * 当如果图片加载框架设置了缓存且 bitmap 已经被回收，
//                 * 但是 canvas 在 draw 时，继续使用被回收的 bitmap，会抛出异常：
//                 * a BitmapDrawable: Canvas: trying to use a recycled bitmap.
//                 * 故此处不使用 bitmap.recycle() 方法（或者图片加载框架去除缓存设置）。
//                 */
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
        // 调用 setImageDrawable(null) 方法,然后 GC 会完成图片的回收
        imageView.setImageDrawable(null);
        // 手动调用 GC（但是 GC 并不一定是马上执行的，只能说是加速 GC 回收）
//        System.gc();
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImage(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Drawable) {
            imageView.setImageDrawable((Drawable) obj);
        } else if (obj instanceof Bitmap) {
            imageView.setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Integer) {
            imageView.setImageResource((Integer) obj);
        } else {
            Log.e("ImageDrawee", "Unable to identify picture resources.");
        }
    }

    public float getScale() {
        return imageView.getScale();
    }

    public void setScale(float scale) {
        imageView.setScale(scale);
    }

    public float getMaxScale() {
        return imageView.getMaximumScale();
    }

    public void setMaxScale(float scale) {
        this.imageView.setMaximumScale(scale);
    }

    public float getMinScale() {
        return imageView.getMinimumScale();
    }

    public void setMinScale(float scale) {
        imageView.setMinimumScale(scale);
    }
}
