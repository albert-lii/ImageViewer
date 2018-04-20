package com.liyi.viewer.view;


public interface IImagePreview {
    /**
     * 处理预览数据信息
     */
    void handlePreviewInfo();

    /**
     * 执行开始动画
     */
    void excuteEnterAnim();

    /**
     * 执行结束动画
     */
    void excuteExitAnim();
}
