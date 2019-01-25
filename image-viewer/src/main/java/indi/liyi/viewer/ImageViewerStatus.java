package indi.liyi.viewer;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 图片预览器的状态
 */
@IntDef({ImageViewerStatus.STATUS_READY_OPEN,
        ImageViewerStatus.STATUS_OPENING,
        ImageViewerStatus.STATUS_COMPLETE_OPEN,
        ImageViewerStatus.STATUS_WATCHING,
        ImageViewerStatus.STATUS_READY_CLOSE,
        ImageViewerStatus.STATUS_CLOSING,
        ImageViewerStatus.STATUS_COMPLETE_CLOSE,
        ImageViewerStatus.STATUS_SILENCE,
        ImageViewerStatus.STATUS_DRAGGING,
        ImageViewerStatus.STATUS_READY_REBACK,
        ImageViewerStatus.STATUS_REBACKING,
        ImageViewerStatus.STATUS_COMPLETE_REBACK})
@Retention(RetentionPolicy.SOURCE)
public @interface ImageViewerStatus {
    /**
     * 准备打开图片预览器
     */
    int STATUS_READY_OPEN = 1;
    /**
     * 图片预览器打开中
     */
    int STATUS_OPENING = 2;
    /**
     * 图片预览器打开完成
     */
    int STATUS_COMPLETE_OPEN = 3;
    /**
     * 图片正在被预览中
     */
    int STATUS_WATCHING = 4;
    /**
     * 准备关闭图片预览器
     */
    int STATUS_READY_CLOSE = 5;
    /**
     * 图片预览器关闭中
     */
    int STATUS_CLOSING = 6;
    /**
     * 关闭图片预览器完成
     */
    int STATUS_COMPLETE_CLOSE = 7;
    /**
     * 图片预览器处于未开启状态
     */
    int STATUS_SILENCE = 8;
    /**
     * 图片正在被拖拽中
     */
    int STATUS_DRAGGING = 9;
    /**
     * 准备将图片恢复原样
     */
    int STATUS_READY_REBACK = 10;
    /**
     * 图片正在复位中
     */
    int STATUS_REBACKING = 11;
    /**
     * 图片恢复原样完成
     */
    int STATUS_COMPLETE_REBACK = 12;
}
