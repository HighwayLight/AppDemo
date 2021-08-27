package com.example.myapplication;



import java.util.ArrayList;

/**
 * Created by dushu on 2020/1/5.
 */

public class LoaderM {

    public static final int LOADER_ID = 1000;

    public String getParent(String path) {
        String sp[] = path.split("/");
        return sp[sp.length - 2];
    }

    public int hasDir(ArrayList<MediaAlbumBean> folders, String dirName) {
        for (int i = 0; i < folders.size(); i++) {
            MediaAlbumBean folder = folders.get(i);
            if (folder.title.equals(dirName)) {
                return i;
            }
        }
        return -1;
    }


}
