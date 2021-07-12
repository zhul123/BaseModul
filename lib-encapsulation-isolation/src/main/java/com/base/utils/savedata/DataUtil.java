package com.base.utils.savedata;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * 本地数据存储与读取工具
 */
public class DataUtil {
    private static  DataUtil instance = null;
    private int mode;
    private String cryptKey;

    public static <T> DataUtil getInstance(){
        if(instance == null){
            synchronized (DataUtil.class){
                if (instance == null)
                    instance = new DataUtil();
            }
        }
        instance.mode = 0;
        instance.cryptKey = null;
        return instance;
    }

    public DataUtil setMode(int mode){
        this.mode = mode;
        return this;
    }

    public DataUtil setCryptKey(String cryptKey){
        this.cryptKey = cryptKey;
        return this;
    }

    private MMKV getEditer(){
        if(!TextUtils.isEmpty(cryptKey)){
            int myMode = mode == MMKV.MULTI_PROCESS_MODE ? MMKV.MULTI_PROCESS_MODE : MMKV.SINGLE_PROCESS_MODE;
            return MMKV.defaultMMKV(myMode,cryptKey);
        }else {
            return MMKV.defaultMMKV();
        }
    }

    public void clear(){
        getEditer().clear();
    }

    public String getString(String key){
        return getEditer().getString(key,"");
    }

    public String getString(String key,String defValue){
        return getEditer().getString(key,defValue);
    }

    public int getInt(String key){
        return getEditer().getInt(key,0);
    }

    public int getInt(String key,int defValue){
        return getEditer().getInt(key,defValue);
    }

    public float getFloat(String key){
        return getEditer().getFloat(key,0f);
    }

    public float getFloat(String key, float defValue){
        return getEditer().getFloat(key,defValue);
    }

    public boolean getBoolean(String key){
        return getEditer().getBoolean(key,false);
    }

    public boolean getBoolean(String key,boolean defValue){
        return getEditer().getBoolean(key,defValue);
    }

    public byte[] getByte(String key){
        return getEditer().getBytes(key,null);
    }

    public byte[] getByte(String key,byte[] defValue){
        return getEditer().getBytes(key,defValue);
    }

    public long getLong(String key){
        return getEditer().getLong(key,0);
    }

    public long getLong(String key,long defValue){
        return getEditer().getLong(key,defValue);
    }

    public Set getStringSet(String key){
        return getEditer().getStringSet(key,null);
    }

    public Set getStringSet(String key,Set defValue){
        return getEditer().getStringSet(key,defValue);
    }

    public boolean put(String key, Object value){
        if(value instanceof String) {
            return getEditer().putString(key, (String) value).commit();
        }else if(value instanceof Integer){
            return getEditer().putInt(key, (int) value).commit();
        }if(value instanceof Boolean){
            return getEditer().putBoolean(key, (boolean) value).commit();
        }if(value instanceof byte[]){
            return getEditer().putBytes(key, (byte[]) value).commit();
        }if(value instanceof Float){
            return getEditer().putFloat(key, (float) value).commit();
        }if(value instanceof Long){
            return getEditer().putLong(key, (long) value).commit();
        }if(value instanceof Set){
            return getEditer().putStringSet(key, (Set<String>) value).commit();
        }
        return false;
    }

    public static String getPath(){
       return MMKV.getRootDir();
    }
}
