package com.example.pcx.mymusicwork.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pcx.mymusicwork.R;

/**
 * Created by pcx on 2016/5/14.
 */
public class RockFragment extends Fragment {
    private TextView textView;
    private RecyclerView mRecyclerView;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);

        return view;
    }
}
