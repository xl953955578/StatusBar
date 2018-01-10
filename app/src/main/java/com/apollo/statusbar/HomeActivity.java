package com.apollo.statusbar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private NoScrollViewPager viewPager;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StatusBarUtils.fullScreen(this);
        StatusBarUtils.initStatusBar(HomeActivity.this, getResources().getColor(R.color.colorPrimaryDark), StatusBarUtils.getStatusBarHeight(HomeActivity.this), true, R.id.contentId);
        initBottomBar();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.view_pager);
        viewPager.setNoScroll(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        final List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(BaseFragment.getInstance(0));
        fragments.add(BaseFragment.getInstance(1));
        fragments.add(BaseFragment.getInstance(2));
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(adapter);
    }

    private void initBottomBar() {
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_ui:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.bottom_nav_data:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.bottom_nav_service:
                        viewPager.setCurrentItem(2);
                        break;
                }
                initStatusBar(item.getItemId()==R.id.bottom_nav_service);
                return false;
            }
        });
    }

    private void initStatusBar(boolean isDark){
        StatusBarUtils.initStatusBar(HomeActivity.this, isDark?Color.TRANSPARENT:getResources().getColor(R.color.colorPrimaryDark), isDark?0:StatusBarUtils.getStatusBarHeight(HomeActivity.this), true, R.id.contentId);
        StatusBarUtils.setDarkModeCompat(HomeActivity.this,isDark);
    }
}
