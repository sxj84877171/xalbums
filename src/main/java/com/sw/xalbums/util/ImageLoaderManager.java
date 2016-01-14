package com.sw.xalbums.util;

/**
 * Created by Tim on 2016/01/12.
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sw.xalbums.R;

public class ImageLoaderManager {
    private static ImageLoader imageLoager = null;
    private static DisplayImageOptions mOptions = null;

    public static ImageLoader getImageLoader(Context context) {
        if (imageLoager == null) {
            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
            config.threadPoolSize(3);// 线程池内加载的数量
            config.threadPriority(1);// 线程优先级[1,10]
//			config.memoryCacheSizePercentage(10);// 内存中10%
            config.denyCacheImageMultipleSizesInMemory();
            config.discCacheSize(100 * 1024 * 1024);
            config.memoryCacheExtraOptions(1600, 2400);
            config.memoryCache(new LruMemoryCache(10 * 1024 * 1024));
            config.tasksProcessingOrder(QueueProcessingType.LIFO);
            imageLoager = ImageLoader.getInstance();
            imageLoager.init(config.build());
        }

        return imageLoager;
    }

    public static DisplayImageOptions getImageOptions() {
        if (mOptions == null) {
            mOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.pictures_no)// 设置图片在下载期间显示的图片
                    .showImageOnFail(R.drawable.pictures_no) // 设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(false)// 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(false)// 设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                    .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                    .build();// 构建完成
        }
        return mOptions;
    }
}
