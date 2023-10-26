package com.example.nitcemagazine.MainActivityPages;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.nitcemagazine.Fragments.EductionalFragement;
import com.example.nitcemagazine.Fragments.FestFragement;
import com.example.nitcemagazine.Fragments.HomeFragement;
import com.example.nitcemagazine.Fragments.SportFragement;
import com.example.nitcemagazine.Fragments.TechnicalFragement;

public class PagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new HomeFragement();

            case 1:
                return new SportFragement();

            case 2:
                return new TechnicalFragement();

            case 3:
                return new FestFragement();

            case 4:
                return new EductionalFragement();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
