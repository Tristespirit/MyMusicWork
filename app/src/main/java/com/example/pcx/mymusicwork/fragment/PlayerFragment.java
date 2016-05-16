package com.example.pcx.mymusicwork.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pcx.mymusicwork.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import data.OnlineUrl;
import util.DownloadProgressListener;
import util.FileDownloader;
import util.Player;

/**
 * Created by pcx on 2016/5/15.
 */
public class PlayerFragment extends Fragment {
    protected SeekBar seekBar;
    private List<Map<String, String>> data;
    private int current;
    private ImageView ivButton;
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;

    private EditText pathText; // url地址
    private TextView resultView;
    private Button downloadButton;
    private Button stopButton;
    private ProgressBar progressBar;
    private Button playBtn;
    private Player player; // 播放器
    private SeekBar musicProgress; // 音乐进度
    private OnlineUrl onlineUrl = new OnlineUrl();

    private Handler handler = new UIHandler();
    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) progressBar.getProgress()
                            / (float) progressBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    resultView.setText(result + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) { // 下载完成
                        Toast.makeText(getContext(), R.string.success,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getContext(), R.string.error,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_layout, container, false);
        ButtonClickListener listener = new ButtonClickListener();
        downloadButton = (Button) view.findViewById(R.id.bu_down);
        downloadButton.setOnClickListener(listener);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        playBtn = (Button) view.findViewById(R.id.iv_player);
        playBtn.setOnClickListener(listener);
        musicProgress = (SeekBar) view.findViewById(R.id.seekbar);
        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        onlineUrl.OnlineUrls();
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bu_down: // 开始下载
                    // http://abv.cn/music/光辉岁月.mp3，可以换成其他文件下载的链接
                    String path = "http://tsmusic24.tc.qq.com/106097780.mp3";
                    String filename = path.substring(path.lastIndexOf('/') + 1);

                    try {
                        // URL编码（这里是为了将中文进行URL编码）
                        filename = URLEncoder.encode(filename, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        // File savDir =
                        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        // 保存路径
                        File savDir = Environment.getExternalStorageDirectory();
                        download(path, savDir);
                    } else {
                        Toast.makeText(getContext(),
                                R.string.error, Toast.LENGTH_LONG).show();
                    }
                    downloadButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    break;
                /*case R.id.stopbutton: // 暂停下载
                    exit();
                    Toast.makeText(getContext(),
                            "Now thread is Stopping!!", Toast.LENGTH_LONG).show();
                    downloadButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    break;*/
                case R.id.iv_player:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.playUrl("http://ws.stream.qqmusic.qq.com/106097780.m4a?fromtag=46");
                        }
                    }).start();
                    break;
            }
        }

        /*
         * 由于用户的输入事件(点击button, 触摸屏幕....)是由主线程负责处理的，如果主线程处于工作状态，
         * 此时用户产生的输入事件如果没能在5秒内得到处理，系统就会报“应用无响应”错误。
         * 所以在主线程里不能执行一件比较耗时的工作，否则会因主线程阻塞而无法处理用户的输入事件，
         * 导致“应用无响应”错误的出现。耗时的工作应该在子线程里执行。
         */
        private DownloadTask task;

        private void exit() {
            if (task != null)
                task.exit();
        }

        private void download(String path, File savDir) {
            task = new DownloadTask(path, savDir);
            new Thread(task).start();
        }

        /**
         *
         * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
         * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
         *
         */
        private final class DownloadTask implements Runnable {
            private String path;
            private File saveDir;
            private FileDownloader loader;

            public DownloadTask(String path, File saveDir) {
                this.path = path;
                this.saveDir = saveDir;
            }

            /**
             * 退出下载
             */
            public void exit() {
                if (loader != null)
                    loader.exit();
            }

            DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
                @Override
                public void onDownloadSize(int size) {
                    Message msg = new Message();
                    msg.what = PROCESSING;
                    msg.getData().putInt("size", size);
                    handler.sendMessage(msg);
                }
            };

            public void run() {
                try {
                    // 实例化一个文件下载器
                    loader = new FileDownloader(getContext(), path, saveDir, 3);
                    // 设置进度条最大值
                    progressBar.setMax(loader.getFileSize());
                    loader.download(downloadProgressListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(FAILURE)); // 发送一条空消息对象
                }
            }
        }
    }

    // 进度改变
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }


}
