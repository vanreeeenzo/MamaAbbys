package com.example.mamaabbys;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardViewPagerAdapter extends FragmentStateAdapter {
    public DashboardViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DashboardTabFragment fragment = new DashboardTabFragment();
        Bundle args = new Bundle();
        args.putInt("tab_position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
} 