package com.sw.xalbums.util;

import android.util.Log;

import java.io.File;

/**
 * Created by Tim on 2016/01/11.
 */
public class SpaceUtils {
    private static final String TAG = SpaceUtils.class.getSimpleName();

    // 返回两位有效数字的数据
    public static String convertSizeReturnTowValidNember(long size, boolean bIgnore) {
        String strGb = "";
        String strb = "";
        if (size < 1024 * 1024 && size >= 0) {

            double dG = size / 1024.0;
            strGb = String.format("%.2f", dG);// kb
            strGb = exchangeChar(strGb);
            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 3);
            strb = "KB";

        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {

            double dG = size / 1024.0 / 1024;
            strGb = String.format("%.2f", dG);// mb
            strGb = exchangeChar(strGb);
            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 3);
            strb = "MB";

        } else if (size >= 1024 * 1024 * 1024) {

            double dG = size / 1024.0 / 1024 / 1024;
            strGb = String.format("%.2f", dG);// gb
            strGb = exchangeChar(strGb);
            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 3);
            strb = "GB";

        } else {
            strGb = "0.00KB";
        }

        return strGb + strb;
    }

    // 返回带有一位小数的数据
    public static String convertSizeReturnOneValidNember(long size) {
        String strGb = "";

        if (size < 1024 * 1024 && size >= 0) {

            double dG = size / 1024.0;
            strGb = String.format("%.1f", dG);// kb

            strGb = exchangeChar(strGb);

            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 2) + "KB";

        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {

            double dG = size / 1024.0 / 1024;
            strGb = String.format("%.1f", dG);// mb
            strGb = exchangeChar(strGb);

            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 2)+ "MB";

        } else if (size >= 1024 * 1024 * 1024) {

            double dG = size / 1024.0 / 1024 / 1024;
            strGb = String.format("%.1f", dG);// gb

            strGb = exchangeChar(strGb);

            int indexOf = strGb.indexOf(".");
            strGb = strGb.substring(0, indexOf + 2)+ "GB";

        } else {
            strGb = "0.0KB";
        }

        return strGb;
    }

    // 多语言格式错误"," 和 "."
    private static String exchangeChar(String strGb) {
        int of = strGb.indexOf(",");
        if (of != -1) {
            strGb = strGb.replace(",", ".");
        }
        return strGb;
    }

    public static class SDCardInfo {
        public long total;
        public long free;
    }

    public static int getFreeSpace()
    {
        SDCardInfo info = getSDCardInfo();
        if (null != info) {
            return (int)(info.free/1024/1024);
        }
        return 0;
    }

    public static String getFreeAndTotalSpace() {
        String freeSize = "";
        String totalSize = "";
        SDCardInfo info = getSDCardInfo();
        if (null != info) {
            freeSize = convertSizeReturnOneValidNember(info.free);
            totalSize = convertSizeReturnOneValidNember(info.total);
        }
        return freeSize+"/"+totalSize + "/" + info.free + "/" + info.total;
    }

    @SuppressWarnings("deprecation")
    public static SDCardInfo getSDCardInfo() {
        String sDcString = android.os.Environment.getExternalStorageState();

        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File pathFile = android.os.Environment
                    .getExternalStorageDirectory();

            try {
                android.os.StatFs statfs = new android.os.StatFs(
                        pathFile.getPath());

                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();

                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();

                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();

                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();

                SDCardInfo info = new SDCardInfo();
                // 计算SDCard 总容量大小MB
                info.total = nTotalBlocks * nBlocSize;

                // 计算 SDCard 剩余大小MB
                info.free = nAvailaBlock * nBlocSize;

                return info;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
            }
        }

        return null;
    }
}
