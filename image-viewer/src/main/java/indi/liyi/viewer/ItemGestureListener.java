package indi.liyi.viewer;

import android.view.View;

import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.imgpg.dragger.OnDragStatusListener;


/**
 * item 的操作手势监听类
 */
public class ItemGestureListener implements View.OnClickListener, View.OnLongClickListener, OnDragStatusListener {
    private ViewerWrapper wrapper;
    private ImagePager item;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private OnDragStatusListener mItemDragStatusListener;

    public ItemGestureListener(ViewerWrapper wrapper, ImagePager item,
                               OnItemClickListener itemClickListener,
                               OnItemLongClickListener itemLongClickListener,
                               OnDragStatusListener itemDragStatusListener) {
        this.wrapper = wrapper;
        this.item = item;
        this.mItemClickListener = itemClickListener;
        this.mItemLongClickListener = itemLongClickListener;
        this.mItemDragStatusListener = itemDragStatusListener;
    }

    @Override
    public void onClick(View v) {
        if (!item.isAnimRunning() && !item.isDragged()) {
            if (mItemClickListener != null) {
                final boolean result = mItemClickListener.onItemClick(item.getPosition(), item.getImageView());
                // 判断是否消费了单击事件，如果消费了，则单击事件的后续方法不执行
                if (result) {
                    return;
                }
            }
            wrapper.close();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!item.isAnimRunning() && !item.isDragged()
                && mItemLongClickListener != null) {
            mItemLongClickListener.onItemLongClick(item.getPosition(), item.getImageView());
            return true;
        }
        return false;
    }

    @Override
    public void onDragStatusChanged(int status) {
        if (mItemDragStatusListener != null) {
            mItemDragStatusListener.onDragStatusChanged(status);
        }
    }
}
