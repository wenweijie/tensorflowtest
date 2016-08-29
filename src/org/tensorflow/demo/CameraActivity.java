/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import java.util.ArrayList;
import java.util.List;

import org.tensorflow.demo.fragment.CameraConnectionFragment;
import org.tensorflow.demo.fragment.FaceDetectFragment;
import org.tensorflow.demo.fragment.LocalPictureFragment;

import android.Manifest;
import android.R.integer;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraActivity extends FragmentActivity implements OnClickListener {
    private final static String TAG = "CameraActivity";

    // fragment----support.v4.app/view
    private ViewPager mViewPager;
    private FragmentPagerAdapter mFpAdapter;
    private List<Fragment> mFragList;

    private LinearLayout mCameraLayout;
    private LinearLayout mLocalLayout;
    private LinearLayout mOpencvLayout;
    private LinearLayout mButtomLayout;

    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    
    private CameraConnectionFragment CCFragment = new CameraConnectionFragment();
    private LocalPictureFragment LPFragment = new LocalPictureFragment();
    private FaceDetectFragment FDFragment=new FaceDetectFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        // TargetSDKVersion is Android 6.0 (AndroidSDK - 23)
        if (hasPermission()) {
            // Version >= 6.0
            if (null == savedInstanceState) {
                initView();
            }
        } else {
            // Version < 6.0
            requestPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // setFragment();
                    initView();
                } else {
                    requestPermission();
                }
            }
        }
    }

    // SDK Version Permission
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
                    || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(
                        CameraActivity.this,
                        "Camera AND storage permission are required for this demo",
                        Toast.LENGTH_LONG).show();
            }
            // Will AutoRun onRequestPermissionsResult() function
            requestPermissions(new String[] {
                    PERMISSION_CAMERA,
                    PERMISSION_STORAGE
            }, PERMISSIONS_REQUEST);
        }
    }

    /**
     * Finally want to do .beginTransaction() is Create new fragment and
     * transaction .replace(R.id.fragment_container, newFragment) is in the X
     * view with this fragment, and add the transaction to the back stack.
     * newFragment is want to "new Fragment()" "R.id.container" : is the tag of
     * "R.layout.activity_camera" "CameraConnectionFragment.newInstance()" : is
     * mean "new CameraConnectionFragment()" .commit() is Commit the
     * transaction, make it work
     */
    // private void setFragment() {
    // getFragmentManager()
    // .beginTransaction()
    // .replace(R.id.container, CameraConnectionFragment.newInstance())
    // .commit();
    // }

    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mButtomLayout=(LinearLayout)findViewById(R.id.bottom);
        //not preload fragment
        mViewPager.setOffscreenPageLimit(0);
        mFragList = new ArrayList<Fragment>();
              
        
        mFragList.add(CCFragment);
        mFragList.add(LPFragment);
        mFragList.add(FDFragment);
        
        // ViewPager
        mFpAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragList.size();
            }

            @Override
            public Fragment getItem(int ItemNum) {
                return mFragList.get(ItemNum);
            }
        };

        mViewPager.setAdapter(mFpAdapter);

        mCameraLayout = (LinearLayout) findViewById(R.id.CameraLayout);
        mCameraLayout.setOnClickListener(this);
        mLocalLayout = (LinearLayout) findViewById(R.id.LocalLayout);
        mLocalLayout.setOnClickListener(this);
        mOpencvLayout=(LinearLayout)findViewById(R.id.OpencvLayout);       
        mOpencvLayout.setOnClickListener(this);
       

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int PageNum) {
                switch (PageNum) {
                    case 0: {
                        mCameraLayout
                                .setBackgroundResource(R.drawable.image_toolbar_bg_sel);
                        mLocalLayout
                                .setBackgroundResource(R.drawable.image_toolbar_bg);
                        mOpencvLayout
                        .setBackgroundResource(R.drawable.image_toolbar_bg);
                        break;
                    }
                    case 1: {
                        
                        mCameraLayout
                                .setBackgroundResource(R.drawable.image_toolbar_bg);
                        mLocalLayout
                                .setBackgroundResource(R.drawable.image_toolbar_bg_sel);
                        mOpencvLayout
                        .setBackgroundResource(R.drawable.image_toolbar_bg);
                        
                        
                        break;
                    }
                    case 2:{
                        mCameraLayout
                        .setBackgroundResource(R.drawable.image_toolbar_bg);
                        mLocalLayout
                        .setBackgroundResource(R.drawable.image_toolbar_bg);
                        mOpencvLayout
                        .setBackgroundResource(R.drawable.image_toolbar_bg_sel);
                break;
                    }
                    default: {
                        Log.i(TAG, "Activity setOnPageChangeListener Error:"
                                + PageNum);
                        break;
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.CameraLayout:
                mViewPager.setCurrentItem(0);           
                break;
            case R.id.LocalLayout:    
                mViewPager.setCurrentItem(1);
                break;
            case R.id.OpencvLayout:    
                mViewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);     
        Log.e("---------shit", String.valueOf(mViewPager.getCurrentItem()));
        int current_item=0;
        current_item=mViewPager.getCurrentItem();            
        initView();
        mViewPager.setCurrentItem(current_item);
        
        if(current_item==2){
            mButtomLayout.setVisibility(View.GONE);                 
        }else{
            mButtomLayout.setVisibility(View.VISIBLE);  
        }
        
        Log.e(TAG, "----------CHANGE_ORITATION");
    }
}
