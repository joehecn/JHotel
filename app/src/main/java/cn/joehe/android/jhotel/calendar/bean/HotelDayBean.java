package cn.joehe.android.jhotel.calendar.bean;

/**
 * Created by hemiao on 2017/12/23.
 */

public class HotelDayBean extends HotelDateBean {
    public static final int STATE_IGNORE = 1; // 忽略，不可选

    public static final int STATE_NORMAL = 2;
    public static final int STATE_TODAY = 3;
    public static final int STATE_WEEKEND = 4;

    public static final int STATE_CHOSED = 9;

    private int year;
    private int month;
    private int day;
    private int firstState; // 初始属性 0 2 3 4

    private int state;

    public HotelDayBean() {}
    public HotelDayBean(int year, int month, int day, int state) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.firstState = this.state = state;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void backToFirstState() {
        this.state = this.firstState;
    }

    public void setDate(int year, int month, int day, int state) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.state = state;
    }

    @Override
    public String toString() {
        return this.year + "-" + preZero(this.month + 1) + "-" + preZero(this.day);
    }

    private String preZero(int md) {
        if (md > 9)
            return "" + md;
        return "0" + md;
    }
}
