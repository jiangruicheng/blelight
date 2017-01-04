package com.jiangruicheng.btlight.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.fragment.LightFragment;
import com.jiangruicheng.btlight.fragment.MusicScreen;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowAcivity extends AppCompatActivity {

    @BindView(R.id.frame)
    FrameLayout frame;
    @BindView(R.id.bottom_navigation)
    BottomNavigationBar bottomNavigation;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_acivity);
        unbinder = ButterKnife.bind(this);
        initnavigation();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, new LightFragment()).commit();
        bottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                ontabbarselect(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initnavigation() {
        bottomNavigation.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigation.setActiveColor(android.R.color.holo_blue_light).setBarBackgroundColor(android.R.color.background_light);
        bottomNavigation.addItem(new BottomNavigationItem(R.mipmap.dashuju, "Light"))
                .addItem(new BottomNavigationItem(R.mipmap.dashuju, "Music"))
                .addItem(new BottomNavigationItem(R.mipmap.dashuju, "Timer"))
                .addItem(new BottomNavigationItem(R.mipmap.dashuju, "About"))
                .setFirstSelectedPosition(0).initialise();
    }

    private FragmentManager fragmentManager;

    private void ontabbarselect(int position) {
        switch (position) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.frame, new LightFragment()).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.frame, new MusicScreen()).commit();
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
