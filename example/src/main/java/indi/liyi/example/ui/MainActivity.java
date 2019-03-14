package indi.liyi.example.ui;

import android.content.Intent;
import android.view.View;

import indi.liyi.example.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_simplePrev:
                go2Activity(SimplePrevActivity.class);
                break;
            case R.id.btn_horizontalList:
                go2Activity(HorizontalListActivity.class);
                break;
            case R.id.btn_verticalList:
                go2Activity(VerticalListActivity.class);
                break;
            case R.id.btn_friendCircle:
                go2Activity(FriendCircleActivity.class);
                break;
        }
    }

    private void go2Activity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
