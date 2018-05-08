package com.liyi.viewer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

public class Utils {

    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new Point(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 获取状态栏的高度
     *
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * dp 转 px
     *
     * @param dpVal dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    /**
     * 回收 ImageView 占用的图像内存
     *
     * @param view
     */
    public static void recycleImageView(ImageView view) {
//        if (view == null) return;
//        Drawable drawable = view.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
//            if (bmp != null && !bmp.isRecycled()) {
//                view.setImageDrawable(null);
//                bmp.recycle();
//                bmp = null;
//            }
//        }
//        System.gc();
    }
}
