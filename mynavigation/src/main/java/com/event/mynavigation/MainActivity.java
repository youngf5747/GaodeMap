package com.event.mynavigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

public class MainActivity extends AppCompatActivity {

    private AMap mAMap;
    private MapView mMapView;
//    private AMapNaviView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.navigation));
        mMapView = (MapView) findViewById(R.id.mapview);
//        mMapView = (AMapNaviView) findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        init();

    }

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
