package cn.joehe.android.jhotel.city;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.db.MyHomeData;

/**
 * Created by hemiao on 2017/12/23.
 */

public class SearchFragment extends Fragment {
    // 数据
    private MyHomeData myHomeData;
    private List<String> cityNames;
    private List<String> cityUris;

    private RecyclerView mRecyclerView;
    private TextView mTvNoResult;
    private SearchAdapter mAdapter;
    private List<CityEntity> mDatas;

    private String mQueryText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);
        cityNames = Arrays.asList(getResources().getStringArray(R.array.city_names));
        cityUris = Arrays.asList(getResources().getStringArray(R.array.city_uris));

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mTvNoResult = view.findViewById(R.id.tv_no_result);
        mRecyclerView = view.findViewById(R.id.recy);
        return view;
    }

    public void bindDatas(List<CityEntity> datas) {
        this.mDatas = datas;
        mAdapter = new SearchAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        if (mQueryText != null) {
            mAdapter.getFilter().filter(mQueryText);
        }
    }

    /**
     * 根据newText 进行查找, 显示
     */
    public void bindQueryText(String newText) {
        if (mDatas == null) {
            mQueryText = newText.toLowerCase();
        } else if (!TextUtils.isEmpty(newText)) {
            mAdapter.getFilter().filter(newText.toLowerCase());
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> implements Filterable {
        private List<CityEntity> items = new ArrayList<>();

        public SearchAdapter() {
            items.clear();
            items.addAll(mDatas);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            final VH holder = new VH(LayoutInflater.from(getActivity()).inflate(R.layout.item_city, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();

                    int index = cityNames.indexOf(items.get(position).getName());
                    myHomeData.setCity(items.get(position).getName());
                    myHomeData.setCityUri(cityUris.get(index));
                    myHomeData.setKeyword("");
                    myHomeData.save();
                    getActivity().finish();
                }
            });
            return holder;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tvName.setText(items.get(position).getName());
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<CityEntity> list = new ArrayList<>();
                    for (CityEntity item : mDatas) {
                        if (item.getPinyin().startsWith(constraint.toString()) || item.getName().contains(constraint)) {
                            list.add(item);
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.count = list.size();
                    results.values = list;
                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ArrayList<CityEntity> list = (ArrayList<CityEntity>) results.values;
                    items.clear();
                    items.addAll(list);
                    if (results.count == 0) {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        class VH extends RecyclerView.ViewHolder {
            private TextView tvName;

            public VH(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
