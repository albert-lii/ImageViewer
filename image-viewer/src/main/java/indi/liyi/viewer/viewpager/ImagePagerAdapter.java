package indi.liyi.viewer.viewpager;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import indi.liyi.viewer.EventHandler;
import indi.liyi.viewer.ImageDrawee;
import indi.liyi.viewer.ImageLoader;
import indi.liyi.viewer.ImageTransfer;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.otherui.ProgressUI;


public class ImagePagerAdapter extends PagerAdapter {
    private ProgressUI progressUI;

    private int mStartPosition;
    private List<ViewData> mSourceList;
    private ImageLoader mLoader;
    private ImageTransfer mTransfer;
    private EventHandler mEventHandler;
    // 是否已经执行进场动画
    private boolean hasPlayEnterAnim;

    /**
     * 回收被移除的页面，用于复用
     * PS: 经多次测试，复用 View 后，内存开销会增大（主要是 Java 开销），
     * 增大量与缓存的 View 所加载的图片的大小相关
     * 故此处暂不做复用处理
     */
//    private LinkedList<ImageDrawee> mCacheBox;
    public ImagePagerAdapter() {
        hasPlayEnterAnim = false;
//        mCacheBox = new LinkedList();
    }

    public void setSourceList(List<ViewData> list) {
        this.mSourceList = list;
    }

    public void setImageLoader(@NonNull ImageLoader loader) {
        this.mLoader = loader;
    }

    public void setProgressUI(ProgressUI progressUI) {
        this.progressUI = progressUI;
    }

    public void setStartPosition(int position) {
        this.mStartPosition = position;
    }

    public void setImageTransfer(ImageTransfer transfer) {
        this.mTransfer = transfer;
    }

    public void setEventHandler(EventHandler handler) {
        this.mEventHandler = handler;
    }

    @Override
    public int getCount() {
        return mSourceList != null ? mSourceList.size() : 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageDrawee drawee;
//        if (mCacheBox.size() == 0) {
        drawee = new ImageDrawee(container.getContext());
        drawee.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        drawee.setProgressUI(progressUI);
//        } else {
//            drawee = mCacheBox.removeFirst();
//        }
        configureItem(position, drawee);
        if (mStartPosition == position && !hasPlayEnterAnim && mTransfer != null) {
            mTransfer.with(drawee.getImageView())
                    .loadEnterData(mSourceList.get(position))
                    .play();
            hasPlayEnterAnim = true;
        }
        container.addView(drawee);
        return drawee;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ImageDrawee drawee = (ImageDrawee) object;
        drawee.recycle();
        // 移除页面
        container.removeView(drawee);
//        if (mCacheBox != null) {
//            mCacheBox.addLast(drawee);
//        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void clear() {
//        if (mCacheBox != null) {
//            mCacheBox.clear();
//            mCacheBox = null;
//        }
        mSourceList = null;
        mLoader = null;
        mTransfer = null;
        mEventHandler = null;
    }

    /**
     * 配置 item
     */
    private void configureItem(final int position, final ImageDrawee drawee) {
        drawee.setTag(position);
        // 加载图片
        loadImage(position, drawee);
        // 单击事件
        drawee.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventHandler != null) {
                    mEventHandler.joinClick(position, drawee.getImageView());
                }
            }
        });
        // 长按事件
        drawee.getImageView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mEventHandler != null) {
                    return mEventHandler.joinLongPress(position, drawee.getImageView());
                }
                return false;
            }
        });
    }

    /**
     * 加载图片
     */
    private void loadImage(final int position, final ImageDrawee drawee) {
        mLoader.displayImage(mSourceList.get(position).getImageSrc(), drawee.getImageView(), new ImageLoader.LoadCallback(drawee) {

            @Override
            public void onLoadStarted(Object placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoading(float progress) {
                super.onLoading(progress);
                drawee.handleProgress(progress);
            }

            @Override
            public void onLoadSucceed(Object source) {
                super.onLoadSucceed(source);
                configImageSize(position, drawee);
            }

            @Override
            public void onLoadFailed(Object error) {
                super.onLoadFailed(error);
                configImageSize(position, drawee);
            }
        });
    }

    /**
     * 配置图片的原始宽高
     */
    private void configImageSize(int position, ImageDrawee drawee) {
        if (mSourceList.get(position).getImageWidth() == 0 ||
                mSourceList.get(position).getImageHeight() == 0) {
            Drawable drawable = drawee.getImageView().getDrawable();
            if (drawable != null) {
                mSourceList.get(position).setImageWidth(drawable.getIntrinsicWidth());
                mSourceList.get(position).setImageHeight(drawable.getIntrinsicHeight());
            }
        }
    }
}
