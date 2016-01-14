package com.sw.xalbums.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sw.assetmgr.log.FLog;
import com.sw.assetmgr.protocol.AssetDateGroup;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.R;
import com.sw.xalbums.adapter.PhotoRecyclerViewAdapter;
import com.sw.xalbums.fragment.AllPhotoFragment;
import com.sw.xalbums.listener.OnHoverItemListener;
import com.sw.xalbums.util.PhotoCache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Elvis on 2016/1/6.
 */
public class DragFrameLayout extends FrameLayout {

    public static final String TAG = DragFrameLayout.class.getSimpleName();

    public DragFrameLayout(Context context) {
        this(context, null);
    }

    public DragFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        initAnimationFrameLayout();
    }

    private Context context;
    private ListView listView;
    private AttributeSet attrs;
    private int defStyleAttr;
    private View hoverView = null;
    private int hoverIndex = -1;
    private AnimatorSet mAnimatorSet;
    private OnHoverItemListener hoverItemListener;
    private List<AssetItem> dragDatas = new ArrayList<AssetItem>();
    private int bitmapWidth = 0 ;
    private int bitmapHeight = 0;

    public void setHoverItemListener(OnHoverItemListener hoverItemListener) {
        this.hoverItemListener = hoverItemListener;
    }

    private void initAnimationFrameLayout() {
        /*if (animationFrameLayout == null) {
            animationFrameLayout = new FrameLayout(context, attrs, defStyleAttr);
            addView(animationFrameLayout);
            animationFrameLayout.setVisibility(View.GONE);
        }*/
    }

    public final static Long animationTime = 200l;
    private float mfDownX;
    private float mfDownY;
    private boolean mAnimationEnd = true;
    private RecyclerTouchView recyclerView;
    private Handler mHandler = new Handler();
    private List<ViewHolder> animationList = new ArrayList<ViewHolder>();
    private boolean isDrag = false;
    /**
     * 正在拖拽的view
     */
    private View dragView;


    public boolean dispatchTouchEvent(MotionEvent ev) {
        initRecyclearTouchView();
        initListView();
        if (recyclerView != null && recyclerView.getScreenMode() == AllPhotoFragment.PHOTO_MODE_RIGHT_SIDE) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mfDownX = ev.getX() - recyclerView.getX();
                    mfDownY = ev.getY() - recyclerView.getY();
                    dragView = recyclerView.findChildViewUnder(mfDownX, mfDownY);
                    if (dragView == null) {
                        return super.dispatchTouchEvent(ev);
                    }
                    int position = recyclerView.getChildAdapterPosition(dragView);
                    mHandler.postDelayed(mLongClickRunnable, 500);
                    mfDownX = ev.getRawX();
                    mfDownY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x2 = ev.getRawX();
                    float y2 = ev.getRawY();
                    if (!isInTouchView(x2, y2) && !isDrag) {
                        mHandler.removeCallbacks(mLongClickRunnable);
                    } else if (isDrag) {
                        onDragItem(ev);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int upX = (int) ev.getX();
                    int upY = (int) ev.getY();
                    mHandler.removeCallbacks(mLongClickRunnable);
                    if (isDrag) {
                        onStopDrag(ev);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isInTouchView(float x, float y) {
        if (abs(x - mfDownX) > 100) {
            return false;
        }
        if (abs(y - mfDownY) > 100) {
            return false;
        }
        return true;
    }

    private float abs(float x) {
        return x > 0 ? x : -x;
    }

    // 用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true;
            if (dragView != null) {
                animateReorder(dragView);
            }
        }
    };


    /**
     */
    private void onDragItem(MotionEvent ev) {
        float moveX = ev.getRawX();
        float moveY = ev.getRawY();
        if (mAnimationEnd) {
            float dx = (moveX - mfDownX);
            float dy = (moveY - mfDownY);
            mfDownX = moveX;
            mfDownY = moveY;
            for (int i = 0; i < animationList.size(); i++) {
                ImageView imageView = animationList.get(i).imageView;
                animationList.get(i).x = imageView.getTranslationX() + dx;
                animationList.get(i).y = imageView.getTranslationY() + dy;
                imageView.setTranslationX(animationList.get(i).x);
                imageView.setTranslationY(animationList.get(i).y);
            }
        }

        if (listView != null) {
            if (hoverView != null) {
                hoverView.setScaleX(1.0f);
                hoverView.setScaleY(1.0f);
                hoverView = null;
            }
            int position = listView.pointToPosition((int) (ev.getX() - listView.getX()), (int) (ev.getY() - listView.getY()));
            if (position >= 0) {
                hoverView = listView.getChildAt(position - listView.getFirstVisiblePosition());
                hoverIndex = position;
                hoverView.setScaleX(1.1f);
                hoverView.setScaleY(1.1f);
            }
        }
    }

    private void onStopDrag(final MotionEvent ev) {
        if (mAnimationEnd) {
            onDragItem(ev);
            isDrag = false;
//        animationFrameLayout = null;
            if (hoverItemListener != null && hoverView != null) {
                int ret = hoverItemListener.onCheckData(hoverIndex, dragDatas);
                if (ret > OnHoverItemListener.CONTAIN_WHOLE) {
                    animateIn(ev);
                } else {
                    animateBack();
                }
            } else {
                animateBack();
            }
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    onStopDrag(ev);

                }
            }, animationTime / 2);
        }

    }

    private void animateBack() {
        List<Animator> resultList = new LinkedList<Animator>();
        int size = animationList.size();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            FLog.i("DragFrameLayout", "mAnimatorSet.cancel");
            mAnimatorSet = null;
        }
        for (int i = 0; i < size; i++) {
            ViewHolder viewIndex = animationList.get(i);
            FLog.i(TAG, "positon x=" + viewIndex.x + ",y=" + viewIndex.y);
            resultList.add(createTranslationAnimations(viewIndex.imageView,
                    viewIndex.x, 0, viewIndex.y, 0, 0, i * 10));
        }


        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(resultList);
        mAnimatorSet.setDuration(animationTime);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
                removeAllViews();
                mAnimatorSet = null;
            }
        });
        mAnimatorSet.start();
    }

    private void animateIn(MotionEvent ev) {
        FLog.i(TAG, "animateIn");
        List<Animator> resultList = new LinkedList<Animator>();
        int size = animationList.size();
        for (int i = 0; i < size; i++) {
            ViewHolder viewIndex = animationList.get(i);
            long time = 50 * i;
            if (time > animationTime * 2 - 50) {
                time = animationTime * 2 - 50;
            }
            resultList.add(createTranslationAnimations(viewIndex.imageView,
                    viewIndex.x, viewIndex.x + (hoverView.getPivotX() - 25 + hoverView.getX() - ev.getX()), viewIndex.y, viewIndex.y + (hoverView.getPivotY() + hoverView.getY() - ev.getY()), 0, time));
        }

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            FLog.i("DragFrameLayout", "mAnimatorSet.cancel");
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(resultList);
        mAnimatorSet.setDuration(animationTime * 2);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
                removeAllViews();
                mAnimatorSet = null;
                if (hoverItemListener != null && hoverView != null) {
                    hoverItemListener.onHover(hoverIndex, dragDatas);
                }
                if (hoverView != null) {
                    hoverView.setScaleX(1.0f);
                    hoverView.setScaleY(1.0f);
                    hoverView = null;
                }

            }

        });
        mAnimatorSet.start();


    }

    @Override
    public void removeAllViews() {
        for (int i = 0; i < animationList.size(); i++) {
            removeView(animationList.get(i).imageView);
        }
        animationList.clear();
       /* if(animationFrameLayout != null){
            animationFrameLayout.setVisibility(View.GONE);
        }*/

    }

    private void initRecyclearTouchView() {
        if (recyclerView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    RecyclerTouchView gridView = findGridView((ViewGroup) childAt);
                    if (gridView != null) {
                        recyclerView = gridView;
                        break;
                    }
                }
            }
            if (recyclerView != null) {
                //  recyclerView.setLongTouchItemListener(longTouchItemListener);
            }
        }
    }

    private void initListView() {
        if (listView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    ListView lView = findListView((ViewGroup) childAt);
                    if (lView != null) {
                        listView = lView;
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param indexView
     */
    private void animateReorder(View indexView) {
        System.out.println("animateReorder" + indexView.getX() + ", y= " + indexView.getY());
        List<Animator> resultList1 = new LinkedList<Animator>();
        List<Animator> resultList2 = new LinkedList<Animator>();
        getChoosedView();
        int size = animationList.size();
        for (int i = 0; i < size; i++) {
            ViewHolder viewIndex = animationList.get(i);
            resultList1.add(createTranslationAnimations(viewIndex.getImageView(), 0,
                    50, 0,
                    -50, -1f
                            * (size - i), 0));
            resultList2.add(createTranslationAnimations(viewIndex.getImageView(), 50,
                    indexView.getX() - viewIndex.getX() + ((size - i) * 1), -50,
                    indexView.getY() - viewIndex.getY() - ((size - i) * 1), 0, 0));
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(resultList1);
        mAnimatorSet.setDuration(animationTime);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
                mAnimatorSet = null;
            }
        });
        mAnimatorSet.start();



    }

    /**
     * @param viewGroup
     * @return
     */
    private RecyclerTouchView findGridView(ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerTouchView) {
            return (RecyclerTouchView) viewGroup;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                RecyclerTouchView gridView = findGridView((ViewGroup) childAt);
                if (gridView != null) {
                    return gridView;
                }
            }
        }
        return null;
    }

    /**
     * @param viewGroup
     * @return
     */
    private ListView findListView(ViewGroup viewGroup) {
        if (viewGroup instanceof ListView) {
            return (ListView) viewGroup;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                ListView listView = findListView((ViewGroup) childAt);
                if (listView != null) {
                    return listView;
                }
            }
        }
        return null;
    }

    private void cleanAnimationView() {

        removeAllViews();
//        animationFrameLayout.removeAllViews();
    }

    /**
     * 创建移动动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @param rotation
     * @return
     */
    private AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY, float endY, float rotation,
                                                    long startDelay) {
        ObjectAnimator animX = createTranslationAnimationsX(view, startX, endX);
        ObjectAnimator animY = createTranslationAnimationsY(view, startY, endY);
        ObjectAnimator animRotation = createTranslationAnimationsR(view, rotation);


        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setStartDelay(startDelay);

        animSetXY.playTogether(animX, animY, animRotation);

        return animSetXY;
    }

    private ObjectAnimator createTranslationAnimationsX(View view, float startX,
                                                        float endX) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        return animX;
    }

    private ObjectAnimator createTranslationAnimationsY(View view, float startY,
                                                        float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        return animX;
    }

    private ObjectAnimator createTranslationAnimationsR(View view, float rotation) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "rotation",
                rotation);
        return animX;
    }

    private void initBitmapSize(){
        View view = recyclerView.getChildAt(0);
        View bitmapView = view;//.findViewById(R.id.text);
        bitmapView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(bitmapView.getDrawingCache());
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        bitmapView.setDrawingCacheEnabled(false);
        bitmapView.destroyDrawingCache();
    }

    private List<ViewHolder> getChoosedView() {
        cleanAnimationView();
        initBitmapSize();
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        PhotoRecyclerViewAdapter adapter = ((PhotoRecyclerViewAdapter) recyclerView.getAdapter());
        SparseBooleanArray sparseBooleanArray = adapter.getSelectedItems();
        View view = LayoutInflater.from(context).inflate(R.layout.griditem, null, false);
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            int index = sparseBooleanArray.keyAt(i);
            if (sparseBooleanArray.get(index)) {
                AssetItem assetItem = adapter.getItem(index);
                if (!(assetItem instanceof AssetDateGroup)) {
                    dragDatas.add(assetItem);
                    RecyclerView.ViewHolder vHolder = recyclerView.findViewHolderForLayoutPosition(index);
                    View temp = null;
                    if (vHolder != null) {
                        temp = vHolder.itemView;
                        if (temp != null) {
                            View tempView = temp.findViewById(R.id.text);
                            tempView.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(tempView.getDrawingCache());
                            ImageView imageView = new ImageView(getContext());
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    bitmapWidth, bitmapHeight);
                            int left = (int) (temp.getX() + recyclerView.getX());
                            int top = (int) (temp.getY() + recyclerView.getY());
                            int right = 0;
                            int bottom = 0;
                            if (left + bitmap.getWidth() > getWidth()) {
                                right = getWidth() - bitmap.getWidth() - left;
                            }
                            if (top + bitmap.getHeight() > getHeight()) {
                                bottom = getHeight() - bitmap.getHeight() - top;
                            }
                            layoutParams.setMargins(left, top, right, bottom);
                            imageView.setImageBitmap(bitmap);
                            addView(imageView, layoutParams);
                            ViewHolder viewHolder = new ViewHolder();
                            viewHolder.setImageView(imageView);
                            viewHolder.setX(temp.getX());
                            viewHolder.setY(temp.getY());
                            animationList.add(viewHolder);
                            tempView.destroyDrawingCache();
                        }
                    }else{
                        int fristPostion = manager.findFirstVisibleItemPosition();
                        int lastPosion = manager.findLastVisibleItemPosition();
                        if(index < fristPostion) {
                            float x = recyclerView.getChildAt(index % manager.getSpanCount()).getX();
                            float y = recyclerView.getChildAt(0).getY() - ((fristPostion - index) / manager.getSpanCount()) * (manager.getWidth() / manager.getSpanCount());
                            ImageView imageView = new ImageView(getContext());
                            Bitmap bitmap = PhotoCache.getInstance(context).getSync(assetItem.getPath());
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    bitmapWidth, bitmapHeight);
                            int left = (int) (x + recyclerView.getX());
                            int top = (int) (y + recyclerView.getY());
                            int right = 0;
                            int bottom = 0;
                            if (left + bitmap.getWidth() > getWidth()) {
                                right = getWidth() - bitmap.getWidth() - left;
                            }
                            if (top + bitmap.getHeight() > getHeight()) {
                                bottom = getHeight() - bitmap.getHeight() - top;
                            }
                            layoutParams.setMargins(left, top, right, bottom);
                            imageView.setImageBitmap(bitmap);
                            addView(imageView, layoutParams);
                            ViewHolder viewHolder = new ViewHolder();
                            viewHolder.setImageView(imageView);
                            viewHolder.setX(x);
                            viewHolder.setY(y);
                            animationList.add(viewHolder);
                        }else if(index > lastPosion){
                            float x = recyclerView.getChildAt(index % manager.getSpanCount()).getX();
                            float y = recyclerView.getChildAt(0).getY() + ((index - fristPostion) / manager.getSpanCount()) * (manager.getWidth() / manager.getSpanCount());
                            ImageView imageView = new ImageView(getContext());
                            Bitmap bitmap = PhotoCache.getInstance(context).getSync(assetItem.getPath());
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    bitmapWidth, bitmapHeight);
                            int left = (int) (x + recyclerView.getX());
                            int top = (int) (y + recyclerView.getY());
                            int right = 0;
                            int bottom = 0;
                            if (left + bitmap.getWidth() > getWidth()) {
                                right = getWidth() - bitmap.getWidth() - left;
                            }
                            if (top + bitmap.getHeight() > getHeight()) {
                                bottom = getHeight() - bitmap.getHeight() - top;
                            }
                            layoutParams.setMargins(left, top, right, bottom);
                            imageView.setImageBitmap(bitmap);
                            addView(imageView, layoutParams);
                            ViewHolder viewHolder = new ViewHolder();
                            viewHolder.setImageView(imageView);
                            viewHolder.setX(x);
                            viewHolder.setY(y);
                            animationList.add(viewHolder);
                        }

                    }

//                    }
                }
            }
        }
        return animationList;
    }

    static class ViewHolder {
        ImageView imageView;
        float x;
        float y;

        public ImageView getImageView() {
            return imageView;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public float getY() {
            return y;
        }

        public float getX() {
            return x;
        }


    }
}
