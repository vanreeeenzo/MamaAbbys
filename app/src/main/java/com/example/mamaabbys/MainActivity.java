package com.example.mamaabbys;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DashboardPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Create adapter
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Inventory");
                    tab.setIcon(R.drawable.ic_package);
                    break;
                case 1:
                    tab.setText("Sales");
                    tab.setIcon(R.drawable.ic_chart);
                    break;
                case 2:
                    tab.setText("Delivery");
                    tab.setIcon(R.drawable.ic_truck);
                    break;
            }
        }).attach();

        FirebaseDatabase.getInstance().getReference().child("Inventory").child("Meat").setValue("TJ Hotdog");
    }
}