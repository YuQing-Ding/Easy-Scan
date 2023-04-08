/*
 * Copyright (C) 2019 Easy Software. All Rights Reserved.
 */
package com.EasyCN.Easy.Scan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;


public class EasyScanMainActivity extends AppCompatActivity {
    ImageView imageView;

public static final String TAG="EasyScanMainActivity";
public static final String TEST="EasyScanMainActivity_test";
    private AlertDialog.Builder alertDialog;
    ImageButton scan;
    private AdView firstad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;
        imageView =(ImageView)findViewById(R.id.imageView);
        StatConfig.setDebugEnable(false);
        StatService.registerActivityLifecycleCallbacks(this.getApplication());
        firstad = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        firstad.loadAd(adRequest);
        if (ConnectivityHelper.isConnectedToNetwork(context)) {
            //Show the connected screen
        } else {
            Intent intent = new Intent(EasyScanMainActivity.this, network_error.class);
            startActivity(intent);
            super.onBackPressed();
        }
        Glide.with(this).load("https://download.easysoftware.xyz/App_Logo/Logo.png").into(imageView);
        scan = (ImageButton)findViewById(R.id.camera);
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EasyScanMainActivity.this, GeneralActivity.class);
                startActivity(intent);
                }
        });

        // 请选择您的初始化方式
         initAccessToken();
//        initAccessTokenWithAkSk();
        // OCR.getInstance().initWithToken(getApplicationContext(), "您获取的oauth access_token");
    }


    private void initAccessToken() {

        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(), "请填入您的AK", "请填入您的SK");
    }

    private void alertText(String title, String message) {
        boolean isNeedLoop = false;
        if (Looper.myLooper() == null) {
            Looper.prepare();
            isNeedLoop = true;

        }

    }
    //动态处理权限问题

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]  permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance().release();
    }
}
