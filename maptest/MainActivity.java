package com.jikexueyuan.maptest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener {
    private HorizontalListView horizontalListView;
    private SimpleAdapter simpleAdapter;
    private MapView mMapView = null;
    private LocationClient mLocationClient;
    private Location mlocation = new Location();
    private Marker mMarker;
    private BDLocationListener myListener = new MyLocationListener();
    private EditText et_id, et_latitude, et_longitude;
    private Button btn_send;
    private BaiduMap mbaidumap;
    private WriteService.Binder writerbinder = null;
    private List<Location> locations = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();
    private List<Map<String, Integer>> imgs = new ArrayList<>();
    private int[] icons = new int[]{R.drawable.icon_marka, R.drawable.icon_markb,
            R.drawable.icon_markc, R.drawable.icon_markd, R.drawable.icon_marke,
            R.drawable.icon_markf, R.drawable.icon_markg, R.drawable.icon_markh,
            R.drawable.icon_marki, R.drawable.icon_markj};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
        Intent whiteIntent = new Intent(MainActivity.this, WriteService.class);
        bindService(whiteIntent, this, Context.BIND_AUTO_CREATE);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        mLocationClient.requestLocation();
        btn_send.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mbaidumap = mMapView.getMap();
        mbaidumap.setMyLocationEnabled(true);
    }

    private void init() {
        mlocation.setId(100);
        mlocation.setRadius(0.0f);
        mlocation.setLatitude(0.0);
        mlocation.setLongitude(0.0);
        et_id = (EditText) findViewById(R.id.et_id);
        et_latitude = (EditText) findViewById(R.id.et_latitude);
        et_longitude = (EditText) findViewById(R.id.et_longitude);
        btn_send = (Button) findViewById(R.id.btn_send);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
    public void onServiceConnected(ComponentName name, IBinder service) {
        writerbinder = (WriteService.Binder) service;
        writerbinder.getService().setCallback(new WriteService.Callback() {
                                                  @Override
                                                  public void onDataChange(Location location) {
                                                      if (location.getId() == 100) {
                                                      } else {
                                                          if (locations.size() == 0)
                                                              locations.add(location);
                                                          for (int i = 0; i < locations.size(); i++) {
                                                              if (locations.get(i).getId() == location.getId()) {
                                                                  locations.remove(i);
                                                                  locations.add(location);
                                                              } else {
                                                                  locations.add(location);
                                                              }
                                                          }
                                                          mbaidumap.clear();
                                                          showmlocation();
                                                          for (int j = 0; j < locations.size(); j++) {
                                                              Location locationb = locations.get(j);
                                                              BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(icons[locationb.getId()]);
                                                              LatLng ll = new LatLng(locationb.getLatitude(), locationb.getLongitude());
                                                              MarkerOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(5);
                                                              Marker Marker = (Marker) mbaidumap.addOverlay(oo);
                                                          }
                                                      }
                                                  }
                                              }
        );
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onClick(View v) {
        mlocation.setLongitude(Double.parseDouble(et_longitude.getText().toString()));
        mlocation.setLatitude(Double.parseDouble(et_latitude.getText().toString()));
        mlocation.setId(Integer.parseInt(et_id.getText().toString()));
        mbaidumap.clear();
        showmlocation();
        Map<String, Integer> map = new HashMap<>();
        map.put("image", R.drawable.icon_gcoding);
        imgs.add(map);
        horizontalListView = (HorizontalListView) findViewById(R.id.horizontalScrollView);
        simpleAdapter = new SimpleAdapter(this, imgs, R.layout.adapter, new String[]{"image"}, new int[]{R.id.iv});
        horizontalListView.setAdapter(simpleAdapter);
    }

    private void showmlocation() {
        BitmapDescriptor mbd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        LatLng mll = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        MarkerOptions moo = new MarkerOptions().position(mll).icon(mbd).zIndex(9);
        mMarker = null;
        mMarker = (Marker) mbaidumap.addOverlay(moo);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            writerbinder.setLatitude(mlocation.getLatitude());
            writerbinder.setRadius(mlocation.getRadius());
            writerbinder.setLongitude(mlocation.getLongitude());
            writerbinder.setId(mlocation.getId());
//            若想从百度定位API收集坐标显示在map中则用下面代码代替
//            System.out.println("定位的角度为：" + location.getRadius());
//            System.out.println("定位的纬度为：" + location.getLatitude());
//            System.out.println("定位的经度度为：" + location.getLongitude());
//            writerbinder.setLatitude(location.getLatitude());
//            writerbinder.setRadius(location.getRadius());
//            writerbinder.setLongitude(location.getLongitude());
//            writerbinder.setId(mlocation.getId());
//            mbaidumap.clear();
//            showmlocation();
        }

    }
}
