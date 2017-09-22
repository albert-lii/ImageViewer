package com.liyi.viewer;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class ViewerAdapter extends PagerAdapter {
    private List<View> mViews;

    public ViewerAdapter() {

    }

    public void setData(List<View> views) {
        this.mViews = views;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews != null) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mViews != null ? mViews.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
