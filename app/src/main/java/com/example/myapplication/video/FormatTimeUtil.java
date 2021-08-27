package com.example.myapplication.video;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dushu on 2021/4/25 .
 */
public class FormatTimeUtil {

    public static String formatLongToTimeStr(long currentPosition) {
        SimpleDateFormat format =   new SimpleDateFormat( "mm:ss" );
        String d = format.format(currentPosition);
//        try {
//            Date date=format.parse(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return d;
    }
}
