package com.liyi.viewer.data;


public class ViewData {
    // 外部 View 在 ImageViewer 中的 X 轴坐标
    private float x;
    // 外部 View 在 ImageViewer 中的 Y 轴坐标
    private float y;
    // 外部 View 的宽度
    private float width;
    // 外部 View 的高度
    private float height;
    // 图片的实际宽度
    private float imageWidth;
    // 图片的实际高度
    private float imageHeight;

    public ViewData() {

    }

    public ViewData(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(float imageWidth) {
        this.imageWidth = imageWidth;
    }

    public float getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(float imageHeight) {
        this.imageHeight = imageHeight;
    }
}
