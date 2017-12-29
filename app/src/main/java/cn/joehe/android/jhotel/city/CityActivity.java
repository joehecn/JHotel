package cn.joehe.android.jhotel.city;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.db.MyHomeData;
import me.yokeyword.indexablerv.EntityWrapper;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;

import static cn.joehe.android.jhotel.util.Utility.setBarTransparent;

/**
 * Created by hemiao on 2017/12/23.
 */

public class CityActivity extends AppCompatActivity {
    // 数据
    private MyHomeData myHomeData;
    private List<String> cityNames;
    private List<String> cityUris;
    private List<CityEntity> mDatas;
    // 控件
    private ImageButton mBtnNavBack;
    private TextView mTvNavTitle;

    private SearchView mSearchView;
    private IndexableLayout indexableLayout;
    private SearchFragment mSearchFragment;
    private FrameLayout mProgressBar;

    private SimpleHeaderAdapter mHotCityAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        // 标题栏背景透明
        setBarTransparent(getWindow());

        // 初始化控件
        mBtnNavBack = findViewById(R.id.btn_nav_back);
        mTvNavTitle = findViewById(R.id.tv_nav_title);

        mSearchView = findViewById(R.id.searchview);
        indexableLayout = findViewById(R.id.indexableLayout);
        mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
        mProgressBar = findViewById(R.id.progress);

        mTvNavTitle.setText("选择城市");
        mBtnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);
        cityNames = Arrays.asList(getResources().getStringArray(R.array.city_names));
        cityUris = Arrays.asList(getResources().getStringArray(R.array.city_uris));

        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));


        indexableLayout.setLayoutManager(new GridLayoutManager(this, 2));

        // 多音字处理
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)));

        // 快速排序。  排序规则设置为：只按首字母  （默认全拼音排序）  效率很高，是默认的10倍左右。  按需开启～
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);

        // setAdapter
        CityAdapter adapter = new CityAdapter(this);
        indexableLayout.setAdapter(adapter);
        // set Datas
        mDatas = initDatas();

        adapter.setDatas(mDatas, new IndexableAdapter.IndexCallback<CityEntity>() {
            @Override
            public void onFinished(List<EntityWrapper<CityEntity>> datas) {
                // 数据处理完成后回调
                mSearchFragment.bindDatas(mDatas);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        // set Center OverlayView
        indexableLayout.setOverlayStyle_Center();

        // set Listener
        adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
                Log.d("CityActivity ---", "onItemClick: " + entity.getName());
                int index = cityNames.indexOf(entity.getName());
                myHomeData.setCity(entity.getName());
                myHomeData.setCityUri(cityUris.get(index));
                myHomeData.setKeyword("");
                myHomeData.save();
                finish();
            }
        });

        // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
        // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter

        mHotCityAdapter = new SimpleHeaderAdapter<>(adapter, "热", "热门城市", iniyHotCityDatas());
        // 热门城市
        indexableLayout.addHeaderAdapter(mHotCityAdapter);
        // 定位
        final List<CityEntity> gpsCity = iniyGPSCityDatas();
        final SimpleHeaderAdapter gpsHeaderAdapter = new SimpleHeaderAdapter<>(adapter, "定", "当前城市", gpsCity);
        indexableLayout.addHeaderAdapter(gpsHeaderAdapter);

        // 显示真实索引

        // 搜索Demo
        initSearch();
    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
        for (String item : cityNames) {
            CityEntity cityEntity = new CityEntity();
            cityEntity.setName(item);
            list.add(cityEntity);
        }
        return list;
    }

    private List<CityEntity> iniyHotCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("北京"));
        list.add(new CityEntity("上海"));
        list.add(new CityEntity("广州"));
        list.add(new CityEntity("成都"));
        list.add(new CityEntity("杭州"));
        list.add(new CityEntity("深圳"));
        list.add(new CityEntity("三亚"));
        list.add(new CityEntity("西安"));
        list.add(new CityEntity("重庆"));
        list.add(new CityEntity("香港"));
        list.add(new CityEntity("武汉"));
        list.add(new CityEntity("南京"));
        list.add(new CityEntity("昆明"));
        list.add(new CityEntity("厦门"));
        list.add(new CityEntity("苏州"));
        return list;
    }

    private List<CityEntity> iniyGPSCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity(myHomeData.getCity()));
        return list;
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    @Override
    public void onBackPressed() {
        if (!mSearchFragment.isHidden()) {
            // 隐藏 搜索
            mSearchView.setQuery(null, false);
            getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
            return;
        }
        super.onBackPressed();
    }
}
