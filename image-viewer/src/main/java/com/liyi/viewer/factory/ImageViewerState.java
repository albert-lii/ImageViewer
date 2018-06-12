package com.liyi.viewer.factory;

/**
 * 图片浏览器的状态
 */
public final class ImageViewerState {
    /**
     * 正在浏览中
     */
    public static final int STATE_WATCHING = 1;
    /**
     * 已经关闭浏览
     */
    public static final int STATE_CLOSED = 2;
    /**
     * 图片正在被拖拽中
     */
    public static final int STATE_DRAGGING = 3;
    /**
     * 图片正在复位中
     */
    public static final int STATE_RESETTING = 4;
}
