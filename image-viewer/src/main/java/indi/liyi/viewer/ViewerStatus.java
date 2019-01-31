package indi.liyi.viewer;

/**
 * 图片预览器的状态
 */
public final class ViewerStatus {
    /**
     * 准备打开图片预览器
     */
    public static int STATUS_READY_OPEN = 1;
    /**
     * 图片预览器打开中
     */
    public static int STATUS_OPENING = 2;
    /**
     * 图片预览器打开完成
     */
    public static int STATUS_COMPLETE_OPEN = 3;
    /**
     * 图片正在被预览中
     */
    public static int STATUS_WATCHING = 4;
    /**
     * 准备关闭图片预览器
     */
    public static int STATUS_READY_CLOSE = 5;
    /**
     * 图片预览器关闭中
     */
    public static int STATUS_CLOSING = 6;
    /**
     * 关闭图片预览器完成
     */
    public static int STATUS_COMPLETE_CLOSE = 7;
    /**
     * 图片预览器处于未开启状态
     */
    public static int STATUS_SILENCE = 8;
}
