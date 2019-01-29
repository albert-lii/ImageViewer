package indi.liyi.example.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.liyi.example.R;
import indi.liyi.viewer.glide.GlideApp;

public class GlideUtil {
    public static void loadImage(Context context, final Object src, final ImageView view) {
        GlideApp.with(context)
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadImage(Context context, final Object src, final SimpleTarget<Drawable> target) {
        GlideApp.with(context)
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }
}
