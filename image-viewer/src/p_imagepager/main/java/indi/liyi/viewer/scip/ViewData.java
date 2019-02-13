package indi.liyi.viewer.scip;


public class ViewData {
    // 无效值
    public static final float INVALID_VAL = -1000;
    // 图片资源
    private Object imageSrc;
    // 图片的原始宽度
    private float imageWidth = INVALID_VAL;
    // 图片的原始高度
    private float imageHeight = INVALID_VAL;
    // 目标 view 的 x 轴坐标
    private float targetX = INVALID_VAL;
    // 目标 view 的 y 轴坐标
    private float targetY = INVALID_VAL;
    // 目标 view 的宽度
    private float targetWidth = INVALID_VAL;
    // 目标 view 的高度
    private float targetHeight = INVALID_VAL;


    public ViewData() {

    }

    public ViewData(Object imageSrc) {
        this.imageSrc = imageSrc;
    }

    public ViewData(float targetX, float targetY, float targetWidth, float targetHeight) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public ViewData(Object imageSrc, float targetX, float targetY, float targetWidth, float targetHeight) {
        this.imageSrc = imageSrc;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public ViewData(Object imageSrc, float imageWidth, float imageHeight, float targetX, float targetY, float targetWidth, float targetHeight) {
        this.imageSrc = imageSrc;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public Object getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(Object imageSrc) {
        this.imageSrc = imageSrc;
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
}
