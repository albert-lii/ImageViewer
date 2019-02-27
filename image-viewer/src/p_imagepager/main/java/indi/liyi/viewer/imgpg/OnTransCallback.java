package indi.liyi.viewer.imgpg;

/**
 * 进退场过渡效果的回调监听
 */
public interface OnTransCallback {
    /**
     * 过渡效果开始执行
     */
    void onStart();

    /**
     * 过渡效果执行中
     *
     * @param progress 执行进度,范围 0-1
     */
    void onRunning(float progress);

    /**
     * 过渡效果执行完毕
     */
    void onEnd();
}
