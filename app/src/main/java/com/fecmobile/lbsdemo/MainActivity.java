package com.fecmobile.lbsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.fecmobile.lbsdemo.holder.CommViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements CloudListener {

    private ListView listView;
    private List<CloudPoiInfo> list;
    private CommAdapter<String> adapter;
    private BDLocation loctaion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        CloudManager.getInstance().init(this);
        setContentView(R.layout.activity_main);
        initView();
        new GetLocation(this, new LoctaionInteface() {
            @Override
            public void onSucceed(BDLocation mLoctaion) {
                loctaion = mLoctaion;
                sheach("distance:1");
            }
            @Override
            public void onLose(String str) {

            }
        });
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new CommAdapter<String>(this, list, R.layout.item_list) {
            @Override
            public void convert(CommViewHolder holder, CloudPoiInfo item) {
                try {
                    int jl = item.distance;
                    String jlStr = jl >= 1000 ? ((float) jl / 1000) + "公里" : jl + "米";

                    holder.setImageByUrl(R.id.item_img, new JSONObject(String.valueOf(item.extras.get("imgUrl"))).getString("mid"))
                            .setText(R.id.item_name, item.title)
                            .setText(R.id.item_addr, "地址：" + item.address)
                            .setText(R.id.item_price, "价格：¥" + String.valueOf(item.extras.get("price")))
                            .setText(R.id.item_jl, "距离：" + jlStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        listView.setAdapter(adapter);
    }

    //距离最近排序
    public void distanceJin(View view) {
        sheach("distance:1");
    }

    //距离最远排序
    public void distanceYuan(View view) {
        sheach("distance:-1");

    }

    //价格升序
    public void priceSheng(View view) {
        sheach("price:1");
    }

    //价格降序
    public void priceJiang(View view) {
        sheach("price:-1");
    }

    //跳转地图界面
    public void toMap(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }


    private void sheach(String sortby) {
        NearbySearchInfo info = new NearbySearchInfo();
        info.ak = "LBwSoeEwmf93yqo4fm1G4GXKrQ7VXjsf";
        info.location = loctaion.getCity();
        info.geoTableId = 147551;
        info.radius = 100000;
        info.location = loctaion.getLongitude() + "," + loctaion.getLatitude();
        info.sortby = sortby;


        CloudManager.getInstance().nearbySearch(info);
    }

    @Override
    public void onGetSearchResult(CloudSearchResult cloudSearchResult, int i) {
        list.clear();
        list.addAll(cloudSearchResult.poiList);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDetailSearchResult(DetailSearchResult detailSearchResult, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloudManager.getInstance().destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
