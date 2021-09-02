package com.example.myapplication.PhotoAlbum.loader;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.myapplication.process.AnyProcess;


/**
 * Created by dushu on 2021/1/6 .
 */
@AnyProcess
public class MediaEntity implements Parcelable {
    public String URI;//资源地址
    public String name;
    public long time;
    public int mediaType;//MediaType 媒体类型
    public long size;
    public int id;//媒体ID
    public String parentDir;

    public boolean isSelected = false;
    public int itemIndex = 0;//多选模式下 选中的item的index


    @AnyProcess
    public MediaEntity(String URI,  String name,  long time, int mediaType, long size, int id, String parentDir) {
        this.URI = URI;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
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
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeInt(this.mediaType);
        dest.writeLong(this.size);
        dest.writeInt(this.id);
        dest.writeString(this.parentDir);
    }

    protected MediaEntity(Parcel in) {
        this.URI = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
        this.mediaType = in.readInt();
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
