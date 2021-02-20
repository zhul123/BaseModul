package com.component.providers.common;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tmall.wireless.tangram.dataparser.concrete.Card;

import java.util.List;

public interface BlocksProvider extends IProvider {
    String GROUP = "/blocks";
    //入口
    String PROVIDER_PATH = "/provider" + GROUP;
    List<Card> getParentCards();
}
