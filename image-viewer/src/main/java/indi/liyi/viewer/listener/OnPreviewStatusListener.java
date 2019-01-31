package indi.liyi.viewer.listener;

import indi.liyi.viewer.sipr.ScaleImagePager;

/**
 * 监听图片浏览器的状态
 */
public interface OnPreviewStatusListener {

    /**
     * 监听图片预览器的当前状态
     */
    void onPreviewStatus(int status, ScaleImagePager imagePager);
}
