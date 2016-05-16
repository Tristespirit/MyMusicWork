package com.example.pcx.mymusicwork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.pcx.mymusicwork.bean.DataBean;
import com.example.pcx.mymusicwork.bean.ForSetData;
import com.example.pcx.mymusicwork.bean.OnlineUrlData;
import com.example.pcx.mymusicwork.bean.TextData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DBOpenHelper;
import util.Net;

/**
 * Created by pcx on 2016/5/14.
 */
public class MusicService extends Service {
    private Map<String,String> map = new HashMap<>();
    private Map<String,String>maps = new HashMap<>();
    private Map<String,String>mapss = new HashMap<>();
    private DBOpenHelper openHelper;
    private int SHOW_RESPONSE = 0x123;
    private final int REPEAT_MSG = 0x01;
    private OnParserCallBack callBack;
    private MusicServiceBinder binder = new MusicServiceBinder();
    private Handler handler = new Handler();
    private Message message = new Message();
    /*private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {

                if (msg.what == 0) {

                } else if (msg.what == 1) {
                    //mData[1] = (String) msg.obj;
                    //parserChinaData(mData[1]);

                } else if (msg.what == 2) {
                    //mData[2] = (String) msg.obj;
                    //parserChinaData(mData[2]);

                } else if (msg.what == 3) {
                    //mData[3] = (String) msg.obj;
                    //parserChinaData(mData[3]);

                } else if (msg.what == 4) {
                    //mData[4] = (String) msg.obj;
                    //parserChinaData(mData[4]);

                } else if (msg.what == 5) {
                    //mData[5] = (String) msg.obj;
                    //parserChinaData(mData[5]);

                } else if (msg.what == 6) {
                    //mData[6] = (String) msg.obj;
                    //parserChinaData(mData[6]);

                } else if (msg.what == 7) {
                    //mData[7] = (String) msg.obj;
                    //parserChinaData(mData[7]);

                } else {
                    //mData[8] = (String) msg.obj;
                    //parserChinaData(mData[8]);

                }
            }
        }
    };*/

    public interface OnParserCallBack {
        void OnParserComplete(DataBean dataBean);
    }
    public MusicService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public void onCreate() {
        super.onCreate();
        handler.sendEmptyMessage(REPEAT_MSG);
        myHttp();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }
    public void myHttp(){
        map.put("showapi_appid","18997");
        maps.put("showapi_sign","77c2bc666f31408f96be54412b844e6a");
        mapss.put("topid", "5");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = Net.post("http://route.showapi.com/213-4", map, maps, mapss);
                if (response != null){
                    //message.what = 0;
                    //message.obj = response;
                    Log.i("iiiiiii", ">>>>>>>>>>>>" + response);
                    parserChinaData(response);

                //hd.sendMessage(message);

            }

            }
        }).start();

            //if (message.obj != null && message.what == SHOW_RESPONSE){


    }


    public DataBean parserChinaData(String mData) {
        DataBean dataBean = null;
        TextData textData = null;
        OnlineUrlData onlineUrlData = null;
        try {
            JSONObject json  = new JSONObject(mData);
            int code = json.getInt("showapi_res_code");
            if (code == 0) {
                dataBean = new DataBean();
                textData = new TextData();
                onlineUrlData = new OnlineUrlData();
                JSONObject bodyJson = json.getJSONObject("showapi_res_body");
                JSONObject pageJson = bodyJson.getJSONObject("pagebean");
                JSONArray songListJson = pageJson.getJSONArray("songlist");
                List<TextData> dataBeanList = new ArrayList<>();
                List<OnlineUrlData> onlineUrlDatas = new ArrayList<>();
                ForSetData forSetData = new ForSetData();
                for (int i = 0;i < songListJson.length();i++){
                    JSONObject songJson = songListJson.getJSONObject(i);

                    dataBean.setUrl_albumpic_big(songJson.optString("albumpic_big"));

                    dataBean.setUrl_albumpic_small(songJson.optString("albumpic_small"));
                    dataBean.setDownUrl(songJson.optString("downUrl"));
                    textData.setSingerName(songJson.optString("singername"));
                    //dataBean.setSeconds(songJson.getInt("seconds"));
                    textData.setSongName(songJson.optString("songname"));
                    Log.v("ASGERGSERSRGSR", textData.getSingerName() + "dshhhhhhhhhsdfhsdf");

                    onlineUrlData.setOnlineUrl(songJson.optString("url"));
                    Log.v("ASGERGSERSRGSR", onlineUrlData.getOnlineUrl() + "dshhhhhhhhhsdfhsdf");
                    dataBean.setSecret(i);
                    dataBeanList.add(textData);
                    onlineUrlDatas.add(onlineUrlData);
                }
                forSetData.setList(dataBeanList);

                forSetData.setOnlineUrlDataList(onlineUrlDatas);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return dataBean;
    }
    public void setCallBack(OnParserCallBack callback) {
        this.callBack = callback;
    }

    public void removeCallBack() {
        callBack = null;
    }

    public class MusicServiceBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }

    }
    public MusicService(Context context) {
        openHelper = new DBOpenHelper(context);
    }

    /**
     * 获取每条线程已经下载的文件长度
     *
     * @param path
     * @return
     */
    public Map<Integer, Integer> getData(String path) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db
                .rawQuery(
                        "select threadid, downlength from filedownlog where downpath=?",
                        new String[] { path });
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        while (cursor.moveToNext()) {
            data.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public void save(String path, Map<Integer, Integer> map) {// int threadid,
        // int position
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL(
                        "insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
                        new Object[] { path, entry.getKey(), entry.getValue() });
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     *
     */
    public void update(String path, int threadId, int pos) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL(
                "update filedownlog set downlength=? where downpath=? and threadid=?",
                new Object[] { pos, path, threadId });
        db.close();
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     *
     * @param path
     */
    public void delete(String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from filedownlog where downpath=?",
                new Object[] { path });
        db.close();
    }
}
