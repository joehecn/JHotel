package cn.joehe.android.jhotel.http;

/**
 * Created by hemiao on 2017/12/24.
 */

public class QsSearch {
    private String cityUrl;
    private String q;
    private String cityName;
    private String lq;
    private String limit;
    private String fromDate;
    private String toDate;
    private String prs;
    private String levels;
    // private long _v;
    private boolean priceSort = false;
    private boolean scoreSort = false;
    // private long _;

    public QsSearch(String cityUrl, String q, String cityName, String lq, String limit, String fromDate, String toDate, String prs, String levels) {
        this.cityUrl = cityUrl;
        this.q = q;
        this.cityName = cityName;
        this.lq = lq;
        this.limit = limit;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.prs = prs;
        this.levels = levels;
        // this._v = System.currentTimeMillis();
        // this.priceSort = priceSort;
        // this.scoreSort = scoreSort;
        // this._ = System.currentTimeMillis();
    }
}
