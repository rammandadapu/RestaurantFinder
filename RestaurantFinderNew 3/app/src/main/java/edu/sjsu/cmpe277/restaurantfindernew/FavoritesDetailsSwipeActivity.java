package edu.sjsu.cmpe277.restaurantfindernew;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoritesDetailsSwipeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ViewPager viewPager;
    PageAdapter adapter;
    List<String> businessIdList;
    List<String> businessNamesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details_swipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        String businessId = extras.getString(RestaurantDetailsActivity.TAG_RES_ID);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        businessIdList=extras.getStringArrayList("list");
        businessNamesList=extras.getStringArrayList("namesList");
        viewPager=(ViewPager)findViewById(R.id.pager);
        adapter=new PageAdapter(getSupportFragmentManager(),businessIdList,businessId);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(businessIdList.indexOf(businessId));

        getSupportActionBar().setTitle(businessNamesList.get(businessIdList.indexOf(businessId)));
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float f, int j) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setTitle(businessNamesList.get(position));
    }
}
