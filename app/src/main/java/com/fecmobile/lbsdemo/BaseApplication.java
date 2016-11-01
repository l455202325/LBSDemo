package com.fecmobile.lbsdemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class BaseApplication extends Application {
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    /**
     * 使用universal-image-loader时的一些默认配置这个配置为广告的配置 这个是普通图片的，为正方形
     */
    public static DisplayImageOptions options;

    /**
     * 会员头像模式值
     */
    public static DisplayImageOptions headOptions;

    /**
     * 广告模式值
     */
    public static DisplayImageOptions advOptions;

    /**
     * 商品加载失败时候的图像
     */
    public static DisplayImageOptions productOptions;

    public static DisplayImageOptions shopProductOption;

    /**
     * 商品详情页面加载失败时候的图像
     */
    public static DisplayImageOptions productDetailOptions;

    /**
     * 广告加载失败时候的图像
     */
    public static DisplayImageOptions bannerDetailOptions;

    /**
     * 启动加载失败时候的图像
     */
    public static DisplayImageOptions bannerStartOptions;
    // 收藏店铺默认图片
    public static DisplayImageOptions collectionOption;
    private boolean hasLogedYunXin = false;

    private static BaseApplication application;

    public boolean isLogedYunXin() {
        return hasLogedYunXin;
    }


    public static synchronized BaseApplication getInstance() {
        if (null == application) {
            application = new BaseApplication();
        }

        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /********** 异步下载图片缓存类 初始化 */
        initImageLoader(getApplicationContext());
        options = optionsInit(0);
    }


    /**
     * 初始化options
     *
     * @param flag 根据不同的flag返回不同的option，主要是默认图片不同
     * @return
     */
    private DisplayImageOptions optionsInit(int flag) {
        int sourceId = 0;

        if (flag == 0) {
            sourceId = R.mipmap.ic_launcher;
        }

        return new DisplayImageOptions.Builder()
                .showStubImage(sourceId)// 加载等待 时显示的图片
                .showImageForEmptyUri(sourceId)// 加载数据为空时显示的图片
                .showImageOnFail(sourceId)// 加载失败时显示的图片
                .cacheInMemory()
                .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)    //设置图片的缩放类型，该方法可以有效减少内存的占用
                .cacheOnDisc() /***
                 .displayer(new RoundedBitmapDisplayer(20))
                 */.build();

    }

/*    private LoginInfo getLoginInfo() {
        String account = (String) SPUtil.get(getApplicationContext(),"YunXinAccount","");;
        String token = (String) SPUtil.get(getApplicationContext(),"YunXinToken","");

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }*/


    /**
     * 图片加载库初始化
     */
    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO) // Not
                        // necessary
                        // in
                        // common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        // imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    /** 日志输出控制器  */

}
