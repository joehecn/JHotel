package cn.joehe.android.jhotel;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.joehe.android.jhotel.calendar.CalendarActivity;
import cn.joehe.android.jhotel.city.CityActivity;
import cn.joehe.android.jhotel.db.MyHomeData;
import cn.joehe.android.jhotel.hotellist.HotellistActivity;
import cn.joehe.android.jhotel.keyword.KeywordActivity;
import cn.joehe.android.jhotel.util.Utility;
import cn.joehe.android.jhotel.widget.HmDialog;

import static cn.joehe.android.jhotel.util.Utility.setBarTransparent;

public class MainActivity extends AppCompatActivity {
    // 常量 requestCode 请求码, 自己随便定义的
    private final int SDK_PERMISSION_REQUEST = 1270;
    // 数据
    private MyHomeData myHomeData;
    // 监听网络
    private boolean networkStatus = false;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    // 定位相关
    private LocationClient mLocationClient;
    private LocationClientOption option;
    private List<String> cityCodes;
    private List<String> cityNames;
    private List<String> cityUris;
    // 声明一个AlertDialog构造器
    // 申请定位权限
    private AlertDialog.Builder builder;
    // 定位结果回调，重写onReceiveLocation方法
    private BDAbstractLocationListener mListener;

    // 控件
    private ImageButton mBtnNavBack;
    private TextView mTvMyLocation;
    private TextView mTvSetPlace;
    private TextView mTvCity;
    private TextView mTvCalendar;
    private TextView mTvKeyword;
    private Button mBtnSearch;
    private TextView mTvPromise;
    private TextView mTvOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 标题栏背景透明
        setBarTransparent(getWindow());

        // 数据
        myHomeData = DataSupport.findFirst(MyHomeData.class);
        if (myHomeData == null) {
            String[] dates = Utility.getDates();
            myHomeData = new MyHomeData();
            myHomeData.setCity("");
            myHomeData.setCityUri("");
            myHomeData.setInDate(dates[0]);
            myHomeData.setOutDate(dates[1]);
            myHomeData.setKeyword("");
            myHomeData.save();
        } else if (Utility.needUpdateDate(myHomeData.getInDate())) {
            String[] dates = Utility.getDates();
            myHomeData.setInDate(dates[0]);
            myHomeData.setOutDate(dates[1]);
            myHomeData.save();
        }

        // 获取广播实例
        // 是否在线
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        // 初始化 百度地图定位api
        cityCodes = Arrays.asList(getResources().getStringArray(R.array.city_codes));
        cityNames = Arrays.asList(getResources().getStringArray(R.array.city_names));
        cityUris = Arrays.asList(getResources().getStringArray(R.array.city_uris));
        initBaiduLocation();

        // 初始化控件
        mBtnNavBack = findViewById(R.id.btn_nav_back);
        mTvMyLocation = findViewById(R.id.tv_my_location);
        mTvSetPlace = findViewById(R.id.tv_set_place);
        mTvCity = findViewById(R.id.tv_city);
        mTvCalendar = findViewById(R.id.tv_calendar);
        mTvKeyword = findViewById(R.id.tv_keyword);
        mBtnSearch = findViewById(R.id.btn_search);
        mTvPromise = findViewById(R.id.tv_promise);
        mTvOrder = findViewById(R.id.tv_order);

        // 事件监听
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        myHomeData = DataSupport.findFirst(MyHomeData.class);

        if (Utility.needUpdateDate(myHomeData.getInDate())) {
            String[] dates = Utility.getDates();
            myHomeData.setInDate(dates[0]);
            myHomeData.setOutDate(dates[1]);
            myHomeData.save();
        }

        mTvCity.setText(myHomeData.getCity());

        String[] covers = Utility.coverDates(myHomeData.getInDate(), myHomeData.getOutDate());
        String _text = "<br><small><font color='#8c8c8c'>" +
                covers[2] + "入住, " + covers[3] + "离店 ( " + covers[4] + "晚 )</font></small>";
        mTvCalendar.setText(Html.fromHtml(covers[0] + " - " + covers[1] + _text));

        String keyword = myHomeData.getKeyword();

        if (keyword.equals("")) {
            mTvKeyword.setText("搜索酒店名、地名、地标");
            mTvKeyword.setTextColor(getResources().getColor(R.color.colorSecondary));
        } else {
            mTvKeyword.setText(keyword);
            mTvKeyword.setTextColor(getResources().getColor(R.color.colorImportant));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @TargetApi( Build.VERSION_CODES.M )
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    mTvMyLocation.setText("定位中...");
                    mLocationClient.start();
                } else {
                    // Permission Denied
                    // 弹出对话框，提示用户权限不够
                    showSimpleDialog();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi( Build.VERSION_CODES.M )
    private void getPersimmions() {
        if (mTvMyLocation.getText().toString().equals(getString(R.string.startlocation)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 必须权限，用户如果禁止，则每次都会申请
             */
            // 定位精确位置
            // ACCESS_FINE_LOCATION 用于访问GPS定位
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            // ACCESS_COARSE_LOCATION 用于进行网络定位
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // WRITE_EXTERNAL_STORAGE 读写权限
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            // READ_PHONE_STATE 读取电话状态权限
            if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            } else {
                mTvMyLocation.setText("定位中...");
                mLocationClient.start();
            }
        }
    }

    // 初始化 百度地图定位api
    private void initBaiduLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        option = new LocationClientOption();

        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setCoorType("bd09ll");

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(1000);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setWifiCacheTimeOut(5*60*1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);

        mListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                    Log.d("MyLocationListener----", "onReceiveLocation: " + bdLocation.getAddrStr());
                    Log.d("MyLocationListener----", "onReceiveLocation: " + bdLocation.getDistrict());
                    Log.d("MyLocationListener----", "onReceiveLocation: " + bdLocation.getStreet());
                    Log.d("MyLocationListener----", "onReceiveLocation: " + bdLocation.getStreetNumber());

                    int index = cityCodes.indexOf(bdLocation.getCityCode());
                    if (index > 0) {
                        String keyword = bdLocation.getDistrict() + bdLocation.getStreet() + bdLocation.getStreetNumber();
                        myHomeData.setCity(cityNames.get(index));
                        myHomeData.setCityUri(cityUris.get(index));
                        myHomeData.setKeyword(keyword);
                        myHomeData.save();

                        mTvCity.setText(myHomeData.getCity());
                        mTvKeyword.setText(myHomeData.getKeyword());
                        mTvKeyword.setTextColor(getResources().getColor(R.color.colorImportant));
                    } else {
                        Toast.makeText(MainActivity.this, "定位失败，请手动选择城市", Toast.LENGTH_SHORT).show();
                    }

                }
                mLocationClient.stop();
                mTvMyLocation.setText(getString(R.string.startlocation));
            }
        };
        //注册监听函数
        mLocationClient.registerLocationListener(mListener);
    }

    // 事件监听
    private void setListeners() {
        Onclick onclick = new Onclick();
        mBtnNavBack.setOnClickListener(onclick);
        mTvMyLocation.setOnClickListener(onclick);
        mTvSetPlace.setOnClickListener(onclick);
        mTvCalendar.setOnClickListener(onclick);
        mTvKeyword.setOnClickListener(onclick);
        mBtnSearch.setOnClickListener(onclick);
        mTvPromise.setOnClickListener(onclick);
        mTvOrder.setOnClickListener(onclick);
    }

    private void showSimpleDialog() {
        builder = new AlertDialog.Builder(this);

        builder.setTitle("申请权限");
        builder.setMessage("获取位置需要定位权限，请批准。");
        builder.setCancelable(false);

        builder.setPositiveButton("去设置页面设置权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 事件监听回调
    private class Onclick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent;

            // 为了离线能用，所以提出来
            if (R.id.btn_nav_back == view.getId()) {
                finish();
                return;
            }

            if (!networkStatus) {
                Toast.makeText(MainActivity.this, "没有网络", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (view.getId()) {
                case R.id.tv_my_location:
                    getPersimmions();
                    break;
                case R.id.tv_set_place:
                    intent = new Intent(MainActivity.this, CityActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_calendar:
                    intent = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_keyword:
                    if (myHomeData.getCity().equals("") || myHomeData.getCityUri().equals(""))
                        Toast.makeText(MainActivity.this, "请先选择城市", Toast.LENGTH_SHORT).show();
                    else {
                        intent = new Intent(MainActivity.this, KeywordActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.btn_search:
                    if (myHomeData.getCity().equals("") || myHomeData.getCityUri().equals("") ||
                            myHomeData.getInDate().equals("") || myHomeData.getOutDate().equals(""))
                        Toast.makeText(MainActivity.this, "请先选择城市和日期", Toast.LENGTH_SHORT).show();
                    else {
                        intent = new Intent(MainActivity.this, HotellistActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.tv_promise:
                    final HmDialog hmDialog = new HmDialog(MainActivity.this);
                    hmDialog.show();
                    break;
                case R.id.tv_order:
                    Toast.makeText(MainActivity.this, "搜索订单", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                networkStatus = true;
                if (myHomeData.getCity().equals("")) {
                    getPersimmions();
                }
                Toast.makeText(MainActivity.this, "系统在线", Toast.LENGTH_SHORT).show();
            } else {
                networkStatus = false;
                Toast.makeText(MainActivity.this, "系统离线", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
