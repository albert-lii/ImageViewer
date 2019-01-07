package com.liyi.viewer;

import android.widget.ImageView;

/**
 * 自定义图片加载器
 *
 * @param <T>
 */
public interface ImageLoader<T> {
    /**
     * @param position  图片的位置
     * @param source    图片的资源
     * @param imageView
     */
    void displayImage(int position, T source, ImageView imageView);
}
