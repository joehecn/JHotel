package cn.joehe.android.jhotel.keyword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.db.MyHomeData;
import cn.joehe.android.jhotel.http.DataFilter;
import cn.joehe.android.jhotel.http.MineRequest;
import cn.joehe.android.jhotel.http.MineResult;
import cn.joehe.android.jhotel.http.QsFilter;
import cn.joehe.android.jhotel.http.Query;
import cn.joehe.android.jhotel.keyword.bean.KeywordCategoryBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordDataBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordItemBean;
import cn.joehe.android.jhotel.widget.CodeDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.joehe.android.jhotel.util.Utility.setBarTransparent;

/**
 * Created by hemiao on 2017/12/23.
 */

public class KeywordActivity extends AppCompatActivity {
    // 数据
    private MyHomeData myHomeData;
    private List<KeywordDataBean> items = new ArrayList<>(); // 所有的条目
    private KeywordAdapter adapter;

    // 控件
    private ImageButton mBtnNavBack;
    private TextView mTvNavTitle;

    private RecyclerView mRvKeyword;

    private SearchView mSvSearchview;
    private SearchFragment mSearchFragment;
    private FrameLayout mProgressBar;

    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        // 标题栏背景透明
        setBarTransparent(getWindow());
        // 设置标题
        mTvNavTitle = findViewById(R.id.tv_nav_title);
        mTvNavTitle.setText("搜索关键词");
        // 返回主界面
        mBtnNavBack = findViewById(R.id.btn_nav_back);
        mBtnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化控件
        mSvSearchview = findViewById(R.id.searchview);
        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) mSvSearchview.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));

        mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
        mProgressBar = findViewById(R.id.progress);

        mRvKeyword = findViewById(R.id.rv_keyword);
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                KeywordDataBean bean = items.get(position);
                if (bean instanceof KeywordCategoryBean)
                    return 3;
                return 1;
            }
        });
        mRvKeyword.setLayoutManager(mGridLayoutManager);
        adapter = new KeywordAdapter(this, items);
        mRvKeyword.setAdapter(adapter);
        adapter.setOnItemClickLister(new KeywordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                KeywordItemBean bean = (KeywordItemBean) items.get(position);
                if (bean != null) {
                    sovleItemClick(bean);
                }
            }
        });

        // 初始化数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);

        QsFilter qs = new QsFilter(myHomeData.getCityUri());
        Query<QsFilter> query = new Query<>("/render/hotel/location/filter.json", qs);
        final String queryStr = query.getJSON();
        Log.d("---- KeywordActivity", "onCreate: " + queryStr);

        Call<ResponseBody> call = MineRequest.getCall(queryStr);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body = response.body().string();
                    MineResult<DataFilter> result = gsonBody(body);

                    if (!result.isRet() && result.getCodeImg() != null) {
                        CodeDialog codeDialog = new CodeDialog(KeywordActivity.this, result.getCodeImg(), queryStr, new CodeDialog.IOnSubmitListener() {
                            @Override
                            public void onSubmit(CodeDialog dialog) {
                                String body = dialog.getBody();
                                MineResult<DataFilter> result = gsonBody(body);
                                DataFilter data = result.getData();
//                                Log.d("---- KeywordActivity", "onSubmit: " + data.getRegion());
//                                Log.d("---- KeywordActivity", "onSubmit: " + data.getAirAndStation());
                                initItems(data.getBeans());
                                dialog.dismiss();
                            }
                        });
                        codeDialog.setCancelable(false);
                        codeDialog.show();
                    } else {
                        DataFilter data = result.getData();
//                        Log.d("---- KeywordActivity", "onResponse: " + data.getRegion());
//                        Log.d("---- KeywordActivity", "onResponse: " + data.getAirAndStation());
                        initItems(data.getBeans());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 搜索
        initSearch();
    }

    /**
     * 点击事件
     * @param bean
     */
    private void sovleItemClick(KeywordItemBean bean) {
        myHomeData.setKeyword(bean.getValue());
        myHomeData.save();
        finish();
    }

    private MineResult<DataFilter> gsonBody(String body) {
        Gson gson = new Gson();
        Type mType = new TypeToken<MineResult<DataFilter>>(){}.getType();
        MineResult<DataFilter> result = gson.fromJson(body, mType);
        return result;
    }

    private void initItems(List<KeywordDataBean> beans) {
        items.addAll(beans);
        adapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

        mSvSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0) {
                    if (mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
                    }
                } else {
                    if (!mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
                    }
                }

                mSearchFragment.bindQueryText(newText);
                return false;
            }
        });
    }
}
