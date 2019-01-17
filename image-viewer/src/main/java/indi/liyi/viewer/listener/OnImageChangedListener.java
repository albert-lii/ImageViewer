package indi.liyi.viewer.listener;

import indi.liyi.viewer.widget.ScaleImageView;

/**
 * 图片的切换监听事件
 */
public interface OnImageChangedListener {

    void onImageSelected(int position, ScaleImageView view);
}
