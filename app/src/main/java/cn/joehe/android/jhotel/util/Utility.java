package cn.joehe.android.jhotel.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.Window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hemiao on 2017/12/23.
 */

public class Utility {

    private static String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private static Calendar c;

    private static Date getDay(int amount) {
        c = Calendar.getInstance();
        if (amount > 0) {
            c.add(Calendar.DAY_OF_MONTH, amount);
        }
        return c.getTime();
    }

    private static String formatChinaDate(String date) {
        String[] arr = date.split("-");
        return Integer.valueOf(arr[1]) + "月" + Integer.valueOf(arr[2]) + "日";
    }

    private static String[] coverChinese(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setLenient(false);

        String[] dates = new String[2];

        dates[0] = formatChinaDate(date);

        String today = sf.format(getDay(0));
        String tomorrow = sf.format(getDay(1));
        String afterTomorrow = sf.format(getDay(2));
        if (date.equals(today)) {
            dates[1] = "今天";
        } else if (date.equals(tomorrow)) {
            dates[1] = "明天";
        } else if (date.equals(afterTomorrow)) {
            dates[1] = "后天";
        } else {
            try {
                c = Calendar.getInstance();
                c.setTime(sf.parse(date));
                int w = c.get(Calendar.DAY_OF_WEEK) - 1;
                if (w < 0) w = 0;
                dates[1] = weekDays[w];
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return dates;
    }

    private static int getDateDiff(String beginDateStr, String endDateStr) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setLenient(false);

        try {
            Date beginDate = sf.parse(beginDateStr);
            Date endDate = sf.parse(endDateStr);

            return (int) ((endDate.getTime() - beginDate.getTime()) / (24*60*60*1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @TargetApi( Build.VERSION_CODES.LOLLIPOP )
    public static void setBarTransparent(Window window) {
        // 标题栏背景透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static boolean needUpdateDate(String dateDb) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setLenient(false);

        String today = sf.format(getDay(0));

        int diff = getDateDiff(dateDb, today);

        // 如果当前日期大于数据库日期，需要更新
        if (diff > 0) {
            return true;
        }
        return false;
    }

    // 获取 今天 和 明天: xxxx-xx-xx
    public static String[] getDates() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setLenient(false);

        String[] dates = new String[2];
        dates[0] = sf.format(getDay(0)); // 今天
        dates[1] = sf.format(getDay(1)); // 明天
        return dates;
    }

    public static String[] coverDates(String beginDateStr, String endDateStr) {
        String[] covers = new String[5];
        String[] dates0 = coverChinese(beginDateStr);
        String[] dates1 = coverChinese(endDateStr);
        int diff = getDateDiff(beginDateStr, endDateStr);

        covers[0] = dates0[0];
        covers[1] = dates1[0];
        covers[2] = dates0[1];
        covers[3] = dates1[1];
        covers[4] = Integer.toString(diff);
        return covers;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap stringToBitmap(String base64Data){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(base64Data, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
