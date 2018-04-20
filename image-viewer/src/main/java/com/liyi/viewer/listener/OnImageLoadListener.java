package com.liyi.viewer.listener;

import android.view.View;
import android.widget.ImageView;

/**
 * 图片加载监听
 */
public interface OnImageLoadListener {
    /**
     * 图片显示
     *
     * @param position 当前图片位置
     * @param src      图片资源
     * @param view
     */
    void displayImage(int position, Object src, ImageView view);
}
