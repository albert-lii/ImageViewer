package indi.liyi.viewer;


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


public interface IViewer {
    /**
     * 获取图片索引的 view
     */
    TextView getIndexView();

    /**
     * 设置起始位置
     *
     * @param position
     */
    ImageViewer setStartPosition(int position);

    /**
     * 设置图片资源
     *
     * @param list
     */
    ImageViewer setImageData(List list);

    /**
     * 设置目标 view 的相关数据
     *
     * @param list
     */
    ImageViewer setViewData(List<ViewData> list);

    /**
     * 设置图片加载器
     *
     * @param loader
     */
    ImageViewer setImageLoader(BaseImageLoader loader);

    /**
     * 是否显示图片索引
     */
    ImageViewer showIndex(boolean show);

    /**
     * 是否允许拖拽图片
     */
    ImageViewer canDragged(boolean can);

    /**
     * 设置拖拽模式
     *
     * @param mode {@link indi.liyi.viewer.scip.dragger.DragMode}
     */
    ImageViewer setDragMode(int mode);

    /**
     * 是否使用进场动画
     */
    ImageViewer doEnterAnim(boolean isDo);

    /**
     * 是否使用退场动画
     */
    ImageViewer doExitAnim(boolean isDo);

    /**
     * 设置进场与退场动画的执行时间
     */
    ImageViewer setDuration(int duration);

    /**
     * 设置图片的切换事件监听
     */
    ImageViewer setOnItemChangedListener(OnItemChangedListener listener);

    /**
     * 设置图片的单击事件监听
     */
    ImageViewer setOnItemClickListener(OnItemClickListener listener);

    /**
     * 设置图片的长按事件监听
     */
    ImageViewer setOnItemLongClickListener(OnItemLongClickListener listener);

    /**
     * 设置图片的拖拽事件监听
     */
    ImageViewer setOnDragStatusListener(OnDragStatusListener listener);

    /**
     * 设置图片预览器的预览状态监听
     */
    ImageViewer setOnPreviewStatusListener(OnPreviewStatusListener listener);

    /**
     * 打开图片预览器
     */
    void watch();

    /**
     * 关闭图片预览器
     */
    void close();

    /**
     * 清除所有数据
     */
    void clear();

    /**
     * 获取图片预览器的当前状态
     *
     * @return {@link ViewerStatus}
     */
    int getViewStatus();

    /**
     * 是否允许图片缩放
     */
    ImageViewer setScaleable(boolean scaleable);

    /**
     * 图片是否可缩放
     *
     * @return
     */
    boolean isScaleable();

    /**
     * 获取图片当前的缩放等级
     */
    float getScale();

    /**
     * 设置图片的最大缩放等级
     */
    ImageViewer setMaxScale(float maxScale);

    /**
     * 获取图片的最大缩放等级
     */
    float getMaxScale();

    /**
     * 设置图片的最小缩放等级
     */
    ImageViewer setMinScale(float minScale);

    /**
     * 获取图片的最小缩放等级
     */
    float getMinScale();

    /**
     * 获取当前的 itemView
     */
    ScaleImagePager getCurrentItem();

    /**
     * 获取当前的 itemView 的位置
     */
    int getCurrentPosition();
}
