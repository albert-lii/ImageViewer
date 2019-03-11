package indi.liyi.viewer.otherui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * 索引视图基类
 */
public abstract class IndexUI {
    private View indexView;

    /**
     * 创建索引视图
     */
    public abstract View createView(Context context);

    /**
     * 初始设置
     */
    public abstract void init(int position, int length);

    /**
     * 处理 item 切换事件
     */
    public abstract void handleItemChanged(int position, int length);

    public void setup(@NonNull ViewGroup parent, int startPosition, int length) {
        if (indexView == null) {
            indexView = createView(parent.getContext());
        }
        if (indexView.getParent() == null) {
            parent.addView(indexView);
        }
        init(startPosition, length);
        show();
    }

    /**
     * 显示索引
     */
    public void show() {
        if (indexView != null) {
            indexView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏索引
     */
    public void hide() {
        if (indexView != null) {
            indexView.setVisibility(View.GONE);
        }
    }

    public View getIndexView() {
        return indexView;
    }
}
