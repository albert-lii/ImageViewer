package indi.liyi.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.liyi.example.R;


/**
 * Created by albertlii on 2017/9/20.
 */
public class MainAty extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_simple:
                startActivity(new Intent(MainAty.this, SimplePreivewAty.class));
                break;

            case R.id.btn_custom:
                startActivity(new Intent(MainAty.this, CustomPreviewAty.class));
                break;

            case R.id.btn_horizontal_list:
                startActivity(new Intent(MainAty.this, HorizontalListAty.class));
                break;

            case R.id.btn_vertical_list:
                startActivity(new Intent(MainAty.this, VerticalListAty.class));
                break;

            case R.id.btn_image_pager:
                startActivity(new Intent(MainAty.this, ImagePagerAty.class));
                break;
        }
    }
}
