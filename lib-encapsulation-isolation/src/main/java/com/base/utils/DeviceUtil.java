package com.base.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.base.app.BaseApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;


public class DeviceUtil {
    public static String imeiStr = null;

    public static String getIMEI(){
        if(imeiStr != null)
            return imeiStr;
        Context context = BaseApplication.getInstance().getApplicationContext();
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 + Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 + Build.USER.length()%10 ; //13 digits

        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = "";
        try{
            m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        }catch (Exception e){
            System.out.println("======WIFI:"+e.getMessage());
        }

        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();; // Local Bluetooth adapter

        String m_szBTMAC = "";
        try{
            m_szBTMAC = m_BluetoothAdapter.getAddress();
        }catch (Exception e){
            System.out.println("======BTMAC:"+e.getMessage());
        }

        String m_szLongID = m_szDevIDShort + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(),0,m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i=0;i<p_md5Data.length;i++) {
            int b =  (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID+="0";
            // add number to string
            m_szUniqueID+= Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        System.out.println("=========m_szUniqueID:"+m_szUniqueID);
        imeiStr = m_szUniqueID;
        return m_szUniqueID;
    }

    public static String getWlanMac(){
        String m_szWLANMAC = "";
        try{
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                WifiManager wm = (WifiManager) BaseApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
            }else{
                m_szWLANMAC = loadFileAsString("/sys/class/net/wlan0/address")
                        .toUpperCase().substring(0, 17);
            }
        }catch (Exception e){
            System.out.println("======WIFI:"+e.getMessage());
        }
        return m_szWLANMAC;
    }

    private static String loadFileAsString(String filePath)
            throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * @param context * @return
     */
    public static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            ToastUtil.getInstance().makeText(e.getMessage());
            // 赋予默认值
            e.printStackTrace();
            LogUtil.e("zhulei6.0-",e.getMessage());
            e.printStackTrace();
        }

        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    public static String getMacAddress() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();//去空格
                    break;
                }
            }
        } catch (IOException ex) {
            ToastUtil.getInstance().makeText(ex.getMessage());
            // 赋予默认值
            ex.printStackTrace();
            LogUtil.e("zhulei6.0-7.0",ex.getMessage());
        }

        return macSerial;
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     * @return
     */
    public static String getMacFromHardware() {
        try {
            Enumeration<NetworkInterface> all = (Enumeration<NetworkInterface>) Collections.list(NetworkInterface.getNetworkInterfaces());
            if(all == null){
                return "";
            }
            while (all.hasMoreElements()){
                NetworkInterface nif = all.nextElement();
                if (!nif.getName().equals("wlan0"))
                    continue;
                byte macBytes[] = nif.getHardwareAddress();
                if (macBytes == null) return "";
                StringBuilder res1 = new StringBuilder();
                for (Byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            ToastUtil.getInstance().makeText(e.getMessage());
            // 赋予默认值
            e.printStackTrace();
            LogUtil.e("zhulei7.0+",e.getMessage());
        }

        return "";
    }

    /**
     * 获取mac地址（适配所有Android版本）
     * @return
     */
    public static String getMacNew(){
        Context context = BaseApplication.getInstance();
        String mac = "";
        mac = getWlanMac();
        if(!TextUtils.isEmpty(mac)){
            return mac;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        if(TextUtils.isEmpty(mac)){
            mac = getNetworkInfoAsMap();
        }
//        if(BuildConfig.DEBUG) {
//            ToastUtil.getInstance().makeText(mac);
//        }
        return mac;
    }

    /**
     * 获取Mac地址及IP信息<br/>
     * IdeaHub涉及的网卡及IP包括: 有线网络, Wi-Fi, 无线热点<br/>
     * @return map key:networkName value:mac and ips
     */
    private static String getNetworkInfoAsMap() {
        try {
            Enumeration<NetworkInterface> networkInfos = NetworkInterface.getNetworkInterfaces();

            if(networkInfos == null){
                return "";
            }
            while (networkInfos.hasMoreElements()) {
                NetworkInterface networkInterface = networkInfos.nextElement();

                // 获取MAC地址
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null || macBytes.length == 0) {
                    continue;
                }

                StringBuilder res1 = new StringBuilder();
                for (Byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean getDeviceIsPhone() {
        TelephonyManager telephony = (TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        }else{
            return true;
        }
    }
}
