package com.sw.xalbums.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;
import com.sw.assetmgr.local.AssetManagerFactory;
import com.sw.assetmgr.local.IAssetManager;
import com.sw.assetmgr.local.IAssettManagerListener;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.R;
import com.sw.xalbums.adapter.CircularAdapter;
import com.sw.xalbums.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressLint("NewApi")
public class ListBuddiesFragment extends Fragment implements
        ListBuddiesLayout.OnBuddyItemClickListener {
    private static final String TAG = ListBuddiesFragment.class.getSimpleName();

    int mMarginDefault;
    int[] mScrollConfig;
    // private boolean isOpenActivities;

    // 适配器
    private CircularAdapter mAdapterLeft;
    private CircularAdapter mAdapterRight;
    private CircularAdapter mAdapterRight3;
    private CircularAdapter mAdapterRight4;
    private CircularAdapter mAdapterRight5;
    private CircularAdapter mAdapterRight6;
    private CircularAdapter mAdapterRight7;
    private CircularAdapter mAdapterRight8;
    private CircularAdapter mAdapterRight9;

    @InjectView(R.id.listbuddies)
    ListBuddiesLayout mListBuddies;

    // 适配器数据
    private List<String> mImagesLeft = new ArrayList<String>();
    private List<String> mImagesRight = new ArrayList<String>();
    private List<String> mImagesRight3 = new ArrayList<String>();
    private List<String> mImagesRight4 = new ArrayList<String>();
    private List<String> mImagesRight5 = new ArrayList<String>();
    private List<String> mImagesRight6 = new ArrayList<String>();
    private List<String> mImagesRight7 = new ArrayList<String>();
    private List<String> mImagesRight8 = new ArrayList<String>();
    private List<String> mImagesRight9 = new ArrayList<String>();
    // private TextView t;
    // private LinearLayout l;

    private int mRow = 3;// 设置列值
    private int speed = 20;

    private TextView check;// 检测中(1/100)
    private TextView deal;// 16张
    private TextView description;// 相似照片可进行处理

    private RelativeLayout label;
    private TextView label_detail;// 瘦身后图片会覆盖原图片
    private ImageView label_iamge;// 瘦身后图片会覆盖原图片小图标

    private TextView button;// 最下面的button

    private Handler handler = new Handler();// 更新界面
    static ListBuddiesFragment fragment;

    private AllPhotoFragment allPhotoFragment;

    private int function = 0;// 要处理的类型

    public void setFunction(int function) {
        this.function = function;
    }

    public void setAllPhotoFragment(AllPhotoFragment allPhotoFragment) {
        this.allPhotoFragment = allPhotoFragment;
    }

    public static ListBuddiesFragment newInstance() {
        if(fragment == null){
            fragment = new ListBuddiesFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMarginDefault = getResources()
                .getDimensionPixelSize(
                        com.jpardogo.listbuddies.lib.R.dimen.default_margin_between_lists);
        mScrollConfig = getResources().getIntArray(R.attr.scrollFaster);
    }

    private int width;// 屏幕宽度

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scroll, container,
                false);
        ButterKnife.inject(this, rootView);

        initRollInformation(rootView);

        setOnclickListener();

        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
//        int leftMargin = width / 3;
//        int caculateW = width - leftMargin;
        int dpW = ScreenUtil.dp2px(getActivity(), 100);

        int _width = (width - dpW) / mRow;

        // If we do this we need to uncomment the container on the xml layout
        createListBuddiesLayoutDinamically(rootView);
        // mImagesLeft.addAll(Arrays.asList(ImagesUrls.imageUrls_left));
        // mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
        initData(_width);
//		mListBuddies.setOnItemClickListener(this);
        return rootView;
    }

    private void initData(int _width) {
        switch (mRow) {
            case 2:
                // mAdapterLeft = new CircularAdapter(getActivity(),
                // getResources().getDimensionPixelSize(R.dimen.item_height_small),
                // mImagesLeft);
                // mAdapterRight = new CircularAdapter(getActivity(),
                // getResources().getDimensionPixelSize(R.dimen.item_height_tall),
                // mImagesRight);
//			width: 720,width/4:180,caculateW:540,_width:90

                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);

                CircularAdapter[] d2 = new CircularAdapter[2];
                d2[0] = mAdapterLeft;
                d2[1] = mAdapterRight;
                mListBuddies.setAdapters(d2);

                break;
            case 3:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);

                CircularAdapter[] d3 = new CircularAdapter[3];
                d3[0] = mAdapterLeft;
                d3[1] = mAdapterRight;
                d3[2] = mAdapterRight3;
                mListBuddies.setAdapters(d3);

                break;
            case 4:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);

                CircularAdapter[] d4 = new CircularAdapter[4];
                d4[0] = mAdapterLeft;
                d4[1] = mAdapterRight;
                d4[2] = mAdapterRight3;
                d4[3] = mAdapterRight4;
                mListBuddies.setAdapters(d4);
                break;
            case 5:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);
                mAdapterRight5 = new CircularAdapter(getActivity(), _width,
                        mImagesRight5);

                CircularAdapter[] d5 = new CircularAdapter[5];
                d5[0] = mAdapterLeft;
                d5[1] = mAdapterRight;
                d5[2] = mAdapterRight3;
                d5[3] = mAdapterRight4;
                d5[4] = mAdapterRight4;
                mListBuddies.setAdapters(d5);
                break;
            case 6:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);
                mAdapterRight5 = new CircularAdapter(getActivity(), _width,
                        mImagesRight5);
                mAdapterRight6 = new CircularAdapter(getActivity(), _width,
                        mImagesRight6);

                CircularAdapter[] d6 = new CircularAdapter[6];
                d6[0] = mAdapterLeft;
                d6[1] = mAdapterRight;
                d6[2] = mAdapterRight3;
                d6[3] = mAdapterRight4;
                d6[4] = mAdapterRight5;
                d6[5] = mAdapterRight6;
                mListBuddies.setAdapters(d6);
                break;
            case 7:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);
                mAdapterRight5 = new CircularAdapter(getActivity(), _width,
                        mImagesRight5);
                mAdapterRight6 = new CircularAdapter(getActivity(), _width,
                        mImagesRight6);
                mAdapterRight7 = new CircularAdapter(getActivity(), _width,
                        mImagesRight7);

                CircularAdapter[] d7 = new CircularAdapter[7];
                d7[0] = mAdapterLeft;
                d7[1] = mAdapterRight;
                d7[2] = mAdapterRight3;
                d7[3] = mAdapterRight4;
                d7[4] = mAdapterRight5;
                d7[5] = mAdapterRight6;
                d7[6] = mAdapterRight7;
                mListBuddies.setAdapters(d7);
                break;
            case 8:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);
                mAdapterRight5 = new CircularAdapter(getActivity(), _width,
                        mImagesRight5);
                mAdapterRight6 = new CircularAdapter(getActivity(), _width,
                        mImagesRight6);
                mAdapterRight7 = new CircularAdapter(getActivity(), _width,
                        mImagesRight7);
                mAdapterRight8 = new CircularAdapter(getActivity(), _width,
                        mImagesRight8);

                CircularAdapter[] d8 = new CircularAdapter[8];
                d8[0] = mAdapterLeft;
                d8[1] = mAdapterRight;
                d8[2] = mAdapterRight3;
                d8[3] = mAdapterRight4;
                d8[4] = mAdapterRight5;
                d8[5] = mAdapterRight6;
                d8[6] = mAdapterRight7;
                d8[7] = mAdapterRight8;
                mListBuddies.setAdapters(d8);
                break;
            case 9:
                mAdapterLeft = new CircularAdapter(getActivity(), _width,
                        mImagesLeft);
                mAdapterRight = new CircularAdapter(getActivity(), _width,
                        mImagesRight);
                mAdapterRight3 = new CircularAdapter(getActivity(), _width,
                        mImagesRight3);
                mAdapterRight4 = new CircularAdapter(getActivity(), _width,
                        mImagesRight4);
                mAdapterRight5 = new CircularAdapter(getActivity(), _width,
                        mImagesRight5);
                mAdapterRight6 = new CircularAdapter(getActivity(), _width,
                        mImagesRight6);
                mAdapterRight7 = new CircularAdapter(getActivity(), _width,
                        mImagesRight7);
                mAdapterRight8 = new CircularAdapter(getActivity(), _width,
                        mImagesRight8);
                mAdapterRight9 = new CircularAdapter(getActivity(), _width,
                        mImagesRight9);

                CircularAdapter[] d9 = new CircularAdapter[9];
                d9[0] = mAdapterLeft;
                d9[1] = mAdapterRight;
                d9[2] = mAdapterRight3;
                d9[3] = mAdapterRight4;
                d9[4] = mAdapterRight5;
                d9[5] = mAdapterRight6;
                d9[6] = mAdapterRight7;
                d9[7] = mAdapterRight8;
                d9[8] = mAdapterRight9;
                mListBuddies.setAdapters(d9);
                break;
            default:
                break;
        }
    }

    int count = 0;
    private void setOnclickListener() {
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: 最底下按钮点击动作
                count++;
                if (count == 1) {
                    setSpeed(0);
                } else {
                    setSpeed(speed);
                    count = 0;
                }
            }
        });
    }

    /**
     * 滚动扫描的描述信息
     *
     * @param rootView
     */
    private void initRollInformation(View rootView) {
        button = (TextView) rootView.findViewById(R.id.auto_main_button);
        check = (TextView) rootView.findViewById(R.id.auto_main_check);
        deal = (TextView) rootView.findViewById(R.id.auto_main_deal);
        description = (TextView) rootView.findViewById(R.id.auto_main_current_des);
        label = (RelativeLayout) rootView.findViewById(R.id.auto_main_deal_des_root);
        label_detail = (TextView) rootView.findViewById(R.id.auto_main_deal_des);
        label_iamge = (ImageView) rootView.findViewById(R.id.auto_main_deal_des_pic);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("被销毁了   fragment....");
        setSpeed(0);
    }

    private void createListBuddiesLayoutDinamically(View rootView) {
        mListBuddies = (ListBuddiesLayout) rootView
                .findViewById(R.id.listbuddies);
//        FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mListBuddies.getLayoutParams();
//        params.setMargins(instanceL, 0, 0, 0);
//        mListBuddies.setLayoutParams(params);
        mListBuddies.setListViewRows(getActivity(), mRow);// 必须先执行该方法
        resetLayout(speed);// 设置初始速度值及相关参数
        getImagePath();// 获取数据(路径path)
        // Once the container is created we can add the ListViewLayout into it
        // ((FrameLayout)rootView.findViewById(R.id.<container_id>)).addView(mListBuddies);
    }

    List<AssetItem> itemsBig = new ArrayList<>();
    /**
     * 获取数据
     */
    private void getImagePath() {

        IAssetManager iAssetManager = AssetManagerFactory.getInstance(getActivity(), AssetManagerFactory.PHOTO_MGR_ALL);
        final List<AssetItem> items = (List<AssetItem>) iAssetManager.getAssetsSync();

        iAssetManager.setContentManagerListener(new IAssettManagerListener() {
            @Override
            public void onDeletePhotosProgress(long deletedSize, double percent) {

            }

            @Override
            public void onPhoto(List<?> photos) {

            }

            @Override
            public void onPhotoProgress(final AssetItem asset, final long rightItemsToal, final long scannedTotoal) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (asset != null) {
                            itemsBig.add(asset);
                        }
                        String checkMsg = getString(R.string.main_fragment_sroll_checking);
                        String checkMsgFormat = String.format(checkMsg, scannedTotoal, items.size());
                        check.setText(checkMsgFormat);
                        String textL = (String) deal.getText();
                        boolean b = textL.equals(rightItemsToal + "");
                        if (!b) {
                            deal.setText(rightItemsToal + "");
                        }
                        System.out.println("--------" + rightItemsToal + "----" + scannedTotoal + "--" + itemsBig.size());
                    }
                });

            }

            @Override
            public void onDeletePhoto(List<?> photos) {

            }

            @Override
            public void onPhotosSize(long size) {

            }

            @Override
            public void onScanFinished() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSpeed(0);
                        if(allPhotoFragment != null){
//                            allPhotoFragment.swapData(itemsBig);
                            allPhotoFragment.setFunctionData(itemsBig,function);
                        }


                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
//                        AllPhotoFragment allPhotoFragment = AllPhotoFragment.newInstance();
//                        ListBuddiesFragment.newInstance();
                        transaction.remove(ListBuddiesFragment.newInstance());
                        transaction.commit();
                    }
                });

            }

            @Override
            public void onDeleteFinished() {

            }
        });
        iAssetManager.startScanBigAssetsByDateGroup();

        System.out.println("----------------总个数: " + items.size());
        int _size = items.size();
        AssetItem item = items.get(0);
        if (_size == 1) {
            mImagesLeft.add(item.getPath());
            mImagesRight = mImagesLeft;
            mImagesRight3 = mImagesLeft;
        } else if (_size == 2) {
            mImagesLeft.add(items.get(0).getPath());
            mImagesRight.add(items.get(1).getPath());
            mImagesRight3 = mImagesLeft;
        } else {
            int mCount = 0;
            for (AssetItem f : items) {
                String path = f.getPath();
                int row = mCount % mRow;
                mCount++;
                if (row == 0) {
                    mImagesLeft.add(path);
                } else if (row == 1) {
                    mImagesRight.add(path);
                } else if (row == 2) {
                    mImagesRight3.add(path);
                } else if (row == 3) {
//				setNullForSmooth(mImagesLeft, mImagesRight3, mImagesRight, mImagesRight3, mImagesRight);
//				setNullForSmooth(mImagesLeft, mImagesRight3, mImagesRight, mImagesRight3, mImagesRight);
                    mImagesRight4.add(path);
                } else if (row == 4) {
//				setNullForSmooth(mImagesRight4, mImagesLeft, mImagesRight, mImagesRight3, mImagesRight3);
//				setNullForSmooth(mImagesRight4, mImagesLeft, mImagesRight, mImagesRight3, mImagesRight3);
                    mImagesRight5.add(path);
                } else if (row == 5) {
//				setNullForSmooth(mImagesRight5, mImagesRight, mImagesRight6, mImagesRight5,mImagesRight4);
//				setNullForSmooth(mImagesRight5, mImagesRight, mImagesRight6, mImagesRight5,mImagesRight4);
                    mImagesRight6.add(path);
                } else if (row == 6) {
//				setNullForSmooth(mImagesRight7, mImagesLeft, mImagesRight3, mImagesRight7,mImagesRight5);
//				setNullForSmooth(mImagesRight7, mImagesLeft, mImagesRight3, mImagesRight7,mImagesRight5);
                    mImagesRight7.add(path);
                } else if (row == 7) {
//				setNullForSmooth(mImagesLeft, mImagesRight5, mImagesRight, mImagesRight7,mImagesRight6);
//				setNullForSmooth(mImagesLeft, mImagesRight5, mImagesRight, mImagesRight7,mImagesRight6);
                    mImagesRight8.add(path);
                } else if (row == 8) {
//				setNullForSmooth(mImagesRight9, mImagesLeft, mImagesRight9, mImagesRight7,mImagesRight6);
//				setNullForSmooth(mImagesRight9, mImagesLeft, mImagesRight9, mImagesRight7,mImagesRight6);
                    mImagesRight9.add(path);
                } else {
                    System.err.println("哎呀,出错啦,row: " + row);
                }

            }
        }

    }

    private void setNullForSmooth(List<String> mImages1, List<String> mImages2,
                                  List<String> mImages3, List<String> mImages4, List<String> mImages5) {
        mImages1.add(null);
        mImages2.add(null);
        mImages3.add(null);
        mImages4.add(null);
        mImages5.add(null);
    }


    @Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy,
                                   int position, long id) {
        // setSpeed(0);
        // System.out.println("点击了......");
        // setSpeed(0);// 设置速度为零
        // if (isOpenActivities) {
        // Intent intent = new Intent(getActivity(), DetailActivity.class);
        // // 传路径(本地 + 网络)
        // intent.putExtra(DetailActivity.EXTRA_URL, getImage(buddy, position));
        // startActivity(intent);
        // } else {
        // Resources resources = getResources();
        // Toast.makeText(getActivity(), resources.getString(R.string.list) +
        // ": " + buddy + " " + resources.getString(R.string.position) + ": " +
        // position, Toast.LENGTH_SHORT).show();
        // }
    }

//	private String getImage(int buddy, int position) {
//		// return buddy == 0 ? ImagesUrls.imageUrls_left[position] :
//		// ImagesUrls.imageUrls_right[position];
//		return buddy == 0 ? mImagesLeft.get(position) : mImagesRight
//				.get(position);
//	}

    public void setGap(int value, int row) {
        mListBuddies.setGap(value);
    }

    public void setSpeed(int value) {
        mListBuddies.setSpeed(value);
    }

    public void setDividerHeight(int value) {
        mListBuddies.setDividerHeight(value);
    }

    public void setListViewRows(Context context, int value) {
        mListBuddies.setListViewRows(context, value);
    }

    public void setGapColor(int color) {
        mListBuddies.setGapColor(color);
    }

    public void setAutoScrollFaster(int option) {
        mListBuddies.setAutoScrollFaster(option);
    }

    public void setScrollFaster(int option) {
        mListBuddies.setManualScrollFaster(option);
    }

    public void setDivider(Drawable drawable) {
        mListBuddies.setDivider(drawable);
    }

    // public void setOpenActivities(Boolean openActivities) {
    // this.isOpenActivities = openActivities;
    // }

    /**
     * @param speed 设置初始速度及相关参数
     */
    public void resetLayout(int speed) {
        mListBuddies
                .setGap(mMarginDefault)
                .setSpeed(speed)
                .setDividerHeight(mMarginDefault)
                .setGapColor(getResources().getColor(R.color.frame))
                .setAutoScrollFaster(
                        mScrollConfig[ScrollConfigOptions.RIGHT
                                .getConfigValue()])
                .setManualScrollFaster(
                        mScrollConfig[ScrollConfigOptions.LEFT.getConfigValue()])
                .setDivider(getResources().getDrawable(R.drawable.divider));
    }
}