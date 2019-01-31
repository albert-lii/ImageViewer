package indi.liyi.viewer.scip.dragger;

/**
 * 监听图片被拖拽时的状态
 */
public interface OnDragStatusListener {

    /**
     * @param status {@link DragStatus}
     */
    void onDragStatusChanged(int status);
}
