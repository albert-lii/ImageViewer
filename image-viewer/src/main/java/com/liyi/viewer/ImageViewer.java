package com.liyi.viewer;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.liyi.viewer.data.PreviewData;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnImageLoadListener;
import com.liyi.viewer.view.ImagePreviewActivity;

import java.util.List;

public class ImageViewer {
    private PreviewData mPreviewData;

    private ImageViewer() {
        mPreviewData = new PreviewData();
    }

    public static ImageViewer getInstance() {
        return ImageViewerHolder.INSTANCE;
    }

    private static class ImageViewerHolder {
        private static ImageViewer INSTANCE = new ImageViewer();
    }

    /**
     * 设置点击的图片的位置
     *
     * @param position
     * @return
     */
    public ImageViewer clickPosition(int position) {
        mPreviewData.setClickPosition(position);
        return this;
    }

    /**
     * 设置被点击的图片，即过渡 view
     *
     * @param view
     */
    public ImageViewer transitionView(ImageView view) {
        mPreviewData.setTransitionView(view);
        return this;
    }

    /**
     * 设置图片资源
     *
     * @param list
     * @return
     */
    public ImageViewer imageData(List<Object> list) {
        mPreviewData.setImageList(list);
        return this;
    }

    /**
     * 设置 view 的数据
     *
     * @param list
     * @return
     */
    public ImageViewer viewData(List<ViewData> list) {
        mPreviewData.setViewDataList(list);
        return this;
    }

    /**
     * 设置图片加载监听
     *
     * @param listener
     * @return
     */
    public ImageViewer imageLoadListener(OnImageLoadListener listener) {
        mPreviewData.setImageLoadListener(listener);
        return this;
    }

    /**
     * 设置图片切换监听
     *
     * @param listener
     * @return
     */
    public ImageViewer imageChangedListener(OnImageChangedListener listener) {
        mPreviewData.setImageChangedListener(listener);
        return this;
    }

    /**
     * 设置是否执行预览的启动动画
     *
     * @param isDo
     * @return
     */
    public ImageViewer doEnterAnim(boolean isDo) {
        mPreviewData.setDoEnterAnim(isDo);
        return this;
    }

    /**
     * 设置是否执行预览的关闭动画
     *
     * @param isDo
     * @return
     */
    public ImageViewer doExitAnim(boolean isDo) {
        mPreviewData.setDoExitAnim(isDo);
        return this;
    }

    /**
     * 开启图片预览
     *
     * @param context
     */
    public void preview(@Nullable Context context) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        context.startActivity(intent);
    }

    public void close() {
        clear();
    }

    /**
     * 获取预览数据
     *
     * @return
     */
    public PreviewData getPreviewData() {
        return mPreviewData;
    }

    /**
     * 清除数据
     */
    private void clear() {
        if (mPreviewData != null) {
            mPreviewData.clear();
        }
        mPreviewData = null;
    }
}
