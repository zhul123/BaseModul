package com.base.http;

import android.content.Context;

import java.util.Map;


public class GetHeaderUtils {

    private Context mContext;
    public static final String APP_VERSION_NAME = "appVersionName";
    public static final String APP_VERSION_CODE = "appVersionCode";
    public static final String API_VERSION = "version";
    public static final String OS_NAME = "platform";
    public static final String NETWORKING = "netType";
    public static final String RESOLUTION = "resolution";
    public static final String MODEL = "deviceType";
    public static final String TIMESTAMP = "timestamp";
    public static final String SCREENSIZE = "screenSize";
    public static final String DEVICEID = "deviceId";
    public static final String CHANNEL = "channel";
    public static final String LANGUAGE = "language"; //en, zh
    public static final String TOKEN = "accessToken";
    public static final String APP_LOCATION = "userLocation";
    public static final String APP_LONGITUDE = "longitude";
    public static final String APP_LATITUDE = "latitude";
    public static final String APP_GPSSECURITY = "gpsSecurity";
    public static final String APP_HEADER_KEY = "baseinfo";
    public static final String ANDROID_IMEI_ID = "androidImeiId";
    public static final String DEEPLINK_ROUTER = "deepLinkRouter";
    /**
     * 接口灰度发布
     */
    private static final String GRAY_NUM = "grayNum";

    private static volatile GetHeaderUtils instance = null;
    private Map<String, String> param;
/*
    private GetHeaderUtils() {
        mContext = BaseApplication.getInstance();
    }

    *//**
     * 静态内部类
     *//*
    public static GetHeaderUtils getInstance() {
        if (instance == null) {
            synchronized (GetHeaderUtils.class) {
                if (instance == null) {
                    instance = new GetHeaderUtils();
                }
            }
        }
        return instance;
    }

    private synchronized String getHeaderString() throws Exception {
        //header中的value不能为null
        param = new HashMap<>();
        param.put(API_VERSION, getApiVersion());
        param.put(OS_NAME, getOsName());
        param.put(TIMESTAMP, getTimestamp());
        param.put(SCREENSIZE, getScreensize());
        param.put(DEVICEID, getDeviceid());
        param.put(MODEL, getModel());
        param.put(CHANNEL, getChannel());
        param.put(NETWORKING, getNetworking());
        param.put(LANGUAGE, getLanguage());
        param.put(RESOLUTION, getResolution());
        param.put(APP_VERSION_NAME, Utils.getVersion(mContext));
        param.put(APP_VERSION_CODE, getAppVersion());
        param.put(TOKEN, AppSharedPreferencesHelper.getCurrentUserToken() != null ? AppSharedPreferencesHelper.getCurrentUserToken() : "");
        param.put(APP_LOCATION, SystemFunction.mLocation);
        param.put(APP_LONGITUDE, SystemFunction.mLongitude);
        param.put(APP_LATITUDE, SystemFunction.mLatitude);
        param.put(ANDROID_IMEI_ID, DeviceInfo.getIMEI(mContext));
        String headerString = JSON.toJSONString(param);
        param.clear();
        return URLEncoder.encode(AES128.getInstance().encryptHeader(headerString));
    }


    private static String appVersion;

    private String getAppVersion() {
        if (null == appVersion) {
            appVersion = String.valueOf(Utils.getVersionCode(mContext));
        }
        return appVersion;
    }

    private String getOsVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public static String NETWORK_TYPE_WIFI = "1";
    //3G, 4G etc
    public static String NETWORK_TYPE_MOBILE = "2";

    private String getNetworking() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return NETWORK_TYPE_WIFI;
        }
        return NETWORK_TYPE_MOBILE;
    }

    private String getResolution() {
        DisplayMetrics dm = new DisplayMetrics();

        WindowManager manager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return screenWidth + "x" + screenHeight;
    }

    private String getModel() {
        return Build.MODEL;
    }

    private String getTimestamp() {
        Calendar calendar = Calendar.getInstance();//获取当前日历对象
        long localTime = calendar.getTimeInMillis() / 1000;//获取当前时区下日期时间对应的时间戳, 服务器仅支持10位时间戳,精确到秒
        return localTime + "";
    }

    private synchronized String getExDeviceId(){
        return getDeviceid();
    }

    private String getDeviceid() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getChannel() {
        final String channel = Utils.getChannel(mContext, "UMENG_CHANNEL");
        return channel;
    }

    private String getLanguage() {
        final String language = Utils.getChannel(mContext, "LANGUAGE");
        return language;
    }

    *//**
     * 以下皆为固定参数 无实际意义
     *
     * @return
     *//*
    private String getApiVersion() {
        return "1.0.1";
    }

    private String getOsName() {
        return "2";
    }

    private String getScreensize() {
        return "22";
    }


    public synchronized Map<String, String> getHeaderMap(){
        Map<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put(APP_HEADER_KEY, getHeaderString());
            hashMap.put(GRAY_NUM, getExDeviceId());
            if(!TextUtils.isEmpty(SystemFunction.getDeeplinkRouter())) {
                //和IOS有差异，Android进行了encode，ios没有encode
                hashMap.put(DEEPLINK_ROUTER, URLEncoder.encode(SystemFunction.getDeeplinkRouter(), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }*/
}
