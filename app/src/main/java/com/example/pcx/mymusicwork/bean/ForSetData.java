package com.example.pcx.mymusicwork.bean;

import java.util.List;

/**
 * Created by pcx on 2016/5/15.
 */
public class ForSetData {
    private List<TextData> list;
    private List<OnlineUrlData> onlineUrlDataList;

    public List<TextData> getList() {
        return list;
    }

    public void setList(List<TextData> list) {
        this.list = list;
    }

    public List<OnlineUrlData> getOnlineUrlDataList() {
        return onlineUrlDataList;
    }

    public void setOnlineUrlDataList(List<OnlineUrlData> onlineUrlDataList) {
        this.onlineUrlDataList = onlineUrlDataList;
    }
}
