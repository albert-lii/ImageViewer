package com.liyi.viewer.listener;

import com.liyi.viewer.widget.ScaleImageView;

/**
 * 图片的切换监听事件
 */
public interface OnImageChangedListener {

    void onImageSelected(int position, ScaleImageView view);
}
