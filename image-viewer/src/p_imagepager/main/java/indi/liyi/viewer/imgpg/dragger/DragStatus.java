package indi.liyi.viewer.imgpg.dragger;


public final class DragStatus {
    /**
     * 准备拖拽 imageView
     */
    public static final int STATUS_READY = 1;
    /**
     * imageView 正在被拖拽中
     */
    public static final int STATUS_DRAGGING = 2;
    /**
     * imageView 开始复位
     */
    public static final int STATUS_BEGIN_RESTORE = 3;
    /**
     * imageView 正在复位中
     */
    public static final int STATUS_RESTORING = 4;
    /**
     * imageView 复位完毕
     */
    public static final int STATUS_END_RESTORE = 5;
    /**
     * 开始退出预览
     */
    public static final int STATUS_BEGIN_EXIT = 6;
    /**
     * 正在退出预览中中
     */
    public static final int STATUS_EXITTING = 7;
    /**
     * 退出预览完毕
     */
    public static final int STATUS_END_EXIT = 8;
}
