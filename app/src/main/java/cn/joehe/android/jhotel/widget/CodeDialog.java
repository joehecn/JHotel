package cn.joehe.android.jhotel.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import cn.joehe.android.jhotel.R;
import cn.joehe.android.jhotel.http.CodeBody;
import cn.joehe.android.jhotel.http.MineRequest;
import cn.joehe.android.jhotel.http.MineResult;
import cn.joehe.android.jhotel.util.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hemiao on 2017/12/24.
 */

public class CodeDialog extends Dialog {
    private String codeImg;
    private String query;

    private String body;

    private ImageView mIvCode;
    private LinearLayout mWrapCode;
    private EditText mEtCode;
    private Button mBtnSubmitCode;

    private IOnSubmitListener submitListener;

    public CodeDialog(@NonNull Context context, String codeImg, String query, IOnSubmitListener submitListener) {
        super(context);
        this.codeImg = codeImg;
        this.query = query;
        this.submitListener = submitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_dialog);

        mIvCode = findViewById(R.id.iv_code);
        mWrapCode = findViewById(R.id.wrap_code);
        mEtCode = findViewById(R.id.et_code);
        mBtnSubmitCode = findViewById(R.id.btn_submit_code);

        mIvCode.setImageBitmap(Utility.stringToBitmap(codeImg));

        // 获取焦点并弹出软键盘
        mEtCode.setFocusable(true);
        mEtCode.setFocusableInTouchMode(true);
        mEtCode.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        mWrapCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<MineResult> call = MineRequest.getCodeCall();
                call.enqueue(new Callback<MineResult>() {
                    @Override
                    public void onResponse(Call<MineResult> call, Response<MineResult> response) {
                        MineResult result = response.body();
                        mIvCode.setImageBitmap(Utility.stringToBitmap(result.getCodeImg()));
                    }

                    @Override
                    public void onFailure(Call<MineResult> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        mBtnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mEtCode.getText().toString().trim();
                if (code.length() == 4) {
                    mEtCode.setText("");
                    CodeBody codeBody = new CodeBody(code, query);
                    Call<ResponseBody> call = MineRequest.postSubmitCall(codeBody);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                body = response.body().string();
                                Gson gson = new Gson();
                                MineResult result = gson.fromJson(body, MineResult.class);
                                if (!result.isRet() && result.getCodeImg() != null) {
                                    mIvCode.setImageBitmap(Utility.stringToBitmap(result.getCodeImg()));
                                } else {
                                    Log.d("---- CodeDialog", "onResponse: " + result.isRet());
                                    if (submitListener != null)
                                        submitListener.onSubmit(CodeDialog.this);
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
                } else {
                    Toast.makeText(getContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getBody() {
        return body;
    }

    public interface IOnSubmitListener {
        void onSubmit(CodeDialog dialog);
    }
}
