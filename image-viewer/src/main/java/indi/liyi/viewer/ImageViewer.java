package indi.liyi.viewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnItemClickListener;
import indi.liyi.viewer.listener.OnItemLongClickListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.scip.BaseImageLoader;
import indi.liyi.viewer.scip.ScaleImagePager;
import indi.liyi.viewer.scip.ViewData;
import indi.liyi.viewer.scip.dragger.OnDragStatusListener;


public class ImageViewer extends FrameLayout implements IViewer {
    private ViewerWrapper mWrapper;

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
        mWrapper = new ViewerWrapper(this, attrs);
    }

    @Override
    public TextView getIndexView() {
        return mWrapper.getIndexView();
    }

    @Override
    public ImageViewer setStartPosition(int position) {
        mWrapper.setStartPosition(position);
        return this;
    }

    @Override
    public ImageViewer setImageData(List list) {
        mWrapper.setImageData(list);
        return this;
    }

    @Override
    public ImageViewer setViewData(List<ViewData> list) {
        mWrapper.setViewData(list);
        return this;
    }

    @Override
    public ImageViewer bindViewGroup(@NonNull ViewGroup viewGroup, boolean needStatusBarHeight) {
        return null;
    }

    @Override
    public ImageViewer setImageLoader(BaseImageLoader loader) {
        mWrapper.setImageLoader(loader);
        return this;
    }

    @Override
    public ImageViewer showIndex(boolean show) {
        mWrapper.showIndex(show);
        return this;
    }

    @Override
    public ImageViewer canDragged(boolean can) {
        mWrapper.doDrag(can);
        return this;
    }

    @Override
    public ImageViewer setDragMode(int mode) {
        mWrapper.setDragType(mode);
        return this;
    }

    @Override
    public ImageViewer doEnterAnim(boolean isDo) {
        mWrapper.doEnterAnim(isDo);
        return this;
    }

    @Override
    public ImageViewer doExitAnim(boolean isDo) {
        mWrapper.doExitAnim(isDo);
        return this;
    }

    @Override
    public ImageViewer setDuration(int duration) {
        mWrapper.setDuration(duration);
        return this;
    }

    @Override
    public ImageViewer setOnItemChangedListener(OnItemChangedListener listener) {
        mWrapper.setOnItemChangedListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemClickListener(OnItemClickListener listener) {
        mWrapper.setOnItemClickListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnItemLongClickListener(OnItemLongClickListener listener) {
        mWrapper.setOnItemLongClickListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnDragStatusListener(OnDragStatusListener listener) {
        mWrapper.setOnDragStatusListener(listener);
        return this;
    }

    @Override
    public ImageViewer setOnPreviewStatusListener(OnPreviewStatusListener listener) {
        mWrapper.setOnPreviewStatusListener(listener);
        return this;
    }

    @Override
    public void watch() {
        mWrapper.watch();
    }

    @Override
    public void close() {
        mWrapper.close();
    }

    @Override
    public void clear() {
        mWrapper.clear();
    }

    @Override
    public int getViewStatus() {
        return mWrapper.getViewStatus();
    }

    @Override
    public ImageViewer setScaleable(boolean scaleable) {
        mWrapper.setScaleable(scaleable);
        return this;
    }

    @Override
    public boolean isScaleable() {
        return mWrapper.isScaleable();
    }

    @Override
    public float getScale() {
        return mWrapper.getCurrentItemScale();
    }

    @Override
    public ImageViewer setMaxScale(float maxScaleLevel) {
        mWrapper.setMaxScale(maxScaleLevel);
        return this;
    }

    @Override
    public float getMaxScale() {
        return mWrapper.getMaxScale();
    }

    @Override
    public ImageViewer setMinScale(float minScaleLevel) {
        mWrapper.setMinScale(minScaleLevel);
        return this;
    }

    @Override
    public float getMinScale() {
        return mWrapper.getMinScale();
    }

    @Override
    public int getCurrentPosition() {
        return mWrapper.getCurrentPosition();
    }

    @Override
    public ScaleImagePager getCurrentItem() {
        return mWrapper.getCurrentItem();
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
            if (!mWrapper.isImageAnimRunning()) {
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
