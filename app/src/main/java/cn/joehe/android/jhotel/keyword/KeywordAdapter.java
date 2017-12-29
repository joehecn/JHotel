package cn.joehe.android.jhotel.keyword;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.keyword.bean.KeywordCategoryBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordDataBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordItemBean;

/**
 * Created by hemiao on 2017/12/23.
 */

public class KeywordAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int TV_CATEGORY = 1003;
    private static final int TV_ITEM = 1005;

    private List<KeywordDataBean> items;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public KeywordAdapter(Context mContext, List<KeywordDataBean> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TV_CATEGORY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_category, null);
            view.setTag(TV_CATEGORY);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_item, null);
            view.setTag(TV_ITEM);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof CategoryViewHolder) {
            KeywordCategoryBean item = (KeywordCategoryBean) items.get(position);
            ((CategoryViewHolder) holder).value.setText(item.getValue());
        } else {
            KeywordItemBean item = (KeywordItemBean) items.get(position);
            ((ItemViewHolder) holder).value.setText(item.getValue());

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
        KeywordDataBean item = items.get(position);
        if (item instanceof KeywordCategoryBean)
            return TV_CATEGORY;
        return TV_ITEM;
    }

    @Override
    public int getItemCount() {
//        Log.d("---- KeywordAdapter", "getItemCount: " + items.size());
        return items.size();
    }

    public void setOnItemClickLister(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private class CategoryViewHolder extends ViewHolder {
        TextView value;

        public CategoryViewHolder(View view) {
            super(view);

            value = (TextView) view;
        }
    }

    private class ItemViewHolder extends ViewHolder {
        TextView value;

        public ItemViewHolder(View view) {
            super(view);

            value = (TextView) view;
        }
    }
}
