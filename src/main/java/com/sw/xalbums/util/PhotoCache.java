package com.sw.xalbums.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.io.OutputStream;
import libcore.io.DiskLruCache;

/**
 * Created by dongjl1 on 2016/1/8.
 */
public class PhotoCache {
    private static PhotoCache cache;
    private static ConcurrentHashMap<String, SoftReference<Bitmap>> currentHashmap
            = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
    private  LruCache<String, Bitmap> mCache;
    private Context mContext;
    final static int maxCacheSize = (int)(Runtime.getRuntime().maxMemory() / 8);
    private Set<Loadimage> mLoaderTasks;
    DiskLruCache mDiskLruCache = null;
    private PhotoCache(Context ctx)
    {
        mContext = ctx;
        mLoaderTasks = new HashSet<>();
        mCache = new LruCache<String , Bitmap>(maxCacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getByteCount();
            }
            @Override
            protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue)
            {
                if(oldValue != null)
                {
                    currentHashmap.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }
        };
        try {
            File cacheDir = getDiskCacheDir(mContext, "thumb");
            if (!cacheDir.exists()) {
              cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static PhotoCache getInstance(Context ctx)
    {
        if(cache == null)
            cache = new PhotoCache(ctx);
        return cache;
    }

    public void add(String path,Bitmap bitmap)
    {
        String md5key = hashKeyForDisk(path);
        mCache.put(md5key,bitmap);
    }

    public Bitmap get(String key)
    {
        Bitmap bitmap = null ;
        String md5key = hashKeyForDisk(key);
        try
        {
            bitmap = mCache.get(md5key);
            if(bitmap == null)
            {
                if(currentHashmap.get(md5key) != null)
                {
                        return currentHashmap.get(md5key).get();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public synchronized Bitmap getSync(String key)
    {
        Bitmap bitmap = null;
        String md5key = hashKeyForDisk(key);
        if(bitmap == null)
        {
            try{
                DiskLruCache.Snapshot snapShot;
                snapShot = mDiskLruCache.get(md5key);
                if (snapShot != null) {
                    InputStream inputStream = snapShot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    mCache.put(md5key, bitmap);
                }else {
                    bitmap = getImageThumbnail(mContext, mContext.getContentResolver(), key);
                    if (bitmap == null) {
                        bitmap = decodeSampledBitmapFromResource(key, 50, 50);
                    }
                    if (bitmap != null) {
                        mCache.put(md5key, bitmap);
                        DiskLruCache.Editor editor = mDiskLruCache.edit(md5key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            editor.commit();
                        }
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return bitmap;
    }


    public void setImageView(String imageFilePath, ImageView imageView)
    {
        if(imageFilePath == null || imageFilePath.trim().equals(""))
        {
            imageView.setImageBitmap(null);
        }else{
            Bitmap bitmap = get(imageFilePath);
           if(bitmap != null)
           {
               imageView.setImageBitmap(bitmap);
           }
            else {
               imageView.setImageBitmap(null);
               Loadimage task = new Loadimage();
               mLoaderTasks.add(task);
               task.execute(imageFilePath, imageView);
           }
        }
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
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

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public  Bitmap decodeSampledBitmapFromResource(String path,int reqWidth, int reqHeight) {
        Bitmap bitmap =  null;
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            if(fis.available() < (1024*10))
            {
                bitmap = BitmapFactory.decodeFile(path);
            }
            else {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    public  Bitmap getImageThumbnail(Context context, ContentResolver cr, String Imagepath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, };
        String whereClause = MediaStore.Images.Media.DATA + " = \"" + Imagepath + "\"";
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String imagePath = "";
        Bitmap bitmap = null;
        if (cursor == null || cursor.getCount() == 0) {
            bitmap = getVideoThumbnail(context,cr,Imagepath);
            if(null != bitmap )
                return bitmap;
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                _id = cursor.getInt(_idColumn);
                imagePath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MICRO_KIND,
                options);
        return bitmap;
    }

    public  int calculateInSampleSize(BitmapFactory.Options options,  int reqWidth,int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public  Bitmap getVideoThumbnail(Context context, ContentResolver cr, String videoPath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, };
        String whereClause = MediaStore.Video.Media.DATA + " = '" + videoPath + "'";
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String videopath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            do {
                _id = cursor.getInt(_idColumn);
                videopath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id, MediaStore.Video.Thumbnails.MICRO_KIND,
                options);
        return bitmap;
    }

    class Loadimage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;

        @Override
        protected Bitmap doInBackground(Object... params) {
            imv = (ImageView) params[1];
            path = (String) params[0];
            Bitmap bitmap = getSync(path);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imv.getTag() == null || !imv.getTag().toString().equals(path)) {
            /*
             * The path is not same. This means that this image view is
             * handled by some other async task. We don't do anything and
             * return.
             */
                return;
            }
            if (bitmap != null && imv != null) {
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(bitmap);
            } else {
                imv.setVisibility(View.GONE);
            }
        }

    }

    public void cancelAllTask()
    {
        if(mLoaderTasks != null){
            for(Loadimage task : mLoaderTasks)
            {
                task.cancel(true);
            }
        }
    }
}
