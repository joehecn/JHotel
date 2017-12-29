package cn.joehe.android.jhotel.hotellist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Type;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.db.MyHomeData;
import cn.joehe.android.jhotel.http.DataSearch;
import cn.joehe.android.jhotel.http.MineRequest;
import cn.joehe.android.jhotel.http.MineResult;
import cn.joehe.android.jhotel.http.QsSearch;
import cn.joehe.android.jhotel.http.Query;
import cn.joehe.android.jhotel.widget.CodeDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.joehe.android.jhotel.util.Utility.setBarTransparent;

/**
 * Created by hemiao on 2017/12/23.
 */

public class HotellistActivity extends AppCompatActivity {

    // 控件
    private ImageButton mBtnNavBack;
    private TextView mTvNavTitle;

    // 数据
    private MyHomeData myHomeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotellist);
        // 标题栏背景透明
        setBarTransparent(getWindow());
        // 设置标题
        mTvNavTitle = findViewById(R.id.tv_nav_title);
        mTvNavTitle.setText("酒店列表");
        // 返回主界面
        mBtnNavBack = findViewById(R.id.btn_nav_back);
        mBtnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);

        String q = null;
        if (myHomeData.getKeyword().length() > 0) {
            q = myHomeData.getKeyword();
        }
        QsSearch qs = new QsSearch(myHomeData.getCityUri(), q, myHomeData.getCity(), null, "0,10", myHomeData.getInDate(), myHomeData.getOutDate(), null, null);
        Query<QsSearch> query = new Query<>("/hotel/search.json", qs);
        final String queryStr = query.getJSON();
        Log.d("---- HotellistActivity", "onCreate: " + queryStr);
        Call<ResponseBody> call = MineRequest.getCall(queryStr);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body = response.body().string();
                    MineResult<DataSearch> result = gsonBody(body);

                    if (!result.isRet() && result.getCodeImg() != null) {
                        CodeDialog codeDialog = new CodeDialog(HotellistActivity.this, result.getCodeImg(), queryStr, new CodeDialog.IOnSubmitListener() {
                            @Override
                            public void onSubmit(CodeDialog dialog) {
                                String body = dialog.getBody();
                                MineResult<DataSearch> result = gsonBody(body);
                                DataSearch data = result.getData();
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getHotelSearchCount());
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getHdsSeq());
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getRoomId());
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getTotalPrice());
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getDate());
                                Log.d("---- HotellistActivity", "onSubmit: " + data.getPrice());
                                dialog.dismiss();
                            }
                        });
                        codeDialog.setCancelable(false);
                        codeDialog.show();
                    } else {
                        DataSearch data = result.getData();
                        Log.d("---- HotellistActivity", "onResponse: " + data.getHotelSearchCount());
                        Log.d("---- HotellistActivity", "onResponse: " + data.getHdsSeq());
                        Log.d("---- HotellistActivity", "onResponse: " + data.getRoomId());
                        Log.d("---- HotellistActivity", "onResponse: " + data.getTotalPrice());
                        Log.d("---- HotellistActivity", "onResponse: " + data.getDate());
                        Log.d("---- HotellistActivity", "onResponse: " + data.getPrice());
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
    }

    private MineResult<DataSearch> gsonBody(String body) {
        Gson gson = new Gson();
        Type mType = new TypeToken<MineResult<DataSearch>>(){}.getType();
        MineResult<DataSearch> result = gson.fromJson(body, mType);
        return result;
    }
}
