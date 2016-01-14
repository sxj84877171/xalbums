package com.sw.xalbums.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.assetmgr.local.AllVirtualAlbumsManager;
import com.sw.assetmgr.local.AssetManagerFactory;
import com.sw.assetmgr.local.IAssetManager;
import com.sw.assetmgr.protocol.AlbumPhotosInfoGroup;
import com.sw.assetmgr.protocol.AlbumsInfo;
import com.sw.assetmgr.protocol.AssetDateGroup;
import com.sw.assetmgr.protocol.AssetItem;
import com.sw.xalbums.R;
import com.sw.xalbums.dialog.XalBumsDialog;
import com.sw.xalbums.listener.OnHoverItemListener;
import com.sw.xalbums.util.PhotoCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elvis on 2016/1/6.
 */
public class CategoryListViewAdapter extends BaseAdapter implements OnHoverItemListener {

    public static final int ALL_ALBUM_INDEX = 0;
    public static final int NEW_ALBUM_INDEX = 1;

    private Context mContext;
    /**
     * 右边空间容器adapter
     */
    private PhotoRecyclerViewAdapter contentAdapter;
    /**
     * 所有实际相册
     */
    private List<AlbumPhotosInfoGroup> groupsPhotos;
    /**
     * 所有照片
     */
    private List<AssetItem> allPhotoDatas;
    /**
     * 当前的选择项目
     */
    private int checkIndex = 0;
    /**
     * 临时相册
     */
    private List<AlbumPhotosInfoGroup> fictitiousAlbumPhotosInfoGroup = new ArrayList<>();

    public void setFictitiousPhotos(List<AlbumPhotosInfoGroup> albumPhotosInfoGroup) {
        if (albumPhotosInfoGroup != null) {
            fictitiousAlbumPhotosInfoGroup.clear();
            this.fictitiousAlbumPhotosInfoGroup.addAll(albumPhotosInfoGroup);
            contentAdapter.swapData((List<AssetItem>) fictitiousAlbumPhotosInfoGroup.get(0).getListAssets(), true);
            checkIndex = -1;
        }
        notifyDataSetChanged();
    }

    public CategoryListViewAdapter(Context context, PhotoRecyclerViewAdapter photoRecyclerViewAdapter) {
        this.contentAdapter = photoRecyclerViewAdapter;
        this.mContext = context;
        IAssetManager photoMgr = AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_MGR_ALL);
        allPhotoDatas = (List<AssetItem>) photoMgr.getAssetsSyncByDateGroup();
        AllVirtualAlbumsManager assetManager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
        groupsPhotos = assetManager.getAllGroupPhotos();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        int count = 2;
        if (fictitiousAlbumPhotosInfoGroup != null) {
            count+=fictitiousAlbumPhotosInfoGroup.size();
        }
        if (groupsPhotos != null) {

            count += groupsPhotos.size();
        }
        return count;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = getAllPhotoItemView();
        }
        if (position == 1) {
            convertView = getNewFolderView(position);
        }
        int fictitiousSize = 0;
        if (fictitiousAlbumPhotosInfoGroup != null) {
            fictitiousSize = fictitiousAlbumPhotosInfoGroup.size();
        }
        if (position > 1 && position < fictitiousSize + 2) {
            convertView = getFictitiousView(position);
        }

        if (position >= fictitiousSize + 2) {
            convertView = getAlbumsView(position, fictitiousSize);
        }
        return convertView;
    }

    @NonNull
    private View getFictitiousView(final int position) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_type2_layout, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView textView = (TextView) convertView.findViewById(R.id.category_name);
        final AlbumsInfo albumsInfo = (AlbumsInfo) fictitiousAlbumPhotosInfoGroup.get(position - 2).getGroupTag();
        textView.setText(albumsInfo.getName());
        convertView.findViewById(R.id.layout).setBackgroundResource(R.drawable.bg_album_to_other);
        final List<AssetItem> das = (List<AssetItem>) fictitiousAlbumPhotosInfoGroup.get(position - 2).getListAssets();
        if (das != null && das.size() > 0) {
            imageView.setTag(das.get(0).getPath());
            PhotoCache.getInstance(mContext).setImageView(das.get(0).getPath(), imageView);
        } else {
            imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIndex = position;
                contentAdapter.swapData(das);
            }
        });
        return convertView;
    }

    @NonNull
    private View getNewFolderView(final int position) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_type2_layout, null);
        convertView.findViewById(R.id.layout).setBackgroundResource(R.drawable.bg_new_album);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XalBumsDialog.Builder build = new XalBumsDialog.Builder(mContext);
                build.setTitle(R.string.input_albums_name).setInput(true).setPositiveButton(R.string.yes_button_content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AllVirtualAlbumsManager manager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
                        String name = ((XalBumsDialog) dialog).getInputMessage();
                        if (name != null && !"".equals(name.trim())) {
                            manager.createAssetsFolder(name, false);
                            checkIndex = position;
                        }
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.no_button_content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
        return convertView;
    }

    @NonNull
    private View getAlbumsView(final int position, int fictitiousSize) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_type2_layout, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView textView = (TextView) convertView.findViewById(R.id.category_name);
        convertView.findViewById(R.id.layout).setBackgroundResource(R.drawable.bg_album);
        convertView.findViewById(R.id.delete_icon).setVisibility(View.GONE);
        AlbumPhotosInfoGroup group = groupsPhotos.get(position - fictitiousSize - 2);
        final AlbumsInfo albumsInfo = (AlbumsInfo) group.getGroupTag();
        textView.setText(albumsInfo.getName());
        final List<AssetItem> das = (List<AssetItem>) group.getListAssets();
        if (das != null && das.size() > 0) {
            imageView.setTag(das.get(0).getPath());
            PhotoCache.getInstance(mContext).setImageView(das.get(0).getPath(), imageView);
        } else {
            imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupsPhotos != null) {
                    contentAdapter.swapData(das);
                }
                checkIndex = position;
                v.findViewById(R.id.layout).setBackgroundResource(R.drawable.img_album_choosed);
                notifyDataSetChanged();
            }
        });
        if (checkIndex == position) {
            convertView.findViewById(R.id.layout).setBackgroundResource(R.drawable.img_album_choosed);
        } else {
            convertView.findViewById(R.id.layout).setBackgroundResource(0);
        }
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view = v.findViewById(R.id.delete_icon);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        convertView.findViewById(R.id.delete_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XalBumsDialog.Builder builder = new XalBumsDialog.Builder(mContext);
                builder.setTitle(R.string.delete_albums);
                String message = mContext.getString(R.string.delete_albums_message);
                message = String.format(message, albumsInfo.getName());
                builder.setMessage(message);
                builder.setPositiveButton(R.string.yes_button_content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AllVirtualAlbumsManager assetManager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
                        assetManager.deleteAssetsFolder(albumsInfo);
                        checkIndex = 0;
                        dialog.dismiss();
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(R.string.no_button_content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        notifyDataSetChanged();
                    }
                });
                builder.create().show();
            }
        });
        return convertView;
    }

    @NonNull
    private View getAllPhotoItemView() {
        View convertView  = LayoutInflater.from(mContext).inflate(R.layout.listview_item_layout, null);
        ImageView imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
        ImageView imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
        ImageView imageView4 = (ImageView) convertView.findViewById(R.id.imageView4);
        if (allPhotoDatas != null) {
            int index = 0;
            if (allPhotoDatas.size() > index) {
                while ((allPhotoDatas.get(index) instanceof AssetDateGroup)) {
                    index++;
                }
                imageView1.setTag(allPhotoDatas.get(index).getPath());
                PhotoCache.getInstance(mContext).setImageView(allPhotoDatas.get(index).getPath(), imageView1);
                index++;

            }
            if (allPhotoDatas.size() > index) {
                while ((allPhotoDatas.get(index) instanceof AssetDateGroup)) {
                    index++;
                }
                imageView2.setTag(allPhotoDatas.get(index).getPath());
                PhotoCache.getInstance(mContext).setImageView(allPhotoDatas.get(index).getPath(), imageView2);
                index++;
            }
            if (allPhotoDatas.size() > index) {
                while ((allPhotoDatas.get(index) instanceof AssetDateGroup)) {
                    index++;
                }
                imageView3.setTag(allPhotoDatas.get(index).getPath());
                PhotoCache.getInstance(mContext).setImageView(allPhotoDatas.get(index).getPath(), imageView3);
                index++;
            }
            if (allPhotoDatas.size() > index) {
                while ((allPhotoDatas.get(index) instanceof AssetDateGroup)) {
                    index++;
                }
                imageView4.setTag(allPhotoDatas.get(index).getPath());
                PhotoCache.getInstance(mContext).setImageView(allPhotoDatas.get(index).getPath(), imageView4);
            }
            index++;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentAdapter.swapData(allPhotoDatas);
                checkIndex = 0;
                notifyDataSetChanged();
            }
        });
        if (checkIndex == 0) {
            if (!allPhotoDatas.equals(contentAdapter.getAdapterData())) {
                contentAdapter.swapData(allPhotoDatas);
            }
        }
        return convertView;
    }

    @Override
    public boolean onHover(int position, final List<AssetItem> datas) {
        if (position == 0) {
            return false;
        }
        if (position == 1) {
            XalBumsDialog.Builder build = new XalBumsDialog.Builder(mContext);
            final XalBumsDialog dailog;
            dailog = build.setTitle(R.string.input_albums_name).setInput(true).setPositiveButton(R.string.yes_button_content, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String albumsName = ((XalBumsDialog) dialog).getInputMessage();
                    if (albumsName != null && !"".equals(albumsName.trim())) {
                        AllVirtualAlbumsManager manager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
                        AlbumsInfo albumsInfo = manager.createAssetsFolder(albumsName, false);
                        if (albumsInfo != null) {
                            manager.moveAssetsToFolder(albumsInfo, datas);
                        }
                    } else {
                        Toast.makeText(mContext, "input is empty.", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    notifyDataSetChanged();
                }
            }).setNegativeButton(R.string.no_button_content, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dailog.show();
            return true;
        } else {
            AllVirtualAlbumsManager manager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
            AlbumsInfo info = (AlbumsInfo) groupsPhotos.get(position - (2 + fictitiousAlbumPhotosInfoGroup.size())).getGroupTag();
            manager.moveAssetsToFolder(info, datas);
            notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public int onCheckData(int position, List<AssetItem> datas) {
        if (position == 0) {
            return CONTAIN_WHOLE;
        }
        if (position == 1) {
            return CONTAIN_NO;
        }
        int fictitiousSize = 0;
        if (fictitiousAlbumPhotosInfoGroup != null) {
            fictitiousSize = fictitiousAlbumPhotosInfoGroup.size();
        }
        if (position < fictitiousSize + 2) {
            return CONTAIN_WHOLE;
        }

        if (position >= fictitiousSize + 2) {
            boolean contain = false;
            boolean whole = true;
            for (AssetItem asset : datas) {
                if (groupsPhotos.get(position - fictitiousSize - 2).getListAssets().contains(asset)) {
                    contain = true;
                } else {
                    whole = false;
                }
            }
            if (!contain) {
                return CONTAIN_NO;
            } else if (!whole) {
                return CONTAIN_PART;
            } else {
                return CONTAIN_WHOLE;
            }
        }
        return CONTAIN_NO;
    }

    @Override
    public void notifyDataSetChanged() {
        AllVirtualAlbumsManager assetManager = (AllVirtualAlbumsManager) AssetManagerFactory.getInstance(mContext, AssetManagerFactory.PHOTO_VIRTUAL_ALBUM_MGR);
        groupsPhotos = assetManager.getAllGroupPhotos();
        super.notifyDataSetChanged();
    }
}
