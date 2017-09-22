package com.liyi.viewer;


import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A control for viewing images
 */
public class ImageViewer extends DialogFragment implements DialogInterface.OnKeyListener {
    private View contentView;
    private View v_bg;
    private PhotoView photoVi_current;
    private ViewPager viewpager;
    private TextView tv_index;

    private ViewerAdapter mAdapter;
    private ImageLoader mImageLoader;
    private List<View> mViews;
    private ViewGroup.LayoutParams mCurrentParams;

    // The location of the image on the screen
    private List<Rect> mLocations;
    // The sequence number of the first picture shown
    private int mBeginIndex;
    private Point mScreenPoint;

    public static ImageViewer newInstance() {
        return new ImageViewer();
    }

    public ImageViewer() {
        this.mViews = new ArrayList<View>();
        this.mLocations = new ArrayList<Rect>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.style_dlg_fullscreen);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dlgfmt_image_viewer, null, false);
        dialog.setContentView(contentView);
        initUI();
        addListeners();
        fullScreen();
        return dialog;
    }

    private <T> T $(int resId, View parent) {
        return (T) parent.findViewById(resId);
    }

    private void initUI() {
        v_bg = $(R.id.v_imgViewer_bg, contentView);
        viewpager = $(R.id.vp_imgViewer, contentView);
        tv_index = $(R.id.tv_imgViewer_index, contentView);
        if (mViews != null && mViews.size() > 0) {
            mAdapter = new ViewerAdapter();
            mAdapter.setData(mViews);
            viewpager.setAdapter(mAdapter);
            viewpager.setCurrentItem(mBeginIndex);
        }
    }

    private void addListeners() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mViews != null) {
                    photoVi_current = $(R.id.photoVi_item_imgViewer, mViews.get(position));
                    // Sometimes the click event of the photoview will be invalidated, and this is to
                    // prevent the click event of the photoview from invalidation
                    photoVi_current.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float x, float y) {
                            close();
                        }
                    });
                    if (tv_index.getVisibility() == View.VISIBLE) {
                        tv_index.setText((position + 1) + "/" + mViews.size());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public ImageViewer setLocations(List<Rect> rects) {
        this.mLocations = rects;
        return this;
    }

    /**
     * Set up the image loader
     *
     * @param imageLoader
     * @return
     */
    public ImageViewer setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
        return this;
    }

    /**
     * Set the source of the image
     */
    public ImageViewer setBitmaps(int index, List<Bitmap> bitmaps, Context context) {
        mBeginIndex = index;
        if (bitmaps == null || bitmaps.size() == 0) {
            return this;
        }
        mScreenPoint = getScreenSize(context);
        mViews.clear();
        for (int i = 0; i < bitmaps.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_image_viewer, null);
            PhotoView photoView = $(R.id.photoVi_item_imgViewer, view);
            if (mImageLoader != null) {
                mImageLoader.displayImage(i, bitmaps.get(i), photoView);
            } else {
                photoView.setImageBitmap(bitmaps.get(i));
            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    close();
                }
            });
            if (i == 0) {
                photoVi_current = photoView;
            }
            mViews.add(view);
        }
        return this;
    }

    /**
     * Set the source of the image
     *
     * @param index
     * @param resources
     * @param context
     * @return
     */
    public ImageViewer setResources(int index, List<Integer> resources, Context context) {
        mBeginIndex = index;
        if (resources == null || resources.size() == 0) {
            return this;
        }
        mScreenPoint = getScreenSize(context);
        mViews.clear();
        for (int i = 0; i < resources.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image_viewer, null);
            PhotoView photoView = $(R.id.photoVi_item_imgViewer, view);
            if (mImageLoader != null) {
                mImageLoader.displayImage(i, resources.get(i), photoView);
            } else {
                photoView.setImageResource(resources.get(i));
            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    close();
                }
            });
            if (i == mBeginIndex) {
                photoVi_current = photoView;
            }
            mViews.add(view);
        }
        return this;
    }

    /**
     * Set the source of the image
     *
     * @param index
     * @param urls
     * @param context
     * @return
     */
    public ImageViewer setImageUrls(int index, List<String> urls, Context context) {
        mBeginIndex = index;
        if (urls == null || urls.size() == 0) {
            return this;
        }
        mScreenPoint = getScreenSize(context);
        mViews.clear();
        for (int i = 0; i < urls.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_image_viewer, null);
            PhotoView photoView = $(R.id.photoVi_item_imgViewer, view);
            if (mImageLoader != null) {
                mImageLoader.displayImage(i, urls.get(i), photoView);
            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    close();
                }
            });
            if (i == 0) {
                photoVi_current = photoView;
            }
            mViews.add(view);
        }
        return this;
    }

    /**
     * Zoom in to full-screen animation
     */
    private void fullScreen() {
        if (mLocations != null && mLocations.size() > 0 && mBeginIndex < mLocations.size()) {
            final Rect rect = mLocations.get(mBeginIndex);
            final int width = rect.right - rect.left;
            final int height = rect.bottom - rect.top;
            ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction;
                    if (Build.VERSION.SDK_INT >= 14) {
                        fraction = animation.getAnimatedFraction();
                    } else {
                        float currentValue = (float) animation.getAnimatedValue();
                        fraction = currentValue / 100f;
                    }
                    // Make the animation look more fluid
                    if (fraction < 0.2f) {
                        photoVi_current.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        photoVi_current.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        photoVi_current.setAdjustViewBounds(true);
                    }
                    // In animation execution, the transition values of the X coordinates of photoview from rect.left to 0
                    float x = calculateValue(fraction, rect.left, 0);
                    // In animation execution, the coordinates of the photoview from rect.top to 0
                    float y = calculateValue(fraction, rect.top, 0);
                    mCurrentParams = photoVi_current.getLayoutParams();
                    // From the current thumbnail width to full screen
                    mCurrentParams.width = calculateValue(fraction, width, mScreenPoint.x);
                    // From the current thumbnail height to full screen
                    mCurrentParams.height = calculateValue(fraction, height, mScreenPoint.y);

                    photoVi_current.setX(x);
                    photoVi_current.setY(y);
                    photoVi_current.setLayoutParams(mCurrentParams);
                    v_bg.setAlpha(fraction);
                    if (fraction == 1) {
                        if (mViews.size() > 1) {
                            tv_index.setVisibility(View.VISIBLE);
                            tv_index.setText((mBeginIndex + 1) + "/" + mViews.size());
                        } else {
                            tv_index.setVisibility(View.GONE);
                        }
                    }
                }
            });
            animator.setDuration(240);
            animator.start();
        }
    }

    /**
     * Close the dialogfragment animation
     */
    public void close() {
        if (mLocations != null && mLocations.size() > 0 && viewpager.getCurrentItem() < mLocations.size()) {
            final Rect rect = mLocations.get(viewpager.getCurrentItem());
            final int width = rect.right - rect.left;
            final int height = rect.bottom - rect.top;
            ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction;
                    if (Build.VERSION.SDK_INT >= 14) {
                        fraction = animation.getAnimatedFraction();
                    } else {
                        float currentValue = (float) animation.getAnimatedValue();
                        fraction = currentValue / 100f;
                    }
                    float x = calculateValue(fraction, 0, rect.left);
                    float y = calculateValue(fraction, 0, rect.top);
                    mCurrentParams = photoVi_current.getLayoutParams();
                    mCurrentParams.width = calculateValue(fraction, mScreenPoint.x, width);
                    mCurrentParams.height = calculateValue(fraction, mScreenPoint.y, height);

                    photoVi_current.setX(x);
                    photoVi_current.setY(y);
                    photoVi_current.setLayoutParams(mCurrentParams);
                    v_bg.setAlpha(1 - fraction);
                    // Make the animation look more fluid
                    if (fraction > 0.8f && fraction < 1f) {
                        photoVi_current.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    if (fraction == 1) {
                        dismiss();
                    }
                }
            });
            animator.setDuration(240);
            animator.start();
            tv_index.setVisibility(View.GONE);
        } else {
            dismiss();
        }
    }

    /**
     * Compute a transition value, which can be used directly with an IntEvaluator object if the SDK version is version 14 or above
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    private int calculateValue(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Get screen size
     *
     * @param context
     * @return
     */
    private Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new Point(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    public int getBeginIndex() {
        return mBeginIndex;
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
            return true;
        } else {
            // Note here that you need to spread the event when it is not a return key,
            // otherwise you cannot handle other click events
            return false;
        }
    }
}
