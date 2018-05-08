package com.liyi.viewer.widget;


import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.factory.ImageLoader;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnViewClickListener;
import com.liyi.viewer.listener.OnWatchStatusListener;

import java.util.List;

/**
 * 图片浏览器的主要方法
 */
public interface IImageViewer {
    /**
     * 设置图片背景
     *
     * @param drawable
     */
    void setImageBackground(Drawable drawable);

    /**
     * 设置图片背景
     *
     * @param resid
     */
    void setImageBackgroundResource(@DrawableRes int resid);

    /**
     * 设置图片背景
     *
     * @param color
     */
    void setImageBackgroundColor(@ColorInt int color);

    /**
     * 设置开始展示的图片的位置
     *
     * @param position
     */
    void setStartPosition(int position);

    /**
     * 设置图片资源
     *
     * @param list
     */
    void setImageData(List<Object> list);

    /**
     * 设置 View 数据
     *
     * @param list
     */
    void setViewData(List<ViewData> list);

    /**
     * 设置图片加载类
     *
     * @param loader
     */
    void setImageLoader(ImageLoader loader);

    /**
     * 设置图片切换监听
     *
     * @param listener
     */
    void setOnImageChangedListener(OnImageChangedListener listener);

    /**
     * 设置图片的 View 容器的单击监听
     *
     * @param listener
     */
    void setOnViewClickListener(OnViewClickListener listener);

    /**
     * 设置图片浏览状态监听
     *
     * @param listener
     */
    void setOnWatchStatusListener(OnWatchStatusListener listener);

    /**
     * 是否显示图片序号
     *
     * @param show
     */
    void showIndex(boolean show);

    /**
     * 是否允许图片被拖拽
     *
     * @param isDo
     */
    void doDragAction(boolean isDo);

    /**
     * 是否开启图片浏览启动动画
     *
     * @param isDo
     */
    void doEnterAnim(boolean isDo);

    /**
     * 是否开启图片浏览退出动画
     *
     * @param isDo
     */
    void doExitAnim(boolean isDo);

    /**
     * 设置打开和关闭的动画执行时间
     *
     * @param duration
     */
    void setAnimDuration(int duration);

    /**
     * 执行开始动画
     */
    void excuteEnterAnim();

    /**
     * 执行结束动画
     */
    void excuteExitAnim();

    /**
     * 开启图片浏览
     */
    void watch();

    /**
     * 关闭图片浏览
     */
    void close();

    /**
     * 移除所有数据（只移除图片资源和 View 信息）
     */
    void removeAllData();

    /**
     * 清除所有数据
     */
    void clear();

    /**
     * 设置图片是否可缩放
     *
     * @param zoomable
     */
    void setImageZoomable(boolean zoomable);

    /**
     * 获取图片是否可缩放
     *
     * @return
     */
    boolean isImageZoomable();

    /**
     * 获取图片当前的缩放级别
     *
     * @return
     */
    float getImageScale();

    /**
     * 获取当前图片的位置
     *
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取当前的 Item 视图
     *
     * @return
     */
    View getCurrentView();
}
