package com.sw.xalbums.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {
	public static int px2dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
	
	public static int dp2px(Context context, float dp){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dp * scale + 0.5f); 
	}
	
	public static int getScreenPxWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		return metric.widthPixels;
	}
	
	public static int getScreenPxHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		return metric.heightPixels;
	}
}
