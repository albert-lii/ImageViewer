package com.liyi.viewer.factory;

import android.widget.ImageView;

/**
 * 图片加载
 */
public interface ImageLoader<T> {
    /**
     * 图片显示
     *
     * @param position 当前图片位置
     * @param src      图片资源
     * @param view
     */
    void displayImage(int position, T src, ImageView view);
}
