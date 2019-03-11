package indi.liyi.viewer;

import android.os.Handler;
import android.widget.ImageView;

import indi.liyi.viewer.listener.OnDragStatusListener;
import indi.liyi.viewer.listener.OnBrowseStatusListener;
import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongPressListener;


public class EventHandler extends Handler {
    // 取消浏览
    public static final int EVENT_VIEW_CANCEL = 1;

    // 是否有动画正在执行
    private boolean hasAnimRunning;
    private OnItemClickListener mItemClickListener;
    private OnItemLongPressListener mItemLongPressListener;
    private OnItemChangedListener mItemChangedListener;
    private OnDragStatusListener mDragStatusListener;
    private OnBrowseStatusListener mBrowseStatusListener;

    public EventHandler() {

    }

    public void setAnimRunning(boolean running) {
        this.hasAnimRunning = running;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongPressListener(OnItemLongPressListener itemLongPressListener) {
        this.mItemLongPressListener = itemLongPressListener;
    }

    public void setOnItemLongPressListener(OnItemChangedListener itemChangedListeneri) {
        this.mItemChangedListener = itemChangedListeneri;
    }

    public void setOnDragStatusListener(OnDragStatusListener dragStatusListener) {
        this.mDragStatusListener = dragStatusListener;
    }

    public void setOnBrowseStatusListener(OnBrowseStatusListener browseStatusListener) {
        this.mBrowseStatusListener = browseStatusListener;
    }


    /**
     * 添加点击事件
     */
    public void joinClick(final int position, final ImageView imageView) {
        if (!hasAnimRunning && mItemClickListener != null) {
            final boolean result = mItemClickListener.onItemClick(position, imageView);
            // 判断是否消费了单击事件，如果消费了，则单击事件的后续方法不执行
            if (result) {
                return;
            }
        }
        // 发送取消浏览信号
        sendEmptyMessage(EVENT_VIEW_CANCEL);
    }

    /**
     * 添加长按事件
     */
    public boolean joinLongPress(final int position, final ImageView imageView) {
        if (!hasAnimRunning && mItemLongPressListener != null) {
            return mItemLongPressListener.onItemLongPress(position, imageView);
        }
        return false;
    }

    /**
     * 记录当前 item 切换事件
     */
    public void noteItemChanged(int position, ImageDrawee drawee) {
        if (mItemChangedListener != null) {
            mItemChangedListener.onItemChanged(position, drawee);
        }
    }

    /**
     * 记录当前的拖拽状态
     */
    public void noteDragStatus(int status) {
        if (mDragStatusListener != null) {
            mDragStatusListener.onDragStatusChanged(status);
        }
    }

    /**
     * 记录当前 ImageViewer 的状态
     */
    public void noteBrowseStatus(int status) {
        if (mBrowseStatusListener != null) {
            mBrowseStatusListener.onBrowseStatus(status);
        }
    }

    public void clear() {
        mItemClickListener = null;
        mItemLongPressListener = null;
        mItemChangedListener = null;
        mDragStatusListener = null;
        mBrowseStatusListener = null;
    }
}
