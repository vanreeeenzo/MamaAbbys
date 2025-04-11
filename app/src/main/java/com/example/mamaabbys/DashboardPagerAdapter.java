package com.example.mamaabbys;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new InventoryFragment();
            case 1:
                return new SalesFragment();
            case 2:
                return new DeliveryFragment();
            default:
                return new InventoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
} 