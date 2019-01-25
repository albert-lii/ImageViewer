package indi.liyi.example.activity;


import android.graphics.Point;
import android.os.Bundle;
import android.widget.ImageView;

import indi.liyi.example.R;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.dragger.AgileDragger;
import indi.liyi.viewer.sipr.ScaleImagePager;

/**
 * 单个图片时，直接使用 ScaleImageView
 */
public class ImagePagerAty extends BaseActivity {
    private ImageView imageView;
    private ScaleImagePager imagePreview;

    private ViewData mViewData;
    private Point mScreenSize;
    private boolean isCancelByBack;
    private AgileDragger mDefDragger;


    @Override
    int onBindLayoutResID() {
        return R.layout.aty_image_pager;
    }

    @Override
    void onInit(Bundle savedInstanceState) {
//        initView();
//        addListener();
//        loadImage();
    }
//
//    private void initView() {
//        imageView = findViewById(R.id.imageView);
//        imagePreview = findViewById(R.id.imagePreivew);
//
//        mViewData = new ViewDsata();
//        mScreenSize = Utils.getScreenSize(this);
//        mDefDragger = new AgileDragger();
//        mDefDragger.setBackground(imagePreview.getBackground());
//        imagePreview.setDefSize(mScreenSize.x, mScreenSize.y);
//        imagePreview.setImageDraggerType(ImageDraggerType.DRAG_TYPE_WX);
//    }
//
//    private void addListener() {
//        imagePreview.setOnViewClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imagePreview.cancel();
//            }
//        });
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int[] location = new int[2];
//                // 获取在整个屏幕内的绝对坐标
//                imageView.getLocationOnScreen(location);
//                mViewData.setTargetX(location[0]);
//                // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
//                mViewData.setTargetY(location[1]);
//                mViewData.setTargetWidth(imageView.getWidth());
//                mViewData.setTargetHeight(imageView.getHeight());
//                imagePreview.setViewData(mViewData);
//                imagePreview.start();
//            }
//        });
//    }
//
//    private void loadImage() {
//        GlideUtil.loadImage(this, Utils.getImageList().get(0), new SimpleTarget<Drawable>() {
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//                super.onLoadCleared(placeholder);
//                imagePreview.getImageView().setImageDrawable(placeholder);
//            }
//
//            @Override
//            public void onLoadStarted(@Nullable Drawable placeholder) {
//                super.onLoadStarted(placeholder);
//                imagePreview.showProgess();
//                imagePreview.getImageView().setImageDrawable(placeholder);
//            }
//
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                super.onLoadFailed(errorDrawable);
//                imagePreview.hideProgress();
//                imagePreview.getImageView().setImageDrawable(errorDrawable);
//            }
//
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                if (resource != null) {
//                    imagePreview.hideProgress();
//                    imageView.setImageDrawable(resource);
//                    imagePreview.getImageView().setImageDrawable(resource);
//                    mViewData.setImageWidth(resource.getIntrinsicWidth());
//                    mViewData.setImageHeight(resource.getIntrinsicHeight());
//                }
//            }
//        });
//    }
//
//    @Override
//    public void finish() {
//        if (imagePreview != null) {
//            imagePreview.recycle();
//        }
//        super.finish();
//    }
//
//    /**
//     * 监听返回键
//     *
//     * @param keyCode
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (imagePreview.getVisibility() == View.VISIBLE) {
//            if (!isCancelByBack) {
//                imagePreview.cancel();
//                isCancelByBack = true;
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
