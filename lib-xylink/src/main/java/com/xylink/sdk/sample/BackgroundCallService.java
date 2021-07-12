package com.xylink.sdk.sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.SimpleNemoSDkListener;
import com.ainemo.sdk.otf.VideoInfo;
import com.base.app.BaseApplication;
import com.base.utils.ScreenUtils;
import com.xylink.sdk.sample.utils.SmallViewUtil;
import com.xylink.sdk.sample.view.SpeakerVideoGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;

public class BackgroundCallService extends Service {
    public static final int NOTIFICATION_ONGOING_ID = 20;
    private static final String CHANNEL_ID_IN_CALL = "XYSDK_IN_CALL";
    private NotificationManager notificationManager;
    View mView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private int screenWidth;
    private int halfX;
    private int offset = 60;
    private boolean isAdd;
    private SpeakerVideoGroup mVideoGroup;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

/*
    public Notification getInCallNotification() {
        Intent intent = new Intent(BaseApplication.getInstance(), XyCallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(BaseApplication.getInstance(),
                UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext(), getInCallChannelId())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(NotificationCompat.FLAG_ONGOING_EVENT)
                .setSound(null)
                .setVibrate(new long[]{0})
                .setContentTitle(MyApplication.getContext().getString(R.string.app_name))
                .setContentText("XYSDK正在运行")
                .setContentIntent(pendingIntent);
        return builder.build();
    }*/

    public String getInCallChannelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_IN_CALL, "通话中",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("XYSDK正在通话中");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        return CHANNEL_ID_IN_CALL;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void myEvent(Object type){
        if(type instanceof Integer){
            int myType = (int) type;
            switch (myType){
                case 0:
//                    hideSmallView();
                    break;
                case 1:
//                    showSmallView();
                    break;
            }
        }
    }
}
