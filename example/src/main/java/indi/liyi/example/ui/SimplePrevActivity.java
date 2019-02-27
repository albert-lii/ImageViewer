package indi.liyi.example.ui;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.liyi.grid.AutoGridView;
import com.liyi.grid.adapter.SimpleAutoGridAdapter;

import indi.liyi.example.R;
import indi.liyi.example.utils.ImageLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.example.utils.glide.GlideUtil;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.listener.OnPreviewStatusListener;

/**
 * 简单的预览界面
 */
public class SimplePrevActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private AutoGridView autoGv;
    private SimpleAutoGridAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_simple_prev;
    }

    @Override
    public void initView() {
        autoGv = findViewById(R.id.autoGridView);
        imageViewer = findViewById(R.id.imagePreivew);


        adapter = new SimpleAutoGridAdapter();
        adapter.setSource(SourceUtil.getImageList());
        adapter.setImageLoader(new SimpleAutoGridAdapter.ImageLoader() {
            @Override
            public void onLoadImage(final int position, Object source, final ImageView imageView, int viewType) {
                GlideUtil.loadImage(SimplePrevActivity.this, source, imageView);
            }
        });
        imageViewer.setImageData(SourceUtil.getImageList())
                .setImageLoader(new ImageLoader());
    }

    @Override
    public void addListener() {
        autoGv.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                // 方法1：
                imageViewer.bindViewGroup(autoGv, false)
                        .setStartPosition(position)
                        .watch();

                // 方法2：
//                mVdList.clear();
//                for (int i = 0; i < autoGv.getChildCount(); i++) {
//                    ViewData viewData = mVdList.get(i);
//                    viewData.setTargetX(autoGv.getChildAt(i).getX());
//                    // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
//                    viewData.setTargetY(autoGv.getChildAt(i).getY());
//                    viewData.setTargetWidth(autoGv.getChildAt(i).getMeasuredWidth());
//                    viewData.setTargetHeight(autoGv.getChildAt(i).getMeasuredHeight());
//                    viewData.setImageSrc(mSourceList.get(i));
//                    mVdList.set(i, viewData);
//                }
//                imageViewer.setStartPosition(position);
//                imageViewer.setViewData(mVdList);
//                imageViewer.watch();
            }
        });
        autoGv.setAdapter(adapter);

        imageViewer.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int status, ImagePager imagePager) {
                if (status == ViewerStatus.STATUS_COMPLETE_OPEN) {
                    changeStatusBarColor(R.color.colorBlack);
                } else if (status == ViewerStatus.STATUS_COMPLETE_CLOSE) {
                    changeStatusBarColor(R.color.colorPrimaryDark);
                }
            }
        });
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean b = imageViewer.onKeyDown(keyCode, event);
        if (b) {
            return b;
        }
        return super.onKeyDown(keyCode, event);
    }
}
