package indi.liyi.viewer.listener;

import android.widget.ImageView;

/**
 * item 的单击事件
 */
public interface OnItemClickListener {

    /**
     * @param position
     * @param imageView
     * @return {@code true}: 消费了点击事件，后续方法不再执行 {@code false}: 没有消费点击事件，后续方法继续执行
     */
    boolean onItemClick(int position, ImageView imageView);
}
