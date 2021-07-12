package com.xylink.sdk.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.view.WindowManager;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.MakeCallResponse;
import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKErrorCode;
import com.ainemo.sdk.otf.Settings;
import com.ainemo.sdk.otf.VideoConfig;
import com.base.okpermission.OKPermissionListener;
import com.base.okpermission.OKPermissionManager;
import com.base.okpermission.PermissionItem;
import com.base.utils.ToastUtil;
import com.base.utils.savedata.DataUtil;
import com.base.widget.CustomProgressDialog;
import com.xylink.sdk.sample.activitys.xycall.XyCallActivity;
import com.xylink.sdk.sample.uikit.PasswordEditText;
import com.xylink.sdk.sample.utils.TextUtils;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.Context.ACTIVITY_SERVICE;

public class XyLinkUtil {

    public static final String AINEMO_EXT_ID = "20460033ce1dd43d66d3202da4c34cc1a2462e5d";//企业ID
    public static final String DEFAULTMEETINGID = "123456123456123456";//企业ID
    public static final String TOKEN = "3bfdda817a691f033110e3c6d41c4dbc3d97f2b246b739ea854aaf253f0c633d";
    public static volatile XyLinkUtil instance = null;
    public static boolean xyLoadSuc = false;
    public static boolean xyLoginSuc = false;
    private Context mContext;
    private String mCallNum;
    private String mCallPsd;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://joinMeeting
                    jointMeeting(mContext,mCallNum,mCallPsd);
                    break;
            }
        }
    };
    public static XyLinkUtil getInstance(){
        if(instance == null){
            synchronized (XyLinkUtil.class){
                if(instance == null){
                    instance = new XyLinkUtil();
                }
            }
        }
        return instance;
    }
    public void initXyLink(Context context){
        mContext = context;
        System.out.println("=======initXy");
        // 替换你自己的企业id
        Settings settings = new Settings(AINEMO_EXT_ID);
        String meetingRoomId = DataUtil.getInstance().getString("roomId",DEFAULTMEETINGID);
        if(TextUtils.isEmpty(meetingRoomId) || mContext == null){
            xyLoadSuc = false;
            return;
        }
        if(xyLoadSuc){
            return;
        }

        // Note: 默认或者不设置为360P, 360P满足大部分场景 如特殊场景需要720P, 请综合手机性能设置720P, 如果手机性
        // 能过差会出现卡顿,无法传输的情况, 请自己权衡.
        settings.setVideoMaxResolutionTx(VideoConfig.VD_640x360);
        settings.setDefaultCameraId(1);//0后置，1前置，默认1

        settings.setUiNeedSpeakers(true);

        int pId = Process.myPid();
        String processName = "";
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> ps = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo p : ps) {
            if (p.pid == pId) {
                processName = p.processName;
                break;
            }
        }

        // 避免被初始化多次
        if (processName.equals(context.getPackageName())) {
            try {
                NemoSDK nemoSDK = NemoSDK.getInstance();
                nemoSDK.init(context, settings);
                xyLoadSuc = true;
                System.out.println("=========xyinitSuc");
            } catch (Exception e){
                System.out.println("=========xyinitfail:"+e.getMessage());
            }
        }
    }
    public void xyLogin(Context context){
        mContext = context;
        String meetingRoomId = DataUtil.getInstance().getString("roomId",DEFAULTMEETINGID);
        xyLogin(context,meetingRoomId,null,null,false);
    }
    public void xyLogin(Context context,String userUuid,String callNum,String meetingPsd,boolean showLoading) {

        mContext = context;
        if(showLoading) {
            CustomProgressDialog.show(context, "视频会议登录中", false, null);
        }

        if(!xyLoadSuc){
            initXyLink(context);
            return;
        }
        //如果已经登录成功，则不重复登录
        if(xyLoginSuc || TextUtils.isEmpty(userUuid))
            return;

        String roomName = DataUtil.getInstance().getString("roomName");
        if(TextUtils.isEmpty(roomName)) {
            roomName = "会议室";
        }
        NemoSDK.getInstance().loginExternalAccount(roomName,userUuid, new ConnectNemoCallback() {
            @Override
            public void onFailed(int errorCode) {
                if(showLoading) {
                    CustomProgressDialog.dimiss();
                    ToastUtil.getInstance().makeText("ERR:" + errorCode + ";登录失败");
                }
                System.out.println("=======errorCode"+errorCode);
            }

            @Override
            public void onSuccess(LoginResponseData resp, boolean isDetectingNetworkTopology) {
                xyLoginSuc = true;
                if(showLoading){
                    CustomProgressDialog.dimiss();
                    mContext = context;
                    XyLinkUtil.this.mCallNum = callNum;
                    XyLinkUtil.this.mCallPsd = meetingPsd;
                    mHandler.sendEmptyMessage(1);
                }
                System.out.println("=======onSuccess"+resp.getCallNumber());
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {

            }
        });
    }

    public void xyLogout(){
        xyLoginSuc = false;
        NemoSDK.getInstance().logout();
    }

    public void gotoCallActivity(Context context,String callNum){

        mContext = context;
        PermissionItem[] PERMISSION_ALL = new PermissionItem[]{
                new PermissionItem(Manifest.permission.CAMERA, R.string.permission_read_phone_state, R.mipmap.ic_launcher),
                new PermissionItem(Manifest.permission.RECORD_AUDIO, R.string.permission_read_phone_state, R.mipmap.ic_launcher)};
        boolean isOpen = OKPermissionManager.applyPermissionNoDialog(context, false, PERMISSION_ALL, new OKPermissionListener() {

            @Override
            public void onOKPermission(@NonNull String[] permissions, @NonNull int[] grantResults, boolean success) {
                jointMeeting(context,callNum,null);
            }

            @Override
            public void onRefusePermission() {
            }

            @Override
            public void onAppSettingsSuccess() {
            }
        });
        if(isOpen){
            jointMeeting(context,callNum,null);
        }
    }

    private AlertDialog passwordDialog;
    private void jointMeeting(Context context, final String callNumber, String meetingPassword) {
        mContext = context;
        if(TextUtils.isEmpty(callNumber)){
            ToastUtil.getInstance().makeText("会议室号不能为空，请联系会议预定人");
            return;
        }
        this.mContext = context;
        this.mCallNum = callNumber;
        CustomProgressDialog.show(context,"正在加入会议，请稍后。",false,null);
        NemoSDK.getInstance().makeCall(callNumber, meetingPassword, new MakeCallResponse() {
            @Override
            public void onCallSuccess() {
                // 查询号码成功, 进入通话界面
//                CustomProgressDialog.dimiss();
                Intent callIntent = new Intent(context, XyCallActivity.class);
//                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                System.out.println("===========xyLinkUtilStart");
                callIntent.putExtra("number", callNumber);
                // 如果需要初始化默认这是关闭摄像头或者麦克风, 将callPresenter.start()移至XyCallActivity#onCreate()下
                callIntent.putExtra("muteVideo", false);
                callIntent.putExtra("muteAudio", false);
                context.startActivity(callIntent);
                System.out.println("================0:"+new Date().getTime());
            }

            @SuppressLint("CheckResult")
            @Override
            public void onCallFail(int error, String msg) {
                Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    if (NemoSDKErrorCode.WRONG_PASSWORD.getCode() == error) {
                        if(passwordDialog == null){
                            passwordDialog = new AlertDialog.Builder(context, R.style.AlertDialogStyle).create();
                            View view = View.inflate(context, R.layout.view_meeting_password, null);
                            view.findViewById(R.id.iv_close).setOnClickListener(v -> passwordDialog.dismiss());
                            passwordDialog.setView(view);
                            passwordDialog.setCancelable(false);
                            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            PasswordEditText meetingPassword = view.findViewById(R.id.meeting_password);
                            meetingPassword.setOnTextChangeListener((text, isComplete) -> {
                                if (isComplete) {
                                    meetingPassword.setText("");
                                    passwordDialog.dismiss();
                                    jointMeeting(mContext,mCallNum,text);
                                }
                            });
                        }
                        passwordDialog.show();
                    } else {
                        CustomProgressDialog.dimiss();
                        if(error == 12) {//登录失败
                            xyLoginSuc = false;
                            xyLogin(mContext,DataUtil.getInstance().getString("roomId",DEFAULTMEETINGID),mCallNum,meetingPassword,true);
                        }else {
                            ToastUtil.getInstance().makeText("Error Code: " + error + ", msg: " + msg);
                        }
                    }
                });
            }
        });
        // query record permission: 如果要使用录制功能 请务必调此接口
        NemoSDK.getInstance().getRecordingUri(callNumber);
    }

}
