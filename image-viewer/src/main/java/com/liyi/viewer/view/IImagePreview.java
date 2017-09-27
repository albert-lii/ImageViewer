package com.liyi.viewer.view;


import android.content.Intent;

public interface IImagePreview {
    void initUI();

    void addListener();

    void handleIntent(Intent intent);

    /**
     * The image is animated after the click
     */
    void fullScreen();

    /**
     * Animation of the image when exiting
     */
    void restoreImage();
}
