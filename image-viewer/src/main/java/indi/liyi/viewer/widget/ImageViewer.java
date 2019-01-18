package indi.liyi.viewer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import indi.liyi.viewer.ImageLoader;
import indi.liyi.viewer.ImageViewerState;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.listener.OnPageChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;


public class ImageViewer extends FrameLayout implements IImageViewer {
    private ImageViewerAttacher mAttacher;

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
        mAttacher = new ImageViewerAttacher(this, attrs);
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
    public ImageViewer setImageLoader(ImageLoader loader) {
        mAttacher.setImageLoader(loader);
        return this;
    }

    @Override
    public ImageViewer showIndex(boolean show) {
        mAttacher.showIndex(show);
        return this;
    }

    @Override
    public ImageViewer doDrag(boolean isDo) {
        mAttacher.doDrag(isDo);
        return this;
    }

    @Override
    public ImageViewer setDragType(int type) {
        mAttacher.setDragType(type);
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
    public ImageViewer setOnImageChangedListener(OnPageChangedListener listener) {
        mAttacher.setOnImageChangedListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemClickListener(OnItemClickListener listener) {
        mAttacher.setOnViewClickListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemLongClickListener(OnItemLongClickListener listener) {
        mAttacher.setOnItemLongClickListener(listener);
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
    public int getViewState() {
        return mAttacher.getViewState();
    }

    @Override
    public ImageViewer setImageScaleable(boolean scaleable) {
        mAttacher.setImageScaleable(scaleable);
        return this;
    }

    @Override
    public boolean isImageScaleable() {
        return mAttacher.isImageScaleable();
    }

    @Override
    public float getImageScale() {
        return mAttacher.getImageScale();
    }

    @Override
    public ImageViewer setImageMaxScale(float maxScaleLevel) {
        mAttacher.setImageMaxScale(maxScaleLevel);
        return this;
    }

    @Override
    public float getImageMaxScale() {
        return mAttacher.getImageMaxScale();
    }

    @Override
    public ImageViewer setImageMinScale(float minScaleLevel) {
        mAttacher.setImageMinScale(minScaleLevel);
        return this;
    }

    @Override
    public float getImageMinScale() {
        return mAttacher.getImageMinScale();
    }

    @Override
    public int getCurrentPosition() {
        return mAttacher.getCurrentPosition();
    }

    @Override
    public View getCurrentView() {
        return mAttacher.getCurrentView();
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
                if (getViewState() == ImageViewerState.STATE_WATCHING) {
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
