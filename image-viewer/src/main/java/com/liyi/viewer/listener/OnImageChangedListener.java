package com.liyi.viewer.listener;

import android.widget.ImageView;

/**
 * 图片切换监听
 */
public interface OnImageChangedListener {

    /**
     * 监听当前被选中的图片
     *
     * @param position
     */
    void onImageSelected(int position,ImageView view);
}
