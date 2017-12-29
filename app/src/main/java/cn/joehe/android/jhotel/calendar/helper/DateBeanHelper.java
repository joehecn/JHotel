package cn.joehe.android.jhotel.calendar.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.joehe.android.jhotel.calendar.bean.HotelDateBean;
import cn.joehe.android.jhotel.calendar.bean.HotelDayBean;
import cn.joehe.android.jhotel.calendar.bean.HotelMonthBean;

/**
 * Created by hemiao on 2017/12/23.
 */

public class DateBeanHelper {

    // 日期跨度，* 重要: 此算法只考虑跨度(31, 365)天的情况
    // 就是 大于 1个月 小于 1年
    // 入住日期(startDate)可选范围: [今天, 今天 + START_DAY_COUNTS]
    private static final int START_DAY_COUNTS = 90; // (31, 365)
    // 离店日期(endDate)可选范围: 入住日期后15天内
    private static final int END_DAY_COUNTS = 15;

    public static HotelDayBean getHotelDayBean(Calendar calendar) {
        return new HotelDayBean(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0);
    }

    public static HotelDayBean getEndDayBean(Calendar calendar) {
        calendar.add(Calendar.DATE, START_DAY_COUNTS);
        return getHotelDayBean(calendar);
    }

    public static HotelDayBean getAddDayBean(Calendar calendar) {
        calendar.add(Calendar.DATE, END_DAY_COUNTS);
        return getHotelDayBean(calendar);
    }

    public static int[] getPoints() {
        // [ todayPoint, enddayPoint, monthCount, adddayPoint, hadAddMonth ]
        // [ todayPoint, monthCount, hadAddMonth ]
        int[] points = new int[5];

        Calendar calendar = Calendar.getInstance();
        // todayPoint
        points[0] = calendar.get(Calendar.DATE);
        // 今天在哪个月
        int beginMonth = calendar.get(Calendar.MONTH);

        calendar.add(Calendar.DATE, START_DAY_COUNTS);
        // enddayPoint
        int enddayPoint = calendar.get(Calendar.DATE);
        // (今天 + START_DAY_COUNTS)后在哪个月
        int endMonth = calendar.get(Calendar.MONTH);
        // 如果跨年了
        if (beginMonth >= endMonth)
            endMonth = endMonth + 12;
        // monthCount
        points[1] = endMonth - (beginMonth - 1);

        calendar.add(Calendar.DATE, END_DAY_COUNTS);
        // adddayPoint
        int adddayPoint = calendar.get(Calendar.DATE);
        if (adddayPoint > enddayPoint) {
            points[2] = 0;
        } else {
            points[2] = 1;
        }

        return points;
    }

    public static List<HotelDateBean> getHotelDateBeans(Calendar calendar) {
        List<HotelDateBean> beans = new ArrayList<>();
        beans.add(getMonthBean(calendar));
        beans.addAll(getDaysOfMonth(calendar));
        return beans;
    }

    public static List<HotelDateBean> getHotelDateBeans(Calendar calendar, int point) {
        List<HotelDateBean> beans = new ArrayList<>();
        beans.add(getMonthBean(calendar));
        beans.addAll(getDaysOfMonth(calendar, point));
        return beans;
    }

    public static void setAllItemsState(List<HotelDateBean> items, HotelDayBean beginDayBean, HotelDayBean endDayBean) {
        for (HotelDateBean dateBean : items) {
            if (dateBean instanceof HotelDayBean) {
                HotelDayBean dayBean = (HotelDayBean) dateBean;
                if (compareDayBean(dayBean, beginDayBean) < 0 || compareDayBean(dayBean, endDayBean) > 0)
                    dayBean.setState(HotelDayBean.STATE_IGNORE);
                else dayBean.backToFirstState();
            }
        }
    }

    public static int compareDayBean(HotelDayBean dayA, HotelDayBean dayB) {
        if (dayA.getYear() > dayB.getYear())
            return 1;
        else if (dayA.getYear() < dayB.getYear())
            return -1;

        if (dayA.getMonth() > dayB.getMonth())
            return 1;
        else if (dayA.getMonth() < dayB.getMonth())
            return -1;

        if (dayA.getDay() > dayB.getDay())
            return 1;
        else if (dayA.getDay() < dayB.getDay())
            return -1;

        return 0;
    }

    /**
     * 设置给定时间的日历calendar
     *
     * @param bean
     * @return Calendar
     */
    public static Calendar setCalendar(HotelDayBean bean) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, bean.getYear());
        calendar.set(Calendar.MONTH, bean.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, bean.getDay());
        return calendar;
    }

    private static HotelMonthBean getMonthBean(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        HotelMonthBean bean = new HotelMonthBean(year + "年" + month + "月");
        return bean;
    }

    // IM_MONTH_MIDDLE IM_MONTH_ADD
    private static List<HotelDayBean> getDaysOfMonth(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        List<HotelDayBean> dayBeans = new ArrayList<>();

        // 这个月第一天
        calendar.set(Calendar.DATE, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 前面的空白格子
        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            HotelDayBean bean = new HotelDayBean();
            dayBeans.add(bean);
        }
        // 下个月第一天
        calendar.add(Calendar.MONTH, 1);
        // 这个月最后一天
        calendar.add(Calendar.DATE, -1);

        int lastDay = calendar.get(Calendar.DATE);
        // 日历
        for(int i = 0; i < lastDay; i++) {
            int state;

            calendar.set(Calendar.DATE, i + 1);
            if (isWeekend(calendar))
                state = HotelDayBean.STATE_WEEKEND;
            else state = HotelDayBean.STATE_NORMAL;

            HotelDayBean bean = new HotelDayBean(year, month, i + 1, state );
            dayBeans.add(bean);
        }
        return dayBeans;
    }

    // IM_MONTH_FIRST IM_MONTH_END
    private static List<HotelDayBean> getDaysOfMonth(Calendar calendar, int point) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        List<HotelDayBean> dayBeans = new ArrayList<>();

        // 这个月第一天
        calendar.set(Calendar.DATE, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 前面的空白格子
        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            HotelDayBean bean = new HotelDayBean();
            dayBeans.add(bean);
        }
        // 下个月第一天
        calendar.add(Calendar.MONTH, 1);
        // 这个月最后一天
        calendar.add(Calendar.DATE, -1);

        int lastDay = calendar.get(Calendar.DATE);
        // 日历
        for(int i = 0; i < lastDay; i++) {
            calendar.set(Calendar.DATE, i + 1);
            int state = getState(calendar, point);
            HotelDayBean bean = new HotelDayBean(year, month, i + 1, state );
            dayBeans.add(bean);
        }
        return dayBeans;
    }

    // IM_MONTH_FIRST IM_MONTH_END
    private static int getState(Calendar calendar, int point) {
        int state;
        int day = calendar.get(Calendar.DATE);

        if (day == point)
            state = HotelDayBean.STATE_TODAY;
        else if (isWeekend(calendar))
            state = HotelDayBean.STATE_WEEKEND;
        else state = HotelDayBean.STATE_NORMAL;

        return state;
    }

    private static boolean isWeekend(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

            return true;
        }
        return false;
    }
}
