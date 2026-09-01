package com.example.dia;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FeedFragment();
            case 1: return new DocumentsFragment();
            case 2: return new AiFragment();
            case 3: return new ServicesFragment();
            case 4: return new MenuFragment();
            default: return new FeedFragment();
        }
    }

    @Override
    public int getItemCount() { return 5; }
}