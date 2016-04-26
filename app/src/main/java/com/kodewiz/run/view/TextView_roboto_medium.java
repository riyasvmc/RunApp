package com.kodewiz.run.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView_roboto_medium extends TextView{

	public TextView_roboto_medium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextView_roboto_medium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextView_roboto_medium(Context context) {
        super(context);
        init();
    }
	private void init() {
        if (!isInEditMode()) {
            Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/roboto/roboto_medium.ttf");
            setTypeface(typeFace);
        }
    }
}
