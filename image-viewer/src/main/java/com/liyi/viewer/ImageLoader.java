package com.liyi.viewer;


import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载显示类
 */
public abstract class ImageLoader {

    /**
     * Display images
     *
     * @param position Image number
     * @param src      Source of image
     * @param view
     */
    public abstract void displayImage(int position, Object src, ImageView view);
}
