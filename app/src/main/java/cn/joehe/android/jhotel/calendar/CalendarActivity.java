package cn.joehe.android.jhotel.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.calendar.bean.HotelDateBean;
import cn.joehe.android.jhotel.calendar.bean.HotelDayBean;
import cn.joehe.android.jhotel.calendar.bean.HotelMonthBean;
import cn.joehe.android.jhotel.calendar.helper.CalendarAdapter;
import cn.joehe.android.jhotel.calendar.helper.DateBeanHelper;
import cn.joehe.android.jhotel.calendar.view.DividerItemDecortion;
import cn.joehe.android.jhotel.db.MyHomeData;

import static cn.joehe.android.jhotel.util.Utility.setBarTransparent;

/**
 * Created by hemiao on 2017/12/23.
 */

public class CalendarActivity extends AppCompatActivity {
    // 数据
    private MyHomeData myHomeData;

    // 控件
    private ImageButton mBtnNavBack;
    private TextView mTvNavTitle;

    private RecyclerView mRvCalendar;
    private GridLayoutManager mGridLayoutManager;

    private HotelDayBean todayBean; // 这个是个标尺，一旦创建就不再更改
    private HotelDayBean beginDayBean;
    private HotelDayBean endDayBean;

    private List<HotelDateBean> items = new ArrayList<>(); // 所有的条目
    private CalendarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        // 标题栏背景透明
        setBarTransparent(getWindow());

        // 数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);

        // 初始化控件
        mBtnNavBack = findViewById(R.id.btn_nav_back);
        mTvNavTitle = findViewById(R.id.tv_nav_title);
        mTvNavTitle.setText("选择入住离店日期");

        mRvCalendar = findViewById(R.id.rv_calendar);
        mGridLayoutManager = new GridLayoutManager(this, 7);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                HotelDateBean bean = items.get(position);
                if (bean instanceof HotelMonthBean)
                    return 7;
                return 1;
            }
        });
        mRvCalendar.setLayoutManager(mGridLayoutManager);
        mRvCalendar.addItemDecoration(new DividerItemDecortion(this));

        Calendar calendar = Calendar.getInstance();
        todayBean = DateBeanHelper.getHotelDayBean(calendar);
        beginDayBean = DateBeanHelper.getHotelDayBean(calendar);
        endDayBean = DateBeanHelper.getEndDayBean(calendar);
        // [ todayPoint, monthCount, hadAddMonth ]
        int[] points = DateBeanHelper.getPoints();
        initItems(points);
        DateBeanHelper.setAllItemsState(items, beginDayBean, endDayBean);

        adapter = new CalendarAdapter(this, items);
        mRvCalendar.setAdapter(adapter);

        adapter.setOnItemClickLister(new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HotelDayBean bean = (HotelDayBean) items.get(position);
                if (bean == null)
                    return;
                sovleItemClick(bean);
            }
        });

        mBtnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initItems(int[] points) {
        // 第一个月
        Calendar calendar = Calendar.getInstance();
        items.addAll(DateBeanHelper.getHotelDateBeans(calendar, points[0]));

        // 中间的月份, 最后一个月
        for (int i = 1; i < points[1]; i++) {
            calendar.add(Calendar.MONTH, 1);
            items.addAll(DateBeanHelper.getHotelDateBeans(calendar));
        }

        // 加一个月
        if (points[2] == 1) {
            calendar.add(Calendar.MONTH, 1);
            items.addAll(DateBeanHelper.getHotelDateBeans(calendar));
        }
    }

    /**
     * 点击事件
     * @param bean
     */
    private void sovleItemClick(HotelDayBean bean) {
        if (bean.getYear() == 0 || bean.getState() == HotelDayBean.STATE_IGNORE)
            return;

        if (beginDayBean.getState() == HotelDayBean.STATE_CHOSED) {
            // 说明是第二次选择, 且点击有效
            if (DateBeanHelper.compareDayBean(bean, beginDayBean) > 0) {
                myHomeData.setInDate(beginDayBean.toString());
                myHomeData.setOutDate(bean.toString());
                myHomeData.save();

//                myHomeData = DataSupport.findFirst(MyHomeData.class);

                finish();
            } else {
                // 第一次点击
                firstClick(bean);
            }
        } else {
            // 第一次点击
            firstClick(bean);
        }
    }

    private void firstClick(HotelDayBean bean) {
        Calendar calendar = DateBeanHelper.setCalendar(bean);
        HotelDayBean addDayBean = DateBeanHelper.getAddDayBean(calendar);
        DateBeanHelper.setAllItemsState(items, todayBean, addDayBean);
        beginDayBean.setDate(bean.getYear(), bean.getMonth(), bean.getDay(), HotelDayBean.STATE_CHOSED);
        bean.setState(HotelDayBean.STATE_CHOSED);
        adapter.notifyDataSetChanged();
    }
}
