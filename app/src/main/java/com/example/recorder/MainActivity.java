package com.example.recorder;

import android.Manifest;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.recorder.fragment.NativeFragment;
import com.example.recorder.widget.MyProgressDialog;
import com.example.recorder.widget.NoScrollViewPager;
import com.example.recorder.widget.PagerSlidingTabStrip;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks  {
    
    private static String appKey = "Insert your appKey here";
    private static String secretKey = "Insert your secretKey here";
    private PagerSlidingTabStrip tab_title;
    private NoScrollViewPager mViewPager;
    private NativeFragment mNativeFragment;
    private MyProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        requestAllPermissions();
    }

    private void init() {
        tab_title = (PagerSlidingTabStrip) findViewById(R.id.tab_title);
        mViewPager = (NoScrollViewPager) findViewById(R.id.mViewPager);
        mViewPager.setPagingEnabled(false);
        mNativeFragment = new NativeFragment();
        FragmentManager fragmentTransaction = getSupportFragmentManager();

        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter(fragmentTransaction);
        pagerAdapter.add(mNativeFragment, "Pronunciation assessment");

        mViewPager.setAdapter(pagerAdapter);
        tab_title.setViewPager(mViewPager);

        dialog = MyProgressDialog.createDialog(MainActivity.this);
//        dialog.show();

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //dialog.show();
                Toast.makeText(MainActivity.this, "Pronunciation assessment", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    
    public void initSDK() {
         Integer ret = SkegnManager.getInstance(this).initEngine(appKey, secretKey);
        if(ret != 0) {
            Toast.makeText(MainActivity.this, "InitEngine failed, please check for errors on logcat", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, " Successfully initialized the evaluation engine", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Rewrite method to pass permission request results to EasyPermission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, perms, grantResults, this);
    }



    /**
     * The permission request method can be directly called.
     */

    public void  requestAllPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
           initSDK();

        } else {
            //No permission to apply for permission
            EasyPermissions.requestPermissions(
                    this, "apply for permission", 1, perms);
        }


    }

    /**
     * Allow permission to trigger
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        initSDK();

    }

    /**
     * Trigger after disabling permissions
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            Toast.makeText(MainActivity.this, "No relevant permissions", Toast.LENGTH_LONG).show();
        }
    }


    private static class FragmentViewPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<CharSequence> mTitleList = new ArrayList<>();

        FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {

            return getCount() > position ? mFragmentList.get(position) : null;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        void add(Fragment fragment, CharSequence title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkegnManager.getInstance(MainActivity.this).deleteSkegn();

    }
}
