package indi.liyi.viewer;

/**
 * 进退场过渡效果的回调监听
 */
public abstract class TransitionCallback {
    /**
     * 过渡效果开始执行
     */
    public void onTransitionStart() {

    }

    /**
     * 过渡效果执行中
     *
     * @param progress 执行进度,范围 0-1
     */
    public void onTransitionRunning(float progress) {

    }

    /**
     * 过渡效果执行完毕
     */
    public void onTransitionEnd() {

    }
}
