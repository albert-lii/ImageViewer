package com.liyi.viewer.listener;

import android.widget.ImageView;

/**
 * 图片浏览状态监听
 */
public interface OnWatchStatusListener {
    /**
     * 开始浏览
     */
    void onWatchStart(int state, int position, ImageView view);

    /**
     * 图片正在被拖拽
     */
    void onWatchDragging(ImageView view);

    /**
     * 图片拖拽后复位
     */
    void onWatchReset(int state, ImageView view);

    /**
     * 结束预览
     *
     * @param state
     */
    void onWatchEnd(int state);

    class State {
        // 开始浏览执行前
        public static final int STATE_START_BEFORE = 1;
        // 开始浏览执行完成后
        public static final int STATE_START_AFTER = 2;
        // 正常退出浏览执行前
        public static final int STATE_END_BEFORE = 3;
        // 正常退出浏览执行结束后
        public static final int STATE_END_AFTER = 4;
        // 拖拽退出浏览执行前
        public static final int STATE_END_DRAG_BEFORE = 5;
        // 拖拽退出浏览执行结束后
        public static final int STATE_END_DRAG_AFTER = 6;
        // 图片复位执行前
        public static final int STATE_RESET_BEFORE = 7;
        // 图片复位执行结束后
        public static final int STATE_RESET_AFTER = 8;
    }
}
