package indi.liyi.viewer.listener;

import indi.liyi.viewer.ImageViewerStatus;
import indi.liyi.viewer.sipr.ScaleImagePager;

/**
 * 监听图片浏览器的状态
 */
public interface OnPreviewStatusListener {

    /**
     * 监听图片预览器的当前状态
     *
     * @param state      图片预览器的当前状态
     * @param imagePager 当前的 itemView
     */
    void onPreviewStatus(@ImageViewerStatus int state, ScaleImagePager imagePager);
}
