package indi.liyi.viewer;

/**
 * 图片浏览器的状态
 */
public final class ViewerStatus {
    /**
     * 图片浏览器处于未开启状态
     */
    public static final int STATUS_SILENCE = 0;
    /**
     * 图片浏览器开始执行进场动画
     */
    public static final int STATUS_BEGIN_OPEN = 1;
    /**
     * 图片浏览器正在执行进场动画
     */
    public static final int STATUS_OPENING = 2;
    /**
     *  图片正在被浏览中
     */
    public static final int STATUS_WATCHING = 3;
    /**
     * 图片浏览器开始执行退场动画
     */
    public static final int STATUS_BEGIN_CLOSE = 4;
    /**
     * 图片浏览器正在执行退场动画
     */
    public static final int STATUS_CLOSING = 5;
}
