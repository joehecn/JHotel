package cn.joehe.android.jhotel.http;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import cn.joehe.android.jhotel.keyword.bean.KeywordCategoryBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordDataBean;
import cn.joehe.android.jhotel.keyword.bean.KeywordItemBean;

/**
 * Created by hemiao on 2017/12/24.
 */

public class DataFilter {
    private String[] categorys;
    private FDetails details;

    public List<KeywordDataBean> getBeans() {
        List<KeywordDataBean> beans = new ArrayList<>();
        if (cheakTitle("观光景点"))
            beans.addAll(details.coverItems("观光景点", details.getTouristspots()));
        if (cheakTitle("车站/机场"))
            beans.addAll(details.coverItems("车站/机场", details.getAirAndStation()));
        if (cheakTitle("地铁线路"))
            beans.addAll(details.coverItems("地铁线路", details.getSubways()));
        if (cheakTitle("商圈"))
            beans.addAll(details.coverItems("商圈", details.getTradingArea()));
        return beans;
    }

    private boolean cheakTitle(String title) {
        boolean cheaked = false;
        int len = categorys.length;

        for (int i = 0; i < len; i++) {
            if (categorys[i].equals(title)) {
                cheaked = true;
                break;
            }
        }

        return cheaked;
    }

    private class FDetails {
        @SerializedName("商圈")
        private List<FItem> tradingArea;
        @SerializedName("区域")
        private List<FItem> region;
        @SerializedName("行政区")
        private List<FItem> administrativeArea;
        @SerializedName("地铁线路")
        private List<FItem> subways;
        @SerializedName("车站/机场")
        private List<FItem> airAndStation;
        @SerializedName("观光景点")
        private List<FItem> touristspots;
        @SerializedName("医院")
        private List<FItem> healthCares;
        @SerializedName("大学")
        private List<FItem> universitys;
        @SerializedName("郊游景点")
        private List<FItem> outing;

        public List<FItem> getTradingArea() {
            return tradingArea;
        }

        public List<FItem> getRegion() {
            return region;
        }

        public List<FItem> getAdministrativeArea() {
            return administrativeArea;
        }

        public List<FItem> getSubways() {
            return subways;
        }

        public List<FItem> getAirAndStation() {
            return airAndStation;
        }

        public List<FItem> getTouristspots() {
            return touristspots;
        }

        public List<FItem> getHealthCares() {
            return healthCares;
        }

        public List<FItem> getUniversitys() {
            return universitys;
        }

        public List<FItem> getOuting() {
            return outing;
        }

        public List<KeywordDataBean> coverItems(String title, List<FItem> items) {
            List<KeywordDataBean> beans = new ArrayList<>();
            beans.add(new KeywordCategoryBean(title));

            for(FItem item : items) {
                String[] values = GetValues(title, item);
                int len = values.length;

                for (int i = 0; i < len; i++) {
                    beans.add(new KeywordItemBean(values[i]));
                }
            }

            return beans;
        }

        private String[] GetValues(String title, FItem item) {
            if (title.equals("地铁线路"))
                return item.getQueryValue().split(",");
            else
                return item.getValue().split(",");
        }

        private class FItem {
            private String type;
            private String value;
            private String queryValue;

            public String getType() {
                return type;
            }

            public String getValue() {
                return value;
            }

            public String getQueryValue() {
                return queryValue;
            }

        }
    }
}
