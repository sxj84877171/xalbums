package com.sw.xalbums.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sw.assetmgr.protocol.AssetDateGroup;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.R;
import com.sw.xalbums.fragment.AllPhotoFragment;
import com.sw.xalbums.util.PhotoCache;
import com.sw.xalbums.util.ScreenUtil;
import com.sw.xalbums.view.RecyclerTouchView;

import java.util.List;

/**
 * Created by Elvis on 2016/1/6.
 */
public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private int mItemHeight = 0;
    private RecyclerView mRecyclerView ;
    private List<AssetItem> mDatas ;
    private boolean bShowChk = false;
    private PhotoCache mThumbCach;
    private SparseBooleanArray booleanArray ;
    private  int mFontSize = 12;
    public   static final int ITEM_TYPE_DATE = 0;
    public static final int ITEM_TYPE_IMG = 1;

    public PhotoRecyclerViewAdapter(Context mContext,RecyclerView recyclerView,List<AssetItem> datas) {
        this.mContext = mContext;
        this.mRecyclerView = recyclerView;
        this.mDatas = datas;
        mThumbCach = PhotoCache.getInstance(mContext);
        booleanArray = new SparseBooleanArray(mDatas.size());
    }

    public void swapData(List<AssetItem> imageData)
    {
        mDatas = imageData;
        booleanArray.clear();
        booleanArray = new SparseBooleanArray(mDatas.size());
        notifyDataSetChanged();
    }
    
    public List<AssetItem> getAdapterData(){
        return mDatas;
    }

    public void swapData(List<AssetItem> imageData, boolean selectAll)
    {
        mDatas = imageData;
        booleanArray.clear();
        booleanArray = new SparseBooleanArray(mDatas.size());
        if(selectAll)
        {
            selectAll();
        }
        else
         notifyDataSetChanged();
    }


    public  void cancelAll()
    {
        if(mThumbCach != null)
        {
            mThumbCach.cancelAllTask();
        }
    }

    /**
     * Called when RecyclerView needs a new {@link } of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        switch (viewType)
        {
            case ITEM_TYPE_DATE:
                holder = new MyViewHolder(LayoutInflater.from(
                        mContext).inflate(R.layout.griditem_date, parent,
                        false), this, viewType);
                break;
            case ITEM_TYPE_IMG:
                holder = new MyViewHolder(LayoutInflater.from(
                        mContext).inflate(R.layout.griditem, parent,
                        false), this, viewType);
                break;
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
       AssetItem ai = this.getItem(position);
        if(ai instanceof AssetDateGroup)
        {
            return ITEM_TYPE_DATE;
        }
       return ITEM_TYPE_IMG;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AssetItem fi = mDatas.get(position);
        if(holder.mType == ITEM_TYPE_IMG) {
            holder.tv.setTag(fi.getPath());
            boolean isChecked = booleanArray.get(position);
            holder.chk.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            holder.layer.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            mThumbCach.setImageView(fi.getPath(), holder.tv);
        }
        else
        {
            AssetDateGroup adg = (AssetDateGroup)fi;
            String year = adg.getYear();
            String day = adg.getDay();
            String week = adg.getWeek();
            String month = adg.getMonth();
            holder.txDate.setText(year+"/"+month);
            holder.txDay.setText(day);
            holder.txWeekDay.setText(week);

            holder.txDate.setTextSize(mFontSize);
            holder.txDay.setTextSize(mFontSize+8);
            holder.txWeekDay.setTextSize(mFontSize);
            if(booleanArray.get(position)) {
                holder.chk.setVisibility(View.VISIBLE);
            }
            else {
                holder.chk.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void selectAll()
    {
        for (int i = 0 ;i < mDatas.size(); i++) {
            booleanArray.put(i, true);
        }
        notifyDataSetChanged();
    }

    public AssetItem getItem(int position)
    {
      return   mDatas.get(position);
    }

    public void setSelected(int position, boolean b)
    {
        booleanArray.put(position, b);
        //find date head
        int headPos = findDateHead(position, false);
        if(headPos != -1) {
            int nextHeadPos = findDateHead(position,true);
            if (!b) {
                booleanArray.put(headPos, b);
                notifyItemChanged(headPos);
            } else {
                int i;
                for (i = headPos + 1; i <= nextHeadPos; i++) {
                    if (!booleanArray.get(i)) {
                        break;
                    }
                }
                i--;
                if (i == nextHeadPos) {
                    booleanArray.put(headPos, b);
                    notifyItemChanged(headPos);
                }
            }
        }

    }

    private int findDateHead(int position, boolean forward)
    {
        if(!forward) {
            if(position == 0) return  -1;
            int newPos = position - 1;
            AssetItem ai = mDatas.get(newPos);
            while (!(ai instanceof AssetDateGroup)) {
                newPos--;
                if(newPos<0) return -1;
                ai = mDatas.get(newPos);
            }
            return newPos;
        }
        else
        {
            int newPos = position;
            if(position < mDatas.size()-1) {
                newPos = position + 1;
                AssetItem ai = null;
                ai = mDatas.get(newPos);
                while (!(ai instanceof AssetDateGroup)) {
                    newPos++;
                    if(newPos>=mDatas.size()) {
                        newPos --;
                        break;
                    }
                    ai = mDatas.get(newPos);
                }
            }
            if (mDatas.get(newPos) instanceof AssetDateGroup) {
                newPos --;
            }
            return newPos;
        }
    }

    public void setSelectedGroup(int position, boolean b)//position is a date
    {
        int newPos = position + 1;
        int count = 0;
        AssetItem ai = mDatas.get(newPos);
        while (!(ai instanceof AssetDateGroup))
        {
            count ++;
            booleanArray.put(newPos, b);
            newPos ++;
            if(newPos>=mDatas.size())
            {
                newPos--;
                break;
            }
            ai = mDatas.get(newPos);
        }
        booleanArray.put(position, b);
        notifyItemRangeChanged(position + 1,count);

    }

    public SparseBooleanArray getSelectedItems()
    {
        return booleanArray;
    }
    public void setPhotoMode(int mode)
    {
        ((RecyclerTouchView) mRecyclerView).setScreenMode(mode);
        if(mode == AllPhotoFragment.PHOTO_MODE_FULL_SCREEN) {
            booleanArray.clear();
            bShowChk = false;
            notifyDataSetChanged();
        }
        else {
            bShowChk = true;
        }

    }

     public void setTextSize()
    {
        GridLayoutManager layoutManager = (GridLayoutManager)mRecyclerView.getLayoutManager();
        float photoWallWidth = mRecyclerView.getWidth();
        int spanCount = layoutManager.getSpanCount();
        int cellWidth = (int) photoWallWidth/spanCount;
        int fontSize = adjustFontSize(cellWidth);
        mFontSize = fontSize;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mDatas==null?0:mDatas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private Handler handler = new Handler();
        ImageView tv;
        ImageView chk;
        View layer;
        public  int mType = ITEM_TYPE_IMG;
        TextView txDate;
        TextView txDay;
        TextView txWeekDay;
        public  static boolean ignoreResize = false;
        private PhotoRecyclerViewAdapter mAdapter ;

        public MyViewHolder(View view, PhotoRecyclerViewAdapter adapter, int type) {
            super(view);
            this.mAdapter = adapter;
            mType = type;
            getUIItems(view);
        }

        public void getUIItems(View view)
        {
            if(mType == ITEM_TYPE_IMG) {
                tv = (ImageView) view.findViewById(R.id.text);
                layer = view.findViewById(R.id.opacityLayer);
            }
            if(mType == ITEM_TYPE_DATE)
            {
                 txDate = (TextView)view.findViewById(R.id.txDate);
                 txDay = (TextView)view.findViewById(R.id.txDay);
                 txWeekDay = (TextView)view.findViewById(R.id.txWeekDay);
//                txWeekDay.addOnLayoutChangeListener(this);
//                txDay.addOnLayoutChangeListener(this);
//                txDate.addOnLayoutChangeListener(this);
            }
            chk = (ImageView) view.findViewById(R.id.img_chk);
        }

    }

    public static int adjustFontSize(int screenWidth) {
        /**
         * 1. 在视图的 onsizechanged里获取视图宽度，一般情况下默认宽度是320，所以计算一个缩放比率
         rate = (float) w/320   w是实际宽度
         2.然后在设置字体尺寸时 paint.setTextSize((int)(8*rate));   8是在分辨率宽为320 下需要设置的字体大小
         实际字体大小 = 默认字体大小 x  rate
         */
        int rate = (int)(5*(float) screenWidth/76); //我自己测试这个倍数比较适合，当然你可以测试后再修改
        return rate; //字体太小也不好看的
    }
}

