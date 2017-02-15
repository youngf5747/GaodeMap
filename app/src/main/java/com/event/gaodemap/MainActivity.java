package com.event.gaodemap;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

public class MainActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener {

    private MapView mMapView;
    private AMap mAMap;

    private AMapLocationClient locationClient = null;

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
//                String result = Utils.getLocationStr(loc);
                updatePosition(loc);
            } else {
//                tvResult.setText("定位失败，loc is null");
            }
        }
    };
    private String strAddress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("高德地图Test");
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        init();
        initLocation();
        ToggleButton tb = (ToggleButton) findViewById(R.id.tb);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 设置使用卫星地图
                    mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                } else {
                    // 设置使用普通地图
                    mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                }
            }
        });

        Button btnSearch = (Button) findViewById(R.id.btn_search);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
//        final RadioButton manual = (RadioButton) findViewById(R.id.manual);
        final EditText etLocation = (EditText) findViewById(R.id.et_loc);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.check(R.id.manual);
                strAddress = etLocation.getText().toString().toString();
                if (TextUtils.isEmpty(strAddress)) {
                    Toast.makeText(MainActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    mAMap.clear();
                    GeocodeSearch geocodeSearch = new GeocodeSearch(MainActivity.this);
                    geocodeSearch.setOnGeocodeSearchListener(MainActivity.this);
                    GeocodeQuery query = new GeocodeQuery(strAddress, "武汉");
                    geocodeSearch.getFromLocationNameAsyn(query);
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.gps) {
                    startLocation();
                } else {
                    stopLocation();
                }
            }
        });
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        // 设置为false时, 曾无法定位 E/CellLocation: create CdmaCellLocation
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
//        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    private void updatePosition(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(pos);
        mAMap.moveCamera(cu);
        mAMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true);
        mAMap.addMarker(markerOptions);
    }

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            CameraUpdate zoomUpdate = CameraUpdateFactory.zoomTo(15);
            mAMap.moveCamera(zoomUpdate);
            CameraUpdate tiltUpdate = CameraUpdateFactory.changeTilt(30);
            mAMap.moveCamera(tiltUpdate);
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

    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
//            locationOption = null;
        }
    }

    @Override
    protected void onDestroy() {
        destroyLocation();
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        // 根据经纬度查询地址
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        // 获得解析得到的第一个地址
        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
        // 获得解析得到的经纬度
        LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();
        double dLat = latLonPoint.getLatitude();
        double dLng = latLonPoint.getLongitude();
//        Toast.makeText(MainActivity.this, "经度:" + dLng + "\n纬度:" + dLat,
//                Toast.LENGTH_LONG).show();
        LatLng pos = new LatLng(dLat, dLng);
        // 创建一个设置经纬度的 CameraUpdate
        CameraUpdate cameraUpdate = CameraUpdateFactory.changeLatLng(pos);
        // 更新地图的显示区域
        mAMap.moveCamera(cameraUpdate);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos)
                .title(strAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED))
                .draggable(true);
        mAMap.addMarker(markerOptions).showInfoWindow();
        /*GroundOverlayOptions options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .position(pos, 64);
        mAMap.addGroundOverlay(options);
        CircleOptions circleOptions = new CircleOptions()
                .center(pos) // 设置圆心
                .fillColor(0x80ffff00) // 设置圆心的填充颜色
                .radius(80) // 设置圆形的半径
                .strokeWidth(1) // 设置圆形的线条宽度
                .strokeColor(0xff000000); // 设置圆形的线条颜色
        mAMap.addCircle(circleOptions);*/
    }
//    private void GroundOverlayOptions(){}
}
