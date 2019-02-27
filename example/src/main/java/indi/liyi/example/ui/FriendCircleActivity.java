package indi.liyi.example.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import indi.liyi.example.R;
import indi.liyi.example.adapter.FriendCircleAdapter;
import indi.liyi.example.utils.ImageLoader;
import indi.liyi.example.utils.SourceUtil;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.imgpg.ImagePager;
import indi.liyi.viewer.listener.OnPreviewStatusListener;

/**
 * 朋友圈页面
 */
public class FriendCircleActivity extends BaseActivity {
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private FriendCircleAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_friend_circle;
    }

    @Override
    public void initView() {
        imageViewer = findViewById(R.id.imageViewer);
        recyclerView = findViewById(R.id.recyclerview);

        imageViewer.setImageLoader(new ImageLoader());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendCircleAdapter();
        adapter.setData(SourceUtil.getFriendCircleList());
    }

    @Override
    public void addListener() {
        adapter.setOnItemClickCallback(new FriendCircleAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, List<String> list, ViewGroup gridview) {
                imageViewer.setImageData(list)
                        .bindViewGroup(gridview, false)
                        .setStartPosition(position)
                        .watch();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
