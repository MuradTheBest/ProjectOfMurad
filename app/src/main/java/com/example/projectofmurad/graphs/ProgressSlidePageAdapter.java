package com.example.projectofmurad.graphs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectofmurad.UserAttendancesFragment;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;

public class ProgressSlidePageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    public ProgressSlidePageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ProgressSlidePageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ProgressSlidePageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return UserAttendancesFragment.newInstance(FirebaseUtils.getCurrentUID(), null, false);
            case 1:
                return new PrivateTrainingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
