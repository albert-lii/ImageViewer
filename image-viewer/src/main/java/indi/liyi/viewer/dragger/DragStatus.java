package indi.liyi.viewer.dragger;


public final class DragStatus {
    /**
     * 准备开始拖拽
     */
    public static final int STATUS_READY = 1;
    /**
     * 正在拖拽中
     */
    public static final int STATUS_DRAGGING = 2;
    /**
     * 开始执行复原动画
     */
    public static final int STATUS_BEGIN_RESTORE = 3;
    /**
     * 复原动画执行中
     */
    public static final int STATUS_RESTORING = 4;
    /**
     * 复原动画执行完毕
     */
    public static final int STATUS_COMPLETE_RESTORE = 5;
    /**
     * 开始执行退出浏览动画
     */
    public static final int STATUS_BEGIN_EXIT = 6;
    /**
     * 退出浏览动画执行中
     */
    public static final int STATUS_EXITTING = 7;
    /**
     * 退出浏览动画执行完毕
     */
    public static final int STATUS_COMPLETE_EXIT = 8;
}
