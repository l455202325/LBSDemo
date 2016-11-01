package com.fecmobile.lbsdemo;


import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class GetLocation {
    public LocationClient mLocationClient = null;
    private BDLocationListener mBdLocationListener = null;
    private int i = 0;

    public GetLocation(Context context, final LoctaionInteface mLoctaionInteface) {
        super();
        //参数:1.上下文对象,2.回调接口
        init(context, mLoctaionInteface);
    }

    private void init(Context context, final LoctaionInteface mLoctaionInteface) {
        mLocationClient = new LocationClient(context);
        mBdLocationListener = new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation loctaion) {
                if (loctaion.getAddrStr() != null) {
//                    i++;
//                    if (i > 2) {
                        //过滤掉前3次的定位数据
                        mLoctaionInteface.onSucceed(loctaion);
                        mLocationClient.stop();
//                    }

                } else {
                    mLoctaionInteface.onLose("定位失败");
                    mLocationClient.stop();
                }
            }
        };
        mLocationClient.registerLocationListener(mBdLocationListener);
        InitLocation();
        mLocationClient.start();
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02  bd09ll
        int span = 1000;
        option.setScanSpan(span);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stop();
        }
    }

    public void startLocation() {
        if (null != mLocationClient) {
            i = 0;
            mLocationClient.start();
        }
    }
}
