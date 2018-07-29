package com.liyi.example.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.liyi.example.Utils;
import com.liyi.viewer.ViewData;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity {
    protected List<String> mImageList = new ArrayList<>();
    protected List<ViewData> mViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(onBindLayoutResID());
        mImageList = Utils.getImageList();
        for (int i = 0, len = mImageList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            mViewList.add(viewData);
        }
        onInit(savedInstanceState);
    }

    @LayoutRes
    abstract int onBindLayoutResID();

    abstract void onInit(Bundle savedInstanceState);
}
