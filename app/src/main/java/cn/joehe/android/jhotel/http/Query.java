package cn.joehe.android.jhotel.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hemiao on 2017/12/24.
 */

public class Query<T> {
    private String route;
    private T qs;
    private boolean json = true;

    public Query(String route, T qs) {
        this.route = route;
        this.qs = qs;
    }

    public String getJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

