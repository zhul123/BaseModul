package com.base.utils;

import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.base.UniqueKeyTreeMap;
import com.alibaba.android.arouter.exception.HandlerException;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.facade.template.IInterceptorGroup;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.alibaba.android.arouter.facade.template.IProviderGroup;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.facade.template.IRouteRoot;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.arouter.utils.ClassUtils;
import com.alibaba.android.arouter.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.alibaba.android.arouter.launcher.ARouter.logger;
import static com.alibaba.android.arouter.utils.Consts.DOT;
import static com.alibaba.android.arouter.utils.Consts.ROUTE_ROOT_PAKCAGE;
import static com.alibaba.android.arouter.utils.Consts.SDK_NAME;
import static com.alibaba.android.arouter.utils.Consts.SEPARATOR;
import static com.alibaba.android.arouter.utils.Consts.SUFFIX_INTERCEPTORS;
import static com.alibaba.android.arouter.utils.Consts.SUFFIX_PROVIDERS;
import static com.alibaba.android.arouter.utils.Consts.SUFFIX_ROOT;

/**
 * @author :  zl
 * @desc :
 */

public class ARouterUtils {

    private static final String TAG = "ARouterUtils";
    public static final String AROUTERRULE = "capinfo://native";

    private static Context mContext;

    public static class Warehouse {
        // Cache route and metas
        static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
        static Map<String, RouteMeta> routes = new HashMap<>();

        // Cache provider
        static Map<Class, IProvider> providers = new HashMap<>();
        static Map<String, RouteMeta> providersIndex = new HashMap<>();

        // Cache interceptor
        static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
        static List<IInterceptor> interceptors = new ArrayList<>();

        static void clear() {
            routes.clear();
            groupsIndex.clear();
            providers.clear();
            providersIndex.clear();
            interceptors.clear();
            interceptorsIndex.clear();
        }
    }

    public synchronized static void init(Context context) throws HandlerException {
        mContext = context;

        try {
            Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);

            for (String className : routerMap) {
                if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                    // This one of root elements, load root.
                    ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.groupsIndex);
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS)) {
                    // Load interceptorMeta
                    ((IInterceptorGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.interceptorsIndex);
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS)) {
                    // Load providerIndex
                    ((IProviderGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.providersIndex);
                }
            }

            if (Warehouse.groupsIndex.size() == 0) {
                logger.error(TAG, "No mapping files were found, check your configuration please!");
            }

            if (ARouter.debuggable()) {
                logger.debug(TAG, String.format(Locale.getDefault(), "LogisticsCenter has already been loaded, GroupIndex[%d], InterceptorIndex[%d], ProviderIndex[%d]", Warehouse.groupsIndex.size(), Warehouse.interceptorsIndex.size(), Warehouse.providersIndex.size()));
            }
        } catch (Exception e) {
            throw new HandlerException(TAG + "ARouter init logistics center exception! [" + e.getMessage() + "]");
        }
    }

    public static String nativePathBylastPath(String lastPath) {
        if(null == Warehouse.groupsIndex){
            return "";
        }
        for (String group : Warehouse.groupsIndex.keySet()) {
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(group);  // Load route meta.
            if (null == groupMeta) {
                return "";
            } else {
                // Load route and cache it into memory, then delete from metas.
                try {
                    IRouteGroup iGroupInstance = groupMeta.getConstructor().newInstance();
                    iGroupInstance.loadInto(Warehouse.routes);
                    if(null == Warehouse.routes){
                        return "";
                    }
                    for (String key : Warehouse.routes.keySet()){
                        if(!TextUtils.isEmpty(key)){
                            if(Uri.parse(key).getLastPathSegment().equals(lastPath)) {
                                return key;
                            }
                        }
                    }
                } catch (Exception e) {
                    return "";
                }

            }
        }
        return "";
    }

}
