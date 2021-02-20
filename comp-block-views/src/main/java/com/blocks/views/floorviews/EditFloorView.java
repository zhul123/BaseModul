package com.blocks.views.floorviews;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.base.http.MyHttpUtils;
import com.base.imagehelper.ImageHelper;
import com.blocks.views.R;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.commviews.bottomdialog.BottomPopDialog;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.support.SimpleClickSupport;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 信息录入
 */
public class EditFloorView extends BaseFloorView implements BaseFloorView.OnChildClickListener {
    protected static final String EDITTEXT = "edittext";//输入框默认填充值
    protected static final String ENABLE = "enable";//是否可编辑 默认否
    protected static final String INPUTTYPE = "inputType";// edittext输入属性
    protected static final String HINT = "hint";
    protected static final String HAVEBOTTOMLINE = "haveBottomLine";
    protected static final String LEFTDRAWABLE = "leftImgUrl";
    private String HINTDEFAULT = "请输入";//默认值
    View mainView;
    private TextView mTextView;
    private EditText mEditText;
    private ImageView mCodeImageView;
    private ImageView mLeftImageView;
    private ImageView mChooseImageView;
    private BottomPopDialog mBottomPopDialog;

    public EditFloorView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        mainView = LayoutInflater.from(mContext).inflate(R.layout.floor_edittext, null);
        addView(mainView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mEditText = findViewById(R.id.et_editview);
        mCodeImageView = findViewById(R.id.iv_code_image);
        mLeftImageView = findViewById(R.id.iv_left);
        mChooseImageView = findViewById(R.id.iv_choose);
        mTextView = findViewById(R.id.tv_editfloor);
    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        if (cell == null)
            return;

        if (getOptBoolean(HAVEBOTTOMLINE)) {
            mainView.setBackgroundResource(R.drawable.floor_bottom_line);
        } else {
            mainView.setBackgroundResource(0);
        }

        String text = getOptString(ParamsUtils.TEXT);
        if (TextUtils.isEmpty(text)) {
            mTextView.setVisibility(GONE);
            mTextView.setText("");
        } else {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(text);
        }

        mTextView.setTextColor(getOptColor(ParamsUtils.TEXTCOLOR, Color.BLACK));

        String imgUrl = getOptString(ParamsUtils.IMGURL);
        if (!TextUtils.isEmpty(imgUrl)) {
            mCodeImageView.setVisibility(VISIBLE);
            loadImg(imgUrl, "");
        } else {
            mCodeImageView.setVisibility(GONE);
        }

        String leftDrawable = getOptString(LEFTDRAWABLE);
        if (TextUtils.isEmpty(leftDrawable)) {
            mLeftImageView.setVisibility(GONE);
        } else {
            mLeftImageView.setVisibility(VISIBLE);
            if (leftDrawable.indexOf("image://") == -1) {
                ImageHelper.getInstance().setCommImage(leftDrawable, mLeftImageView);
            } else {
                switch (leftDrawable) {
                    case "image://phone":
                        mLeftImageView.setImageResource(R.drawable.icone_phone);
                        break;
                    case "image://password":
                        mLeftImageView.setImageResource(R.drawable.icon_password);
                        break;
                    case "image://code":
                        mLeftImageView.setImageResource(R.drawable.icon_code);
                        break;
                }
            }
        }


        String paramsKey = getOptString(ParamsUtils.PARAMSKEY);
        String hint = getOptString(HINT);
        mEditText.setHint(TextUtils.isEmpty(hint) ? HINTDEFAULT : hint);
        mEditText.setTransformationMethod(null);
        switch (getOptString(INPUTTYPE)) {
            case "password":
                mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case "number":
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "phone":
                mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            default:
                mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }

        mEditText.setEnabled(!getOptBoolean(ENABLE));
        if (cell.getAllBizParams() != null && cell.getAllBizParams().get(paramsKey) != null) {
            String textDes = cell.getAllBizParams().get(paramsKey).toString();
            mEditText.setTag(textDes);
        } else {
            mEditText.setTag(null);
        }

        mEditText.setText(getOptString(EDITTEXT));
        mEditText.addTextChangedListener(mTextWatcher);
        bindChooseView();
    }

    /**
     *
     */
    private void bindChooseView() {
        JSONArray chooseDatas = getOptJsonArray(ParamsUtils.CHOOSEDATAS);
        if (chooseDatas != null) {
            mChooseImageView.setVisibility(VISIBLE);
            mEditText.setHint("请选择");
            mChooseImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBottomPopDialog == null) {
                        mBottomPopDialog = BottomPopDialog.newDialogBuilder(mContext)
                                .setContent(chooseDatas)
                                .setSimpleClickSupport(new SimpleClickSupport() {
                                    @Override
                                    public void defaultClick(View targetView, BaseCell cell, int eventType) {
                                        super.defaultClick(targetView, cell, eventType);
                                        print("==========选择");
                                        mEditText.setText(getOptString(cell,ParamsUtils.TEXT));
                                        mEditText.setTag(getOptString(cell,ParamsUtils.PARAMSKEY));
                                        mBottomPopDialog.dismiss();
                                    }
                                }).build();
                    } else {
                        mBottomPopDialog.setDatas(chooseDatas);
                    }
                    mBottomPopDialog.show();
                }
            });
        } else {
            mEditText.setHint(getOptString(HINT));
            mChooseImageView.setVisibility(GONE);
            mChooseImageView.setOnClickListener(null);
        }
    }

    private void loadImg(String imgUrl, String time) {
        if (TextUtils.isEmpty(time)) {
            time = String.valueOf(System.currentTimeMillis());
            mBaseCell.addBizParam(ParamsUtils.CHECKKEY, time);
        }

        imgUrl = imgUrl + time;
        MyHttpUtils.get(imgUrl, null, new MyHttpUtils.HttpCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (mCodeImageView != null && result != null) {
                    ImageHelper.getInstance().setCommImage(result.toString(), mCodeImageView);
                }
            }

            @Override
            public void onFail(String errMsg) {
            }
        });
    }

    @Override
    protected void setCustomStyle() {

    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !TextUtils.isEmpty(s.toString())) {
                if(mEditText.getTag() != null){
                    mBaseCell.addBizParam(getOptString(ParamsUtils.PARAMSKEY), mEditText.getTag());
                }else {
                    mBaseCell.addBizParam(getOptString(ParamsUtils.PARAMSKEY), s);
                }
                        try {
                            mBaseCell.extras.optJSONObject(ParamsUtils.DATAS).put(EDITTEXT,s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }
        }
    };

    @Override
    public void setLsn() {
        super.setLsn();
        setOnHolderChildClickListener(this, mCodeImageView);
    }

    @Override
    protected void onItemClick() {
    }

    @Override
    public void postUnBindView(BaseCell cell) {
        if (mBottomPopDialog != null) {
            mBottomPopDialog.onDestroy();
            mBottomPopDialog = null;
        }
        if(mEditText != null){
            mEditText.removeTextChangedListener(mTextWatcher);
        }
    }

    @Override
    public void onChildClick(View view, BaseCell cell) {
        if (view.getId() == R.id.iv_code_image) {
            loadImg(getOptString(ParamsUtils.IMGURL), "");
        }
    }
}
