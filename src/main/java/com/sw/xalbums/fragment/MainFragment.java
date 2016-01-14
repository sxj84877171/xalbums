package com.sw.xalbums.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sw.assetmgr.local.AssetManagerFactory;
import com.sw.assetmgr.local.IAssetManager;
import com.sw.assetmgr.local.IAssettManagerListener;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.R;
import com.sw.xalbums.util.SpaceUtils;
import com.sw.xalbums.view.RoundProgressBar;

import java.util.List;

/**
 * Created by Tim on 2016/01/07.
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private RoundProgressBar bar1;// 占用空间百分比
    private RoundProgressBar bar2;// 剩余空间占总空间百分比
    private TextView freeSpace;// 剩余空间  12.4GB
    private TextView usedSpace;// 占用空间  12.4GB/64GB
    private TextView holdPercent;// 占用百分比6
    private TextView main_use_des;// "相册空间占用"
    private ImageView searchAnim_image;// 进去搜索的动画
    private ImageView icon_func_1;// 功能图标
    private ImageView icon_func_2;// 功能图标
    private ImageView icon_func_3;// 功能图标
    private ImageView bigCiecleImage1;// 实心圆(可点击的大圆圈)
    private ImageView bigCiecleImage2;// 空心圆
    private RelativeLayout searchAnim_des;// "正在分析照片..."
    private RelativeLayout compressPhoto;// "照片大瘦身"
    private RelativeLayout similarPhoto;// "相似照片"
    private RelativeLayout uselessPhoto;// "无用途清理"
    private RelativeLayout perRoot;// 占用百分比的根布局
    private LinearLayout arrRoot;// 占用百分比右侧箭头的根布局
    private LinearLayout funRoot;// 最下面功能的根布局
    private LinearLayout funRootView;// 最下面功能的根布局对应的View
    private LinearLayout icon_func_1_des_root;// 功能描述的根布局1
    private LinearLayout icon_func_2_des_root;// 功能描述的根布局2
    private LinearLayout icon_func_3_des_root;// 功能描述的根布局3
    private Animation circleAnimation;// 进去搜索的动画的实例
    private Animation funUpAnim;// 功能弹出动画
    private ScaleAnimation scaleAnimation;// 功能小图标的动画
    private AlphaAnimation AlphaAnimation;// 功能小图标右侧文字的动画

    private Handler handler = new Handler();

    private IAssetManager photoMgr;

    public MainFragment() {
    }

    // 获取mainFragment
    public static MainFragment newInstance() {
        MainFragment fragment = null;
        if (fragment == null) {
            fragment = new MainFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        initViewById(fragmentView);

        initAnim();

        initOnClickListener();

        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchAnim_image.clearAnimation();
        if (cirTask != null) {
            handler.removeCallbacks(cirTask);
            cirTask = null;
        }
    }

    // 拿到控件
    private void initViewById(View fragmentView) {
        bar1 = (RoundProgressBar) fragmentView.findViewById(R.id.roundProgressBar1);
        bar2 = (RoundProgressBar) fragmentView.findViewById(R.id.roundProgressBar2);
        freeSpace = (TextView) fragmentView.findViewById(R.id.main_free_space);
        usedSpace = (TextView) fragmentView.findViewById(R.id.main_used);
        holdPercent = (TextView) fragmentView.findViewById(R.id.main_per);
        main_use_des = (TextView) fragmentView.findViewById(R.id.main_use_des);
        searchAnim_image = (ImageView) fragmentView.findViewById(R.id.img_home_search);
        icon_func_1 = (ImageView) fragmentView.findViewById(R.id.icon_func_1);
        icon_func_2 = (ImageView) fragmentView.findViewById(R.id.icon_func_2);
        icon_func_3 = (ImageView) fragmentView.findViewById(R.id.icon_func_3);
        bigCiecleImage1 = (ImageView) fragmentView.findViewById(R.id.imageView_circle_1);
        bigCiecleImage2 = (ImageView) fragmentView.findViewById(R.id.imageView_circle_2);
        searchAnim_des = (RelativeLayout) fragmentView.findViewById(R.id.scan_album);
        compressPhoto = (RelativeLayout) fragmentView.findViewById(R.id.function_1);
        similarPhoto = (RelativeLayout) fragmentView.findViewById(R.id.function_2);
        uselessPhoto = (RelativeLayout) fragmentView.findViewById(R.id.function_3);
        perRoot = (RelativeLayout) fragmentView.findViewById(R.id.per);
        arrRoot = (LinearLayout) fragmentView.findViewById(R.id.arr_root);
        funRoot = (LinearLayout) fragmentView.findViewById(R.id.fun_replace);
        funRootView = (LinearLayout) fragmentView.findViewById(R.id.fun_);
        icon_func_1_des_root = (LinearLayout) fragmentView.findViewById(R.id.icon_func_1_des);
        icon_func_2_des_root = (LinearLayout) fragmentView.findViewById(R.id.icon_func_2_des);
        icon_func_3_des_root = (LinearLayout) fragmentView.findViewById(R.id.icon_func_3_des);

    }

    // 寻找的动画
    private void initAnim() {
        // 搜索相册的动画
        circleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.circle_anima);
        circleAnimation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        circleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // 功能的动画( 下 -> 上 )
        funUpAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.pc_up);
        funUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                funRoot.setVisibility(View.GONE);
                funRootView.setVisibility(View.VISIBLE);
                icon_func_1.startAnimation(scaleAnimation);
                icon_func_2.startAnimation(scaleAnimation);
                icon_func_3.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        funRoot.startAnimation(funUpAnim);

        // 功能小图标的动画
        scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon_func_1_des_root.setVisibility(View.VISIBLE);
                icon_func_2_des_root.setVisibility(View.VISIBLE);
                icon_func_3_des_root.setVisibility(View.VISIBLE);
                icon_func_1_des_root.startAnimation(AlphaAnimation);
                icon_func_2_des_root.startAnimation(AlphaAnimation);
                icon_func_3_des_root.startAnimation(AlphaAnimation);

                searchAnim_des.setVisibility(View.VISIBLE);
                searchAnim_image.startAnimation(circleAnimation);
                searchAnim_image.setVisibility(View.VISIBLE);
                handler.postDelayed(cirTask, 250);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 功能小图标右侧文字的动画
        AlphaAnimation = new AlphaAnimation(0.2f, 1f);
        AlphaAnimation.setDuration(200);
        AlphaAnimation.setFillAfter(true);

    }

    // 默认250m秒后显示相册数据信息的task
    private Runnable cirTask = new Runnable() {
        @Override
        public void run() {
            setTextData();
//            setVisivible();
            if (cirTask != null) {
                handler.removeCallbacks(cirTask);
                cirTask = null;
            }
        }
    };

    long _size;

    // <TODO>设置主页面数据
    private void setTextData() {
        photoMgr = AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_MGR_ALL);
        photoMgr.setContentManagerListener(new IAssettManagerListener() {
            @Override
            public void onDeletePhotosProgress(long deletedSize, double percent) {
            }

            @Override
            public void onPhoto(List<?> photos) {
            }

            @Override
            public void onPhotoProgress(AssetItem asset, long bitItemsToal, long totoal) {
            }

            @Override
            public void onDeletePhoto(List<?> photos) {
            }

            @Override
            public void onPhotosSize(long size) {
                _size = size;
                System.out.println(_size + "-----------------------------------------");
            }

            @Override
            public void onScanFinished() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData_(_size);
                        setVisivible();
                    }
                });
                photoMgr.stopScan();
                photoMgr.setContentManagerListener(null);
            }

            @Override
            public void onDeleteFinished() {
            }
        });
        photoMgr.startScan(photoMgr.SORT_TYPE_TIME, photoMgr.ORDER_BY_ASC, 0);
    }

    private void setData_(long size) {
        String u = SpaceUtils.getFreeAndTotalSpace();
        String[] spaceinfo = u.split("/");
        String free = spaceinfo[0];
        String total = spaceinfo[1];

        long freeL = Long.parseLong(spaceinfo[2]);// 手机剩余空间
        long totalL = Long.parseLong(spaceinfo[3]);// 手机SDCard总内存

        String useInfo = getString(R.string.main_fragment_used_info);// 12.4GB/64GB
        String freeSpaceInfo = getString(R.string.main_fragment_free_space);// 剩余空间:%s

        int rank = Math.round(100 * size / totalL);
        int rank2 = Math.round(100 * (totalL - freeL) / totalL);


        String used = SpaceUtils.convertSizeReturnOneValidNember(size);
        useInfo = String.format(useInfo, used, total);
        freeSpaceInfo = String.format(freeSpaceInfo, free);
        rank = (rank == 0) ? 1 : rank;
        bar1.setProgress(rank);
        bar2.setProgress(rank2);

        holdPercent.setText(rank + "");
        usedSpace.setText(useInfo);
        freeSpace.setText(freeSpaceInfo);
    }

    // 设置界面可见与不可见
    private void setVisivible() {
        searchAnim_image.setVisibility(View.GONE);
        searchAnim_des.setVisibility(View.GONE);
        searchAnim_image.clearAnimation();

        freeSpace.setVisibility(View.VISIBLE);
        bigCiecleImage1.setVisibility(View.VISIBLE);
        bigCiecleImage2.setVisibility(View.VISIBLE);
        main_use_des.setVisibility(View.VISIBLE);
        usedSpace.setVisibility(View.VISIBLE);
        perRoot.setVisibility(View.VISIBLE);
        arrRoot.setVisibility(View.VISIBLE);
        bar1.setVisibility(View.VISIBLE);
        bar2.setVisibility(View.VISIBLE);

    }

    // 监听事件
    private void initOnClickListener() {
        compressPhoto.setOnClickListener(OnClickListener);
        similarPhoto.setOnClickListener(OnClickListener);
        uselessPhoto.setOnClickListener(OnClickListener);
        bigCiecleImage2.setOnClickListener(OnClickListener);

    }

    // <TODO>点击事件
    private View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_circle_2:// 所有照片
                {
                    IAssetManager photoMgr = AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_MGR_ALL);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();


                    AllPhotoFragment fragment = AllPhotoFragment.newInstance("key","" +  R.id.imageView_circle_2);
                    fragment.swapData((List<AssetItem>) (List<AssetItem>) photoMgr.getAssetsSyncByDateGroup());
                    transaction.replace(R.id.root_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
//                    StatisticsUtil.getDefaultInstance(getActivity()).onCreate();
                }
                break;
                case R.id.function_1:// 大照片瘦身

                {
                    IAssetManager photoMgr = AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_MGR_ALL);

                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();


                    AllPhotoFragment fragment = AllPhotoFragment.newInstance("key","" +  R.id.function_1);
                    fragment.swapData((List<AssetItem>) (List<AssetItem>) photoMgr.getAssetsSyncByDateGroup());

                    transaction.replace(R.id.root_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
//                    StatisticsUtil.getDefaultInstance(getActivity()).onCreate();

                    ListBuddiesFragment listBuddiesFragment = ListBuddiesFragment.newInstance();
                    listBuddiesFragment.setAllPhotoFragment(fragment);
                    listBuddiesFragment.setFunction(1);
                    android.app.FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.root_frame, listBuddiesFragment);
                    ft.commit();
                }
                break;
                case R.id.function_2:// 相似照片查找

                    break;
                case R.id.function_3://无用途清理

                    break;
                default:
                    break;
            }

        }
    };


}
