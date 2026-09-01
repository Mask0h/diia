package com.example.dia;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        viewPager.setAdapter(new ViewPagerAdapter(this));
        viewPager.setUserInputEnabled(false); // свайп отключён как в Дія

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_feed)          viewPager.setCurrentItem(0, false);
            else if (id == R.id.nav_documents) viewPager.setCurrentItem(1, false);
            else if (id == R.id.nav_ai)        viewPager.setCurrentItem(2, false);
            else if (id == R.id.nav_services)  viewPager.setCurrentItem(3, false);
            else if (id == R.id.nav_menu)      viewPager.setCurrentItem(4, false);
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int itemId;
                switch (position) {
                    case 1: itemId = R.id.nav_documents; break;
                    case 2: itemId = R.id.nav_ai; break;
                    case 3: itemId = R.id.nav_services; break;
                    case 4: itemId = R.id.nav_menu; break;
                    default: itemId = R.id.nav_feed; break;
                }
                bottomNav.setSelectedItemId(itemId);
            }
        });
    }
}