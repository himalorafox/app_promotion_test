package com.example.apppromotionsdkapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.apppromotion.AppPromotion;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AppPromotion(this,"com.orafox.story",1);
        // context,package name,popup interval in minutes
    }
}
