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
import indi.liyi.example.utils.ImageLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.example.utils.Utils;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.imgpg.ViewData;
import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnPreviewStatusListener;

/**
 * 横向图片列表页面
 */
public class HorizontalListActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearManager;
    private ImageAdapter adapter;

    private Point mScreenSize;
    private List<String> mImgList;
    private List<ViewData> mVdList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_horizontal_list;
    }

    @Override
    public void initView() {
        recyclerView = findViewById(R.id.recyclerview);
        imageViewer = findViewById(R.id.imageViewer);

        initData();
        mLinearManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLinearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearManager);
        adapter = new ImageAdapter(0);
        adapter.setData(mImgList);

        imageViewer.setImageData(mImgList);
        imageViewer.setImageLoader(new ImageLoader());
    }

    private void initData() {
        mScreenSize = Utils.getScreenSize(this);
        mImgList = SourceUtil.getImageList();
        mVdList = new ArrayList<>();
        for (int i = 0, len = mImgList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setImageSrc(mImgList.get(i));
            viewData.setTargetX(0);
            viewData.setTargetY(0);
            viewData.setTargetWidth(mScreenSize.x);
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
                mVdList.get(position).setTargetX(location[0]);
                imageViewer.setStartPosition(position)
                        .setViewData(mVdList)
                        .watch();
            }
        });
        recyclerView.setAdapter(adapter);
        mLinearManager.scrollToPositionWithOffset(0, 0);

        imageViewer.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onItemChanged(int position, ImagePager view) {
                if (imageViewer.getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), (int) (mVdList.get(position).getTargetX() / 2));
                }
            }
        });

        imageViewer.setOnPreviewStatusListener(new OnPreviewStatusListener() {
            @Override
            public void onPreviewStatus(int status, ImagePager imagePager) {
                if (status == ViewerStatus.STATUS_COMPLETE_OPEN) {
                    changeStatusBarColor(R.color.colorBlack);
                } else if (status == ViewerStatus.STATUS_READY_CLOSE) {
                    // 每次退出浏览时，都将图片显示在中间位置
                    ViewData viewData = mVdList.get(imageViewer.getCurrentPosition());
                    viewData.setTargetX(0);
                    mVdList.set(imageViewer.getCurrentPosition(), viewData);
                    imageViewer.setViewData(mVdList);
                    mLinearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), (int) (viewData.getTargetX() / 2));
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
