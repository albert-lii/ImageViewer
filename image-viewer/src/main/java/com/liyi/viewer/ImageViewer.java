package com.liyi.viewer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.view.ImagePreviewActivity;

import java.util.ArrayList;

public class ImageViewer {
    private ArrayList<ViewData> mViewDatas;
    private ArrayList<Object> mImageDatas;
    private int mBeginIndex;
    private int mIndexPos;
    private boolean isShowProgress;

    private static RequestOptions mOptions;
    private static Bitmap mBeginImage;
    private static Drawable mProgressDrawable;

    private ImageViewer() {
        this.mBeginIndex = 0;
        this.mIndexPos = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.isShowProgress = true;
        this.mOptions = new RequestOptions()
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_error);
        this.mBeginImage = null;
    }

    public static ImageViewer newInstance() {
        return new ImageViewer();
    }

    public ImageViewer beginIndex(@NonNull int index) {
        this.mBeginIndex = index;
        return this;
    }

    /**
     * Bind the first ImageView to display, get the image from this ImageView, and display it
     * <p>
     * The main reason is to show that the animation has been executed and still doesn't capture the image
     *
     * @param view
     * @return
     */
    public ImageViewer beginView(ImageView view) {
        view.buildDrawingCache(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        view.setDrawingCacheEnabled(false);
        this.mBeginImage = bitmap;
        return this;
    }

    public ImageViewer viewData(@NonNull ArrayList<ViewData> viewDatas) {
        this.mViewDatas = viewDatas;
        return this;
    }

    public ImageViewer imageData(@NonNull ArrayList<Object> imageData) {
        this.mImageDatas = imageData;
        return this;
    }

    public ImageViewer indexPos(int pos) {
        this.mIndexPos = pos;
        return this;
    }

    public ImageViewer options(RequestOptions options) {
        this.mOptions = options;
        return this;
    }

    public ImageViewer showProgress(boolean isShow) {
        this.isShowProgress = isShow;
        return this;
    }

    public ImageViewer progressDrawable(@NonNull Drawable drawable) {
        this.mProgressDrawable = drawable;
        return this;
    }

    public void show(@NonNull Context context) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(ImageDefine.BEGIN_INDEX, mBeginIndex);
        intent.putExtra(ImageDefine.VIEW_ARRAY, mViewDatas);
        intent.putExtra(ImageDefine.IMAGE_ARRAY, mImageDatas);
        intent.putExtra(ImageDefine.INDEX_GRAVITY, mIndexPos);
        intent.putExtra(ImageDefine.SHOW_PROGRESS, isShowProgress);
        context.startActivity(intent);
    }

    public static RequestOptions getOptions() {
        return mOptions;
    }

    public static Bitmap getBeginImage() {
        return mBeginImage;
    }

    public static void setBeginImage(Bitmap beginImage) {
        mBeginImage = beginImage;
    }

    public static Drawable getProgressDrawable() {
        return mProgressDrawable;
    }
}
