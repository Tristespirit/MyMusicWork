package data;

import com.example.pcx.mymusicwork.bean.ForSetData;

/**
 * Created by pcx on 2016/5/15.
 */
public class OnlineUrl {
    private ForSetData forSetData = new ForSetData();

    public static String [] musicOnline = new String[]{};
    public void OnlineUrls(){
            if (forSetData.getOnlineUrlDataList()!= null){
            for (int i = 0;i < 100;i++)
            musicOnline [i]= forSetData.getOnlineUrlDataList().get(i).getOnlineUrl();
        }
    }


}
