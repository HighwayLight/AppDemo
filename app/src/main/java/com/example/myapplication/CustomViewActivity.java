package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by dushu on 2021/3/29 .
 */
public class CustomViewActivity extends AppCompatActivity {

    CustomViewGroup customViewGroup;
    public boolean isChange = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        customViewGroup = findViewById(R.id.custom_vg);
        customViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    customViewGroup.change(true);
                    isChange = false;
                } else {
                    customViewGroup.change(false);
                    isChange = true;
                }

            }
        });

//        customViewGroup.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                customViewGroup.setBackgroundColor(Color.RED);
//            }
//        }, 500);
    }
}
