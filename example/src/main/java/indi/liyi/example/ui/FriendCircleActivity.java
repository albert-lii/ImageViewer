package indi.liyi.example.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.adapter.FriendCircleAdapter;
import indi.liyi.example.utils.PhotoLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.sipr.ViewData;

/**
 * 朋友圈页面
 */
public class FriendCircleActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private FriendCircleAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_friend_circle;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        imageViewer.setImageLoader(new PhotoLoader());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FriendCircleAdapter();
        mAdapter.setData(SourceUtil.getFriendCircleList());
    }

    @Override
    public void addListener() {
        mAdapter.setOnItemClickCallback(new FriendCircleAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, List<String> list, List<ViewData> viewDataList) {
                imageViewer.setStartPosition(position);
                imageViewer.setImageData(list);
                imageViewer.setViewData(viewDataList);
                imageViewer.watch();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }
}
