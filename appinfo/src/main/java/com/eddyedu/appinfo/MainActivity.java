package com.eddyedu.appinfo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eddyedu.appinfo.bean.AppInfo;
import com.eddyedu.appinfo.bean.AppInfoAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<AppInfo> data;
    private List<AppInfo> getData;
    private ListView listView;
    private AppInfoAdapter adapter;
    private PackageManager pm;

    private Handler handler;
    public KProgressHUD kProgressHUD ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pm = getPackageManager();
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        data = new ArrayList<>();
        kProgressHUD = new KProgressHUD(this);
        kProgressHUD.setLabel("正在加载，请稍后...")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f);
        adapter = new AppInfoAdapter(data,this);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo info = (AppInfo) parent.getItemAtPosition(position);
                StringBuffer buffer = new StringBuffer();
                buffer.append("应用名称：" + info.getName()).append("\n");
                buffer.append("应用包名：" + info.getPackageName()).append("\n");
                buffer.append("MD5：" + info.getMd5()).append("\n");
                buffer.append("SHA1：" + info.getSha1()).append("\n");
                buffer.append("版本号：" + info.getVersion()).append("\n");
                buffer.append("版本：" + info.getVersionName());
                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cmb.setText(buffer.toString());
                Toast.makeText(MainActivity.this, "应用信息已复制到剪切版", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                data.addAll(getData);
                adapter.notifyDataSetChanged();
                kProgressHUD.dismiss();
            }
        };
       loadingData();
    }

    private void loadingData() {

        kProgressHUD.show();
//        adapter.notifyDataSetChanged();
//        handler.sendEmptyMessage(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
                getData = new ArrayList<AppInfo>();
                for (ApplicationInfo application : applications) {
                    AppInfo info = new AppInfo();
                    info.setName((String) application.loadLabel(pm));
                    String pkgName = application.packageName;
                    info.setPackageName(pkgName);
                    info.setIcon(application.loadIcon(pm));
                    try {
                        PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
                        info.setVersion(packageInfo.versionCode+"");
                        info.setVersionName(packageInfo.versionName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        info.setVersion("");
                        info.setVersionName("");
                    }
                    info.setMd5(getMd5(pkgName));
                    info.setSha1(getSHA1(pkgName));
                    getData.add(info);
                }

                handler.sendEmptyMessage(1);
            }
        }).start();
    }



    private String getSHA1(String pkgName) {
        String sha1 = "";
        sha1 = getAuthString(pkgName);

        return sha1;
    }

    private String getMd5(String pkgName) {
        String md5 = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            md5 = encryptionMD5(sign.toByteArray());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return md5;
    }
    /**
     * MD5加密
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i])).append(":");
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i])).append(":");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5Str = md5StrBuff.toString().toUpperCase();
        if (md5Str.endsWith(":")){
            md5Str = md5Str.substring(0,md5Str.length()-1);
        }
        return md5Str;
    }



    protected  String getAuthString(String pkgName)
    {
        return getSha1print( pkgName);
    }

    private  String getSha1print( String paramString)
    {
        List localList = pm.getInstalledPackages(64);
        System.out.println("大小：" + localList.size());
        Iterator localIterator = localList.iterator();
        PackageInfo localPackageInfo;
        do
        {
            if (!localIterator.hasNext()) {
                return null;
            }
            localPackageInfo = (PackageInfo)localIterator.next();
        } while (!localPackageInfo.packageName.equals(paramString));
        Object localObject = "";
        try
        {
            Signature[] arrayOfSignature = localPackageInfo.signatures;
            X509Certificate localX509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(arrayOfSignature[0].toByteArray()));
            System.out.println("签名为" + arrayOfSignature[0].toCharsString());
            String str = getFingerprintAsString(localX509Certificate);
            localObject = str;
        }
        catch (CertificateException localCertificateException){
//            label155:
//            StringBuffer localStringBuffer;
//            int i;
//            break label155;
        }
        int i;
        StringBuffer localStringBuffer = new StringBuffer();
        for (i = 0;; i++)
        {
            if (i >= ((String)localObject).length()) {
                return localStringBuffer.toString();
            }
            localStringBuffer.append(((String)localObject).charAt(i));
            if ((i > 0) && (i % 2 == 1) && (i < -1 + ((String)localObject).length())) {
                localStringBuffer.append(":");
            }
        }
    }

     String getFingerprintAsString(X509Certificate paramX509Certificate){
        try
        {
            String str = Hex.encode(generateSHA1Fingerprint(paramX509Certificate.getEncoded()));
            return str;
        }
        catch (CertificateEncodingException localCertificateEncodingException) {}
        return null;
    }

    static  class Hex{
        public static String encode(byte[] paramArrayOfByte){
            System.out.println("SHA1的字节长度：" + paramArrayOfByte.length);
            char[] arrayOfChar = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
            StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
            for (int i = 0;; i++)
            {
                if (i >= paramArrayOfByte.length) {
                    return localStringBuilder.toString();
                }
                localStringBuilder.append(arrayOfChar[((0xF0 & paramArrayOfByte[i]) >> 4)]);
                localStringBuilder.append(arrayOfChar[(0xF & paramArrayOfByte[i])]);
            }
        }
    }

    static byte[] generateSHA1Fingerprint(byte[] paramArrayOfByte){
        try{
            byte[] arrayOfByte = MessageDigest.getInstance("SHA1").digest(paramArrayOfByte);
            return arrayOfByte;
        }catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
        return null;
    }
}
