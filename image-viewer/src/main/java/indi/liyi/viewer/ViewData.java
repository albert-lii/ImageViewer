package indi.liyi.viewer;


public class ViewData {
    // 目标 view 的 x 轴坐标
    private float targetX;
    // 目标 view 的 y 轴坐标
    private float targetY;
    // 目标 view 的宽度
    private float targetWidth;
    // 目标 view 的高度
    private float targetHeight;
    // 图片的原始宽度
    private float imageWidth;
    // 图片的原始高度
    private float imageHeight;

    public ViewData() {

    }

    public ViewData(float targetX, float targetY, float targetWidth, float targetHeight) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public float getTargetX() {
        return targetX;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(float targetWidth) {
        this.targetWidth = targetWidth;
    }

    public float getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(float targetHeight) {
        this.targetHeight = targetHeight;
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
