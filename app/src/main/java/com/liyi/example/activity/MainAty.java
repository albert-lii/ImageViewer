package com.liyi.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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
        intiView();
    }

    private void intiView() {
        Button btn_simple = findViewById(R.id.btn_simple);
        Button btn_custom = findViewById(R.id.btn_custom);
        Button btn_land = findViewById(R.id.btn_land_list);
        Button btn_port = findViewById(R.id.btn_port_list);
        btn_simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAty.this, PreviewSimpleAty.class));
            }
        });
        btn_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAty.this, PreviewCustomAty.class));
            }
        });
        btn_land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAty.this, ListLandAty.class));
            }
        });
        btn_port.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAty.this, ListPortAty.class));
            }
        });
    }
}
