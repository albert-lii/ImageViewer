package indi.liyi.viewer.otherui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 加载进度视图基类
 */
public abstract class ProgressUI {
    private View progressView;

    /**
     * 创建加载进度视图
     */
    public abstract View createView(Context context);

    /**
     * 处理进度
     *
     * @param progress 范围: 0-1
     */
    public abstract void handleProgress(float progress);

    public void init(ViewGroup parent) {
        if (progressView == null) {
            progressView = createView(parent.getContext());
        }
        if (progressView.getParent() == null) {
            parent.addView(progressView);
        }
    }

    public void start() {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void stop() {
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    public View getProgressView() {
        return progressView;
    }
}
