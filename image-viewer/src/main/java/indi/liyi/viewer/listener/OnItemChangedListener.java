package indi.liyi.viewer.listener;

import indi.liyi.viewer.imgpg.ImagePager;

/**
 * item 的切换事件
 */
public interface OnItemChangedListener {

    void onItemChanged(int position, ImagePager view);
}
