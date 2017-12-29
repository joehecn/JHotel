package cn.joehe.android.jhotel.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.joehe.android.jhotel.R;

/**
 * Created by hemiao on 2017/12/23.
 */

public class CalendarTextView extends AppCompatTextView {
    public static final int BACK_CIRCLE = 1002;

    private int backType;
    private Paint mCirclePaint;

    public CalendarTextView(Context context) {
        super(context);
    }

    public CalendarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCirclePaint=new Paint();
        mCirclePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width=getWidth();
        int height=getHeight();
        if(backType == BACK_CIRCLE)
            canvas.drawCircle(width/2,height/2,height/2, mCirclePaint);
        super.onDraw(canvas);
    }

    public void setBackType(int backType) {
        this.backType = backType;
    }
}
