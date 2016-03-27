package edu.sjsu.cmpe277.restaurantfindernew;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by ram.mandadapu on 3/26/16.
 */
public class PageAdapter extends FragmentStatePagerAdapter {

    final int MAX_PAGES;
    List<String> busineesIdList;
    public PageAdapter(FragmentManager fm,List<String> busineesIdList,String businessId) {
        super(fm);
        this.busineesIdList=busineesIdList;
        MAX_PAGES=busineesIdList.size();

    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment=  RestaurantDetailsFragment.newInstance(position,busineesIdList.get(position));

        return  fragment;
    }

    @Override
    public int getCount() {
        return MAX_PAGES;
    }
}
