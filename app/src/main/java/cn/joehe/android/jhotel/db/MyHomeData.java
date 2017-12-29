package cn.joehe.android.jhotel.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hemiao on 2017/12/23.
 */

public class MyHomeData extends DataSupport {
    private int id;
    private String city; // 深圳市 北京市
    private String cityUri; // shenzhen beijing_city
    private String inDate; // 2017-12-11
    private String outDate; // 2017-12-18
    private String keyword; // 松原北街

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityUri() {
        return cityUri;
    }

    public void setCityUri(String cityUri) {
        this.cityUri = cityUri;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
