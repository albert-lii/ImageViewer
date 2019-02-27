package indi.liyi.viewer.imgpg.dragger;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import indi.liyi.viewer.ViewerWrapper;

public interface DragHandler {

    /**
     * 注入 ViewerWrapper
     */
    void injectViewerWrapper(ViewerWrapper wrapper);

    /**
     * 初始化
     *
     * @param prevWidth  预览界面的宽
     * @param prevHeight 预览界面的高
     * @param view       被拖拽的 View
     */
    void init(int prevWidth, int prevHeight, View view);

    /**
     * 是否允许更改背景透明度
     */
    void canChangeBgAlpha(boolean isCan);

    /**
     * 设置背景
     */
    void setBackground(Drawable backgroud);

    /**
     * 更改背景透明度
     */
    void changeBackgroundAlpha(int alpha);

    /**
     * 添加图片拖拽状态监听器
     */
    void addDragStatusListener(OnDragStatusListener listener);

    /**
     * 手指按下，准备拖拽图片
     *
     * @param downX 手指按下时的 x 轴坐标
     * @param downY 手指按下时的 y 轴坐标
     */
    void onDown(float downX, float downY);

    /**
     * 拖拽图片中
     *
     * @param lastX 上次手指触摸点的 x 轴坐标
     * @param lastY 上次手指触摸点的 y 轴坐标
     * @param ev    手势事件
     */
    void onDrag(float lastX, float lastY, MotionEvent ev);

    /**
     * 手指抬起，释放图片
     */
    void onUp();

    /**
     * 清除
     */
    void clear();
}
