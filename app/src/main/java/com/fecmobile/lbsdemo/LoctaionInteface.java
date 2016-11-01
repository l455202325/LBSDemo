package com.fecmobile.lbsdemo;

import com.baidu.location.BDLocation;

public interface LoctaionInteface {
	public void onSucceed(BDLocation loctaion);
	public void onLose(String str);

}
