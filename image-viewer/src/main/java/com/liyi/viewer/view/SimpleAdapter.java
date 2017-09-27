package com.liyi.viewer.view;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;


public class SimpleAdapter extends PagerAdapter {
    private ArrayList<PhotoView> mViews;

    public SimpleAdapter(ArrayList<PhotoView> views) {
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return mViews != null ? mViews.size() : 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        if (mViews != null) {
            container.addView(mViews.get(position), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return mViews.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
