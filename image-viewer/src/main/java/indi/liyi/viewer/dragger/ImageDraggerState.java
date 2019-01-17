package indi.liyi.viewer.dragger;


public final class ImageDraggerState {
    /**
     * 准备拖拽 imageView
     */
    public static final int DRAG_STATE_READY = 1;
    /**
     * imageView 正在被拖拽中
     */
    public static final int DRAG_STATE_DRAGGING = 2;
    /**
     * imageView 开始复位
     */
    public static final int DRAG_STATE_BEGIN_REBACK = 3;
    /**
     * imageView 正在复位中
     */
    public static final int DRAG_STATE_REBACKING = 4;
    /**
     * imageView 复位完毕
     */
    public static final int DRAG_STATE_END_REBACK = 5;
    /**
     * imageView 开始退出
     */
    public static final int DRAG_STATE_BEGIN_EXIT = 6;
    /**
     * imageView 正在退出中
     */
    public static final int DRAG_STATE_EXITTING = 7;
    /**
     * imageView 退出完毕
     */
    public static final int DRAG_STATE_END_EXIT = 8;
}
