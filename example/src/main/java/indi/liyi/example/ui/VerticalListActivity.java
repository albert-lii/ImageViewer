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
import indi.liyi.viewer.listener.OnPreviewStatusListener;
import indi.liyi.viewer.scip.ScaleImagePager;

/**
 * 纵向图片列表页面
 */
public class VerticalListActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearManager;
    private ImageAdapter mAdapter;

    private Point mScreenSize;
    private int mStatusBarHeight;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vertical_list;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        mLinearManager = new LinearLayoutManager(this);
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);

        mAdapter = new ImageAdapter(1);
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
                // 去掉状态栏的高度
                mViewList.get(position).setTargetY(location[1] - mStatusBarHeight);
                imageViewer.setStartPosition(position);
                imageViewer.setViewData(mViewList);
                imageViewer.watch();
            }
        });
        recyclerView.setAdapter(mAdapter);
        mLinearManager.scrollToPositionWithOffset(0, 0);

        imageViewer.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int state, ScaleImagePager imagePager) {
                if (state == ViewerStatus.STATUS_READY_CLOSE) {
                    int top = getTop(imageViewer.getCurrentPosition());
//                    ViewData viewData = mViewList.get(imageViewer.getCurrentPosition());
//                    viewData.setTargetY(top);
//                    mViewList.set(imageViewer.getCurrentPosition(), viewData);
                    imageViewer.getCurrentItem().getViewData().setTargetY(top);
                    imageViewer.setViewData(mViewList);
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });
    }


    private void initData() {
        mStatusBarHeight = Utils.getStatusBarHeight(this);
        mScreenSize = Utils.getScreenSize(this);
        for (int i = 0, len = mViewList.size(); i < len; i++) {
            mViewList.get(i).setTargetX(Utils.dp2px(this, 10));
            mViewList.get(i).setTargetWidth(mScreenSize.x - Utils.dp2px(this, 20));
            mViewList.get(i).setTargetHeight(Utils.dp2px(this, 200));
        }
    }

    private int getTop(int position) {
        int top = 0;
        // 当前图片的高度
        float imgH = Float.valueOf(mViewList.get(position).getTargetHeight());
        // 图片距离 imageViewer 的上下边距
        int dis = (int) ((imageViewer.getHeight() - imgH) / 2);
        // 如果图片高度大于等于 imageViewer 的高度
        if (dis <= 0) {
            return top + dis;
        } else {
            float th1 = 0;
            float th2 = 0;
            // 计算当前图片上方所有 Item 的总高度
            for (int i = 0; i < position; i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th1 += Utils.dp2px(this, 210);
            }
            // 计算当前图片下方所有 Item 的总高度
            for (int i = position + 1; i < mSourceList.size(); i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th2 += Utils.dp2px(this, 210);
            }
            if (th1 >= dis && th2 >= dis) {
                return top + dis;
            } else if (th1 < dis) {
                return (int) (top + th1);
            } else if (th2 < dis) {
                return (int) (recyclerView.getHeight() - imgH);
            }
        }
        return 0;
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
