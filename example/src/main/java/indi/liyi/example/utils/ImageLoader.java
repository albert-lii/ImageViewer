package indi.liyi.example.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import indi.liyi.example.utils.glide.GlideApp;
import indi.liyi.example.utils.glide.OnProgressListener;
import indi.liyi.example.utils.glide.ProgressController;
import indi.liyi.viewer.imgpg.BaseImageLoader;
import indi.liyi.viewer.imgpg.ImagePager;


public class ImageLoader extends BaseImageLoader {

    @Override
    public void displayImage(final int position, final Object src, final ImagePager imagePager) {
        GlideApp.with(imagePager.getContext())
                .load(src)
                .into(new CustomViewTarget<ImageView, Drawable>(imagePager.getImageView()) {

                    @Override
                    protected void onResourceLoading(@Nullable Drawable placeholder) {
                        super.onResourceLoading(placeholder);
                        ImageLoader.this.onStart(placeholder, imagePager);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ImageLoader.this.onSuccess(resource, imagePager);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        ImageLoader.this.onSuccess(errorDrawable, imagePager);
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                    }
                });
//                .into(new SimpleTarget<Drawable>() {
//
//                    @Override
//                    public void onLoadStarted(@Nullable Drawable placeholder) {
//                        super.onLoadStarted(placeholder);
//
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        ImageLoader.this.onFailure(errorDrawable, imagePager);
//                    }
//
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        ImageLoader.this.onSuccess(resource, imagePager);
//                    }
//                });
    }

    @Override
    public void onStart(Object placeholder, final ImagePager imagePager) {
        super.onStart(placeholder, imagePager);
        ProgressController.registerListener(imagePager.getViewData().getImageSrc(), new OnProgressListener() {
            @Override
            public void onProgress(float progress, long totalSize) {
                ImageLoader.this.onProgress(progress / 100, imagePager);
            }
        });
    }

    @Override
    public void onSuccess(Object source, ImagePager imagePager) {
        super.onSuccess(source, imagePager);
        ProgressController.unregisterListener(imagePager.getViewData().getImageSrc());
    }

    @Override
    public void onFailure(Object error, ImagePager imagePager) {
        super.onFailure(error, imagePager);
        ProgressController.unregisterListener(imagePager.getViewData().getImageSrc());
    }
}
