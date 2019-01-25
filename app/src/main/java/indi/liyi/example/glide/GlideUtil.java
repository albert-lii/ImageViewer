package indi.liyi.example.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;

import indi.liyi.example.R;

public class GlideUtil {

    public static void loadImage(Context context, Object imgUrl, SimpleTarget<Drawable> target) {
        GlideApp.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_placeholder)
                .into(target);
    }

    public static void loadImage(Context context, Object imgUrl, ImageView imageView) {
        GlideApp.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.img_viewer_placeholder)
                .error(R.drawable.img_viewer_placeholder)
                .into(imageView);
    }
}
