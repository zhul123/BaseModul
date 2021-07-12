package com.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.MainActivity;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {

        //启动app代码
        Intent autoStart = new Intent(context, MainActivity.class);
        autoStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(autoStart);

    }
}