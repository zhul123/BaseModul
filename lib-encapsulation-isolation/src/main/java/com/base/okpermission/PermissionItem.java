package com.base.okpermission;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * @desc : 申请权限的包装类，可以配置提示对话框权限的展示名字和图片
 */

public class PermissionItem implements Serializable, Parcelable {
    public final String permission;
    public final int imageId;
    public final int nameId;

    public PermissionItem(String permission, int nameId, int imageId) {
        this.permission = permission;
        this.imageId = imageId;
        this.nameId = nameId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.permission);
        dest.writeInt(this.imageId);
        dest.writeInt(this.nameId);
    }

    protected PermissionItem(Parcel in) {
        this.permission = in.readString();
        this.imageId = in.readInt();
        this.nameId = in.readInt();
    }

    public static final Creator<PermissionItem> CREATOR = new Creator<PermissionItem>() {
        @Override
        public PermissionItem createFromParcel(Parcel source) {
            return new PermissionItem(source);
        }

        @Override
        public PermissionItem[] newArray(int size) {
            return new PermissionItem[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof PermissionItem)){
            return false;
        }
        PermissionItem permissionItem = (PermissionItem) obj;
        return null == this.permission ? null == permissionItem.permission : this.permission.equals(permissionItem.permission);
    }

    @Override
    public int hashCode() {
        if(TextUtils.isEmpty(this.permission)){
            return 0;
        }
        return this.permission.hashCode();
    }
}
