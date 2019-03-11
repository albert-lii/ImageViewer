package indi.liyi.example.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import indi.liyi.example.R;
import indi.liyi.example.utils.glide.GlideApp;
import indi.liyi.viewer.ImageLoader;

public class PhotoLoader extends ImageLoader {


    @Override
    public void displayImage(final Object src, ImageView imageView, final LoadCallback callback) {
//        GlideApp.with(imageView.getContext())
//                .load(src)
//                .into(imageView);
        GlideApp.with(imageView.getContext())
                .load(src)
//                .placeholder(R.drawable.img_placeholder)
//                .error(R.drawable.img_placeholder)
                .into(new CustomViewTarget<ImageView, Drawable>(imageView) {

                    @Override
                    protected void onResourceLoading(@Nullable Drawable placeholder) {
                        super.onResourceLoading(placeholder);
                        callback.onLoadStarted(placeholder);
//                        ProgressController.registerListener(src, new OnProgressListener() {
//                            @Override
//                            public void onProgress(float progress, long totalSize) {
//                                indi.liyi.example.utils.ImageLoader.this.onProgress(progress / 100, imagePager);
//                            }
//                        });
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        callback.onLoadSucceed(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        callback.onLoadFailed(errorDrawable);
//                        ProgressController.unregisterListener(src);
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
