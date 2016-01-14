package com.sw.xalbums.listener;

import com.sw.assetmgr.protocol.AssetItem;

import java.util.List;

/**
 * Created by Elvis on 2016/1/8.
 */
public interface OnHoverItemListener {

    public static final int CONTAIN_WHOLE = 0;
    public static final int CONTAIN_PART = 1 ;
    public static final int CONTAIN_NO = 2 ;


    public boolean onHover(int position,List<AssetItem> datas);

    public int onCheckData(int position,List<AssetItem> datas);
}
