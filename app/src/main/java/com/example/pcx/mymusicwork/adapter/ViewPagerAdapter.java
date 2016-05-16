package com.example.pcx.mymusicwork.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by pcx on 2016/5/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list_fragment;
    private List<String> list_Title;



    public Fragment getItem(int position) {
        return list_fragment.get(position);
    }

    @Override
    public int getCount() {
        return list_Title.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return list_Title.get(position % list_Title.size());
    }

    public ViewPagerAdapter(FragmentManager fm,List<Fragment> list_fragment, List<String> list_title){
        super(fm);
        this.list_fragment = list_fragment;
        this.list_Title = list_title;
    }

    public Object instantiateItem(ViewGroup container, int position) {

        return list_fragment.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
         //View view = (View) object;
        //container.removeView(view);
        Fragment fragment = (Fragment)object;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
