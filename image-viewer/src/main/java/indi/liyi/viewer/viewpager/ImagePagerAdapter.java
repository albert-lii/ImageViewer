package indi.liyi.viewer.viewpager;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import indi.liyi.viewer.ImageDrawee;


public class ImagePagerAdapter extends PagerAdapter {
    private int mItemCount;

    public ImagePagerAdapter(int count) {
        this.mItemCount = count;
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return null;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object != null) {
            ((ImageDrawee) object).recycle();
            // 移除页面
            container.removeView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
