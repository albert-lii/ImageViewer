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

    /**
     * 状态类
     */
    class State {
        /**
         * 准备开启浏览
         */
        public static final int STATE_READY_WATCH = 1;
        /**
         * 完成开启浏览
         */
        public static final int STATE_COMPLETE_WATCH = 2;
        /**
         * 准备关闭浏览
         */
        public static final int STATE_READY_CLOSE = 3;
        /**
         * 完成关闭浏览
         */
        public static final int STATE_COMPLETE_CLOSE = 4;
        /**
         * 准备图片复位
         */
        public static final int STATE_READY_RESET = 5;
        /**
         * 完成图片复位
         */
        public static final int STATE_COMPLETE_RESET = 6;
    }
}
