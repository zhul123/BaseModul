package com.xylink.sdk.sample.activitys.xycall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.log.L;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.module.call.data.Enums;
import com.ainemo.module.call.data.FECCCommand;
import com.ainemo.module.call.data.NewStatisticsInfo;
import com.ainemo.module.call.data.RemoteUri;
import com.ainemo.sdk.model.AIParam;
import com.ainemo.sdk.otf.ContentType;
import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.Orientation;
import com.ainemo.sdk.otf.RecordCallback;
import com.ainemo.sdk.otf.Roster;
import com.ainemo.sdk.otf.RosterWrapper;
import com.ainemo.sdk.otf.Speaker;
import com.ainemo.sdk.otf.VideoInfo;
import com.ainemo.sdk.otf.WhiteboardChangeListener;
import com.ainemo.shared.UserActionListener;
import com.base.utils.DeviceUtil;
import com.base.utils.ScreenUtils;
import com.base.widget.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xylink.sdk.sample.BackgroundCallService;
import com.xylink.sdk.sample.BuildConfig;
import com.xylink.sdk.sample.R;
import com.xylink.sdk.sample.face.FaceView;
import com.xylink.sdk.sample.share.ShareState;
import com.xylink.sdk.sample.share.SharingValues;
import com.xylink.sdk.sample.share.picture.CirclePageIndicator;
import com.xylink.sdk.sample.share.picture.PicturePagerAdapter;
import com.xylink.sdk.sample.share.screen.ScreenPresenter;
import com.xylink.sdk.sample.utils.ActivityUtils;
import com.xylink.sdk.sample.utils.CommonTime;
import com.xylink.sdk.sample.utils.GalleryLayoutBuilder;
import com.xylink.sdk.sample.utils.LayoutMode;
import com.xylink.sdk.sample.utils.SmallViewUtil;
import com.xylink.sdk.sample.utils.SpeakerLayoutBuilder;
import com.xylink.sdk.sample.utils.TextUtils;
import com.xylink.sdk.sample.utils.VolumeManager;
import com.xylink.sdk.sample.uvc.UVCCameraPresenter;
import com.xylink.sdk.sample.view.CustomAlertDialog;
import com.xylink.sdk.sample.view.Dtmf;
import com.xylink.sdk.sample.view.FeccBar;
import com.xylink.sdk.sample.view.StatisticsRender;
import com.xylink.sdk.sample.view.VideoCell;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import vulture.module.call.nativemedia.NativeDataSourceManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 通话界面demo:
 * 目前提供了演讲模式, 画廊模式, 白板, 屏幕共享, 图片共享, 横竖屏切换等示例, 可根据自己具体业务的需求选择添加
 * <p>
 * 页面内容较多, 为方便将重心放到业务, 将CallPresenter(通话业务), ScreenPresenter(屏幕共享)等独立出来,
 * {@link XyCallPresenter#start()} 业务开始, XyCallPresenter将数据传递给{@link XyCallActivity}进行展示
 * <p>
 * Note: 共享内容: 白板, 屏幕, 图片三者同时存在时, 原则上是可以抢content, 但是从设计上来讲当自己共享其中一种的时候
 * 应将其他两个功能做成不可选状态直至主动结束. demo是在共享一方时, 将其他两个按钮置灰, 请注意设计.
 * <p>
 * 具体流程参考文档 <>http://openapi.xylink.com/android/</>
 */
public class XyCallActivity extends AppCompatActivity implements View.OnClickListener,
        XyCallContract.View, VideoFragment.VideoCallback {
    private static final String TAG = "XyCallActivity";
    private final static int REFRESH_STATISTICS_INFO_DELAYED = 2000;
    private XyCallContract.Presenter callPresenter;
    private View viewToolbar;
    private ImageView ivNetworkState; // 信号
    private TextView tvCallDuration; // 通话时长
    private TextView toolbarCallNumber; // 号码
    private ImageButton ibDropCall; // 挂断
    private ImageButton btMore; // 更多
    private ImageButton btnMoreShare; // 共享
    private ImageButton btnMoreHostMeeting; // 主持会议
    private ImageButton btMuteMic; // 静音
    private TextView tvMuteMic; // 静音
    private ImageButton btCloseVideo; // 关闭视频
    private TextView tvCloseVideo; // 关闭视频
    private LinearLayoutCompat llMoreDialog; // 更多dialog
    private TextView tvMoreRecord;
    private TextView tvMoreCallMode;
    private TextView tvKeyboared; // 键盘
    private TextView tvClosePip; // 关闭画中画
    private TextView tvWhiteboard; // 白板
    private TextView tvShareScreen; // 屏幕共享
    private TextView tvSharePhoto; // 图片共享
    private LinearLayout llSwitchCamera; // 切换摄像头
    private LinearLayout llRecording;
    private TextView tvRecordingDuration; // 录制时长
    private LinearLayout llLockPeople; // 锁定至屏幕
    private ViewStub shareScreenViewStub;
    private View shareScreenView;
    private View volumeView; // 扬声器声音
    private View viewInvite; // 通话中邀请
    private TextView tvInviteNumber; // 邀请人号码
    private View viewCallDetail; // 去电/来电详情UI
    private TextView tvCallNumber; // number
    private TextView tvCallTips;
    private ImageButton btCallAccept; // 接听按钮
//    private ViewPager pagerPicture; // 图片共享
    private CirclePageIndicator pageIndicator;
    private ImageView ivRecordStatus;
    private View mSmallBtn;//最小化
    private ViewStub dtmfLayoutStub;
    private View dtmfLayout;
    private Dtmf dtmf;
    private ConstraintLayout root;
    private ViewStub mHostMeetingStub;
    private boolean defaultCameraFront = false; // 默认摄像头位置

    private boolean isToolbarShowing = false; // toolbar隐藏标记
    private boolean audioMode = false;
    private boolean isMuteBtnEnable = true;
    private String muteStatus = null;
    private boolean isVideoMute = false;
    private boolean isStartRecording = true;
    private boolean isShowingPip = true;
    private boolean isSharePicture = false;
    private int inviteCallIndex = -1;
    private LayoutMode layoutMode = LayoutMode.MODE_SPEAKER;
    private VideoInfo fullVideoInfo;
    private boolean isCallStart;
    private List<VideoInfo> mRemoteVideoInfos;
    // 存储第一页数据, 一二页复用
    private List<VideoInfo> firstPagerVideoInfo;

    private static final int sDefaultTimeout = 5000;
    private Handler handler = new Handler();

    private CompositeDisposable compositeDisposable;
    private VolumeManager mVolumeManager;

    // share screen
    private ScreenPresenter screenPresenter;
    private static final int REQUEST_CODE_CHOOSE = 23;

    // 共享图片
//    private PicturePagerAdapter picturePagerAdapter;
//    private List<String> picturePaths;
    private String outgoingNumber;
    private ShareState shareState = ShareState.NONE;

    // uvc
    private boolean isNeedUVC = false; //需要uvc的时候可以打开
    private UVCCameraPresenter uvcCameraPresenter;
    private LinearLayoutCompat llShareMore;

    private StatisticsRender mStatisticsRender;
    private ViewPagerNoSlide videoPager;
    private VideoPagerAdapter videoPagerAdapter;
    private int currentPagerIndex = 0;
    private CirclePageIndicator videoPagerIndicator;
    private MyVideoPagerListener myVideoPagerListener;
    private Observable<Boolean> confMgmtStateObserver;


    private RosterWrapper rosterWrapper;

    private TextView tvSpeakers;

    private VideoInfo localVideoInfo;
    private Intent backgroundCallService;

    @StringDef({
            MuteStatus.HAND_UP, MuteStatus.HAND_DOWN, MuteStatus.END_SPEACH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MuteStatus {
        String HAND_UP = "HAND_UP";
        String HAND_DOWN = "HAND_DOWN";
        String END_SPEACH = "END_SPEACH";
    }

    @IntDef({
            VideoStatus.VIDEO_STATUS_NORMAL, VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_BW,
            VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE, VideoStatus.VIDEO_STATUS_LOW_AS_REMOTE,
            VideoStatus.VIDEO_STATUS_NETWORK_ERROR, VideoStatus.VIDEO_STATUS_LOCAL_WIFI_ISSUE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStatus {
        int VIDEO_STATUS_NORMAL = 0;
        int VIDEO_STATUS_LOW_AS_LOCAL_BW = 1;
        int VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE = 2;
        int VIDEO_STATUS_LOW_AS_REMOTE = 3;
        int VIDEO_STATUS_NETWORK_ERROR = 4;
        int VIDEO_STATUS_LOCAL_WIFI_ISSUE = 5;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("================1:"+new Date().getTime());
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        System.out.println("================10:"+new Date().getTime());
        setContentView(R.layout.activity_call);
        System.out.println("================100:"+new Date().getTime());
        new XyCallPresenter(this); // init presenter
        System.out.println("================1000:"+new Date().getTime());
        compositeDisposable = new CompositeDisposable();
        initView();
        initListener();
        initData();

        SmallViewUtil.getInstance().init(this);
        callPresenter.start(); // Note: business start here,业务逻辑开始

//        root.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                defaultCameraFront = NemoSDK.defaultCameraId() == 1;
                NemoSDK.getInstance().releaseCamera();
                NemoSDK.getInstance().requestCamera();
                //解决返回时视频方向错乱问题
                if (!DeviceUtil.getDeviceIsPhone()) {
                    NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
                }
//            }
//        },500);

        System.out.println("================3:"+new Date().getTime());
    }

    @Override
    public void setPresenter(XyCallContract.Presenter presenter) {
        callPresenter = presenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("================onStart:"+new Date().getTime());
        if (uvcCameraPresenter != null) {
            uvcCameraPresenter.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (screenPresenter != null) {
            screenPresenter.hideFloatView();
        }

        System.out.println("================onResume1:"+new Date().getTime());
        showToolbar(sDefaultTimeout);
//        EventBus.getDefault().post(0);
        CustomProgressDialog.dimiss();
        hideSmallView();
        System.out.println("================onResume2:"+new Date().getTime());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isDrop) {
            showSmallView();
        }
        if(screenPresenter != null && screenPresenter.isSharingScreen()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                screenPresenter.showFloatView();
            }
        }
//        EventBus.getDefault().post(1);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && screenPresenter != null && screenPresenter.isSharingScreen()) {
            screenPresenter.onStop();
        }
        if (uvcCameraPresenter != null) {
            uvcCameraPresenter.onStop();
        }
        // 应用退到后台
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ActivityUtils.isAppForeground(this)) {
//                Toast.makeText(XyCallActivity.this, "视频通话退到后台，请从通知栏查看通话", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        System.out.println("================onAttachedToWindow:"+new Date().getTime());

    }

    @Override
    public void onBackPressed() {
        // Intercept back event
        hideHostMeeting();
    }

    // remember to release resource when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        //只有点击挂断才销毁
        NemoSDK.getInstance().hangup();
        NemoSDK.getInstance().releaseLayout();
        NemoSDK.getInstance().releaseCamera();
        SmallViewUtil.getInstance().removeFloatView();
        SmallViewUtil.getInstance().destory();
        L.i(TAG, "wang on destroy");
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (screenPresenter != null) {
            screenPresenter.onDestroy();
        }
        pictureData = null;
        mVolumeManager.onDestory();
        List<VideoFragment> fragments = videoPagerAdapter.getFragments();
        for (VideoFragment videoFragment : fragments) {
            videoFragment.onDestroy();
        }
        if(mWebView != null){
            mWebView.clearHistory();
            mWebView.destroy();
        }
    }

    private void initView() {
        System.out.println("================11:"+new Date().getTime());
        root = findViewById(R.id.root);
        tvSpeakers = findViewById(R.id.tv_speakers);
        viewToolbar = findViewById(R.id.group_visibility);
        ivNetworkState = findViewById(R.id.network_state);
        tvCallDuration = findViewById(R.id.network_state_timer);
        toolbarCallNumber = findViewById(R.id.tv_call_number);
        ibDropCall = findViewById(R.id.drop_call);
        btMore = findViewById(R.id.hold_meeting_more);
        btnMoreShare = findViewById(R.id.btn_more_share);
        btnMoreHostMeeting = findViewById(R.id.btn_more_host_meeting);
        btMuteMic = findViewById(R.id.mute_mic_btn);
        tvMuteMic = findViewById(R.id.mute_mic_btn_label);
        btCloseVideo = findViewById(R.id.close_video);
        tvCloseVideo = findViewById(R.id.video_mute_text);
        llMoreDialog = findViewById(R.id.more_layout_dialog);
        tvKeyboared = findViewById(R.id.keyboard);
        tvMoreRecord = findViewById(R.id.tv_more_record);
        tvMoreCallMode = findViewById(R.id.tv_nore_call_mode);
        tvClosePip = findViewById(R.id.textView2);
        tvWhiteboard = findViewById(R.id.tv_whiteboard);
        tvShareScreen = findViewById(R.id.tv_share_screen);
        tvSharePhoto = findViewById(R.id.tv_share_photo);
        ivRecordStatus = findViewById(R.id.video_recording_icon);
        llRecording = findViewById(R.id.conversation_recording_layout);
        tvRecordingDuration = findViewById(R.id.video_recording_timer);
        llLockPeople = findViewById(R.id.layout_lock_people);
        llSwitchCamera = findViewById(R.id.switch_camera_layout);
//        whiteboardLaodingView = findViewById(R.id.view_whiteboard_loading);
        shareScreenViewStub = findViewById(R.id.vs_share_screen);
        volumeView = findViewById(R.id.operation_volume_brightness);
        llShareMore = findViewById(R.id.ll_share_more);

        mSmallBtn = findViewById(R.id.ll_small_view);
        // 通话中邀请
        viewInvite = findViewById(R.id.view_call_invite);
        viewInvite.findViewById(R.id.bt_invite_accept).setOnClickListener(this);
        viewInvite.findViewById(R.id.bt_invite_drop).setOnClickListener(this);
        tvInviteNumber = viewInvite.findViewById(R.id.tv_invite_number);
        // 去电/来电UI
        viewCallDetail = findViewById(R.id.view_call_detail);
        viewCallDetail.findViewById(R.id.bt_call_drop).setOnClickListener(this);
        btCallAccept = viewCallDetail.findViewById(R.id.bt_call_accept);
        tvCallNumber = viewCallDetail.findViewById(R.id.tv_call_name);
        tvCallTips = viewCallDetail.findViewById(R.id.tv_call_tips);
        // 共享图片
//        pagerPicture = findViewById(R.id.pager_picture);
        pageIndicator = findViewById(R.id.pager_indicator);
        //FECC
        // 键盘
        dtmfLayoutStub = findViewById(R.id.vs_dtmf);
        System.out.println("================12:"+new Date().getTime());

        // 统计信息
        ViewStub stub = (ViewStub) findViewById(R.id.view_statistics_info);
        mStatisticsRender = new StatisticsRender(stub, this::stopRefreshStatisticsInfo);

        System.out.println("================13:"+new Date().getTime());
        videoPagerIndicator = findViewById(R.id.pager_indicator_video);
        videoPager = findViewById(R.id.video_pager);
        videoPagerAdapter = new VideoPagerAdapter(getSupportFragmentManager());
        videoPagerAdapter.setVideoCallback(this);
        videoPager.setAdapter(videoPagerAdapter);
        System.out.println("================14:"+new Date().getTime());

        videoPagerIndicator.setViewPager(videoPager);
        myVideoPagerListener = new MyVideoPagerListener();
        videoPagerIndicator.setOnPageChangeListener(myVideoPagerListener);

        System.out.println("================15:"+new Date().getTime());
        checkPermission();
    }

    private void initListener() {
        ibDropCall.setOnClickListener(this);
        btCallAccept.setOnClickListener(this);
        btnMoreShare.setOnClickListener(this);
        btnMoreHostMeeting.setOnClickListener(this);
        btMuteMic.setOnClickListener(this);
        btCloseVideo.setOnClickListener(this);
        btMore.setOnClickListener(this);
        tvKeyboared.setOnClickListener(this);
        tvMoreRecord.setOnClickListener(this);
        tvMoreCallMode.setOnClickListener(this);
        tvClosePip.setOnClickListener(this);
        tvWhiteboard.setOnClickListener(this);
        tvShareScreen.setOnClickListener(this);
        tvSharePhoto.setOnClickListener(this);
        llLockPeople.setOnClickListener(this);
        mSmallBtn.setOnClickListener(this);
        llSwitchCamera.setOnClickListener(this);

        ivNetworkState.setOnClickListener(v -> startRefreshStatisticsInfo());
    }

    private void initData() {
        // 来电 & 去电
        Intent intent = getIntent();
        boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
        if (isIncomingCall) {
            final int callIndex = intent.getIntExtra("callIndex", -1);
            inviteCallIndex = callIndex;
            String callerName = intent.getStringExtra("callerName");
            String callNumber = intent.getStringExtra("callerNumber");
            toolbarCallNumber.setText(callNumber);
            Log.i(TAG, "showIncomingCallDialog=" + callIndex);
            showCallIncoming(callIndex, callNumber, callerName);
        } else {
            outgoingNumber = intent.getStringExtra("number");
            showCallOutGoing(outgoingNumber);
            L.i(TAG, "outgoing number: " + outgoingNumber);
        }

        mVolumeManager = new VolumeManager(this, volumeView, AudioManager.STREAM_VOICE_CALL);
        mVolumeManager.setMuteCallback(mute -> NemoSDK.getInstance().setSpeakerMute(mute));

        // 注册白板监听(接收远端白板, 本地打开白板结果在此处回调)
        NemoSDK.getInstance().registerWhiteboardChangeListener(whiteboardChangeListener);

        // add for: uvc, 不需要的可直接删除
        if (isNeedUVC) {
            uvcCameraPresenter = new UVCCameraPresenter(this);
        }
        if(!DeviceUtil.getDeviceIsPhone()){
            llSwitchCamera.setVisibility(VISIBLE);
        }
    }

    private void hideOrShowToolbar(boolean show) {
        if (show) {
            hideToolbar();
        } else {
            showToolbar(sDefaultTimeout);
        }
    }

    private final Runnable mFadeOut = this::hideToolbar;

    private void hideToolbar() {
        viewToolbar.setVisibility(GONE);
        isToolbarShowing = false;
        llMoreDialog.setVisibility(GONE);
        llShareMore.setVisibility(GONE);
    }

    private void showToolbar(int timeout) {
        if (!isToolbarShowing) { // show toolbar
            viewToolbar.setVisibility(View.VISIBLE);
            isToolbarShowing = true;
            // fecc
            refreshAutoHideToolBar(timeout);
        }
    }

    private void refreshAutoHideToolBar(int timeout){
        if (timeout != 0) {
            handler.removeCallbacks(mFadeOut);
            handler.postDelayed(mFadeOut, timeout);
        }
    }

    // 通话时长
    private void initCallDuration() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
        compositeDisposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> tvCallDuration.setText(CommonTime.formatTime(aLong))));
    }

    private void checkPip() {
        SpeakerVideoFragment videoFragment = (SpeakerVideoFragment) videoPagerAdapter.getItem(0);
        if (videoFragment.isShowingPip()) {
            videoFragment.setShowingPip(false);
            tvClosePip.setText("打开小窗");
        } else {
            videoFragment.setShowingPip(true);
            tvClosePip.setText("关闭小窗");
        }
    }
    private boolean isDrop = false;
    @Override
    public void onClick(View v) {
        refreshAutoHideToolBar(sDefaultTimeout);
        int id = v.getId();
        if (id == R.id.drop_call || id == R.id.bt_call_drop) {
//            NemoSDK.getInstance().hangup();
//            NemoSDK.getInstance().releaseLayout();
//            NemoSDK.getInstance().releaseCamera();
            isDrop = true;

            SmallViewUtil.getInstance().removeFloatView();
            SmallViewUtil.getInstance().destory();
            finish();
            ActivityUtils.moveTaskToFront(this);
        } else if (id == R.id.bt_call_accept) {
            L.i(TAG, "inviteCallIndex::: " + inviteCallIndex);
            NemoSDK.getInstance().answerCall(inviteCallIndex, true);
        } else if (id == R.id.hold_meeting_more) {
            if (layoutMode == LayoutMode.MODE_GALLERY) {
                tvKeyboared.setVisibility(GONE);
                tvClosePip.setVisibility(GONE);
            } else {
                tvKeyboared.setVisibility(VISIBLE);
                tvClosePip.setVisibility(VISIBLE);
            }
            SpeakerVideoFragment videoFragment = (SpeakerVideoFragment) videoPagerAdapter.getItem(0);
            // only LANDSCAPE & meeting member > 0 & speaker mode -> closePip enable
            boolean isClosePipEnable = videoFragment.isLandscape() && mRemoteVideoInfos != null
                    && mRemoteVideoInfos.size() > 0 && currentPagerIndex == 0;
            tvClosePip.setEnabled(isClosePipEnable);
            tvClosePip.setTextColor(isClosePipEnable ? Color.WHITE : Color.GRAY);
            llMoreDialog.setVisibility(llMoreDialog.getVisibility() == VISIBLE ? GONE : VISIBLE);
        } else if (id == R.id.mute_mic_btn) {
            checkPermission();
            if (isMuteBtnEnable) {
                updateMuteStatus(!NemoSDK.getInstance().isMicMuted());
            } else {
                // 举手/取消举手/结束发言
                switch (muteStatus) {
                    case MuteStatus.HAND_UP:
                        NemoSDK.getInstance().handUp();
                        muteStatus = MuteStatus.HAND_DOWN;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_handdown);
                        tvMuteMic.setText("取消举手");
                        break;
                    case MuteStatus.HAND_DOWN:
                        NemoSDK.getInstance().handDown();
                        muteStatus = MuteStatus.HAND_UP;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                        tvMuteMic.setText("举手发言");
                        break;
                    case MuteStatus.END_SPEACH:
                        NemoSDK.getInstance().endSpeech();
                        muteStatus = MuteStatus.HAND_UP;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                        tvMuteMic.setText("举手发言");
                        break;
                }
            }
        } else if (id == R.id.close_video) {
            isVideoMute = !isVideoMute;
            NemoSDK.getInstance().setVideoMute(isVideoMute);
            setVideoState(isVideoMute);
        } else if (id == R.id.tv_more_record) {
            L.i(TAG, "is recording: " + isStartRecording);
            if (NemoSDK.getInstance().isAuthorize()) {
                setRecordVideo(isStartRecording);
                isStartRecording = !isStartRecording;
            } else {
                Toast.makeText(XyCallActivity.this, "端终号不可录制", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tv_nore_call_mode) {
            audioMode = !audioMode;
            setSwitchCallState(audioMode);
            NemoSDK.getInstance().switchCallMode(audioMode);
            //sync mic status
            NemoSDK.getInstance().enableMic(NemoSDK.getInstance().isMicMuted(), true);
            NemoSDK.getInstance().setVideoMute(isVideoMute);
        } else if (id == R.id.btn_more_host_meeting) {
            llMoreDialog.setVisibility(GONE);
            hideToolbar();
            if (llHostMeeting != null && llHostMeeting.getVisibility() == VISIBLE) {
                hideHostMeeting();
            } else {
                showHostMeeting();
            }
        } else if (id == R.id.btn_more_share) {
            handleShareEvent(shareState);
        } else if (id == R.id.keyboard) {
            llMoreDialog.setVisibility(GONE);
            if(dtmfLayout == null){
                // 键盘
                dtmfLayout = dtmfLayoutStub.inflate().findViewById(R.id.dtmf);
                // 键盘
                dtmf = new Dtmf(dtmfLayout, key -> {
                    if (buildLocalLayoutInfo() != null) {
                        if (mRemoteVideoInfos != null && mRemoteVideoInfos.size() > 0) {
                            NemoSDK.getInstance().sendDtmf(mRemoteVideoInfos.get(0).getRemoteID(), key);
                        }
                    }
                });
            }
            dtmfLayout.setVisibility(VISIBLE);
        } else if (id == R.id.textView2) {
            llMoreDialog.setVisibility(GONE);
            hideToolbar();
            checkPip();
        } else if (id == R.id.tv_whiteboard) {
            llShareMore.setVisibility(GONE);
            NemoSDK.getInstance().startWhiteboard();
            L.i("wang 打开白板");
        } else if (id == R.id.tv_share_screen) {
            llShareMore.setVisibility(GONE);
            if (screenPresenter == null) {
                screenPresenter = new ScreenPresenter(XyCallActivity.this);
            }
            screenPresenter.startShare();
        } else if (id == R.id.tv_share_photo) {
            llShareMore.setVisibility(GONE);
            sharePhoto();
        } else if (id == R.id.layout_lock_people) {
            llMoreDialog.setVisibility(GONE);
            ((SpeakerVideoFragment) videoPagerAdapter.getItem(0)).unlockLayout();
            llLockPeople.setVisibility(GONE);
        } else if (id == R.id.bt_invite_accept) { // 通话中邀请接听
            L.i(TAG, "wang invite accept");
            NemoSDK.getInstance().answerCall(inviteCallIndex, true);
            viewInvite.setVisibility(GONE);
        } else if (id == R.id.bt_invite_drop) { // 通话中邀请挂断
            L.i(TAG, "wang invite drop");
            NemoSDK.getInstance().answerCall(inviteCallIndex, false);
            viewInvite.setVisibility(GONE);
        } else if(id == R.id.switch_camera_layout){
            if (uvcCameraPresenter != null && uvcCameraPresenter.hasUvcCamera()) {
                uvcCameraPresenter.switchCamera();
            } else {
                NemoSDK.getInstance().switchCamera(defaultCameraFront ? 0 : 1);  // 0：后置 1：前置
                defaultCameraFront = !defaultCameraFront;
            }
        } else if(id == R.id.ll_small_view){
            L.i(TAG,"small view");
            moveTaskToBack(true);
            ActivityUtils.moveTaskToFront(this);
        }/*else if (id == R.id.pager_picture) {
            L.i(TAG, "wang pager clicked");
            hideOrShowToolbar(isToolbarShowing);

        }*/
    }

    private void sharePhoto() {
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(enable -> {
                    if (enable) {
                        Matisse.from(XyCallActivity.this)
                                .choose(MimeType.of(MimeType.PNG, MimeType.GIF, MimeType.JPEG), false)
                                .countable(true)
                                .maxSelectable(9)
                                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(new GlideEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    }
                });
    }

    private void handleShareEvent(ShareState shareState) {
        switch (shareState) {
            case NONE:
                llShareMore.setVisibility(llShareMore.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
                break;
            case IMAGE:
                // 结束图片分享, Note: remove pictureHandler
                NemoSDK.getInstance().dualStreamStop(ContentType.CONTENT_TYPE_PICTURE);
                break;
            case SCREEN:
                if (screenPresenter != null && screenPresenter.isSharingScreen()) {
                    NemoSDK.getInstance().dualStreamStop(ContentType.CONTENT_TYPE_SCREEN_WITH_AUDIO);
                }
                break;
            case WHITEBOARD:
                new CustomAlertDialog(XyCallActivity.this).builder()
                        .setTitle(getString(R.string.exit_white_board_title))
                        .setMsg(getString(R.string.exit_white_board_content))
                        .setPositiveButton(getString(R.string.sure), v1 -> {
                            NemoSDK.getInstance().stopWhiteboard();
                        })
                        .setNegativeButton(getString(R.string.cancel), v12 -> {
                        }).setCancelable(false).show();
                break;
            default:
        }
    }

    //视频关闭或者开启
    private void setVideoState(boolean videoMute) {
        videoPagerAdapter.setLocalVideoMute(videoMute);
        if (videoMute) {
            btCloseVideo.setImageResource(R.drawable.close_video);
            tvCloseVideo.setText(getResources().getString(R.string.open_video));
        } else {
            btCloseVideo.setImageResource(R.drawable.video);
            tvCloseVideo.setText(getResources().getString(R.string.close_video));
        }
    }

    public void setRecordVideo(boolean isStartRecording) {
        if (isStartRecording) {
            NemoSDK.getInstance().startRecord(outgoingNumber, new RecordCallback() {
                @Override
                public void onFailed(final int errorCode) {
                    Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                        Toast.makeText(XyCallActivity.this, "Record fail: " + errorCode, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onSuccess() {
                    Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(
                            integer -> showRecordStatusNotification(true, NemoSDK.getInstance().getUserName(), true)
                    );
                }
            });
        } else {
            NemoSDK.getInstance().stopRecord();
            showRecordStatusNotification(false, NemoSDK.getInstance().getUserName(), true);
            Toast.makeText(XyCallActivity.this, getString(R.string.third_conf_record_notice), Toast.LENGTH_LONG).show();
        }
    }

    // 去电
    @Override
    public void showCallOutGoing(String outgoingNumber) {
        viewCallDetail.setVisibility(VISIBLE);
        if (getIntent().getBooleanExtra("muteVideo", false)) {
            viewCallDetail.setBackgroundResource(R.drawable.cell_bg_default);
        } else {
            viewCallDetail.setBackgroundResource(R.drawable.bg_outgoing_shadow);
        }
        tvCallTips.setText("视频通话邀请中...");
        btCallAccept.setVisibility(GONE);
        L.i(TAG, "showCallOutGoing callNumber: " + outgoingNumber);
        tvCallNumber.setText(outgoingNumber);
        toolbarCallNumber.setText(outgoingNumber);
    }

    // 来电
    @Override
    public void showCallIncoming(int callIndex, String callNumber, String callName) {
        viewCallDetail.setVisibility(VISIBLE);
        viewCallDetail.setBackgroundColor(Color.BLACK);
        tvCallTips.setText("邀请您视频通话...");
        tvCallNumber.setText(!TextUtils.isEmpty(callName) ? callName : callNumber);
        btCallAccept.setVisibility(VISIBLE);
    }

    @Override
    public void showCallDisconnected(String reason) {
        if(!XyCallActivity.this.isFinishing()) {
            Toast.makeText(this, "Call disconnected reason: " + reason, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 通话接听, 计时 显示toolbar等
     */
    @Override
    public void showCallConnected() {
        isCallStart = true;
        viewCallDetail.setVisibility(GONE);
        initCallDuration();
        showToolbar(sDefaultTimeout);
        VideoInfo videoInfo = buildLocalLayoutInfo();
        videoPagerAdapter.setLocalVideoInfo(videoInfo);
        SmallViewUtil.getInstance().setLocalVideoInfo(videoInfo);
        if (getIntent().getBooleanExtra("muteVideo", false)) {
            isVideoMute = true;
            NemoSDK.getInstance().setVideoMute(isVideoMute);
            setVideoState(isVideoMute);
        }
        if (getIntent().getBooleanExtra("muteAudio", false) && isMuteBtnEnable) {
            updateMuteStatus(true);
        }
        // 会控消息
        if (confMgmtStateObserver != null) {
            confMgmtStateObserver.subscribe(
                    aBoolean -> videoPagerAdapter.setLocalMicMute(aBoolean),
                    throwable -> {
                    });
        }
        NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE && isTabletDevice(this)) {
//            NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mVolumeManager.onVolumeDown();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mVolumeManager.onVolumeUp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理会控消息
     * 控制操作：静音、非静音
     * 控制状态：举手发言、取消举手、结束发言
     *
     * @param operation        操作：mute/unmute
     * @param isMuteIsDisabled 是否为强制静音 true强制静音
     */
    @Override
    public void showConfMgmtStateChanged(String operation, boolean isMuteIsDisabled, String chairmanUri) {
        isMuteBtnEnable = !isMuteIsDisabled;
        findViewById(R.id.ll_chairman_mode).setVisibility(TextUtils.isEmpty(chairmanUri) ? GONE : VISIBLE);
        if ("mute".equalsIgnoreCase(operation)) {
            NemoSDK.getInstance().enableMic(true, isMuteIsDisabled);
            if (isMuteIsDisabled) {
                // 强制静音
                Toast.makeText(XyCallActivity.this, "主持人强制静音, 请举手发言", Toast.LENGTH_LONG).show();
                muteStatus = MuteStatus.HAND_UP;
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                tvMuteMic.setText("举手发言");
            } else {
                Toast.makeText(XyCallActivity.this, "您已被静音", Toast.LENGTH_LONG).show();
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic_muted);
                tvMuteMic.setText("取消静音");
            }
            if (isCallStart) {
                videoPagerAdapter.setLocalMicMute(true);
            }
            confMgmtStateObserver = Observable.just(true);
        } else if ("unmute".equalsIgnoreCase(operation)) {
            NemoSDK.getInstance().enableMic(false, false);
            if (isMuteIsDisabled) {
                muteStatus = MuteStatus.END_SPEACH;
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_end_speech);
                tvMuteMic.setText("结束发言");
            } else {
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic);
                tvMuteMic.setText("静音");
            }
            if (isCallStart) {
                videoPagerAdapter.setLocalMicMute(false);
            }
            confMgmtStateObserver = Observable.just(false);
        }
    }

    @Override
    public void showKickout(int code, String reason) {
        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    Toast.makeText(this, "kick out reason: " + reason, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(XyCallActivity.this, JoinMeetingActivity.class));
                    finish();
                }
        );
    }

    private void updateMuteStatus(boolean isMute) {
        NemoSDK.getInstance().enableMic(isMute, true);
        videoPagerAdapter.setLocalMicMute(isMute);
        if (isMute) {
            btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic_muted);
            tvMuteMic.setText("取消静音");
        } else {
            btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic);
            tvMuteMic.setText("静音");
        }
    }

    /**
     * 本地网络质量提示
     *
     * @param level 1、2、3、4个等级,差-中-良-优
     */
    @Override
    public void showNetLevel(int level) {
        if (ivNetworkState == null) {
            return;
        }
        switch (level) {
            case 4:
                ivNetworkState.setImageResource(R.drawable.network_state_four);
                break;
            case 3:
                ivNetworkState.setImageResource(R.drawable.network_state_three);
                break;
            case 2:
                ivNetworkState.setImageResource(R.drawable.network_state_two);
                break;
            case 1:
                ivNetworkState.setImageResource(R.drawable.network_state_one);
                break;
        }
    }

    @Override
    public void showVideoStatusChange(int videoStatus) {
        if (videoStatus == VideoStatus.VIDEO_STATUS_NORMAL) {
            Toast.makeText(XyCallActivity.this, "网络正常", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_BW) {
            Toast.makeText(XyCallActivity.this, "本地网络不稳定", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE) {
            Toast.makeText(XyCallActivity.this, "系统忙，视频质量降低", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_REMOTE) {
            Toast.makeText(XyCallActivity.this, "对方网络不稳定", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_NETWORK_ERROR) {
            Toast.makeText(XyCallActivity.this, "网络不稳定，请稍候", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOCAL_WIFI_ISSUE) {
            Toast.makeText(XyCallActivity.this, "WiFi信号不稳定", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showIMNotification(String values) {
        if ("[]".equals(values)) {
            Toast.makeText(XyCallActivity.this, R.string.im_notification_ccs_transfer, Toast.LENGTH_SHORT).show();
        } else {
            String val = values.replace("[", "");
            val = val.replace("]", "");
            val = val.replace('"', ' ');
            val = val.replace('"', ' ');
            String str = String.format("%s%s%s", getResources().getString(R.string.queen_top_part), val, getResources().getString(R.string.queen_bottom_part));
            Toast.makeText(XyCallActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void showAiFace(AIParam aiParam, boolean isLocalFace) {
//        L.i(TAG, "aiParam:" + aiParam);
//        if (aiParam == null || aiParam.getParticipantId() < 0) {
//            return;
//        }
//        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                L.i(TAG, "fullVideoInfo:: " + fullVideoInfo.toString());
//                L.i(TAG, "fullVideoInfo is Local:: " + isLocalFace);
//                if (isLocalFace) {
//                    callPresenter.dealLocalAiParam(aiParam, fullVideoInfo != null
//                            && fullVideoInfo.getParticipantId() == NemoSDK.getInstance().getUserId());
//                } else {
//                    callPresenter.dealAiParam(aiParam, fullVideoInfo != null
//                            && fullVideoInfo.getParticipantId() == aiParam.getParticipantId());
//                }
//            }
//        });
    }

    /**
     * 通话中收到laid
     *
     * @param callNumber
     * @param callName
     */
    @Override
    public void showInviteCall(int callIndex, String callNumber, String callName) {
        inviteCallIndex = callIndex;
        viewInvite.setVisibility(VISIBLE);
        toolbarCallNumber.setText(callNumber);
        tvInviteNumber.setText(TextUtils.isEmpty(callName) ? callNumber : callName);
    }

    @Override
    public void showCaptionNotification(String content, String action) {
        TextView tvCaptionNotification = findViewById(R.id.tv_caption_notification);
        if ("push".equals(action)) {
            tvCaptionNotification.setVisibility(View.VISIBLE);
            tvCaptionNotification.setText(content);
        } else if ("cancel".equals(action)) {
            tvCaptionNotification.setVisibility(GONE);
        }
    }

    @Override
    public void onSpeakerChanged(List<Speaker> speakers) {
        if (speakers == null || speakers.isEmpty() || rosterWrapper == null) {
            tvSpeakers.setText(getString(R.string.str_speakers, ""));
            return;
        }
        ArrayList<Roster> rosters = rosterWrapper.getRosters();
        StringBuilder stringBuilder = new StringBuilder();;
        for (Speaker speaker : speakers) {
            for (Roster roster : rosters) {
                if (speaker.getCallUri().equals(roster.getDeviceId())) {
                    stringBuilder.append(roster.getDeviceName()).append("；");
                }
            }
            if (localVideoInfo != null && speaker.getCallUri().equals(localVideoInfo.getRemoteID())) {
                stringBuilder.append(localVideoInfo.getRemoteName()).append("；");
            }
        }
        tvSpeakers.setText(getString(R.string.str_speakers, stringBuilder.toString()));
    }

    @Override
    public void showSmallView() {
        SmallViewUtil.getInstance().showFloatingWindow();
    }

    @Override
    public void hideSmallView() {
        SmallViewUtil.getInstance().removeFloatView();
    }

    @Override
    public void hideInviteCall() {
        viewInvite.setVisibility(GONE);
    }

    @Override
    public void showRecordStatusNotification(boolean isStart, String displayName, boolean canStop) {
        Log.i(TAG, "showRecordStatusNotification: " + isStart);
        if (isStart) {
            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillBefore(true);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            llRecording.setVisibility(View.VISIBLE);
            ivRecordStatus.startAnimation(alphaAnimation);
            tvMoreRecord.setEnabled(canStop);
            tvRecordingDuration.setText(displayName + "正在录制");
            tvMoreRecord.setText(R.string.button_text_stop);
        } else {
            ivRecordStatus.clearAnimation();
            tvMoreRecord.setEnabled(true);
            llRecording.setVisibility(GONE);
            tvMoreRecord.setText(R.string.start_record_video);
        }
    }

    //语音模式
    private void setSwitchCallState(boolean audioMode) {
        List<VideoFragment> fragments = videoPagerAdapter.getFragments();
        for (VideoFragment fragment : fragments) {
            fragment.setAudioOnlyMode(audioMode, isVideoMute);
        }
        if (audioMode) {
            btCloseVideo.setEnabled(false);
            tvMoreCallMode.setText(R.string.close_switch_call_module);
        } else {
            btCloseVideo.setEnabled(true);
            tvMoreCallMode.setText(R.string.switch_call_module);
        }
    }

    private VideoInfo buildLocalLayoutInfo() {
        VideoInfo li = new VideoInfo();
        li.setLayoutVideoState(Enums.LAYOUT_STATE_RECEIVED);
        li.setDataSourceID(NemoSDK.getLocalVideoStreamID());
        li.setVideoMuteReason(Enums.MUTE_BY_USER);
        li.setRemoteName(NemoSDK.getInstance().getUserName());
        li.setParticipantId((int) NemoSDK.getInstance().getUserId());
        li.setRemoteID(RemoteUri.generateUri(String.valueOf(NemoSDK.getInstance().getUserId()), Enums.DEVICE_TYPE_SOFT));
        localVideoInfo = li;
        return li;
    }

    //=========================================================================================
    // face view
    //=========================================================================================
    @Override
    public void showFaceView(List<FaceView> faceViews) {
//        mVideoView.showFaceView(faceViews);
    }

    @Override
    public Activity getCallActivity() {
        return this;
    }

    @Override
    public int[] getMainCellSize() {
//        return new int[]{mVideoView.getWidth(), mVideoView.getHeight()};
        return new int[]{0, 0};
    }

    //=========================================================================================
    // share picture demo: 分享图片
    // NOTE: bitmap only support ARGB_8888
    //=========================================================================================
    private byte[] pictureData;
    private int width;
    private int height;
    private static final int MSG_SHARE_PICTURE = 6002;

    @SuppressLint("HandlerLeak")
    private Handler pictureHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHARE_PICTURE) {
                String dataSourceId = NemoSDK.getInstance().getDataSourceId();
                if (!TextUtils.isEmpty(dataSourceId) && pictureData != null) {
                    L.i(TAG, "send data to remote: " + pictureData.length + " W. " + width + " h." + height);
                    NativeDataSourceManager.putContentData2(dataSourceId,
                            pictureData, pictureData.length, width, height, 0, 0, 0, true);
                }
                pictureHandler.sendEmptyMessageDelayed(MSG_SHARE_PICTURE, 200);
                // 9711360   wang x. 1080 y. 2029
            }
        }
    };
/*
    private class MyPagerListener extends ViewPager.SimpleOnPageChangeListener {
        boolean first = true;

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // start share
            L.i(TAG, "wang onPageSelected: " + position);
            if (picturePaths != null && picturePaths.size() > 0) {
                pictureHandler.removeMessages(MSG_SHARE_PICTURE);
                String picturePath = picturePaths.get(position);
                Glide.with(XyCallActivity.this).asBitmap().apply(new RequestOptions().override(1280, 720))
                        .load(picturePath).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Matrix matrix = new Matrix();
                        matrix.setScale(0.5f, 0.5f);
                        Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
                        if (bitmap != null) {
                            width = bitmap.getWidth();
                            height = bitmap.getHeight();
                            int byteCount = bitmap.getByteCount();
                            ByteBuffer b = ByteBuffer.allocate(byteCount);
                            bitmap.copyPixelsToBuffer(b);
                            pictureData = b.array();
                            pictureHandler.sendEmptyMessage(MSG_SHARE_PICTURE);
                            bitmap.recycle();
                        }
                    }
                });
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            L.i(TAG, "onPageScrolled:: " + first);
            if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                onPageSelected(0);
                first = false;
            }
            hideToolbar();
        }
    }*/

    /**
     * 打开屏幕共享跟接收远端事件再次处理
     *
     * @param state
     */
    @Override
    public void updateSharePictures(NemoSDKListener.NemoDualState state) {
        /*if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STAT_IDLE) {
            pictureHandler.removeMessages(MSG_SHARE_PICTURE);
            pictureData = null;
            pagerPicture.setVisibility(GONE);
            pageIndicator.setVisibility(GONE);
            isSharePicture = false;
            resetShareStates(false, ShareState.IMAGE);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_RECEIVING) {
            resetShareStates(true, ShareState.IMAGE);
            picturePagerAdapter = new PicturePagerAdapter(getSupportFragmentManager());
            picturePagerAdapter.setOnPagerListener(() -> hideOrShowToolbar(isToolbarShowing));
            pagerPicture.setAdapter(picturePagerAdapter);
            pageIndicator.setViewPager(pagerPicture);
            pageIndicator.setOnPageChangeListener(new MyPagerListener());
            picturePagerAdapter.setPicturePaths(picturePaths);
            picturePagerAdapter.notifyDataSetChanged();
            pageIndicator.setVisibility(VISIBLE);
            pagerPicture.setVisibility(VISIBLE);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_NOBANDWIDTH) {
            Toast.makeText(this, "带宽不足, 网络不稳定, 无法分享", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "错误, 无法分享", Toast.LENGTH_SHORT).show();
        }*/
    }

    //=========================================================================================
    // share screen demo: 分享屏幕跟分享白板同时只允许一方
    //=========================================================================================
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (SharingValues.REQUEST_SHARE_SCREEN == requestCode) {
            if (resultCode == RESULT_OK) {
                if (screenPresenter != null) {
                    screenPresenter.onResult(requestCode, resultCode, intent);
                }
            } else {
                // user did not grant permissions
                Toast.makeText(XyCallActivity.this, "share screen cancel", Toast.LENGTH_LONG).show();
            }
        } else if (SharingValues.REQUEST_FLOAT_PERMISSION == requestCode) {
            // home screen float view
            if (Settings.canDrawOverlays(XyCallActivity.this)) {
                if (screenPresenter != null) {
                    screenPresenter.gotPermissionStartShare();
                }
                SmallViewUtil.getInstance().init(this);
            } else {
                Toast.makeText(XyCallActivity.this, "需要打开悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            /*picturePaths = Matisse.obtainPathResult(intent);
            L.i(TAG, "wang::: paths: " + picturePaths.size() + " ;; " + picturePaths);
            if (picturePaths.size() > 0) {
                // start share picture
                NemoSDK.getInstance().dualStreamStart(ContentType.CONTENT_TYPE_PICTURE);
            }*/
        }
    }

    @Override
    public void updateShareScreen(NemoSDKListener.NemoDualState state) {
        if(shareScreenView == null){
            shareScreenView = shareScreenViewStub.inflate().findViewById(R.id.share_screen);
        }
        if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STAT_IDLE) {
            if (screenPresenter != null && screenPresenter.isSharingScreen()) {
                L.i(TAG, "updateShareScreen stop");
                screenPresenter.stopShare();
            }
            shareScreenView.setVisibility(GONE);
            resetShareStates(false, ShareState.SCREEN);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_RECEIVING) {
            resetShareStates(true, ShareState.SCREEN);
            // show floating view
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityUtils.goHome(this);
                if (screenPresenter != null) {
                    screenPresenter.showFloatView(); // 显示悬浮窗
                }
                shareScreenView.setVisibility(VISIBLE);
            }
        } else {
            Toast.makeText(this, "正在分享, 请稍后", Toast.LENGTH_SHORT).show();
        }
    }

    //=========================================================================================
    // whiteboard demo
    //=========================================================================================
    private WhiteboardChangeListener whiteboardChangeListener = new WhiteboardChangeListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardStart() {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    L.i(TAG, "onWhiteboardStart");
                    // fix: 在桌面分享屏幕, 收到其他端的白板, 没有跳转到应用
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!ActivityUtils.isAppForeground(XyCallActivity.this)
                                && !(screenPresenter != null && screenPresenter.isSharingScreen())) {
                            ActivityUtils.moveTaskToFront(XyCallActivity.this);
                        }
                    }
                    videoPager.setScanScroll(false);
                    myVideoPagerListener.onPageSelected(0);
                    videoPager.setCurrentItem(0, false);
                    videoPagerAdapter.getItem(currentPagerIndex).setLandscape(false);
                    NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
                    videoPagerAdapter.onWhiteboardStart();
                    resetShareStates(true, ShareState.WHITEBOARD);
                }
            }, throwable -> {
            });
        }

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardStop() {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    videoPager.setScanScroll(true);
                    videoPagerAdapter.onWhiteboardStop();
                    resetShareStates(false, ShareState.WHITEBOARD);
                }
            }, throwable -> {
            });
        }

        /**
         * 处理白板数据
         *
         * @param message 白板数据
         */
        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardMessage(String message) {

            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    videoPagerAdapter.onWhiteboardMessage(message);
                }
            }, throwable -> {
            });
        }

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardMessages(ArrayList<String> messages) {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
                    }
                    videoPagerAdapter.onWhiteboardMessages(messages);
                }
            }, throwable -> {
            });
        }
    };


    private static final int MSG_ORIENTATION_CHANGED = 60001;

    //=========================================================================================
    // 会控
    //=========================================================================================
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private View hostMeetingView;
    private LinearLayout llHostMeeting;
    private Toolbar hostMeetingToolbar;
    private void showHostMeeting() {
        if(hostMeetingView == null) {
            mHostMeetingStub = findViewById(R.id.vs_host_meeting);
            hostMeetingView = mHostMeetingStub.inflate();
            llHostMeeting = hostMeetingView.findViewById(R.id.ll_host_meeting);
            mProgressBar = hostMeetingView.findViewById(R.id.progress);
            mProgressBar.setMax(100);
            hostMeetingToolbar = hostMeetingView.findViewById(R.id.hold_meeting_toolbar);
            hostMeetingToolbar.setNavigationOnClickListener(v -> onBackPressed());
            mWebView = hostMeetingView.findViewById(R.id.webview);
            WebSettings settings = mWebView.getSettings();
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("searchBoxJavaBridge");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
            settings.setSavePassword(false);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLoadsImagesAutomatically(true); // 加载图片
            settings.setAllowFileAccess(true);
            settings.setAppCacheEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
        }

        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setProgress(0);
        llHostMeeting.setVisibility(VISIBLE);
        layoutMeetingView();
        String meetingHost = NemoSDK.getInstance().getMeetingHost();
        mWebView.loadUrl(meetingHost);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(GONE);
            }
        });
    }

    private void hideHostMeeting() {
        if (llHostMeeting != null && llHostMeeting.getVisibility() == View.VISIBLE) {
            llHostMeeting.setVisibility(View.GONE);
            mWebView.loadUrl("");
        }
    }

    private void layoutMeetingView() {
        if (llHostMeeting != null && llHostMeeting.getVisibility() == View.VISIBLE) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(root);
            int hostMeetingId = llHostMeeting.getId();
            constraintSet.clear(hostMeetingId);
            constraintSet.connect(hostMeetingId, ConstraintSet.END, root.getId(), ConstraintSet.END);
            constraintSet.connect(hostMeetingId, ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
            constraintSet.connect(hostMeetingId, ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                constraintSet.setDimensionRatio(hostMeetingId, "h,1:1");
                hostMeetingToolbar.setVisibility(GONE);
            } else {
                constraintSet.connect(hostMeetingId, ConstraintSet.START, root.getId(), ConstraintSet.START);
                hostMeetingToolbar.setVisibility(VISIBLE);
            }
            constraintSet.applyTo(root);
        }
    }

    private void resetShareStates(boolean isSharing, ShareState shareState) {
        if (isSharing) {
            this.shareState = shareState;
            ((TextView) findViewById(R.id.tv_share)).setText("结束共享");
            btnMoreShare.setImageResource(R.drawable.finish_share);
        } else {
            this.shareState = ShareState.NONE;
            btnMoreShare.setImageResource(R.drawable.share);
            ((TextView) findViewById(R.id.tv_share)).setText("共享");
        }
    }

    private Runnable refreshStatisticsInfoRunnable = this::startRefreshStatisticsInfo;

    private void stopRefreshStatisticsInfo() {
        handler.removeCallbacks(refreshStatisticsInfoRunnable);
    }

    private void startRefreshStatisticsInfo() {
        NewStatisticsInfo newInfo = NemoSDK.getInstance().getStatisticsInfo();
        if (null == newInfo) {
            return;
        }
        mStatisticsRender.show();
        mStatisticsRender.onValue(newInfo);

        handler.removeCallbacks(refreshStatisticsInfoRunnable);
        handler.postDelayed(refreshStatisticsInfoRunnable, REFRESH_STATISTICS_INFO_DELAYED);
    }

    @Override
    public void onRosterChanged(int totalNumber, RosterWrapper rosters) {
        this.rosterWrapper = rosters;
        ((TextView) findViewById(R.id.tv_meeting_members)).setText(String.valueOf(totalNumber));
        videoPagerAdapter.setTotalMeetingMember(totalNumber);
        videoPagerIndicator.notifyDataSetChanged();
    }

    @Override
    public void showVideoDataSourceChange(List<VideoInfo> videoInfos, boolean hasVideoContent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // case-fix: 如果正在共享屏幕, 收到content则APP回到前台
            if (hasVideoContent && !ActivityUtils.isAppForeground(this)
                    && !(screenPresenter != null && screenPresenter.isSharingScreen())) {
                ActivityUtils.moveTaskToFront(this);
            }
        }
        L.i(TAG, "showVideoDataSourceChange currentPagerIndex: " + currentPagerIndex);
        L.i(TAG, "showVideoDataSourceChange videoInfos size: " + videoInfos.size());
        mRemoteVideoInfos = videoInfos;
        if ((currentPagerIndex == 0 || currentPagerIndex == 1) && !hasVideoContent) {
            firstPagerVideoInfo = videoInfos;
        }
        VideoFragment fragment = (VideoFragment) videoPagerAdapter.getItem(currentPagerIndex);
        fragment.setRemoteVideoInfo(videoInfos, hasVideoContent);
    }

    public class MyVideoPagerListener extends ViewPager.SimpleOnPageChangeListener {
        boolean first = true;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            L.i(TAG, "onPageScrolled:: " + first);
            if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                onPageSelected(0);
                first = false;
            }
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (videoPagerAdapter.getCurrentIndex() != position) {
                videoPagerAdapter.setCurrentIndex(position);
            }
            currentPagerIndex = position;
            // 0 / 1 复用List<VideoInfo>
            VideoFragment videoFragment = videoPagerAdapter.getItem(position);
            L.i(TAG, "onPageSelected position: " + position + ", fragment: " + videoFragment);
            if (position == 0) {
                videoFragment.setRemoteVideoInfo(firstPagerVideoInfo, false);
                NemoSDK.getInstance().setLayoutBuilder(new SpeakerLayoutBuilder());
            } else if (position == 1) {
                videoFragment.setRemoteVideoInfo(firstPagerVideoInfo, false);
                NemoSDK.getInstance().setLayoutBuilder(new GalleryLayoutBuilder(1));
            } else {
                // other pager
                NemoSDK.getInstance().setLayoutBuilder(new GalleryLayoutBuilder(position));
            }
            videoFragment.startRender();

            // only LANDSCAPE & meeting member > 0 & speaker mode -> closePip enable
            boolean isClosePipEnable = false;
            if (position == 0) {
                isClosePipEnable = ((SpeakerVideoFragment) videoFragment).isLandscape() && mRemoteVideoInfos != null
                        && mRemoteVideoInfos.size() > 0 && currentPagerIndex == 0;
            }
            tvClosePip.setEnabled(isClosePipEnable);
            tvClosePip.setTextColor(isClosePipEnable ? Color.WHITE : Color.GRAY);
        }
    }

    @Override
    public boolean onVideoCellSingleTapConfirmed(VideoCell cell) {
        hideOrShowToolbar(isToolbarShowing);
        if (dtmfLayout != null && dtmfLayout.getVisibility() == VISIBLE) {
            dtmfLayout.setVisibility(GONE);
            dtmf.clearText();
        }
        hideHostMeeting();
        return false;
    }

    @Override
    public boolean onVideoCellDoubleTap(VideoCell cell) {
        return false;
    }

    @Override
    public void onLockLayoutChanged(int pid) {
        if (currentPagerIndex == 0) {
            llLockPeople.setVisibility(VISIBLE);
        } else {
            llLockPeople.setVisibility(GONE);
        }
    }

    @Override
    public void onFullScreenChanged(VideoCell cell) {
        if (cell != null) {
            fullVideoInfo = cell.getLayoutInfo();
        }
    }

    @Override
    public void onVideoCellGroupClicked(View group) {
        hideOrShowToolbar(isToolbarShowing);
        if (dtmfLayout != null && dtmfLayout.getVisibility() == VISIBLE) {
            dtmfLayout.setVisibility(GONE);
            dtmf.clearText();
        }
        hideHostMeeting();
    }

    @Override
    public void onWhiteboardClicked() {
        hideOrShowToolbar(isToolbarShowing);
        if (dtmfLayout != null && dtmfLayout.getVisibility() == VISIBLE) {
            dtmfLayout.setVisibility(GONE);
            dtmf.clearText();
        }
        hideHostMeeting();
    }

    private ServiceConnection xyCallConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @SuppressLint("CheckResult")
    private void checkPermission() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(hasMicPermission -> {
                    L.i("AAA", "audio permission: " +  hasMicPermission +
                            ", mic state: " + NemoSDK.getInstance().isMicMuted());
                    if (hasMicPermission) {
                        NemoSDK.getInstance().requestAudioMic();
                    }
                });
    }
}
