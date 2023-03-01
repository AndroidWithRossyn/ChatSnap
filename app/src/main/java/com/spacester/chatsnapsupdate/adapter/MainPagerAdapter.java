package com.spacester.chatsnapsupdate.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.spacester.chatsnapsupdate.fragment.ChatFragment;
import com.spacester.chatsnapsupdate.fragment.DiscoverFragment;
import com.spacester.chatsnapsupdate.fragment.EmptyFragment;

@SuppressWarnings("ALL")
public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ChatFragment.create();
            case 1:
                return EmptyFragment.create();
            case 2:
                return DiscoverFragment.create();
        }
        return null;
    }
    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Chat";
            case 1:
                return "Camera";
            case 2:
                return "Discover";

        }

        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
