package indi.liyi.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取数据工具类
 */
public class Utils {

    public static List<String> getImageList() {
        List<String> list = new ArrayList<>();
        String url0 = "http://img5.duitang.com/uploads/item/201404/11/20140411214939_XswXa.jpeg";
        String url1 = "http://att.bbs.duowan.com/forum/201210/20/210446opy9p5pghu015p9u.jpg";
        String url2 = "https://b-ssl.duitang.com/uploads/item/201505/09/20150509221719_kyNrM.jpeg";
        String url3 = "https://b-ssl.duitang.com/uploads/item/201709/26/20170926131419_8YhLA.jpeg";
        String url4 = "https://b-ssl.duitang.com/uploads/item/201505/11/20150511122951_MAwVZ.jpeg";
        String url5 = "https://b-ssl.duitang.com/uploads/item/201704/23/20170423205828_BhNSv.jpeg";
        String url6 = "https://b-ssl.duitang.com/uploads/item/201706/30/20170630181644_j4mh5.jpeg";
        String url7 = "https://b-ssl.duitang.com/uploads/item/201407/22/20140722172759_iPCXv.jpeg";
        String url8 = "https://b-ssl.duitang.com/uploads/item/201511/11/20151111103149_mrRfd.jpeg";
        String url9 = "https://b-ssl.duitang.com/uploads/item/201510/14/20151014172010_RnJVz.jpeg";
        list.add(url0);
        list.add(url1);
        list.add(url2);
        list.add(url3);
        list.add(url4);
        list.add(url5);
        list.add(url6);
        list.add(url7);
        list.add(url8);
        list.add(url9);
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void fullTransparentBar(Activity activity, boolean statusBar, boolean navBar) {
        Window window = activity.getWindow();
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (statusBar && navBar) {
            window.getDecorView().setSystemUiVisibility(
                    // 全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity 顶端布局部分会被状态遮住
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // 隐藏导航栏
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            // 防止系统栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (statusBar && !navBar) {
            window.getDecorView().setSystemUiVisibility(
                    // 全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity 顶端布局部分会被状态遮住
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // 防止系统栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (!statusBar && navBar) {
            window.getDecorView().setSystemUiVisibility(
                    // 全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity 顶端布局部分会被状态遮住
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // 隐藏导航栏
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            // 防止系统栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
