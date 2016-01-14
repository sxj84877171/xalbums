package com.sw.xalbums.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;

import com.sw.xalbums.adapter.PhotoRecyclerViewAdapter;
import com.sw.xalbums.fragment.AllPhotoFragment;
import com.sw.xalbums.util.ScreenUtil;

/**
 * Created by dongjl1 on 2016/1/4.
 */
public class RecyclerTouchView  extends RecyclerView{

    private int mode = 0;//用于标记模式
    private static final int DRAG = 1;//拖动
    private static final int ZOOM = 2;//放大

    private float startDis = 0;
    private int screenMode = AllPhotoFragment.PHOTO_MODE_FULL_SCREEN;
    private GestureDetector mGestureDetector;
    public RecyclerTouchView(Context context) {
        super(context);
    }

    public RecyclerTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RecyclerTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.mGestureDetector = gestureDetector;
    }

    private static float distance(MotionEvent event){
        //两根线的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link -1} if the point does not intersect an item.
     */
    public int pointToPosition(int x, int y) {
        Rect frame = new Rect();
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE://移动事件
                if (mode == DRAG) {//图片拖动事件

                } else if(mode == ZOOM){//图片放大事件
                    float endDis = distance(e);//结束距离
                    if(endDis > 10f){
                        float scale = endDis / startDis;//放大倍数
                        GridLayoutManager layoutManager = (GridLayoutManager)getLayoutManager();
                        int spanCount = layoutManager.getSpanCount();
                        PhotoRecyclerViewAdapter adapter =  (PhotoRecyclerViewAdapter) getAdapter();

                        if(scale>2)
                        {
                            if(spanCount>2)
                            {
                                layoutManager.setSpanCount(spanCount - 1);
                                adapter.setTextSize();
                                adapter.notifyDataSetChanged();
                            }
                            mode = 0;
                        }
                        else if(scale < 0.5)
                        {
                            if(spanCount<6)
                            {
                                layoutManager.setSpanCount(spanCount + 1);
                                adapter.setTextSize();
                                adapter.notifyDataSetChanged();
                            }
                            mode = 0;
                        }

                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mode = 0;
                break;
            //有手指离开屏幕，但屏幕还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            //当屏幕上已经有触点（手指）,再有一个手指压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                startDis = distance(e);
                if(startDis >= 10f)
                    mode = ZOOM;
                break;

        }
//        if(mGestureDetector!=null)
//             return mGestureDetector.onTouchEvent(e);

        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mGestureDetector!=null)
             mGestureDetector.onTouchEvent(ev);
        super.dispatchTouchEvent(ev);
        return true;
    }

    public void selectAll()
    {
        ((PhotoRecyclerViewAdapter)getAdapter()).selectAll();
    }


    public int getItemWidth(){
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        int width = (getWidth() - ScreenUtil.dp2px(getContext(), spanCount - 1)) / spanCount;
        return width;
    }

    public int getItemHeight(){
        return 0;
    }

    public int getNumColumns(){
        int count;
        GridLayoutManager layout = (GridLayoutManager)getLayoutManager();
        count = layout.getSpanCount();
        return count;
    }

    public void setScreenMode(int mode)
    {
        screenMode = mode;
    }
    public int getScreenMode(){

        return screenMode;
    }

    /**
     * 获取选中的item， 坐标及路径，传递fileItem
     */

}
