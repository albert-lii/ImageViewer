package indi.liyi.viewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.sipr.BaseImageLoader;
import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.sipr.ViewData;
import indi.liyi.viewer.sipr.dragger.OnDragStatusListener;


public class ImageViewer extends FrameLayout implements IViewer {
    private ViewerAttacher mAttacher;

    public ImageViewer(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ImageViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ImageViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mAttacher = new ViewerAttacher(this, attrs);
    }

    @Override
    public TextView getIndexView() {
        return mAttacher.getIndexView();
    }

    @Override
    public ImageViewer setStartPosition(int position) {
        mAttacher.setStartPosition(position);
        return this;
    }

    @Override
    public ImageViewer setImageData(List list) {
        mAttacher.setImageData(list);
        return this;
    }

    @Override
    public ImageViewer setViewData(List<ViewData> list) {
        mAttacher.setViewData(list);
        return this;
    }

    @Override
    public ImageViewer setImageLoader(BaseImageLoader loader) {
        mAttacher.setImageLoader(loader);
        return this;
    }

    @Override
    public ImageViewer showIndex(boolean show) {
        mAttacher.showIndex(show);
        return this;
    }

    @Override
    public ImageViewer canDragged(boolean can) {
        mAttacher.doDrag(can);
        return this;
    }

    @Override
    public ImageViewer setDragMode(int mode) {
        mAttacher.setDragType(mode);
        return this;
    }

    @Override
    public ImageViewer doEnterAnim(boolean isDo) {
        mAttacher.doEnterAnim(isDo);
        return this;
    }

    @Override
    public ImageViewer doExitAnim(boolean isDo) {
        mAttacher.doExitAnim(isDo);
        return this;
    }

    @Override
    public ImageViewer setDuration(int duration) {
        mAttacher.setDuration(duration);
        return this;
    }

    @Override
    public ImageViewer setOnItemChangedListener(OnItemChangedListener listener) {
        mAttacher.setOnItemChangedListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemClickListener(OnItemClickListener listener) {
        mAttacher.setOnItemClickListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemLongClickListener(OnItemLongClickListener listener) {
        mAttacher.setOnItemLongClickListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnDragStatusListener(OnDragStatusListener listener) {
        mAttacher.setOnDragStatusListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnPreviewStatusListener(OnPreviewStatusListener listener) {
        mAttacher.setOnPreviewStatusListener(listener);
        return this;
    }

    @Override
    public void watch() {
        mAttacher.watch();
    }

    @Override
    public void close() {
        mAttacher.close();
    }

    @Override
    public void clear() {
        mAttacher.clear();
    }

    @Override
    public int getViewStatus() {
        return mAttacher.getViewStatus();
    }

    @Override
    public ImageViewer setScaleable(boolean scaleable) {
        mAttacher.setScaleable(scaleable);
        return this;
    }

    @Override
    public boolean isScaleable() {
        return mAttacher.isScaleable();
    }

    @Override
    public float getScale() {
        return mAttacher.getCurrentItemScale();
    }

    @Override
    public ImageViewer setMaxScale(float maxScaleLevel) {
        mAttacher.setMaxScale(maxScaleLevel);
        return this;
    }

    @Override
    public float getMaxScale() {
        return mAttacher.getMaxScale();
    }

    @Override
    public ImageViewer setMinScale(float minScaleLevel) {
        mAttacher.setMinScale(minScaleLevel);
        return this;
    }

    @Override
    public float getMinScale() {
        return mAttacher.getMinScale();
    }

    @Override
    public int getCurrentPosition() {
        return mAttacher.getCurrentPosition();
    }

    @Override
    public ScaleImagePager getCurrentItem() {
        return mAttacher.getCurrentItem();
    }

    /**
     * 如果本方法未执行，则是因为图片浏览器为获取到焦点，可在外部手动获取焦点
     * 建议在外部手动调动本方法
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mAttacher.isImageAnimRunning()) {
                if (getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    close();
                    // 消费返回键点击事件，不传递出去
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
    }
}
