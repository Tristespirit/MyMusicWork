package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pcx on 2016/3/22.
 * 用于缓存下载的图片
 */
public class ImageLoader {
    private boolean mIsDiskLruCache = false;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int DISK_CACHE_INDEX = 0;
    private static LruCache<String, Bitmap> mMemoryCach;
    private DiskLruCache mDiskLruCache;
    private Context mContext;

    private static ImageLoader mImageLoader;


    private ImageLoader(Context context){
        mContext = context.getApplicationContext();
        //获取应用最大内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory/8;
        mMemoryCach = new LruCache<String,Bitmap>(cacheSize){
            protected int sizeoOf(String key,Bitmap bitmap){
                return  bitmap.getByteCount();
            }
        };
        File cacheDir = getDiskCacheDir(mContext,"bitamp");
        if (!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        if (getUsableSpace(cacheDir) > DISK_CACHE_SIZE){
            try {
                mDiskLruCache = DiskLruCache.open(cacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCache= true;
            }catch (IOException io){
                io.printStackTrace();
            }
        }

    }
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
    private long getUsableSpace(File path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            return path.getUsableSpace();
        }
        final StatFs statFs = new StatFs(path.getPath());
        return (long) statFs.getBlockSizeLong() * (long) statFs.getAvailableBlocksLong();
    }
    /**
 * 获取ImageLoader实例
**/
    public static ImageLoader getInstance(Context context){
        if (mImageLoader == null){
            mImageLoader = new ImageLoader(context);
        }
        return mImageLoader;
    }

    /**
     * 将图片储存到LruCache
     * key就是url
     * Bitmap就是下载的Bitmap对象
     */
    public void addBitmapToMemory(String key,Bitmap bitmap){

        if (getBitmapFromMemory(key) == null){
            mMemoryCach.put(key, bitmap);
        }
    }

    /**
     * 从LruCache获取图片
     * key就是url
     * @return
     */
    public Bitmap getBitmapFromMemory(String key){
        return mMemoryCach.get(key);
    }

    public Bitmap getBitampFromDiskCache(String url,int reqWidth)throws IOException{
        if (Looper.myLooper() == Looper.getMainLooper()){
            Log.w("DFS", "DS");
        }
        if (mDiskLruCache == null){
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null){
            FileInputStream fileInputStream = (FileInputStream)snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = decodeSampleBitmapFromFileDescriptor(fileDescriptor,reqWidth);
            if (bitmap != null){
                addBitmapToMemory(key,bitmap);
            }
        }
        return bitmap;
    }

    private String hashKeyFromUrl(String url){
        String cacheKey;
        try{
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey =byteToHexString(messageDigest.digest());
        }catch (NoSuchAlgorithmException e){
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String byteToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < bytes.length;i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * BitmapFactory.Options可以定义图片如何读取到内存
     * 定义计算方法
     * @param options
     * @param reqWidth
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth){
        //实际图片宽度
        final int with = options.outWidth;
        int inSampleSize = 1;
        if (with > reqWidth){
            //计算实际宽度和目标宽度的比率
            final int widthRetio = Math.round((float)with/(float)reqWidth);
            inSampleSize = widthRetio;
        }
        return inSampleSize;
    }

    public static Bitmap decadeSampleBitmapResource(String name,int reqWidth){
        //设置inJustDecodeBounds为true
        // 设为true那么将不返回实际的bitmap对象，不给其分配内存空间但是可以得到一些解码边界信息即图片大小等信息
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(name,options);
        //使用上面定义的计算方法来计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth);
        //使用获得的inSampleSize 来解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(name,options);

    }
    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd,int reqWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }
}
