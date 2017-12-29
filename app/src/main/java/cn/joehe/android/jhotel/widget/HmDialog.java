package cn.joehe.android.jhotel.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import cn.joehe.android.jhotel.R;

/**
 * Created by hemiao on 2017/12/23.
 */

public class HmDialog extends Dialog {
    private ImageButton btnCancelDialog;

    public HmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hm_dialog);

        // 设置屏幕宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.9); // 设置 dialog 宽度
        getWindow().setAttributes(p);

        // cancel
        btnCancelDialog = findViewById(R.id.btn_cancel_dialog);
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
