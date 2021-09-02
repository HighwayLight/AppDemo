package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.myapplication.PhotoAlbum.loader.MediaEntity;

import java.util.ArrayList;

/**
 * Created by dushu on 2020/12/31 .
 */
public class MediaAlbumBean implements Parcelable {

    public String id;//相册文件夹id
    public String title;//相册文件夹名称

    private ArrayList<MediaEntity> medias = new ArrayList<>();


    public MediaAlbumBean(){}

    public void addMedias(MediaEntity media) {
        medias.add(media);
    }

    public ArrayList<MediaEntity> getMedias() {
        return this.medias;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeTypedList(this.medias);
    }

    protected MediaAlbumBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.medias = in.createTypedArrayList(MediaEntity.CREATOR);
    }

    public static final Creator<MediaAlbumBean> CREATOR = new Creator<MediaAlbumBean>() {
        @Override
        public MediaAlbumBean createFromParcel(Parcel source) {
            return new MediaAlbumBean(source);
        }

        @Override
        public MediaAlbumBean[] newArray(int size) {
            return new MediaAlbumBean[size];
        }
    };
}
