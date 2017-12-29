package cn.joehe.android.jhotel.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hemiao on 2017/12/24.
 */

public class MineRequest {
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.11.148:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static RequestNine requestNine = retrofit.create(RequestNine.class);
    private static CodeNine codeNine = retrofit.create(CodeNine.class);
    private static SubmitNine submitNine = retrofit.create(SubmitNine.class);

    public static Call<ResponseBody> getCall(String req) {
        return requestNine.getResult(req);
    }
    public static Call<MineResult> getCodeCall() {
        return codeNine.getResult();
    }
    public static Call<ResponseBody> postSubmitCall(CodeBody codeBody) {
        return submitNine.postCode(codeBody);
    }

    private static interface RequestNine {
        @GET("/api/hotel/request_nine")
        Call<ResponseBody> getResult(@retrofit2.http.Query("req") String req);
    }
    private static interface CodeNine {
        @GET("/api/hotel/code_nine")
        Call<MineResult> getResult();
    }
    private static interface SubmitNine {
        @POST("/api/hotel/submit_nine")
        Call<ResponseBody> postCode(@Body CodeBody codeBody);
    }
}
