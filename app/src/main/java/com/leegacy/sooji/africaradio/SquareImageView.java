package com.leegacy.sooji.africaradio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by soo-ji on 16-04-23.
 */
public class SquareImageView extends ImageView{
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int side = Math.min(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(side, side);
    }
}
