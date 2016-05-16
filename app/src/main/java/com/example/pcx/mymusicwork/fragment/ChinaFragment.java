package com.example.pcx.mymusicwork.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pcx.mymusicwork.R;
import com.example.pcx.mymusicwork.activity.MainActivity;
import com.example.pcx.mymusicwork.bean.ForSetData;
import com.example.pcx.mymusicwork.bean.TextData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import data.DividerItemDecoration;
import data.ImagesSmall;
import util.ImageLoader;

/**
 * Created by pcx on 2016/5/14.
 */
public class ChinaFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageLoader imageLoader;
    public static final int PAGE_SIZE = 10;
    private int page;
    private List<ImageView> imageViewsList = new ArrayList<>();

    private ImageView imageView;
    private int columnWidth;
    private ForSetData forSetData = new ForSetData();
    private Message message = new Message();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);

        //imageView = (ImageView)view.findViewById(R.id.iv_album);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment);
        columnWidth = imageView.getWidth();
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new RootAdapter(forSetData.getList()));
    }




    class RootAdapter extends RecyclerView.Adapter<RootAdapter.MyViewHolder>{
        private Set<LoadImageTask> taskCollection;
        private List<TextData> list;

        public RootAdapter(List<TextData> list){
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.recycle_item,parent,false));
            return holder;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            //public ImageView iv_1;
            public TextView tv_1;
            public TextView tv_2;
            //public TextView tv_3;
            //public ImageView iv_2;
            public MyViewHolder(View v) {
                super(v);
                //iv_1 = (ImageView)v.findViewById(R.id.iv_album);
                tv_1 = (TextView)v.findViewById(R.id.tv_name_song);
                tv_2 = (TextView)v.findViewById(R.id.tv_name_singer);
                //tv_3 = (TextView)v.findViewById(R.id.tv_play);
                //iv_2 = (ImageView)v.findViewById(R.id.iv_down);
            }
        }


        public void onBindViewHolder(MyViewHolder holder, int position) {
                Log.d("gagregaergrege", getItem(position).getSingerName() + "nnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                holder.tv_1.setText(getItem(position).getSongName());
            holder.tv_2.setText(getItem(position).getSingerName());

        }

        public int getItemCount() {
            return list.size();
        }
        public TextData getItem(int position){
            return list.get(position);
        }




        private  Handler handler = new Handler() {
            public void handlerMessage(Message message) {
                RootAdapter rootAdapter = (RootAdapter) message.obj;
                rootAdapter.loadMemoryImages();
                Message msg = new Message();
                handler.sendMessageDelayed(msg, 5);
            }
        };

        public void loadMemoryImages() {
            int startIndex = page * PAGE_SIZE;
            int endIndex = page * PAGE_SIZE + PAGE_SIZE;
            if (hasSDCard()) {

                if (startIndex < ImagesSmall.imageUrls.length) {
                    Toast.makeText(getContext(), "loading...", Toast.LENGTH_SHORT).show();
                    if (endIndex > ImagesSmall.imageUrls.length) {
                        endIndex = ImagesSmall.imageUrls.length;
                    }
                    for (int i = startIndex; i < endIndex; i++) {
                        LoadImageTask task = new LoadImageTask();
                        taskCollection.add(task);
                        task.execute(i);
                    }

                }
            }
        }

        private boolean hasSDCard() {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        }

        class LoadImageTask extends AsyncTask<Integer, Void, Bitmap> {
            private int imagePosition;
            //图片的url
            private String mImageUrl;
            //可重复使用的ImageView
            private ImageView mImageView;

            public LoadImageTask() {
                View view = new View(getContext());
                mImageView = (ImageView)view.findViewById(R.id.iv_album);

            }

            protected Bitmap doInBackground(Integer... params) {
                imagePosition = params[0];
                mImageUrl = ImagesSmall.imageUrls[imagePosition];

                Bitmap imageBitmap = imageLoader.getBitmapFromMemory(mImageUrl);
                if (imageBitmap == null) {
                    try {
                        imageBitmap = imageLoader.getBitampFromDiskCache(mImageUrl, columnWidth);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (imageBitmap == null) {
                    imageBitmap = loadImage(mImageUrl);
                }

                return imageBitmap;
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {

                }
                taskCollection.remove(this);

            }

            /**
             * 根据传入的url，对图片进行加载，若sd card有就从中读取，否则从网上下载
             */
            private Bitmap loadImage(String imageUrl) {
                File file = new File(getImagePath(imageUrl));
                if (!file.exists()) {
                    downLoadImage(imageUrl);
                }
                if (imageUrl != null) {
                    Bitmap bitmap = ImageLoader.decadeSampleBitmapResource(file.getPath(), columnWidth);
                    if (bitmap != null) {
                        imageLoader.addBitmapToMemory(imageUrl, bitmap);
                        return bitmap;
                    }
                }
                return null;
            }

            /**
             * 向ImageView中添加图片
             */
            private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);
                if (mImageView != null) {

                     mImageView.setImageBitmap(bitmap);
                } else {
                    ImageView imageView = new ImageView(getContext());
                    //可以设置控件的参数setLayoutParams（）
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(bitmap);
                    //控制图片来匹配ImageView（setScaleType()）,FIT_XY:把图片不按比例扩大/缩小到View的大小显示
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setPadding(5, 5, 5, 5);
                    imageView.setTag(R.string.image_url, mImageUrl);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra("image_position", imagePosition);
                            getActivity().startActivity(intent);
                        }
                    });
                    imageViewsList.add(imageView);
                }

            }

            private void downLoadImage(String imageUrl) {
                HttpURLConnection connection = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                BufferedInputStream bis = null;
                File imageFile = null;
                try {

                    URL url = new URL(imageUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5 * 1000);
                    connection.setReadTimeout(15 * 1000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    bis = new BufferedInputStream(connection.getInputStream());
                    imageFile = new File(getImagePath(imageUrl));
                    fos = new FileOutputStream(imageFile);
                    bos = new BufferedOutputStream(fos);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = bis.read(bytes)) != -1) {
                        bos.write(bytes, 0, length);
                        bos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bis != null) {
                            bis.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (imageFile != null) {
                    Bitmap bitmap = ImageLoader.decadeSampleBitmapResource(imageFile.getPath(), columnWidth);

                    if (bitmap != null) {
                        imageLoader.addBitmapToMemory(imageUrl, bitmap);
                    }

                }

            }

            private String getImagePath(String imageUrl) {
                int lastIndex = imageUrl.lastIndexOf("/");
                String imageName = imageUrl.substring(lastIndex + 1);
                String imageDir = Environment.getExternalStorageDirectory().getPath() + "/PhotoWallFiles/";
                File file = new File(imageDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String imagePath = imageDir + imageName;
                return imagePath;

            }
        }
    }

}
