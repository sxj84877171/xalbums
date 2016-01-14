package com.sw.xalbums.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapter;
import com.sw.xalbums.R;
import com.sw.xalbums.util.PhotoCache;

import java.util.ArrayList;
import java.util.List;

public class CircularAdapter extends CircularLoopAdapter {
    private static final String TAG = CircularAdapter.class.getSimpleName();

    private List<String> mItems = new ArrayList<String>();
    private Context mContext;
    private int mRowHeight;

    public CircularAdapter(Context context, int rowHeight, List<String> imagesUrl) {
        mContext = context;
        mRowHeight = rowHeight;
        mItems = imagesUrl;
    }

    @Override
    public String getItem(int position) {
        return mItems.get(getCircularPosition(position));
    }

    @Override
    protected int getCircularCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setMinimumHeight(mRowHeight);

        PhotoCache photoCache = PhotoCache.getInstance(mContext);
        ViewGroup.LayoutParams layoutParams = holder.image.getLayoutParams();
        layoutParams.width = mRowHeight;
        layoutParams.height = mRowHeight;
        holder.image.setLayoutParams(layoutParams);
        Bitmap bitmap = photoCache.get(getItem(position));
        if(bitmap == null){
            bitmap = photoCache.getSync(getItem(position));
        }
        holder.image.setImageBitmap(bitmap);
        return convertView;
    }

    static class ViewHolder {
        ImageView image;

        public ViewHolder(View convertView) {
            image = (ImageView) convertView.findViewById(R.id.image);
        }
    }
}
