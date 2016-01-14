package com.sw.xalbums.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sw.assetmgr.local.AllVirtualAlbumsManager;
import com.sw.assetmgr.local.AssetManagerFactory;
import com.sw.assetmgr.local.IAssettManagerListener;
import com.sw.assetmgr.photoprocess.CompressImage;
import com.sw.assetmgr.protocol.AlbumPhotosInfoGroup;
import com.sw.assetmgr.protocol.AlbumsInfo;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.HomeActivity;
import com.sw.xalbums.R;
import com.sw.xalbums.adapter.CategoryListViewAdapter;
import com.sw.xalbums.adapter.PhotoRecyclerViewAdapter;
import com.sw.xalbums.util.ScreenUtil;
import com.sw.xalbums.util.SpaceUtils;
import com.sw.xalbums.view.DividerGridItemDecoration;
import com.sw.xalbums.view.DragFrameLayout;
import com.sw.xalbums.view.RecyclerTouchView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = AllPhotoFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public static final int PHOTO_MODE_FULL_SCREEN = 0;
    public static final int PHOTO_MODE_RIGHT_SIDE = 1;

    public int photoMode = PHOTO_MODE_FULL_SCREEN;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerTouchView mRecyclerView;
    ListView mLeftPanel;
    private GestureDetector mGestureDetector;
//    List<AssetItem> mDatas = new ArrayList<>();
    PhotoRecyclerViewAdapter mRecycleAdapter;
    private DragFrameLayout dragFrameLayout;
    private CategoryListViewAdapter listViewAdapter;

    float mRightScaleRate = 1.0f;
    public AllPhotoFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllPhotoFragment newInstance(String param1, String param2) {
        AllPhotoFragment fragment = new AllPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public int getScreenMode() {
        return photoMode;
    }

    public void swapData(List<AssetItem> imageData) {
//        mDatas = imageData;
        if (mRecycleAdapter != null)
            mRecycleAdapter.swapData(imageData);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if((""+ R.id.imageView_circle_2).equals(mParam2)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    AnimatorSet aniSet = generateAnimation("left", 0);
                    aniSet.start();
                }
            });
            photoMode = PHOTO_MODE_FULL_SCREEN ;
        }else{
            photoMode = PHOTO_MODE_RIGHT_SIDE;
        }
    }

    private List<AssetItem> funDatas = new ArrayList<>();

    // [from:1-大照片瘦身;from:2-相似照片处理;from:3-无用途清理]
    public void setFunctionData(List<?> list,int from) {
        Log.i(TAG,"我来自from:1-大照片瘦身;from:2-相似照片处理;from:3-无用途清理-------------" + from);
        if(from == 1){
            funDatas = (List<AssetItem>) list;
            one_key_shoushen_bottom_rootview.setVisibility(View.VISIBLE);
            String shoushen = String.format(getString(R.string.main_fragment_sroll_one_shoushen), funDatas.size());
            one_key_shoushen_bottom_rootview_button.setText(shoushen);

            AllVirtualAlbumsManager assetManager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
            AlbumsInfo albumsInfo = new AlbumsInfo();// assetManager.createAssetsFolder(getString(R.string.main_fragment_fun_1), true);
            albumsInfo.setName(getString(R.string.main_fragment_fun_1));
            AlbumPhotosInfoGroup albumPhotosInfoGroup = new AlbumPhotosInfoGroup();
            albumPhotosInfoGroup.setGroupTag(albumsInfo);
            albumPhotosInfoGroup.setListAssets((List<AssetItem>) list);
            List<AlbumPhotosInfoGroup> groups = new ArrayList<>();
            groups.add(albumPhotosInfoGroup);
            listViewAdapter.setFictitiousPhotos(groups);
        }else if(from == 2){

        }else if(from == 3){

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_all_photo, container, false);
        initViewScrollShouShen(fragmentView);
        initAnim();
        setOnClickListenerr();
        mLeftPanel = (ListView) fragmentView.findViewById(R.id.leftPanel);
        mRecyclerView = (RecyclerTouchView) fragmentView.findViewById(R.id.rvView);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener(getActivity()));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setGestureDetector(this.mGestureDetector);
        mRecycleAdapter = new PhotoRecyclerViewAdapter(getActivity(), mRecyclerView, new ArrayList<AssetItem>());

        mRecyclerView.setAdapter(mRecycleAdapter);
        AllVirtualAlbumsManager assetManager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
        List<AlbumPhotosInfoGroup> groupsPhotos = assetManager.getAllGroupPhotos();//AlbumPhotosInfoGroup
        listViewAdapter = new CategoryListViewAdapter(getActivity(), mRecycleAdapter);
        mLeftPanel.setAdapter(listViewAdapter);

        dragFrameLayout = (DragFrameLayout) fragmentView.findViewById(R.id.dragframe);
        dragFrameLayout.setHoverItemListener(listViewAdapter);
        mRecycleAdapter.setPhotoMode(photoMode);


        return fragmentView;
    }


    // 处理中
    RelativeLayout one_key_shoushen_bottom_rootview;//一键瘦身(已选%s张) 根布局
    TextView one_key_shoushen_bottom_rootview_button;//一键瘦身(已选%s张)  button

    View one_key_shoushen_clude_view;//一键瘦身inlcude
    RelativeLayout shoushen_chuli_rootview;//瘦身处理的(circle动画)布局
    ImageView shoushen_circle_img;//(circle动画)  img
    TextView shoushen_number;//当前瘦身的MB
    TextView shoushen_number_danwei;//瘦身danwei
    TextView main_fragment_sroll_one_shoushen_des;//处理中...(%1$s/%2$s)
    TextView one_key_shoushen_cancle_buttom;//处理过程中停止
    // 完成
    RelativeLayout shoushen_finish_rootview;//瘦身完成的根布局
    TextView main_fragment_sroll_shoushen_ok_des;//瘦身%1$s张照片,节约了%2$s
    TextView one_key_shoushen_share_buttom;//瘦身完成_分享

    private void initViewScrollShouShen(View fragmentView) {
        one_key_shoushen_bottom_rootview = (RelativeLayout) fragmentView.findViewById(R.id.one_key_shoushen_rootview);
        one_key_shoushen_bottom_rootview_button = (TextView) fragmentView.findViewById(R.id.one_key_shoushen_button);
        one_key_shoushen_cancle_buttom = (TextView) fragmentView.findViewById(R.id.one_key_shoushen_cancle_buttom);
        one_key_shoushen_clude_view = fragmentView.findViewById(R.id.shoushen_dealing_include_view);
        shoushen_chuli_rootview = (RelativeLayout) fragmentView.findViewById(R.id.shoushen_chuli_rootview);
        main_fragment_sroll_one_shoushen_des = (TextView) fragmentView.findViewById(R.id.main_fragment_sroll_one_shoushen_des);
        shoushen_circle_img = (ImageView) fragmentView.findViewById(R.id.shoushen_circle_img);
        shoushen_number = (TextView) fragmentView.findViewById(R.id.shoushen_number);
        shoushen_number_danwei = (TextView) fragmentView.findViewById(R.id.shoushen_number_danwei);
        shoushen_finish_rootview = (RelativeLayout) fragmentView.findViewById(R.id.shoushen_finish_rootview);
        main_fragment_sroll_shoushen_ok_des = (TextView) fragmentView.findViewById(R.id.main_fragment_sroll_shoushen_ok_des);
        one_key_shoushen_share_buttom = (TextView) fragmentView.findViewById(R.id.one_key_shoushen_share_buttom);
    }

    Animation circleAnimation;

    private void initAnim() {
        circleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.circle_anima);
        circleAnimation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
    }

    private void setOnClickListenerr() {
        one_key_shoushen_bottom_rootview.setOnClickListener(listener);
        one_key_shoushen_bottom_rootview_button.setOnClickListener(listener);
        one_key_shoushen_share_buttom.setOnClickListener(listener);
        one_key_shoushen_cancle_buttom.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.one_key_shoushen_rootview:
                case R.id.one_key_shoushen_button:
                    setViewData();
//                    System.out.println("----------------------------lll");
//                    one_key_shoushen_bottom_rootview_button.setClickable(false);
                    break;
                case R.id.one_key_shoushen_cancle_buttom:
//                    System.out.println("停止啦-----------------------------------------");
                    CompressImage.stop();
                    shoushen_chuli_rootview.setVisibility(View.GONE);
                    one_key_shoushen_cancle_buttom.setVisibility(View.GONE);
                    String saveinfo = String.format(getString(R.string.main_fragment_sroll_shoushen_ok_des), count, _size);
                    main_fragment_sroll_shoushen_ok_des.setText(saveinfo);
                    one_key_shoushen_share_buttom.setVisibility(View.VISIBLE);
                    shoushen_finish_rootview.setVisibility(View.VISIBLE);
                    break;
                case R.id.one_key_shoushen_share_buttom:
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT,
                            R.string.app_name);
                    share.putExtra(Intent.EXTRA_TEXT,
                            R.string.app_name);
                    share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(Intent.createChooser(share,getActivity().getTitle()));
                    break;
                default:
                    break;
            }

        }
    };

    int count = 0;
    String _size;
//    List<?> bigList;
    private void setViewData() {
        one_key_shoushen_bottom_rootview.setVisibility(View.GONE);
        one_key_shoushen_clude_view.setVisibility(View.VISIBLE);
        shoushen_circle_img.startAnimation(circleAnimation);
        CompressImage.compressImage(funDatas, new IAssettManagerListener() {
            @Override
            public void onDeletePhotosProgress(long deletedSize, double percent) {

            }

            @Override
            public void onPhoto(final List<?> photos) {
//                bigList = photos;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ++count;
                        String shoushen = String.format(getString(R.string.main_fragment_sroll_one_shoushen__deal_des), count, funDatas.size());
                        main_fragment_sroll_one_shoushen_des.setText(shoushen);
                    }
                });
            }

            @Override
            public void onPhotoProgress(AssetItem asset, long rightItemsToal, long scannedTotoal) {

            }

            @Override
            public void onDeletePhoto(List<?> photos) {

            }

            @Override
            public void onPhotosSize(final long size) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _size = SpaceUtils.convertSizeReturnOneValidNember(size);
                        String _s = _size.substring(0, _size.length() - 2);
                        String _sdw = _size.substring(_size.length() - 2);
                        shoushen_number.setText(_s);
                        shoushen_number_danwei.setText(_sdw);
                    }
                });
            }

            @Override
            public void onScanFinished() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        one_key_shoushen_cancle_buttom.setClickable(false);
                        shoushen_chuli_rootview.setVisibility(View.GONE);
                        one_key_shoushen_cancle_buttom.setVisibility(View.GONE);
                        String saveinfo = String.format(getString(R.string.main_fragment_sroll_shoushen_ok_des), count, _size);
                        main_fragment_sroll_shoushen_ok_des.setText(saveinfo);
                        one_key_shoushen_share_buttom.setVisibility(View.VISIBLE);
                        shoushen_finish_rootview.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onDeleteFinished() {

            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public AnimatorSet generateAnimation(String direction, int duration) {

        float leftWidth = mLeftPanel.getWidth();
        float rightWidth = mRecyclerView.getWidth();
        mRecyclerView.setPivotX(mRecyclerView.getWidth());
        mRightScaleRate = (float)(leftWidth + rightWidth ) / rightWidth;
        mRecyclerView.setPivotY(0f);
        AnimatorSet aniSet = new AnimatorSet();

        if (direction.equals("left")) {
            final int curRightWidth = this.mRecyclerView.getWidth();
            final int targetHeight = (int) (50 * ((float) (ScreenUtil.getScreenPxWidth(getActivity())) / (float) curRightWidth));
            float translationX = mRecyclerView.getTranslationX();
            ObjectAnimator moveRight = ObjectAnimator.ofFloat(mRecyclerView, "translationX", translationX, 0 - leftWidth);
            ObjectAnimator moveLeft = ObjectAnimator.ofFloat(mLeftPanel, "translationX", mLeftPanel.getTranslationX(),  mLeftPanel.getTranslationX() - leftWidth);

            final ObjectAnimator newMoveRight = ObjectAnimator.ofFloat(mRecyclerView,"djl", 1.0f, mRightScaleRate);
            newMoveRight.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cVal = (Float) animation.getAnimatedValue();
                    mRecyclerView.setScaleX(cVal);
                    mRecyclerView.setScaleY(cVal);
                }
            });
            ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private IntEvaluator mEvaluator = new IntEvaluator();

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentValue = (Integer) animator.getAnimatedValue();
                    float fraction = currentValue / 100f;
                    mRecyclerView.getLayoutParams().width = mEvaluator.evaluate(fraction, curRightWidth, ScreenUtil.getScreenPxWidth(getActivity()));
                    mRecyclerView.requestLayout();
                }
            });
            aniSet.play(newMoveRight).with(moveLeft);
        } else {
            final int curRightWidth = this.mRecyclerView.getWidth();
            final int curRightHeight = mRecyclerView.getHeight();
            final float translationX = mRecyclerView.getTranslationX();
            ObjectAnimator moveRight = ObjectAnimator.ofFloat(mRecyclerView, "translationX", translationX, 0f);
            ObjectAnimator moveLeft = ObjectAnimator.ofFloat(mLeftPanel, "translationX",  mLeftPanel.getTranslationX(), 0f);
            final ObjectAnimator newMoveRight = ObjectAnimator.ofFloat(mRecyclerView,"djl", mRightScaleRate, 1.0f);
            newMoveRight.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cVal = (Float) animation.getAnimatedValue();
                    mRecyclerView.setScaleX(cVal);
                    mRecyclerView.setScaleY(cVal);
                }
            });
            ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private IntEvaluator mEvaluator = new IntEvaluator();

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentValue = (Integer) animator.getAnimatedValue();
                    float fraction = currentValue / 100f;
                    mRecyclerView.getLayoutParams().width = mEvaluator.evaluate(fraction, curRightWidth, (int) (curRightWidth + translationX));
                    mRecyclerView.requestLayout();
                }
            });
            aniSet.play(moveLeft).with(newMoveRight);
        }
        aniSet.setDuration(duration);

        return aniSet;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private Context mContext;
        private HomeActivity mHomeActivity;

        public GestureListener(Context ctx) {
            mContext = ctx;
            mHomeActivity = (HomeActivity) mContext;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //捕获Down事件
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.d("xalbums", "catched fling event");
            float x1 = e1.getX();
            float x2 = e2.getX();
            float y1 = e1.getY();
            float y2 = e2.getY();
            if (x1 - x2 > 200 && Math.abs(y1 - y2) < 500 && Math.abs(velocityX) > 1000) {
                if (photoMode == PHOTO_MODE_RIGHT_SIDE) {
                    mHomeActivity.freezeToolBar(false);
                    mHomeActivity.changeToggleIndicator(0);
                    AnimatorSet aniSet = generateAnimation("left", 200);
                    aniSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            photoMode = PHOTO_MODE_FULL_SCREEN;
                            mRecycleAdapter.setPhotoMode(photoMode);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    aniSet.start();
                }
            } else if (x1 - x2 < -200 && Math.abs(y1 - y2) < 500 && Math.abs(velocityX) > 1000) {
                if (photoMode == PHOTO_MODE_FULL_SCREEN) {
                    AnimatorSet aniSet = generateAnimation("right", 200);
                    mHomeActivity.freezeToolBar(true);
                    mHomeActivity.lockLeftDrawerClose(true);
                    mHomeActivity.changeToggleIndicator(android.R.drawable.ic_input_add);
                    aniSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mHomeActivity.expandToolBar(false);
                            photoMode = PHOTO_MODE_RIGHT_SIDE;
                            mRecycleAdapter.setPhotoMode(photoMode);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    aniSet.start();
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View v = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            updateUI(v);
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                return onSingleTapUp(e);
            }
            return false;
        }

        private void updateUI(View v) {
            if (v != null) {
                PhotoRecyclerViewAdapter.MyViewHolder viewHolder = (PhotoRecyclerViewAdapter.MyViewHolder) mRecyclerView.getChildViewHolder(v);
                if (viewHolder != null) {
                    if (viewHolder.mType == PhotoRecyclerViewAdapter.ITEM_TYPE_IMG) {
                        ImageView chk = (ImageView) v.findViewById(R.id.img_chk);
                        View layer = v.findViewById(R.id.opacityLayer);
                        if (photoMode == PHOTO_MODE_RIGHT_SIDE) {
                            if (chk.getVisibility() == View.INVISIBLE) {
                                chk.setVisibility(View.VISIBLE);
                                mRecycleAdapter.setSelected(mRecyclerView.getChildAdapterPosition(v), true);
                                layer.setVisibility(View.VISIBLE);
                            } else {
                                chk.setVisibility(View.INVISIBLE);
                                mRecycleAdapter.setSelected(mRecyclerView.getChildAdapterPosition(v), false);
                                layer.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else if (viewHolder.mType == PhotoRecyclerViewAdapter.ITEM_TYPE_DATE) {
                        ImageView chk = (ImageView) v.findViewById(R.id.img_chk);
                        if (photoMode == PHOTO_MODE_RIGHT_SIDE) {
                            if (chk.getVisibility() == View.INVISIBLE) {
                                chk.setVisibility(View.VISIBLE);
                                mRecycleAdapter.setSelectedGroup(mRecyclerView.getChildAdapterPosition(v), true);
                            } else {
                                chk.setVisibility(View.INVISIBLE);
                                mRecycleAdapter.setSelectedGroup(mRecyclerView.getChildAdapterPosition(v), false);
                            }

                        }
                    }
                }

            }
        }
    }
}
