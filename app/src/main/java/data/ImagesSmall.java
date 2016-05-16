package data;

import com.example.pcx.mymusicwork.bean.DataBean;

/**
 * Created by pcx on 2016/5/14.
 */
public class ImagesSmall {
    DataBean dataBean = new DataBean();

    public void getUrls(){
        for (int i = 0; i <= dataBean.getSecret();i++)
            imageUrls[dataBean.getSecret()] = dataBean.getUrl_albumpic_big();

    }
    public final static String []imageUrls = new String[]{


    };
}
