package com.blocks.pages.platform.support;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.base.utils.ARouterUtils;
import com.base.utils.ToastUtil;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.support.SimpleClickSupport;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 自定义点击事件
 *
 * @author zhulei
 * @since 2021-01-03
 */
public class PlatformClickSupport extends SimpleClickSupport {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public void addDisposable(Disposable d) {
        mCompositeDisposable.add(d);
    }
    public PlatformClickSupport() {
        setOptimizedMode(true);
    }

    @Override
    public void defaultClick(View targetView, BaseCell cell, int eventType) {
    }

    @Override
    public void onClick(View targetView, BaseCell cell, int eventType, Map<String, Object> params) {
        super.onClick(targetView, cell, eventType, params);
        ToastUtil.getInstance().makeText("点击事件"+eventType);
        String arouterUrl = getArouterUrl(cell);
        if(!TextUtils.isEmpty(arouterUrl)){
            ARouterUtils.goNext(arouterUrl);
        }
    }

    private String getArouterUrl(BaseCell baseCell){
        String arouterUrl = baseCell.optStringParam(Params.AROUTERURL);
        if(TextUtils.isEmpty(arouterUrl) && null != baseCell.optJsonObjectParam(Params.DATAS)){
            arouterUrl = baseCell.optJsonObjectParam(Params.DATAS).optString(Params.AROUTERURL);
        }
        return arouterUrl;
    }
}
