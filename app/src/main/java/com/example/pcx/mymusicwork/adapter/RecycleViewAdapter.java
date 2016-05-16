package com.example.pcx.mymusicwork.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pcx.mymusicwork.R;
import com.example.pcx.mymusicwork.bean.TextData;

import java.util.List;

/**
 * Created by pcx on 2016/5/14.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    private List<TextData> list;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_1;
        public TextView tv_1;
        public TextView tv_2;
        public TextView tv_3;
        public ImageView iv_2;
        private View rootView;
        public MyViewHolder(View v) {
            super(v);
            iv_1 = (ImageView)v.findViewById(R.id.iv_album);
            tv_1 = (TextView)v.findViewById(R.id.tv_name_song);
            tv_2 = (TextView)v.findViewById(R.id.tv_name_singer);
            tv_3 = (TextView)v.findViewById(R.id.tv_play);
            iv_2 = (ImageView)v.findViewById(R.id.iv_down);
            rootView = v.findViewById(R.id.ly_recycle);
        }
    }


    public RecycleViewAdapter(List<TextData> list){
        this.list = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }


    public int getItemCount() {
        return list.size();
    }
    public TextData getItem(int position){
        return list.get(position);
    }
}
