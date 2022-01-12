package com.example.Gallery07;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.example.Gallery07.Utils.utils_fragment1;


public class MainActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 2;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setmContext(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screensliderpageractivity);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean isDarkMode = preferences.getBoolean("darkMode", false);
        Log.i("isDarkmode", isDarkMode+"");
        if (isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        viewPager2 = findViewById(R.id.viewPager2);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager2.setAdapter(pagerAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("SDK > BuildVersion TRUE");
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);  // Comment 26
                System.out.println("go to requestPermissions");
            }
        }
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment1.stopActionMode();
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_1).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_2).setChecked(true);
                        break;
                }
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_1:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.menu_2:
                        viewPager2.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });
    }

    public void setNavVisible(boolean val) {
        if (val) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setClickable(true);
        } else {
            bottomNavigationView.setVisibility(View.INVISIBLE);
            bottomNavigationView.setClickable(false);
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    utils_fragment1 = new Fragment1();
                    return utils_fragment1;
                case 1:
                    return new Fragment2Container();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}
