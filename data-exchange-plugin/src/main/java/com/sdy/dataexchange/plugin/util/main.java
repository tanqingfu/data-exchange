package com.sdy.dataexchange.plugin.util;

import com.sdy.common.utils.DateUtil;
import org.aspectj.util.FileUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: 王越洋
 * @version: v1.0
 * @description: com.sdy.dataexchange.plugin.config
 * @date:2020/6/9
 */
public class main {
    public static void main(String[] args) throws ParseException {
//        String s = "INSERT INTO `test1128_1` VALUES (7, 'xm_7', 'xb_7', '2020-06-08 12:31:12', 'fh_7');";
//        String value = s.substring(0, s.indexOf("VALUES"));
//        System.out.println(value);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DATETIME_FORMAT);
        Date parse = simpleDateFormat.parse("2020-06-08 12:31:12");
        Date parse1 = simpleDateFormat.parse("2020-06-08 12:32:12");
        long c = Math.abs( parse1.getTime() - parse.getTime()) / (1000 * 1000);
        System.out.println(c);
        long history = parse.getTime();
        long now = parse1.getTime();
        int minutes = (int) ((now - history) / (1000 * 60));
        System.out.println(minutes);
//        File file = new File("D:\\opt\\sql100w.sql");
//        long length = file.length();
//        double fileSizeMb = new BigDecimal(length/1024/1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        if (fileSizeMb>=20){
//
//        }
//        System.out.println(fileSizeMB);


    }
}
