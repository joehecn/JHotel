package cn.joehe.android.jhotel.http;

/**
 * Created by hemiao on 2017/12/24.
 */

public class MineResult<T> {
    private boolean ret;
    private String codeImg;
    private T data;

    public boolean isRet() {
        return ret;
    }

    public String getCodeImg() {
        return codeImg;
    }

    public T getData() {
        return data;
    }
}

