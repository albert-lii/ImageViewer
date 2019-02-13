package indi.liyi.example.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import indi.liyi.viewer.glide.GlideApp;
import indi.liyi.viewer.glide.OnProgressListener;
import indi.liyi.viewer.glide.ProgressInterceptor;
import indi.liyi.viewer.scip.BaseImageLoader;
import indi.liyi.viewer.scip.ScaleImagePager;
import indi.liyi.viewer.scip.ViewData;

public class PhotoLoader extends BaseImageLoader {

    @Override
    public void displayImage(final int position, final Object src, final ScaleImagePager imagePager) {
        ProgressInterceptor.addListener(src, new OnProgressListener() {
            @Override
            public void onProgress(float progress, long totalSize) {
                PhotoLoader.this.onProgress(progress / 100, imagePager);
            }
        });
        GlideApp.with(imagePager.getContext())
                .load(((ViewData) src).getImageSrc())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        PhotoLoader.this.onStart(placeholder, imagePager);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        ProgressInterceptor.removeListener(src);
                        PhotoLoader.this.onFailure(errorDrawable, imagePager);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ProgressInterceptor.removeListener(src);
//                        imagePager.getViewData().setImageWidth(resource.getIntrinsicWidth());
//                        imagePager.getViewData().setImageHeight(resource.getIntrinsicHeight());
                        PhotoLoader.this.onSuccess(resource, imagePager);
                    }
                });
    }
}
