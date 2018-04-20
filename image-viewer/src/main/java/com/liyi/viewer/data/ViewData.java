package com.liyi.viewer.data;


import java.io.Serializable;


public class ViewData implements Serializable {
    private float x;
    private float y;
    private float width;
    private float height;
    private float imageWidth;
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
