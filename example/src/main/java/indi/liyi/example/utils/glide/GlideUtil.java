package indi.liyi.example.utils.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.liyi.example.R;

public class GlideUtil {
    public static void loadImage(Context context, final Object src, final ImageView view) {
        GlideApp.with(context)
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(view);
    }

    public static void loadImage(Context context, final Object src, final ImageView view, RequestListener<Drawable> listener) {
        GlideApp.with(context)
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .listener(listener)
                .into(view);
    }

    public static void loadImage(Context context, final Object src, final SimpleTarget<Drawable> target) {
        GlideApp.with(context)
                .load(src)
                .thumbnail(0.1f)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(target);
    }
}
