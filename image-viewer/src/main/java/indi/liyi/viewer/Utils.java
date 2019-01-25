package indi.liyi.viewer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class Utils {

    /**
     * 获取屏幕的尺寸
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
     * @return
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
     * 获取 View 在屏幕坐标系中的坐标
     *
     * @param view 需要定位位置的 View
     * @return 坐标系数组
     */
    public static int[] getViewLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    /**
     * dp 转 px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    /**
     * 释放 imageView 占据的内存
     * <p>
     * Bitmap 的存储分为两部分，一部分是 Bitmap 的数据，一部分是 Bitmap 的引用。
     * 在 Android2.3 时代，Bitmap 的引用是放在堆中的，而 Bitmap 的数据部分是放在栈中的，需要用户调用 recycle 方法手动进行内存回收；
     * 在 Android2.3 之后，整个 Bitmap（包括数据和引用）都放在了堆中，整个 Bitmap 的回收就全部交给GC了，不用在手动调用 recycle 方法回收内存。
     *
     * @param imageView
     */
    public static void recycleImage(ImageView imageView) {
//        Drawable drawable = imageView.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                /**
//                 * 当 bitmap 已经被回收，但是 canvas 在 draw 时，继续使用被回收的 bitmap，会抛出异常：
//                 * a BitmapDrawable: Canvas: trying to use a recycled bitmap.
//                 * 故此处不使用 bitmap.recycle() 方法。
//                 */
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
        // 调用 setImageDrawable(null) 方法,然后 GC 会完成图片的回收
        imageView.setImageDrawable(null);
        // 手动调用 GC（但是 GC 并不一定是马上执行的，只能说是加速 GC 回收）
        System.gc();
    }
}
