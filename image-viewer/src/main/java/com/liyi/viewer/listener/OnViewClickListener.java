package com.liyi.viewer.listener;

import android.view.View;

/**
 * photoView 的点击事件
 */
public interface OnViewClickListener {

    /**
     * @param position
     * @param view
     * @param x
     * @param y
     * @return {@code true}: 消费点击事件，后续方法不执行  {@code false}: 不消费点击事件，后续方法继续执行
     */
    boolean onViewClick(int position, View view, float x, float y);
}
