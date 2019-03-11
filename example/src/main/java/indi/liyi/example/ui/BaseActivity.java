package indi.liyi.example.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {
//    protected List<String> mSourceList = new ArrayList<>();
//    protected List<ViewData> mVdList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
//        mSourceList = SourceUtil.getImageList();
//        for (int i = 0, len = mSourceList.size(); i < len; i++) {
//            ViewData viewData = new ViewData();
//            mVdList.add(viewData);
//        }
        initView();
        addListener();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void addListener();

    public void changeStatusBarColor(int colorId) {
        // 5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(colorId));
        }
    }
}
