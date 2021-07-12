package com.base.imagehelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.base.config.ImageLoaderConfig;
import com.base.imagehelper.progress.GlideApp;
import com.base.imagehelper.progress.OnProgressListener;
import com.base.imagehelper.progress.ProgressManager;
import com.base.imagehelper.transformation.BlurTransformation;
import com.base.imagehelper.transformation.CircleTransformation;
import com.base.imagehelper.transformation.CornerTransformation;
import com.base.imagehelper.transformation.RadiusTransformation;
import com.base.imagehelper.transformation.RoundedCornersTransformation;
import com.base.imagehelper.util.GlideCacheUtil;
import com.base.imagehelper.util.GlideUtils;
import com.base.utils.PageUtil;
import com.base.utils.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.capinfo.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * Glide工具类
 * ImageViewAware:iamgeloader通过此类封装imageView并且反射获取宽和高 感觉不好
 * 建议全部手动传宽和高 虽然麻烦点
 * <p>
 * 在所有原方法上加入当前activity参数 用来Glide自动注销请求（与生命周期绑定）
 * <p>
 * 自动获取宽和高耗费性能 再多加很多测量的过程
 * RequestBuilder - SingleRequest.obtain - SingleRequest.begin()<图片大小逻辑判断>
 * 没有设置宽和高 RequestManager - ViewTarget getSize - SizeDeterminer getSize 通过LayoutParams 获取宽和高
 * 会通过ViewTreeObserver刷新布局，再回调中获取宽和高。宽和高会回调到SingleRequest中onSizeReady方法从而进行请求
 * <p>
 * todo 关于ImageUtils中setImage方法中有包装类ImageAware类 需要修改代码。
 */
public class ImageHelper {

    private volatile static ImageHelper inst;

    private RequestOptions options;
    private Context mContext;
    //选择填充模式
    private int FITCENTER = 1;
    private int CENTERCROP = 2;
    private int CIRCLECROP = 3;
    private int CENTERINSUDE = 4;
    //默认占位图
    private int defaultHolderId = R.drawable.bg_image_gray;
    //虽然设置默认 建议每次都传指定的值进来
    private int defaultWidth = 720;
    private int defaultHeight = 1280;

    private ImageHelper() {
    }

    public static ImageHelper getInstance() {
        if (inst == null) {
            synchronized (ImageHelper.class) {
                if (inst == null) {
                    inst = new ImageHelper();
                }
            }
        }
        return inst;
    }

    /**
     * 初始化静态方法
     *
     * @param imageLoaderConfig
     */
    public void init(ImageLoaderConfig imageLoaderConfig) {
        mContext = imageLoaderConfig.context;
        GlideUtils.getInstance().initGlide(imageLoaderConfig.context, imageLoaderConfig.cacheUrl, imageLoaderConfig.diskCacheSizeBytes, imageLoaderConfig.memorySize);
    }

    /**
     * list滑动的时候不加载图片，停止了加载图片
     *
     * @return
     */
    public void pauseImageRequests(Activity activity) {
        if (!activity.isFinishing()) {
            GlideApp.with(activity).pauseRequests();
        }
    }

    /**
     * list滑动停止的时候加载图片
     *
     * @return
     */
    public void resumeImageRequests(Activity activity) {
        if (!activity.isFinishing()) {
            GlideApp.with(activity).resumeRequests();
        }
    }

    /**
     * Glide显示正方形缩略图
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setThumbnailPhoto(Activity activity, String url, ImageView imageView, int holderId, int width, int height) {
        //dp转为像素
        if (!activity.isFinishing()) {
            int realWidth = ScreenUtils.dipToPx(activity, width);
            int realHeight = ScreenUtils.dipToPx(activity, height);
            options = getCommonRequestOptions(holderId, realWidth, realHeight);
            GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    /**
     * 用于相册显示大图
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setBigPhoto(Activity activity, String url, ImageView imageView, int holderId, OnProgressListener onProgressListener) {
        if (!activity.isFinishing()) {
            //dp转为像素
            int[] screenBounds = ScreenUtils.getScreenBounds(activity);
            int realWidth = screenBounds[0];
            int realHeight = screenBounds[1];
            options = getCommonRequestOptions(holderId, realWidth, realHeight);
            if (null != onProgressListener) {
                //添加监听
                ProgressManager.addListener(url, onProgressListener);
                GlideApp.with(activity).asDrawable().load(url).apply(options).into(new GlideImageViewTarget(imageView, url));
            } else {
                GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
            }
        }
    }

    /**
     * 圆形图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setCircleImage(Activity activity, String url, ImageView imageView, int holderId, int width, int height) {
        if (!activity.isFinishing()) {
            //dp转为像素
            int realWidth = ScreenUtils.dipToPx(activity, width);
            int realHeight = ScreenUtils.dipToPx(activity, height);
            options = getTransformationRequestOptions(holderId, realWidth, realHeight, new CircleTransformation());
            GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    public void setCircleImage(Activity activity, String url, ImageView imageView, int holderId) {
        if (!activity.isFinishing()) {
            //dp转为像素
            int realWidth = ScreenUtils.getScreenBounds(activity)[0];
            int realHeight = realWidth;
            options = getTransformationRequestOptions(holderId, realWidth, realHeight, new CircleTransformation());
            GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    public void setCircleImageWithCrossFade(Activity activity, String url, ImageView imageView, int holderId) {
        if (!activity.isFinishing()) {
            //dp转为像素
            int realWidth = ScreenUtils.getScreenBounds(activity)[0];
            int realHeight = realWidth;
            options = getTransformationRequestOptions(holderId, realWidth, realHeight, new CircleTransformation());
            GlideApp.with(activity).asDrawable().transition(withCrossFade(500)).load(url).apply(options).into(imageView);
        }
    }

    /**
     * 圆角图片
     * 此方法可兼容老版本 医院详情顶部+Banner 图展示的方法
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setRoundImage(Activity activity, String url, ImageView imageView, int holderId, int width, int height, int Round) {
        //dp转为像素
        int realWidth = ScreenUtils.dipToPx(activity, width);
        int realHeight = ScreenUtils.dipToPx(activity, height);
        options = getTransformationRequestOptions(holderId, realWidth, realHeight, new RadiusTransformation(mContext, Round));
        GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
    }


    /**
     * 圆角图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setRoundImage(Activity activity, String url, ImageView imageView, int holderId, int Round) {
        if (!activity.isFinishing()) {
            //dp转为像素
            options = getTransformationRequestOptions(holderId, 0, 0, new RadiusTransformation(mContext, Round));
            GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    /**
     * 圆角图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setRoundImage(String url, ImageView imageView, int holderId, int Round) {
        if (null != mContext) {
            //dp转为像素
            options = getTransformationRequestOptions(holderId, 0, 0, new RadiusTransformation(mContext, Round));
            GlideApp.with(mContext).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    /**
     * 部分圆角图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setTopRoundImage(String url, ImageView imageView, int holderId, int round) {
        if (null != mContext) {
            CornerTransformation transformation = new CornerTransformation(mContext, ScreenUtils.dipToPx(mContext, round));
            //只是绘制左上角和右上角圆角
            transformation.setNeedCorner(false, false, true, true);
            GlideApp.with(mContext).asDrawable().load(url).transform(transformation).into(imageView);
        }
    }

    /**
     * 顶部圆角图片
     *
     * @param url
     * @param imageView
     * @param holderId  缺省图
     */
    public void setTopRoundImageWithHolderId(String url, ImageView imageView, int holderId, int round) {
        if (null != mContext) {
            CornerTransformation transformation = new CornerTransformation(mContext, ScreenUtils.dipToPx(mContext, round));
            //只是绘制左上角和右上角圆角
            transformation.setNeedCorner(false, false, true, true);
            options = getTransformationRequestOptions(holderId, CENTERCROP);
            GlideApp.with(mContext).asDrawable().load(url).apply(options).transform(transformation).into(imageView);
        }
    }

    /**
     * 同步获取一个圆角图片
     *
     * @param url
     * @param holderId
     * @param Round
     * @return
     */
    public Bitmap setSyncRoundImage(String url, int holderId, int Round) {
        Bitmap bitmap = null;
        if (null != mContext) {
            //dp转为像素
            options = getTransformationRequestOptions(holderId, 0, 0, new RadiusTransformation(mContext, Round));
            try {
                bitmap = GlideApp.with(mContext).asBitmap().load(url).apply(options).submit().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 默认方法，不要圆角，scaleType默认FITCENTER
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public void setCommImage(String url, ImageView imageView, int holderId) {
        setCommImage(url, imageView, holderId, false);
    }
    /**
     * 默认方法，不要圆角，scaleType默认FITCENTER
     *
     * @param url
     * @param imageView
     */
    public void setCommImage(String url, ImageView imageView) {
        setCommImage(url, imageView, R.drawable.bg_image_gray, false);
    }

    /**
     * 默认方法，不要圆角，scaleType默认FITCENTER
     *
     * @param url
     * @param imageView
     * @param holderId
     * @param dontTransform 是否禁止任何图片变换操作，一般情况下传false
     */
    public void setCommImage(String url, ImageView imageView, int holderId, boolean dontTransform) {
        setCommImage(url, imageView, holderId, FITCENTER, 0, dontTransform);
    }

    /**
     * 正常图片
     *
     * @param url
     * @param imageView
     * @param holderId
     * @param scaleType，传0时，默认FITCENTER（ImageView默认就是此类型）
     * @param round,单位是dp，不要round传0
     * @param dontTransform                               true:禁止任何图片变换操作  false:允许图片变换操作  只有在需要设置ImageView的FitXY时，此处传true禁用掉图片变化,因为图片变化操作会导致FitXY失效。如果同时需要圆角处理直接用CircleImageView
     */
    public void setCommImage(String url, ImageView imageView, int holderId, int scaleType, int round, boolean dontTransform) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }
        Context context = imageView.getContext();
        if (PageUtil.isLiveIncludeDialog(context)) {
            RadiusTransformation radiusTransformation = null;
            if (round > 0) {
                radiusTransformation = new RadiusTransformation(context, round);
            }
            //dp转为像素
            options = getRequestOptions(holderId, 0, 0, radiusTransformation, null,
                    false, scaleType <= 0 ? FITCENTER : scaleType, null);
            if (dontTransform) {
                options = options.dontTransform();
            }
            if(url.indexOf(".gif") == -1){
                GlideApp.with(context).asBitmap().load(url).apply(options).into(imageView);
            }else{
                GlideApp.with(context).asGif().load(url).apply(options).into(imageView);
            }
        }
    }

    /**
     * 正常图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    @Deprecated
    public void setNormalImage(Activity activity, String url, ImageView imageView, int holderId) {
        if (!activity.isFinishing()) {
            //dp转为像素
            options = getTransformationRequestOptions(holderId, CENTERINSUDE);
            GlideApp.with(activity).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    /**
     * 正常图片
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    @Deprecated
    public void setNormalImage(String url, ImageView imageView, int holderId) {
        if (null != mContext) {
            //dp转为像素
            options = getTransformationRequestOptions(holderId, CENTERINSUDE);
            GlideApp.with(mContext).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    public interface OnDownloadImageListener {
        void onSuccess(Bitmap bitmap);

        void onFailed(String msg);
    }

    /**
     * 利用天眼埋点上报 Glide 加载图片异常
     */
    public interface OnGlideErrorListener {
        /**
         * 图片加载异常
         *
         * @param url          图片URl
         * @param errorMessage 异常原因
         */
        void onFailed(String url, String errorMessage);
    }

    public Bitmap loadImageSync(final String url) {
        try {
            return GlideApp.with(mContext).asBitmap().load(url).submit().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File loadImageFileSync(final String url) {
        try {
            return GlideApp.with(mContext).asFile().load(url).submit().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBlurView(Activity activity, String url, final ImageView view, int holderId, float radius) {
        if (!activity.isFinishing()) {
            RequestOptions options = getRequestOptions(holderId, 100, 100, new BlurTransformation(activity, radius), null, false, CENTERCROP, null);

            GlideApp.with(activity).asBitmap().load(url).apply(options).into(view);
        }
    }


    public void loadImageAsync(final String url, final OnDownloadImageListener listener) {
        loadImageAsync(url, null, listener, null);
    }

    public void loadImageAsync(final String url, int reqWidth, int reqHeight, final OnDownloadImageListener listener, final OnGlideErrorListener errorListener) {
        RequestOptions options = new RequestOptions();
        options.override(reqWidth, reqHeight);
        loadImageAsync(url, options, listener, errorListener);
    }


    /**
     * 下载网络图片为Bitmap
     *
     * @param url      图片链接
     * @param listener 成功失败的监听
     */
    public void loadImageAsync(final String url, final RequestOptions options, final OnDownloadImageListener listener, final OnGlideErrorListener errorListener) {
        if (null != mContext) {
            Observable.create(new ObservableOnSubscribe<Bitmap>() {
                @Override
                public void subscribe(ObservableEmitter<Bitmap> e) {
                    try {
                        Bitmap bitmap;
                        if (options == null) {
                            bitmap = GlideApp.with(mContext).asBitmap().load(url).submit().get();
                        } else {
                            bitmap = GlideApp.with(mContext).asBitmap().apply(options).load(url).submit().get();
                        }

                        e.onNext(bitmap);
                    } catch (ExecutionException ex) {
                        //下载图片失败
                        ex.printStackTrace();
                        e.onError(new RuntimeException(mContext.getString(R.string.tip_image_load_error)));
                        if (null != errorListener) {
                            errorListener.onFailed(url, ex.getMessage());
                        }
                    } catch (InterruptedException ex) {
                        //线程在等待时被中断
                        ex.printStackTrace();
                        e.onError(new RuntimeException(mContext.getString(R.string.tip_network_error)));
                        if (null != errorListener) {
                            errorListener.onFailed(url, ex.getMessage());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        e.onError(new RuntimeException(mContext.getString(R.string.tip_network_error)));
                        if (null != errorListener) {
                            errorListener.onFailed(url, ex.getMessage());
                        }
                    } finally {
                        e.onComplete();
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap value) {
                            if (listener != null)
                                listener.onSuccess(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (listener != null)
                                listener.onFailed(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @Deprecated
    public void setNormalImage(Context context, String url, ImageView imageView, int holderId) {
        //dp转为像素
        options = getTransformationRequestOptions(holderId, CENTERINSUDE);
        GlideApp.with(context).asDrawable().load(url).apply(options).into(imageView);

    }


    /**
     * 自定义DrawableImageViewTarget 接受回调
     * 显示图片加载的百分比(目前用于加载大图使用)
     */
    private class GlideImageViewTarget extends DrawableImageViewTarget {

        private String mUrl;

        GlideImageViewTarget(ImageView view, String url) {
            super(view);
            this.mUrl = url;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            OnProgressListener onProgressListener = ProgressManager.getProgressListener(mUrl);
            if (onProgressListener != null) {
                onProgressListener.onProgress(true, 100, 0, 0);
                ProgressManager.removeListener(mUrl);
            }
            super.onLoadFailed(errorDrawable);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            OnProgressListener onProgressListener = ProgressManager.getProgressListener(mUrl);
            if (onProgressListener != null) {
                onProgressListener.onProgress(true, 100, 0, 0);
                ProgressManager.removeListener(mUrl);
            }
            super.onResourceReady(resource, transition);
        }
    }

    /**
     * 兼容直接给View设置背景
     * 直接给给VIew设置背景
     */
    public void setBitmap(Activity activity, String url, final View view) {
        GlideApp.with(activity).asDrawable().load(url).into(new SimpleTarget<Drawable>() {

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackgroundDrawable(resource);
            }
        });
    }

    public void setBitmap(Activity activity, String url, final ImageView imageView, int holderId) {
        GlideApp.with(activity).asDrawable().load(url).placeholder(holderId).into(new SimpleTarget<Drawable>() {

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
            }
        });
    }

    public void setBitmap(Activity activity, String url, final ImageView imageView, int holderId, int round) {
        GlideApp.with(activity).asDrawable().load(url).transform(new RadiusTransformation(mContext, round)).placeholder(holderId).into(new SimpleTarget<Drawable>() {

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
            }
        });
    }

    public void setBitmap(Activity activity, String url, final ImageView imageView) {
        if (!activity.isFinishing()) {
            GlideApp.with(activity).asDrawable().load(url).into(new SimpleTarget<Drawable>() {

                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    imageView.setImageDrawable(resource);
                }
            });
        }
    }

    public void setBitmapResourceReady(Activity activity, String url, final ImageView imageView, final SimpleTarget simpleTarget) {
        GlideApp.with(activity).asDrawable().load(url)
                .into(new SimpleTarget<Drawable>() {

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        if (null != simpleTarget) {
                            simpleTarget.onResourceReady(resource, transition);
                        }
                    }
                });
    }

    /**
     * 通用的加载图片RequestOptions
     *
     * @param holderId
     * @param width
     * @param height
     * @return
     */
    public RequestOptions getCommonRequestOptions(int holderId, int width, int height) {
        return getRequestOptions(holderId, width, height, null, null, false, CENTERCROP, null);
    }

    /**
     * 通用的加载图片RequestOptions（带进度监听）
     *
     * @param holderId
     * @param width
     * @param height
     * @return
     */
    public RequestOptions getTransformationRequestOptions(int holderId, int width, int height, BitmapTransformation bitmapTransformation) {
        return getRequestOptions(holderId, width, height, bitmapTransformation, null, false, CENTERCROP, null);
    }

    /**
     * 通用的加载图片RequestOptions（带进度监听）
     *
     * @param holderId
     * @return
     */
    public RequestOptions getTransformationRequestOptions(int holderId, int scaleType) {
        return getRequestOptions(holderId, 0, 0, null, null, false, scaleType, null);
    }

    /**
     * 得到RequestOptions的基本方法
     */
    @SuppressLint("CheckResult")
    public RequestOptions getRequestOptions(int holderId, int width, int height, BitmapTransformation bitmapTransformation, DiskCacheStrategy diskCacheStrategy, boolean isSkipMemoryCache, int iamgeType, DecodeFormat decodeFormat) {
        RequestOptions options = new RequestOptions();
        if (holderId < 0) {
            options.placeholder(defaultHolderId)//加载成功之前占位图
                    .error(defaultHolderId);//加载错误之后的错误图
        } else {
            options.placeholder(holderId)
                    .error(holderId);
        }

        if (width < 0 || height < 0) {
            options.override(defaultWidth, defaultHeight);
        } else {
            options.override(width, height);
        }

        if (iamgeType == FITCENTER) {
            options.fitCenter();  //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
        } else if (iamgeType == CIRCLECROP) {
            options.circleCrop();//指定图片的缩放类型为centerCrop （圆形）
        } else if (iamgeType == CENTERINSUDE) {
            options.centerInside();
        } else {
            //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。
            //默认为此类型
            options.centerCrop();
        }

        //DiskCacheStrategy.ALL    //缓存所有版本的图像
        //DiskCacheStrategy.NONE   //跳过磁盘缓存
        //DiskCacheStrategy.DATA  //只缓存原来分辨率的图片
        //DiskCacheStrategy.RESOURCE;    //只缓存最终的图片
        //默认缓存所有
        if (null != diskCacheStrategy) {
            options.diskCacheStrategy(diskCacheStrategy);
        }

        if (null != bitmapTransformation) {
            options.transform(bitmapTransformation);
        }

        //跳过内存缓存
        options.skipMemoryCache(isSkipMemoryCache);

        //默认DecodeFormat.DEFAULT
        if (null != decodeFormat) {
            options.format(decodeFormat);
        }
        return options;
    }


    /**
     * 清除图片磁盘缓存，只能在子线程中执行（图片库底层已经处理好）
     * 关于图片缓存  不做统一  系统存在两套暂时
     */
    public void clearImageDiskCache(final Context context) {
        try {
            GlideCacheUtil.getInstance().clearImageDiskCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存，只能在主线程执行
     * 只能在主线程执行（图片库底层已经处理好）
     * 关于图片缓存  不做统一  系统存在两套暂时
     */
    public void clearImageMemoryCache(Context context) {
        try {
            GlideCacheUtil.getInstance().clearImageMemoryCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 清除图片所有缓存（图片库底层已经处理好）
     * 关于图片缓存  不做统一  系统存在两套暂时
     */
    public void clearImageAllCache(Context context) {
        try {
            GlideCacheUtil.getInstance().clearImageAllCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * wcd
     *
     * @param imageUrl 图片加载的url
     * @param radius   圆角
     *                 使用这个方法时候，尽量给imageView设置android:scaleType="fitXY";这样写浪费内存，但是当后台返回的图片与你的view宽高比不一致时，不会出bug
     */
    @SuppressLint("CheckResult")
    public void setRadiusDrawable(final ImageView imageView, String imageUrl, int radius) {
       setRadiusDrawable(imageView,imageUrl,R.drawable.img_holder_default,radius);
    } /**
     * wcd
     *
     * @param imageUrl 图片加载的url
     * @param holderId 占位图id
     * @param radius   圆角
     *                 使用这个方法时候，尽量给imageView设置android:scaleType="fitXY";这样写浪费内存，但是当后台返回的图片与你的view宽高比不一致时，不会出bug
     */
    @SuppressLint("CheckResult")
    public void setRadiusDrawable(final ImageView imageView, String imageUrl, @DrawableRes int holderId, int radius) {
        if (imageView == null) return;
        Context context = imageView.getContext();
        if (PageUtil.isLive(context)) {//修复看云的崩溃问题ID：1497032181
            BitmapTransformation radiusTransformation = null;
            if (radius > 0) {
                radiusTransformation = new RadiusTransformation(context, radius);
            }
            RequestOptions options = new RequestOptions();
            options.placeholder(holderId)
                    .error(holderId)
                    .centerCrop();
            if (radiusTransformation != null) {
                options.transform(radiusTransformation);
            }
            GlideApp.with(context).asDrawable().load(imageUrl).apply(options).into(imageView);
        }
    }

    @SuppressLint("CheckResult")
    public void setRadiusDrawableNoScaleType(final ImageView imageView, String imageUrl, @DrawableRes int holderId, int radius) {
        if (imageView == null) return;
        Context context = imageView.getContext();
        BitmapTransformation radiusTransformation = null;
        if (radius > 0) {
            radiusTransformation = new RadiusTransformation(context, radius);
        }
        RequestOptions options = new RequestOptions();
        options.placeholder(holderId)
                .error(holderId);
        if (radiusTransformation != null) {
            options.transform(radiusTransformation);
        }
        GlideApp.with(context).asDrawable().load(imageUrl).apply(options).into(imageView);
    }

    /**
     * @param activity
     * @param url
     * @param view
     * @param holderId -1:为没有默认效果
     * @param radius
     */
    public void setBlurViewBackGround(Activity activity, String url, boolean isLocal, final View view, int holderId, float radius) {
        if (!activity.isFinishing()) {
            options = getRequestOptions(holderId, 0, 0, new BlurTransformation(activity, radius), DiskCacheStrategy.NONE, true, CENTERCROP, null);
            String path;
            if (isLocal) {
                path = Uri.fromFile(new File(url)).toString();
            } else {
                path = url;
            }
            GlideApp.with(activity).asDrawable().load(path).apply(options).into(new SimpleTarget<Drawable>() {

                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    view.setBackgroundDrawable(resource);
                }
            });
        }
    }

    /**
     * @param activity
     * @param url
     * @param holderId -1:为没有默认效果
     * @param radius
     */
    public void setBlurImageView(Activity activity, String url, boolean isLocal, final ImageView imageView, int holderId, float radius) {
        if (!activity.isFinishing()) {
            options = getRequestOptions(holderId, 0, 0, new BlurTransformation(activity, radius), DiskCacheStrategy.NONE, true, CENTERCROP, null);
            String path;
            if (isLocal) {
                path = Uri.fromFile(new File(url)).toString();
            } else {
                path = url;
            }
            GlideApp.with(activity).asDrawable().centerCrop().load(path).apply(options).into(imageView);
        }
    }

    /**
     * 加载本地图片
     *
     * @param activity
     * @param filePath
     * @param imageView
     * @param holderId  -1:为没有默认效果
     */
    public void setLocalImage(Activity activity, String filePath, final ImageView imageView, int holderId, SimpleTarget simpleTarget, int Round) {
        if (!activity.isFinishing()) {
            RadiusTransformation radiusTransformation = null;
            if (Round > 0) {
                radiusTransformation = new RadiusTransformation(mContext, Round);
            }
            options = getRequestOptions(holderId, 0, 0, radiusTransformation, DiskCacheStrategy.NONE, true, CENTERCROP, null);
            GlideApp.with(activity).asBitmap().load(Uri.fromFile(new File(filePath))).placeholder(holderId).apply(options)
                    .into(simpleTarget == null ? new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(bitmap);
                        }
                    } : simpleTarget);
        }
    }

    @SuppressLint("CheckResult")
    public void setDrawable(final ImageView imageView, String imageUrl, @DrawableRes int holderId) {
        if (imageView == null) return;
        Context context = imageView.getContext();
        BitmapTransformation radiusTransformation = null;
        RequestOptions options = new RequestOptions();
        options.placeholder(holderId)
                .error(holderId)
                .fitCenter();
        if (radiusTransformation != null) {
            options.transform(radiusTransformation);
        }
        GlideApp.with(context).asBitmap().load(imageUrl).apply(options).into(imageView);
    }

    public void setDrawable(ImageView imageView, String imageUrl, int holderId, RequestListener<Drawable> listener) {
        if (imageView == null) return;
        Context context = imageView.getContext();
        if (holderId == -1) {
            holderId = defaultHolderId;
        }
        /**
         * 使用默认宽高、默认不转换、不仅从任何缓存、跳过缓冲、
         * */
        RequestOptions options = getRequestOptions(holderId, 0, 0, null, DiskCacheStrategy.NONE, true, CIRCLECROP, null);
        GlideApp.with(context).asDrawable().load(imageUrl).apply(options).listener(listener).into(imageView);

    }

    /**
     * 自定义四个角圆角图片
     *
     * @param url
     * @param imageView
     */
    public void setRoundImageNewOne(String url, ImageView imageView, int top_left, int top_right, int bottom_left, int bottom_right, int holderId) {
        if (null != mContext) {
            // 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
            //组合各种Transformation,
            MultiTransformation<Bitmap> mation = new MultiTransformation<>
                    //Glide设置圆角图片后设置ImageVIew的scanType="centerCrop"无效解决办法,将new CenterCrop()添加至此
                    (new CenterCrop(),
                            new RoundedCornersTransformation(top_left, 0, RoundedCornersTransformation.CornerType.TOP_LEFT),
                            new RoundedCornersTransformation(top_right, 0, RoundedCornersTransformation.CornerType.TOP_RIGHT),
                            new RoundedCornersTransformation(bottom_left, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT),
                            new RoundedCornersTransformation(bottom_right, 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT));
            Glide.with(mContext)
                    .load(url)
                    .placeholder(holderId)//设置加载中图片
                    .error(holderId)
                    //切圆形
                    .apply(RequestOptions.bitmapTransform(mation))
                    .into(imageView);
        }
    }
}
