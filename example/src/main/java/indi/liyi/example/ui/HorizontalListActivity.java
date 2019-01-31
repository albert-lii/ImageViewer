package indi.liyi.example.ui;

import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.ImageView;

import indi.liyi.example.R;
import indi.liyi.example.adapter.ImageAdapter;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.example.utils.Utils;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.sipr.ScaleImagePager;
import indi.liyi.viewer.sipr.ViewData;

/**
 * 横向图片列表页面
 */
public class HorizontalListActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearManager;
    private ImageAdapter mAdapter;

    private Point mScreenSize;

    @Override
    public int getLayoutId() {
        return R.layout.activity_horizontal_list;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        mLinearManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);

        mAdapter = new ImageAdapter(0);
        mAdapter.setData(mSourceList);

        imageViewer.setImageData(mSourceList);
        imageViewer.setImageLoader(new PhotoLoader());
        initData();
    }

    @Override
    public void addListener() {
        mAdapter.setOnItemClickCallback(new ImageAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                mViewList.get(position).setTargetX(location[0]);
                imageViewer.setStartPosition(position);
                imageViewer.setViewData(mViewList);
                imageViewer.watch();
            }
        });
        recyclerView.setAdapter(mAdapter);
        mLinearManager.scrollToPositionWithOffset(0, 0);

        imageViewer.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onItemChanged(int position, ScaleImagePager view) {
                if (imageViewer.getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), (int) (mViewList.get(position).getTargetX() / 2));
                }
            }
        });

        imageViewer.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int status, ScaleImagePager imagePager) {
                if (status == ViewerStatus.STATUS_COMPLETE_OPEN) {
                    setTransparentStatusBar(R.color.colorBlack);
                } else if (status == ViewerStatus.STATUS_READY_CLOSE) {
                    // 每次退出浏览时，都将图片显示在中间位置
                    ViewData viewData = mViewList.get(imageViewer.getCurrentPosition());
                    viewData.setTargetX(0);
                    mViewList.set(imageViewer.getCurrentPosition(), viewData);
                    imageViewer.setViewData(mViewList);
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), (int) (viewData.getTargetX() / 2));
                } else if (status == ViewerStatus.STATUS_COMPLETE_CLOSE) {
                    setTransparentStatusBar(R.color.colorPrimaryDark);
                }
            }
        });
    }

    private void initData() {
        mScreenSize = Utils.getScreenSize(this);
        for (int i = 0, len = mViewList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setTargetX(0);
            viewData.setTargetY(0);
            viewData.setTargetWidth(mScreenSize.x);
            viewData.setTargetHeight(Utils.dp2px(this, 200));
            mViewList.set(i, viewData);
        }
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
