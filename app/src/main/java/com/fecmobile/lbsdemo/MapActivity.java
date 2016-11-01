package com.fecmobile.lbsdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.platform.comapi.map.I;

import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements CloudListener{

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LayoutInflater inflater;
    private BDLocation loctaion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        CloudManager.getInstance().init(this);
        setContentView(R.layout.activity_map);
        inflater = LayoutInflater.from(this);
        mapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        sheach("");
        new GetLocation(this, new LoctaionInteface() {
            @Override
            public void onSucceed(BDLocation mLoctaion) {
                loctaion = mLoctaion;
            }
            @Override
            public void onLose(String str) {

            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                final LatLng latLng = marker.getPosition();
                View view = inflater.inflate(R.layout.item_win, null);
                ImageView iv = (ImageView) view.findViewById(R.id.pop_img);
                TextView nameTV = (TextView) view.findViewById(R.id.pop_name);
                TextView priceTV = (TextView) view.findViewById(R.id.pop_price);
                TextView jlTV = (TextView) view.findViewById(R.id.pop_jl);
                TextView btnTV = (TextView) view.findViewById(R.id.btn);
                BaseApplication.imageLoader.displayImage(bundle.getString("imgUrl"), iv, BaseApplication.options);
                final String name = bundle.getString("name");
                nameTV.setText(name);
                priceTV.setText("¥：" + bundle.getString("price"));
                jlTV.setVisibility(View.GONE);
                btnTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goTODaoHang(new LatLng(loctaion.getLatitude(),loctaion.getLongitude()),latLng,name);
                    }
                });
                InfoWindow mInfoWindow;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                mInfoWindow = new InfoWindow(view,latLng, -50);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    /**
     * 打开导航
     * @param latLng 起点坐标
     * @param latLng1 终点坐标
     */
    private void goTODaoHang(LatLng latLng, LatLng latLng1,String name) {
        NaviParaOption para = new NaviParaOption();
        para.startPoint(latLng);
        para.startName("我的位置");
        para.endPoint(latLng1);
        para.endName(name);

        try {

            BaiduMapNavigation.openBaiduMapNavi(para, this);


        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    BaiduMapNavigation.GetLatestBaiduMapApp(MapActivity.this);
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    private void sheach(String sortby) {
        LocalSearchInfo info = new LocalSearchInfo();
        info.ak = "LBwSoeEwmf93yqo4fm1G4GXKrQ7VXjsf";
        info.geoTableId = 147551;
        info.region = "深圳市";
        CloudManager.getInstance().localSearch(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        CloudManager.getInstance().destroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public void onGetSearchResult(CloudSearchResult result, int i) {
        if (result != null && result.poiList != null && result.poiList.size() > 0) {

            mBaiduMap.clear();
            BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            LatLng ll;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (CloudPoiInfo info : result.poiList) {
                ll = new LatLng(info.latitude, info.longitude);
                OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
                Overlay overlay = mBaiduMap.addOverlay(oo);
                Bundle bundle = new Bundle();
                bundle.putString("name",info.title);
                bundle.putString("price",String.valueOf(info.extras.get("price")));
                try {
                    bundle.putString("imgUrl",new JSONObject(String.valueOf(info.extras.get("imgUrl"))).getString("mid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundle.putString("address",info.address);

                overlay.setExtraInfo(bundle);
                builder.include(ll);
            }
            LatLngBounds bounds = builder.build();
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
            mBaiduMap.animateMapStatus(u);
        }
    }

    @Override
    public void onGetDetailSearchResult(DetailSearchResult detailSearchResult, int i) {

    }
}
