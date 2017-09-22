package com.liyi.example;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.liyi.autogrid.AutoGridView;
import com.liyi.autogrid.BaseGridAdapter;
import com.liyi.viewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by albertlii on 2017/9/20.
 */
public class PicActivity extends AppCompatActivity {
    private AutoGridView autoGridView;
    private List<Integer> mList = new ArrayList<>();
    private List<Rect> mRects = new ArrayList<>();

    private ImageViewer imageViewer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取消状态栏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pic);

        autoGridView = (AutoGridView) findViewById(R.id.autogridview);

        mList.add(R.drawable.langman);
        mList.add(R.drawable.huaijiu);
        mList.add(R.drawable.landiao);
        mList.add(R.drawable.fennen);
        mList.add(R.drawable.heibai);

        autoGridView.setAdapter(new MyAdapter());
        autoGridView.setOnItemClickListener(new AutoGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {
                mRects.clear();
                for (int j = 0; j < autoGridView.getChildCount(); j++) {
                    int[] location = new int[2];
                    // 获取在整个屏幕内的绝对坐标
                    autoGridView.getChildAt(j).getLocationOnScreen(location);
                    mRects.add(new Rect(location[0], location[1],
                            location[0] + autoGridView.getChildAt(i).getMeasuredWidth(),
                            location[1] + autoGridView.getChildAt(i).getMeasuredHeight()));
                }
                if (imageViewer == null) {
                    imageViewer = ImageViewer.newInstance();
                }
                imageViewer.setLocations(mRects)
                        .setResources(i, mList, PicActivity.this)
                        .show(getSupportFragmentManager(), "pic");
            }
        });
    }

    private class MyAdapter extends BaseGridAdapter {

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(PicActivity.this).inflate(R.layout.item_auto_grid, null);
                holder = new ItemHolder();
                holder.iv_grid = (ImageView) view.findViewById(R.id.iv_item_grid);
                view.setTag(holder);
            } else {
                holder = (ItemHolder) view.getTag();
            }
            holder.iv_grid.setImageResource(mList.get(i));
            return view;
        }

        private class ItemHolder {
            private ImageView iv_grid;
        }
    }
}
