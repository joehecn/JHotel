package cn.joehe.android.jhotel.calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

/**
 * Created by hemiao on 2017/12/23.
 */

public class DividerItemDecortion extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
    private Drawable mDivider;

    public DividerItemDecortion(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        drawHorizontal(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + 1;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
