package com.liyi.viewer.data;

import android.widget.ImageView;

import com.liyi.viewer.listener.OnImageChangedListener;
import com.liyi.viewer.listener.OnImageLoadListener;
import com.liyi.viewer.listener.OnPreviewCloseListener;

import java.util.List;

/**
 * 图片预览数据
 */
public class PreviewData {
    // 被点击的图片的位置
    private int clickPosition;
    // 过渡的 view
    private ImageView transitionView;
    // 图片资源列表
    private List<Object> imageList;
    // view 的信息列表
    private List<ViewData> viewDataList;
    // 图片加载事件
    private OnImageLoadListener imageLoadListener;
    // 图片切换事件
    private OnImageChangedListener imageChangedListener;

    // 是否执行启动动画，
    private boolean doEnterAnim;
    // 是否执行关闭动画，默认开启
    private boolean doExitAnim;
    private OnPreviewCloseListener previewCloseListener;

    public PreviewData() {
        // 默认为 0
        this.clickPosition = 0;
        // 默认开启
        this.doEnterAnim = true;
        // 默认开启
        this.doExitAnim = true;
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        transitionView = null;
        if (imageList != null && imageList.size() > 0) {
            imageList.clear();
        }
        if (viewDataList != null && viewDataList.size() > 0) {
            viewDataList.clear();
        }
        imageLoadListener = null;
        imageChangedListener = null;
    }


    public int getClickPosition() {
        return clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    public ImageView getTransitionView() {
        return transitionView;
    }

    public void setTransitionView(ImageView transitionView) {
        this.transitionView = transitionView;
    }

    public List<Object> getImageList() {
        return imageList;
    }

    public void setImageList(List<Object> imageList) {
        this.imageList = imageList;
    }

    public List<ViewData> getViewDataList() {
        return viewDataList;
    }

    public void setViewDataList(List<ViewData> viewDataList) {
        this.viewDataList = viewDataList;
    }

    public OnImageLoadListener getImageLoadListener() {
        return imageLoadListener;
    }


    public void setImageLoadListener(OnImageLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
    }

    public OnImageChangedListener getImageChangedListener() {
        return imageChangedListener;
    }

    public void setImageChangedListener(OnImageChangedListener imageChangedListener) {
        this.imageChangedListener = imageChangedListener;
    }

    public boolean isDoEnterAnim() {
        return doEnterAnim;
    }

    public void setDoEnterAnim(boolean doEnterAnim) {
        this.doEnterAnim = doEnterAnim;
    }

    public boolean isDoExitAnim() {
        return doExitAnim;
    }

    public void setDoExitAnim(boolean doExitAnim) {
        this.doExitAnim = doExitAnim;
    }

    public OnPreviewCloseListener getPreviewCloseListener() {
        return previewCloseListener;
    }

    public void setPreviewCloseListener(OnPreviewCloseListener previewCloseListener) {
        this.previewCloseListener = previewCloseListener;
    }
}
