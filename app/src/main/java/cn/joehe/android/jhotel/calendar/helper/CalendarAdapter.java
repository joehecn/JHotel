package cn.joehe.android.jhotel.calendar.helper;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.calendar.bean.HotelDateBean;
import cn.joehe.android.jhotel.calendar.bean.HotelDayBean;
import cn.joehe.android.jhotel.calendar.bean.HotelMonthBean;
import cn.joehe.android.jhotel.calendar.view.CalendarTextView;

/**
 * Created by hemiao on 2017/12/23.
 */

public class CalendarAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int TV_DAY = 1003;
    private static final int TV_MONTH = 1005;

    private List<HotelDateBean> items;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public CalendarAdapter(Context mContext, List<HotelDateBean> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TV_DAY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_day, null);
            view.setTag(TV_DAY);
            return new DayViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month, null);
            view.setTag(TV_MONTH);
            return new MonthViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Resources resources = mContext.getResources();
        if (holder instanceof MonthViewHolder) {
            HotelMonthBean item = (HotelMonthBean) items.get(position);
            ((MonthViewHolder) holder).month.setText(item.getMonth());
        } else {
            HotelDayBean item = (HotelDayBean) items.get(position);
            int day = item.getDay();
            int state = item.getState();
            if (day == 0)
                ((DayViewHolder) holder).day.setText("");
            else if (state == HotelDayBean.STATE_TODAY)
                ((DayViewHolder) holder).day.setText("今天");
            else ((DayViewHolder) holder).day.setText("" + day);

            ((DayViewHolder) holder).day.setBackgroundDrawable(null);
            ((DayViewHolder) holder).day.setBackType(0);
            ((DayViewHolder) holder).hint.setVisibility(View.GONE);
            switch (state) {
                case HotelDayBean.STATE_IGNORE:
                    ((DayViewHolder) holder).day.setTextColor(resources.getColor(R.color.colorBack));
                    break;
                case HotelDayBean.STATE_NORMAL:
                    ((DayViewHolder) holder).day.setTextColor(resources.getColor(R.color.colorImportant));
                    break;
                case HotelDayBean.STATE_TODAY:
                case HotelDayBean.STATE_WEEKEND:
                    ((DayViewHolder) holder).day.setTextColor(resources.getColor(R.color.colorWeekend));
                    break;
                case HotelDayBean.STATE_CHOSED:
                    ((DayViewHolder) holder).hint.setText("入住");
                    ((DayViewHolder) holder).hint.setVisibility(View.VISIBLE);
                    ((DayViewHolder) holder).day.setBackType(CalendarTextView.BACK_CIRCLE);
                    ((DayViewHolder) holder).day.setTextColor(resources.getColor(R.color.colorFront));
                    ((DayViewHolder) holder).day.setBackgroundDrawable(null);
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        HotelDateBean item = items.get(position);
        if (item instanceof HotelMonthBean)
            return TV_MONTH;
        return TV_DAY;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickLister(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private class DayViewHolder extends ViewHolder {
        FrameLayout dayContainer;
        CalendarTextView day;
        TextView hint;

        public DayViewHolder(View view) {
            super(view);

            dayContainer = view.findViewById(R.id.day_container);
            day = view.findViewById(R.id.day);
            hint = view.findViewById(R.id.hint);
        }
    }

    private class MonthViewHolder extends ViewHolder {
        TextView month;

        public MonthViewHolder(View view) {
            super(view);

            month = (TextView) view;
        }
    }
}
