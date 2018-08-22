package com.wan.guajiangviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wan.guajiangview.GuaJiangView;

public class MainActivity extends AppCompatActivity {

    private GuaJiangView mView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mView1 = (GuaJiangView) findViewById(R.id.view1);
        mView1.setText("hello world");

    }
}
