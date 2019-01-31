package indi.liyi.viewer.scip.dragger;

import android.graphics.drawable.Drawable;

import indi.liyi.viewer.ViewerAttacher;
import indi.liyi.viewer.scip.ScaleImagePager;

public interface DragHandler {
    /**
     * 注入 ScaleImagePager
     *
     * @param imagePager
     */
    void injectImagePager(ScaleImagePager imagePager);

    /**
     * 注入 ViewerAttacher
     *
     * @param attacher
     */
    void injectImageViewerAttacher(ViewerAttacher attacher);

    /**
     * 设置背景
     *
     * @param backgroud
     */
    void setBackground(Drawable backgroud);

    /**
     * 是否允许更改背景透明度
     */
    void canChangeBgAlpha(boolean isCan);

    /**
     * 更改背景透明度
     *
     * @param alpha
     */
    void changeBackgroundAlpha(int alpha);

    /**
     * 添加图片拖拽状态监听器
     *
     * @param listener
     */
    void addDragStatusListener(OnDragStatusListener listener);

    /**
     * 手指按下，准备拖拽图片
     *
     * @param prevWidth  预览界面的宽度
     * @param prevHeight 预览界面的高度
     */
    void onDown(float prevWidth, float prevHeight);

    /**
     * 拖拽图片
     *
     * @param downX 手指按下时的 x 轴坐标
     * @param downY 手指按下时的 y 轴坐标
     * @param curX  手指当前的 x 轴坐标
     * @param curY  手指当前的 y 轴坐标
     */
    void onDrag(float downX, final float downY, final float curX, final float curY);

    /**
     * 手指抬起，释放图片
     */
    void onUp();

    /**
     * 清除
     */
    void clear();
}
