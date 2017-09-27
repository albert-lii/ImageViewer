package com.liyi.viewer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.liyi.viewer.data.ViewData;
import com.liyi.viewer.view.ImagePreviewActivity;

import java.util.ArrayList;

public class ImageViewer {
    private ArrayList<ViewData> mViewDatas;
    private ArrayList<Object> mImageDatas;
    private int mBeginIndex;
    private int mIndexPos;
    public static RequestOptions Options;

    private ImageViewer() {
        this.mBeginIndex = 0;
        this.mIndexPos = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
    }

    public static ImageViewer newInstance() {
        return new ImageViewer();
    }

    public ImageViewer beginIndex(@NonNull int index) {
        this.mBeginIndex = index;
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

    public void show(@NonNull Context context) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(ImageDefine.BEGIN_INDEX, mBeginIndex);
        intent.putExtra(ImageDefine.VIEW_ARRAY, mViewDatas);
        intent.putExtra(ImageDefine.IMAGE_ARRAY, mImageDatas);
        intent.putExtra(ImageDefine.INDEX_GRAVITY, mIndexPos);
        context.startActivity(intent);
    }
}
