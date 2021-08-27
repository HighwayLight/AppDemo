package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.myapplication.process.AnyProcess;


/**
 * Created by dushu on 2021/1/6 .
 */
@AnyProcess
public class MediaEntity implements Parcelable {
    public String URI;//资源地址
    public int type;//MediaType 媒体类型
    public long size;
    public int id;//媒体ID
    public String parentDir;

    @AnyProcess
    public MediaEntity(String URI, int type, long size, int id, String parentDir) {
        this.URI = URI;
        this.type = type;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.URI);
        dest.writeInt(this.type);
        dest.writeLong(this.size);
        dest.writeInt(this.id);
        dest.writeString(this.parentDir);
    }

    protected MediaEntity(Parcel in) {
        this.URI = in.readString();
        this.type = in.readInt();
        this.size = in.readLong();
        this.id = in.readInt();
        this.parentDir = in.readString();
    }

    public static final Creator<MediaEntity> CREATOR = new Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel source) {
            return new MediaEntity(source);
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[size];
        }
    };
}
