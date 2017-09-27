package com.liyi.viewer.view;


import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.liyi.viewer.ImageDefine;
import com.liyi.viewer.ImageViewer;
import com.liyi.viewer.R;
import com.liyi.viewer.data.ViewData;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImagePreviewActivity extends Activity implements IImagePreview {
    private View v_bg;
    private ViewPager viewpager;
    private ImageView iv_show;
    private TextView tv_index;

    private ArrayList<PhotoView> mPhotoViewList;
    private ArrayList<ViewData> mViewDataList;
    private ArrayList<Object> mImageList;
    private ViewData mCurViewData;

    private Point mScreenSize;
    private int mBeginIndex;
    private int mIndexPos;
    // Determine if the first picture you need to display is loaded
    private boolean isBeginLoaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initUI();
        addListener();
        handleIntent(getIntent());
    }

    @Override
    public void initUI() {
        v_bg = findViewById(R.id.v_preview_bg);
        viewpager = (ViewPager) findViewById(R.id.vp_preview);
        iv_show = (ImageView) findViewById(R.id.iv_preview_show);
        tv_index = (TextView) findViewById(R.id.tv_preview_index);

        v_bg.setAlpha(0f);
    }

    @Override
    public void addListener() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurViewData = mViewDataList.get(position);
                // Prevent click event failure
                PhotoView photoView = mPhotoViewList.get(position);
                photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        restoreImage();
                    }
                });
                tv_index.setText((position + 1) + "/" + mImageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        mViewDataList = (ArrayList<ViewData>) intent.getSerializableExtra(ImageDefine.VIEW_ARRAY);
        mImageList = (ArrayList<Object>) intent.getSerializableExtra(ImageDefine.IMAGE_ARRAY);
        mBeginIndex = intent.getIntExtra(ImageDefine.BEGIN_INDEX, 0);
        mIndexPos = intent.getIntExtra(ImageDefine.INDEX_GRAVITY, Gravity.TOP);

        mScreenSize = getScreenSize(this);
        mCurViewData = mViewDataList.get(mBeginIndex);
        isBeginLoaded = false;

        if (mPhotoViewList == null) {
            mPhotoViewList = new ArrayList<PhotoView>();
        }
        mPhotoViewList.clear();
        for (int i = 0; i < mImageList.size(); i++) {
            final PhotoView photoView = new PhotoView(this);
            loadImage(i, mImageList.get(i), photoView, false);
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    restoreImage();
                }
            });
            mPhotoViewList.add(photoView);
        }
        viewpager.setAdapter(new SimpleAdapter(mPhotoViewList));
        viewpager.setCurrentItem(mBeginIndex);
        viewpager.setVisibility(View.GONE);

        iv_show.setLayoutParams(new FrameLayout.LayoutParams((int) mCurViewData.width, (int) mCurViewData.height));
        iv_show.setX(mCurViewData.x);
        iv_show.setY(mCurViewData.y);
        iv_show.setVisibility(View.GONE);

        tv_index.setText((mBeginIndex + 1) + "/" + mImageList.size());
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) tv_index.getLayoutParams();
        lp.gravity = mIndexPos;
        tv_index.setLayoutParams(lp);

        if (!isBeginLoaded) {
            loadImage(mBeginIndex, mImageList.get(mBeginIndex), iv_show, true);
        } else {
            iv_show.setImageDrawable(mPhotoViewList.get(mBeginIndex).getDrawable());
            fullScreen();
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                fullScreen();
//            }
//        }, 200);
    }

    @Override
    public void fullScreen() {
        Drawable d = iv_show.getDrawable();
        // The width and height of the original image
        float ori_w = d.getIntrinsicWidth();
        float ori_h = d.getIntrinsicHeight();
        // Scale of the original image
        float scale = Math.min((mScreenSize.x / ori_w), (mScreenSize.y / ori_h));
        // The width and height of the ImageView now
        final float cur_w = mCurViewData.width;
        final float cur_h = mCurViewData.height;
        // The width and height of the zoomed ImageView
        final float img_w = ori_w * scale;
        final float img_h = ori_h * scale;
        // Initial coordinates of the ImageView
        final float from_x = mCurViewData.x;
        final float from_y = mCurViewData.y;
        // Destination coordinates of ImageView
        final float to_x = (mScreenSize.x - img_w) / 2;
        final float to_y = (mScreenSize.y - img_h) / 2;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float fraction = currentValue / 100f;
                float width = evaluator.evaluate(fraction, cur_w, img_w);
                float height = evaluator.evaluate(fraction, cur_h, img_h);
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv_show.getLayoutParams();
                layoutParams.width = (int) width;
                layoutParams.height = (int) height;
                iv_show.setLayoutParams(layoutParams);
                iv_show.setX(x);
                iv_show.setY(y);
                v_bg.setAlpha(fraction);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                iv_show.setScaleType(ImageView.ScaleType.FIT_XY);
                iv_show.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewpager.setVisibility(View.VISIBLE);
                tv_index.setVisibility(View.VISIBLE);
                iv_show.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(200);
        animator.start();
    }

    @Override
    public void restoreImage() {
        PhotoView photoView = mPhotoViewList.get(viewpager.getCurrentItem());
        Drawable d = photoView.getDrawable();
        // The width and height of the original image
        float ori_w = d.getIntrinsicWidth();
        float ori_h = d.getIntrinsicHeight();
        // Scale of the original image
        float scale = Math.min((mScreenSize.x / ori_w), (mScreenSize.y / ori_h));
        // The width and height of the ImageView now
        final float cur_w = ori_w * scale;
        final float cur_h = ori_h * scale;
        // The width and height of the zoomed ImageView
        final float img_w = mCurViewData.width;
        final float img_h = mCurViewData.height;
        // Initial coordinates of the ImageView
        final float from_x = (mScreenSize.x - cur_w) / 2;
        final float from_y = (mScreenSize.y - cur_h) / 2;
        // Destination coordinates of ImageView
        final float to_x = mCurViewData.x;
        final float to_y = mCurViewData.y;
        iv_show.setImageDrawable(d);
        viewpager.setVisibility(View.GONE);
        iv_show.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator evaluator = new FloatEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float fraction = currentValue / 100f;
                float width = evaluator.evaluate(fraction, cur_w, img_w);
                float height = evaluator.evaluate(fraction, cur_h, img_h);
                float x = evaluator.evaluate(fraction, from_x, to_x);
                float y = evaluator.evaluate(fraction, from_y, to_y);

                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) iv_show.getLayoutParams();
                lp.width = (int) width;
                lp.height = (int) height;
                iv_show.setLayoutParams(lp);
                iv_show.setX(x);
                iv_show.setY(y);
                v_bg.setAlpha(1 - fraction);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv_index.setVisibility(View.GONE);
                iv_show.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new Point(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    private void loadImage(final int index, Object src, final ImageView view, final boolean isShow) {
        final RequestBuilder builder = Glide.with(this).asBitmap().load(src);
        if (ImageViewer.getOptions() != null) {
            builder.apply(ImageViewer.getOptions());
        }
        builder.into(new ImageViewTarget<Bitmap>(view) {
            @Override
            protected void setResource(@Nullable Bitmap resource) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (index == mBeginIndex) {
                    if (isShow) {
                        if (!isBeginLoaded) {
                            Glide.with(ImagePreviewActivity.this).clear(mPhotoViewList.get(index));
                            mPhotoViewList.get(index).setImageDrawable(errorDrawable);
                        }
                        fullScreen();
                    } else {
                        isBeginLoaded = true;
                    }
                }
                Toast.makeText(ImagePreviewActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResourceReady(Bitmap resource, @Nullable Transition transition) {
                super.onResourceReady(resource, transition);
                view.setImageBitmap(resource);
                if (index == mBeginIndex) {
                    if (isShow) {
                        if (!isBeginLoaded) {
                            Glide.with(ImagePreviewActivity.this).clear(mPhotoViewList.get(index));
                            mPhotoViewList.get(index).setImageBitmap(resource);
                        }
                        fullScreen();
                    } else {
                        isBeginLoaded = true;
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            iv_show.setVisibility(View.VISIBLE);
            viewpager.setVisibility(View.GONE);
            restoreImage();
        }
        return true;
    }
}
