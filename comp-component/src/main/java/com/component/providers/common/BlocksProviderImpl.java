package com.component.providers.common;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tmall.wireless.tangram.dataparser.concrete.Card;

import java.util.List;

@Route(path = BlocksProvider.PROVIDER_PATH ,name = "测试服务")
public class BlocksProviderImpl implements BlocksProvider {
    private Context mContext;

    @Override
    public List<Card> getParentCards() {
        Toast.makeText(mContext,"provwwider",Toast.LENGTH_LONG).show();
        if (mContext != null) {
            return null;
        } else {
            return null;
        }
    }

    @Override
    public void init(Context context) {
        this.mContext = context;
    }
}
