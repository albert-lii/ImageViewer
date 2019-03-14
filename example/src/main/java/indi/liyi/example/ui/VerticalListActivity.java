package indi.liyi.example.ui;

import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.adapter.ImageAdapter;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.example.utils.Utils;
import indi.liyi.viewer.ImageDrawee;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.listener.OnBrowseStatusListener;
import indi.liyi.viewer.listener.OnItemChangedListener;

/**
 * 纵向图片列表页面
 */
public class VerticalListActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearManager;
    private ImageAdapter adapter;

    private Point mScreenSize;
    private List<String> mImgList;
    private List<ViewData> mVdList;
    private int mStatusBarHeight;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vertical_list;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        initData();
        linearManager = new LinearLayoutManager(this);
        linearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearManager);
        adapter = new ImageAdapter(1);
        adapter.setData(mImgList);

        imageViewer.overlayStatusBar(false)
                .imageLoader(new PhotoLoader());
    }

    private void initData() {
        mScreenSize = Utils.getScreenSize(this);
        mStatusBarHeight = Utils.getStatusBarHeight(this);
        mImgList = SourceUtil.getImageList();
        mVdList = new ArrayList<>();
        for (int i = 0, len = mImgList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setImageSrc(mImgList.get(i));
            viewData.setTargetX(Utils.dp2px(this, 10));
            viewData.setTargetWidth(mScreenSize.x - Utils.dp2px(this, 20));
            viewData.setTargetHeight(Utils.dp2px(this, 200));
            mVdList.add(viewData);
        }
    }

    @Override
    public void addListener() {
        adapter.setOnItemClickCallback(new ImageAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                // 去掉状态栏的高度
                mVdList.get(position).setTargetY(location[1] - mStatusBarHeight);
                imageViewer.viewData(mVdList)
                        .watch(position);
            }
        });
        recyclerView.setAdapter(adapter);
        linearManager.scrollToPositionWithOffset(0, 0);

        imageViewer.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onItemChanged(int position, ImageDrawee drawee) {
                if (imageViewer.getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    int top = getTop(imageViewer.getCurrentPosition());
                    mVdList.get(imageViewer.getCurrentPosition()).setTargetY(top);
                    linearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });
//        imageViewer.setOnBrowseStatusListener(new OnBrowseStatusListener() {
//            @Override
//            public void onBrowseStatus(int status) {
//                if (status == ViewerStatus.STATUS_BEGIN_CLOSE) {
//                    int top = getTop(imageViewer.getCurrentPosition());
//                    mVdList.get(imageViewer.getCurrentPosition()).setTargetY(top);
//                    linearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
//                }
//            }
//        });
    }

    private int getTop(int position) {
        int top = 0;
        // 当前图片的高度
        float imgH = Float.valueOf(mVdList.get(position).getTargetHeight());
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
            for (int i = position + 1; i < mImgList.size(); i++) {
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
