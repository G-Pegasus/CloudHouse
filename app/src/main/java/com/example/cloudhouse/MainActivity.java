package com.example.cloudhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import fragment.MainFragment;
import fragment.MeFragment;
import fragment.SoloFragment;
import fragment.SquareFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected LinearLayout mMenuMain;
    protected LinearLayout mMenuSolo;
    protected LinearLayout mMenuSquare;
    protected LinearLayout mMenuMe;

    protected MainFragment mMainFragment = new MainFragment();
    protected SoloFragment mSoloFragment = new SoloFragment();
    protected SquareFragment mSquareFragment = new SquareFragment();
    protected MeFragment mMeFragment = new MeFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 获取管理类
        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_content, mMainFragment)
                .add(R.id.container_content, mSoloFragment)
                .hide(mSoloFragment)
                .add(R.id.container_content, mSquareFragment)
                .hide(mSquareFragment)
                .add(R.id.container_content, mMeFragment)
                .hide(mMeFragment)
                // 事物添加 默认：显示首页  其他页面：隐藏
                // 提交
                .commit();
    }

    public void initView() {
        mMenuMain = this.findViewById(R.id.menu_main);
        mMenuSolo = this.findViewById(R.id.menu_solo);
        mMenuSquare = this.findViewById(R.id.menu_square);
        mMenuMe = this.findViewById(R.id.menu_me);

        mMenuMain.setOnClickListener(this);
        mMenuSolo.setOnClickListener(this);
        mMenuSquare.setOnClickListener(this);
        mMenuMe.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_main:
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(mMainFragment)
                        .hide(mSoloFragment)
                        .hide(mSquareFragment)
                        .hide(mMeFragment)
                        .commit();
                break;

            case R.id.menu_solo:
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(mSoloFragment)
                        .hide(mMainFragment)
                        .hide(mSquareFragment)
                        .hide(mMeFragment)
                        .commit();
                break;

            case R.id.menu_square:
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(mSquareFragment)
                        .hide(mMainFragment)
                        .hide(mSoloFragment)
                        .hide(mMeFragment)
                        .commit();
                break;

            case R.id.menu_me:
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(mMeFragment)
                        .hide(mMainFragment)
                        .hide(mSoloFragment)
                        .hide(mSquareFragment)
                        .commit();
                break;
        }
    }
}