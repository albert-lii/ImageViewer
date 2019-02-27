package indi.liyi.viewer.listener;

import indi.liyi.viewer.imgpg.ImagePager;

/**
 * 图片浏览器状态的监听事件
 */
public interface OnPreviewStatusListener {

    /**
     * 监听图片预览器的当前状态
     */
    void onPreviewStatus(int status, ImagePager imagePager);
}
