package cn.joehe.android.jhotel.keyword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.db.MyHomeData;
import cn.joehe.android.jhotel.http.DataSuggest;
import cn.joehe.android.jhotel.http.MineRequest;
import cn.joehe.android.jhotel.http.MineResult;
import cn.joehe.android.jhotel.http.QsSuggest;
import cn.joehe.android.jhotel.http.Query;
import cn.joehe.android.jhotel.keyword.bean.KeywordItemBean;
import cn.joehe.android.jhotel.widget.CodeDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hemiao on 2017/12/23.
 */

public class SearchFragment extends Fragment {
    private MyHomeData myHomeData;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private TextView mTvNoResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mTvNoResult = view.findViewById(R.id.tv_no_result);
        mRecyclerView = view.findViewById(R.id.recy);

        mAdapter = new SearchAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public void bindQueryText(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            Log.d("---- SearchFragment", "bindQueryText: " + newText);
            // 网络查找
            mAdapter.findKeyword(newText.toLowerCase());
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> {
        private List<KeywordItemBean> items = new ArrayList<>();

        public SearchAdapter() {
            items.clear();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            final VH holder = new VH(LayoutInflater.from(getActivity()).inflate(R.layout.item_city, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    myHomeData.setKeyword(items.get(position).getValue());
                    myHomeData.save();
                    getActivity().finish();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tvName.setText(items.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void findKeyword(String q) {
            QsSuggest qs = new QsSuggest(myHomeData.getCity(), q);
            Query<QsSuggest> query = new Query<>("/render/keyword/suggest.json", qs);
            final String queryStr = query.getJSON();
            Log.d("---- SearchFragment", "findKeyword: " + queryStr);

            Call<ResponseBody> call = MineRequest.getCall(queryStr);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String body = response.body().string();
                        Log.d("---- SearchFragment", "onResponse: " + body);
                        MineResult<List<DataSuggest>> result = gsonBody(body);

                        if (!result.isRet() && result.getCodeImg() != null) {
                            CodeDialog codeDialog = new CodeDialog(getActivity(), result.getCodeImg(), queryStr, new CodeDialog.IOnSubmitListener() {
                                @Override
                                public void onSubmit(CodeDialog dialog) {
                                    String body = dialog.getBody();
                                    MineResult<List<DataSuggest>> result = gsonBody(body);

                                    List<DataSuggest> data = result.getData();
                                    initItems(data);
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            List<DataSuggest> data = result.getData();
                            Log.d("---- SearchFragment", "onResponse: " + data.get(0).getAhead());
                            initItems(data);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                private void initItems(List<DataSuggest> data) {
                    ArrayList<KeywordItemBean> list = new ArrayList<>();
                    for (DataSuggest item : data) {
                        list.add(new KeywordItemBean(item.getAhead()));
                    }
                    items.clear();
                    items.addAll(list);

                    if (items.size() > 0) {
                        mTvNoResult.setVisibility(View.INVISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    }

                    notifyDataSetChanged();
                }

                private MineResult<List<DataSuggest>> gsonBody(String body) {
                    Gson gson = new Gson();
                    Type mType = new TypeToken<MineResult<List<DataSuggest>>>(){}.getType();
                    MineResult<List<DataSuggest>> result = gson.fromJson(body, mType);
                    return result;
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
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
