package indi.liyi.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import indi.liyi.example.utils.glide.SourceUtil;
import indi.liyi.viewer.sipr.ViewData;

public abstract class BaseActivity extends Activity {
    protected List<String> mSourceList = new ArrayList<>();
    protected List<ViewData> mViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mSourceList = SourceUtil.getImageList();
        for (int i = 0, len = mSourceList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            mViewList.add(viewData);
        }
        initView();
        addListener();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void addListener();
}
