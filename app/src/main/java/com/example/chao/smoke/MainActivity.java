package com.example.chao.smoke;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao on 2017/4/30.
 * 主活动四个fragment在此初始化
 */

public class MainActivity extends FragmentActivity
{
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private LinearLayout mTabBtnlianjie;
    private LinearLayout mTabBtnaddDeviced;
    private LinearLayout mTabBtnWatchDeviced;
    private LinearLayout mTabBtnSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager)findViewById(R.id.id_viewpager);
        ((Data)getApplication()).topicList.add("DeviceList :");
        initView();
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                    return fragmentList.get(position);

            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int currentIndex;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTabBtn();
                switch (position)
                {
                    case 0:
                        ((ImageButton) mTabBtnlianjie.findViewById(R.id.btn_tab_bottom_weixin))
                                .setImageResource(R.drawable.tab_weixin_pressed);
                        break;
                    case 1:
                        ((ImageButton) mTabBtnaddDeviced.findViewById(R.id.btn_tab_bottom_friend))
                                .setImageResource(R.drawable.tab_find_frd_pressed);
                        break;
                    case 2:
                        ((ImageButton) mTabBtnWatchDeviced.findViewById(R.id.btn_tab_bottom_contact))
                                .setImageResource(R.drawable.tab_address_pressed);
                        break;
                    case 3:
                        ((ImageButton) mTabBtnSettings.findViewById(R.id.btn_tab_bottom_setting))
                                .setImageResource(R.drawable.tab_settings_pressed);
                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void initView()
    {
        mTabBtnlianjie = (LinearLayout) findViewById(R.id.id_tab_bottom_weixin);
        mTabBtnaddDeviced = (LinearLayout)findViewById(R.id.id_tab_bottom_friend);
        mTabBtnWatchDeviced = (LinearLayout) findViewById(R.id.id_tab_bottom_contact);
        mTabBtnSettings = (LinearLayout) findViewById(R.id.id_tab_bottom_setting);
        connectTab connectTab = new connectTab();
        addDevicedTab addDevicedTab = new addDevicedTab();
        showDeviceTab showDeviceTab = new showDeviceTab();
        setingTab setingTab = new setingTab();
        fragmentList.add(connectTab);
        fragmentList.add(addDevicedTab);
        fragmentList.add(showDeviceTab);
        fragmentList.add(setingTab);
    }
    protected void resetTabBtn() {
        ((ImageButton) mTabBtnlianjie.findViewById(R.id.btn_tab_bottom_weixin))
                .setImageResource(R.drawable.tab_weixin_normal);
        ((ImageButton) mTabBtnaddDeviced.findViewById(R.id.btn_tab_bottom_friend))
                .setImageResource(R.drawable.tab_find_frd_normal);
        ((ImageButton) mTabBtnWatchDeviced.findViewById(R.id.btn_tab_bottom_contact))
                .setImageResource(R.drawable.tab_address_normal);
        ((ImageButton) mTabBtnSettings.findViewById(R.id.btn_tab_bottom_setting))
                .setImageResource(R.drawable.tab_settings_normal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((Data)getApplicationContext()).topicList.clear();
    }
}
