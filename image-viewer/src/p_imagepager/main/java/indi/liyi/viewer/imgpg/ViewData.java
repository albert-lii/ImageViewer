package indi.liyi.viewer.imgpg;


public class ViewData {
    // 无效值
    public static final float INVALID_VAL = -1000;
    // 图片资源
    private Object imageSrc;
    // 图片的原始宽度
    private int imageWidth;
    // 图片的原始高度
    private int imageHeight;
    // 目标 view 的 x 轴坐标
    private float targetX = INVALID_VAL;
    // 目标 view 的 y 轴坐标
    private float targetY = INVALID_VAL;
    // 目标 view 的宽度
    private int targetWidth;
    // 目标 view 的高度
    private int targetHeight;


    public ViewData() {

    }

    public ViewData(Object imageSrc) {
        this.imageSrc = imageSrc;
    }

    public ViewData(float targetX, float targetY, int targetWidth, int targetHeight) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public ViewData(Object imageSrc, float targetX, float targetY, int targetWidth, int targetHeight) {
        this.imageSrc = imageSrc;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public ViewData(Object imageSrc, int imageWidth, int imageHeight, float targetX, float targetY, int targetWidth, int targetHeight) {
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

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
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

    public int getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }
}
