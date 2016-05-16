package com.example.pcx.mymusicwork.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.pcx.mymusicwork.R;
import com.example.pcx.mymusicwork.adapter.ViewPagerAdapter;
import com.example.pcx.mymusicwork.bean.DataBean;
import com.example.pcx.mymusicwork.fragment.BalladFragment;
import com.example.pcx.mymusicwork.fragment.ChinaFragment;
import com.example.pcx.mymusicwork.fragment.EuropeFragment;
import com.example.pcx.mymusicwork.fragment.HongKongFragment;
import com.example.pcx.mymusicwork.fragment.Hot_musicFragment;
import com.example.pcx.mymusicwork.fragment.JapanFragment;
import com.example.pcx.mymusicwork.fragment.KoreaFragment;
import com.example.pcx.mymusicwork.fragment.RockFragment;
import com.example.pcx.mymusicwork.fragment.SalesFragment;
import com.example.pcx.mymusicwork.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    //需要的fragment列表
    private List<Fragment> list_fragment;
    private List<String> list_title;
    //fragment
    private ChinaFragment mChinaFragment;
    private HongKongFragment mHongKongFragment;
    private EuropeFragment mEuropeFragment;
    private KoreaFragment mKoreaFragment;
    private JapanFragment mJapanFragment;
    private BalladFragment mBalladFragment;
    private RockFragment mRockFragment;
    private Hot_musicFragment mHot_musicFragment;
    private SalesFragment mSalesFragment;
    private TextView textView;

    private final int count = 0;
    private MusicService mService;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent startIntent = new Intent(this, MusicService.class);
        startService(startIntent);
        initService();
        initControl();
    }
    private void initService(){
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicService.MusicServiceBinder)service).getService();
            mService.setCallBack(new MusicService.OnParserCallBack() {
                @Override
                public void OnParserComplete(DataBean dataBean) {

                }
            });


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService.removeCallBack();

        }
    };


    public void initControl() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.vp_tab);
        //初始化
        mChinaFragment = new ChinaFragment();
        mHongKongFragment = new HongKongFragment();
        mEuropeFragment = new EuropeFragment();
        mKoreaFragment = new KoreaFragment();
        mJapanFragment = new JapanFragment();
        mBalladFragment = new BalladFragment();
        mRockFragment = new RockFragment();
        mHot_musicFragment = new Hot_musicFragment();
        mSalesFragment = new SalesFragment();
        //向list中add数据
        list_fragment = new ArrayList<>();
        list_fragment.add(mChinaFragment);
        list_fragment.add(mHongKongFragment);
        list_fragment.add(mEuropeFragment);
        list_fragment.add(mKoreaFragment);
        list_fragment.add(mJapanFragment);
        list_fragment.add(mBalladFragment);
        list_fragment.add(mRockFragment);
        list_fragment.add(mHot_musicFragment);
        list_fragment.add(mSalesFragment);
        //tab名称
        list_title = new ArrayList<>();
        list_title.add(getString(R.string.china_land));
        list_title.add(getString(R.string.HongKong));
        list_title.add(getString(R.string.europe));
        list_title.add(getString(R.string.korea));
        list_title.add(getString(R.string.japan));
        list_title.add(getString(R.string.ballad));
        list_title.add(getString(R.string.rock));
        list_title.add(getString(R.string.hot_music));
        list_title.add(getString(R.string.sales));
        //setting
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //add tab name
        for (int i = 0; i < 9; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(list_title.get(i)));
        }

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), list_fragment,list_title);

        //加载viewpager
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //设置setOnTabSelectedListener
    }

    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}
