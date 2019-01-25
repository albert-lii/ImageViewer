package indi.liyi.viewer;

import android.view.View;

import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.sipr.ScaleImagePager;


public class ImageViewerGestureListener implements View.OnClickListener, View.OnLongClickListener {
    private ImageViewerAttacher attacher;
    private ScaleImagePager item;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public ImageViewerGestureListener(ImageViewerAttacher attacher, ScaleImagePager item,
                                      OnItemClickListener itemClickListener,
                                      OnItemLongClickListener itemLongClickListener) {
        this.attacher = attacher;
        this.item = item;
        this.mItemClickListener = itemClickListener;
        this.mItemLongClickListener = itemLongClickListener;
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
            attacher.close();
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
}
