package com.liyi.viewer.factory;

/**
 * 图片拖拽
 */
public interface ImageDragger {
    /**
     * 获取图片当前的缩放级别
     *
     * @return
     */
    float getImageScale();

    /**
     * 拖拽图片
     *
     * @param y1        上次拖拽时的 Y 轴坐标
     * @param y2        本次拖拽时的 Y 轴坐标
     * @param alphaBase 拖拽图片时的背景透明度的基数
     */
    void dragImage(final float y1, final float y2, final float alphaBase);

    /**
     * 松开图片
     *
     * @param maxTranslateY
     */
    void releaseImage(final float maxTranslateY);
}
