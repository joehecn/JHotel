package cn.joehe.android.jhotel.keyword.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.joehe.android.jhotel.R;

/**
 * Created by hemiao on 2017/12/23.
 */

public class BorderTextView extends AppCompatTextView {

    public BorderTextView(Context context) {
        super(context);
    }
    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int sroke_width = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        //  将边框设为黑色
        paint.setColor(getResources().getColor(R.color.colorSecondary));
        //  画TextView的4个边
        // 上
        canvas.drawLine(sroke_width, sroke_width, this.getWidth() - sroke_width, sroke_width, paint);
        // 右
        canvas.drawLine(sroke_width, sroke_width, sroke_width, this.getHeight() - sroke_width, paint);

        canvas.drawLine(this.getWidth() - sroke_width, sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        canvas.drawLine(sroke_width, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        super.onDraw(canvas);
    }
}
