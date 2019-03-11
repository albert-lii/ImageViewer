package indi.liyi.viewer.listener;

/**
 * 监听图片被拖拽时的状态
 */
public interface OnDragStatusListener {

    /**
     * @param status {@link indi.liyi.viewer.dragger.DragStatus}
     */
    void onDragStatusChanged(int status);
}
