package com.sdy.dataexchange.web.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFormatUtil {

    public static String data2Str(Date date){
        if(date == null){
            return "";
        }
        DateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return format.format(date);
    }

}
