package com.sw.xalbums;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sw.assetmgr.statistics.StatisticsUtil;
import com.sw.xalbums.fragment.MainFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = HomeActivity.class.getSimpleName();

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.text_name_black);// 设置logo

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

//        changeToggleIndicator(R.drawable.btn_menu_normal);

        drawer.setDrawerListener(toggle);



        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        IAssetManager photoMgr = AssetManagerFactory.getInstance(this, AssetManagerFactory.PHOTO_MGR_ALL);

//        FragmentManager manager = getFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//
//
//        AllPhotoFragment fragment = new AllPhotoFragment();
//        fragment.swapData((List<AssetItem>) (List<AssetItem>) photoMgr.getAssetsSync());
//
//        transaction.replace(R.id.root_frame, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
        ////////////////////////////////////
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = new MainFragment();
        transaction.replace(R.id.root_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        StatisticsUtil.getDefaultInstance(this).onCreate();
        ////////////////////////////////////

//        android.app.FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.root_frame, ListBuddiesFragment.newInstance());
//        ft.commit();

    }

    public void setTitle(String title){
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.left_menu_check_update) {// update
            //TODO
        } else if (id == R.id.left_menu_share) {// share
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT,
                    R.string.app_name);
            share.putExtra(Intent.EXTRA_TEXT,
                    R.string.app_name);
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(Intent.createChooser(share,
                    getTitle()));
        } else if (id == R.id.left_menu_feedback) {// feedback
            //TODO
        } else if (id == R.id.left_menu_about_us) {// about
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About:");
            builder.setMessage("Version: " + getVersionCode());
//            builder.setCancelable(false);
            builder.setPositiveButton("OK", null);
//            builder.setNegativeButton("Cancel", null);
//            final AlertDialog dialog = builder.create();
            builder.show();
        } else if (id == R.id.left_menu_exit) {// exit
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getVersionCode() {
        String version = "";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionCode", e);
        }
        return version;
    }

    public void lockLeftDrawerClose(boolean b) {
        if (b)
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void lockRightDrawerClose(boolean b) {

    }

    public void changeToggleIndicator(int rid) {
        if (rid > 0) {
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setHomeAsUpIndicator(rid);
        } else {
            toggle.setDrawerIndicatorEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        StatisticsUtil.getDefaultInstance(this).onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatisticsUtil.getDefaultInstance(this).onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatisticsUtil.getDefaultInstance(this).onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsUtil.getDefaultInstance(this).onResume();
    }

    public void expandToolBar(boolean ani)
    {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.setExpanded(true, ani);
    }

    public void freezeToolBar(boolean b) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        if (b) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }
        toolbar.setLayoutParams(params);
    }

}
