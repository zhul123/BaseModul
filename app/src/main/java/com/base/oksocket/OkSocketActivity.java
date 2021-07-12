package com.base.oksocket;

import android.os.Bundle;
import android.widget.Toast;

import com.component.base.BaseActivity;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import androidx.annotation.Nullable;

import static android.widget.Toast.LENGTH_SHORT;

public class OkSocketActivity extends BaseActivity {
    @Override
    protected int getlayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSocket();
    }

    private void initSocket() {
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo("104.238.184.237", 8080);
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        IConnectionManager mManager = OkSocket.open(info);
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        mManager.registerReceiver(new SocketActionAdapter(){
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Toast.makeText(getBaseContext(), "连接成功", LENGTH_SHORT).show();
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
//                if(mManager != null && is心跳包)
                //喂狗操作
                mManager.getPulseManager().feed();
            }
        });

//获得当前连接通道的参配对象
        OkSocketOptions options= mManager.getOption();
//基于当前参配对象构建一个参配建造者类
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
//修改参配设置(其他参配请参阅类文档)
//        builder.setSinglePackageBytes(size);
//建造一个新的参配对象并且付给通道
        mManager.option(builder.build());
        
        //调用通道进行连接
        mManager.connect();
    }
}
